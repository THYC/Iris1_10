package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.hordeManager;
import net.teraoctet.iris.utils.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command_Bank 
implements CommandExecutor
{   
    Economy economy = new Economy();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("bank") && (sender.isOp() || sender.hasPermission("iris.bank")))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("Vous ne pouvez pas utiliser cette commande ici");
                return true;
            }
            if(args.length == 0)
            {
                double credit = 0;
                if (player.hasPermission("iris.horde") && !player.isOp())
                {
                    credit = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Horde.banque", 0);
                }
                else
                {
                    credit = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "banque", 0);
                }
                player.sendMessage(formatMsg.format("<green><gras><star><star> Solde de votre compte : <yellow>" + credit + " emeraudes <star><star>"));
                player.sendMessage(formatMsg.format("<gras><white>-------------------------------------------"));
                if (sender.hasPermission("iris.bank.transaction"))
                {
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank retrait <green>[Nombre_d'emeraude] <gray>effectuer un retrait d'emeraudes"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank depot <green>[Nombre_d'emeraude] <gray>effectuer un depot d'emeraudes"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank vireh <green>[Nombre_d'emeraude] <gray>effectuer un virement sur votre horde"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank virej <green>[joueur] [Nombre_d'emeraude] <gray>effectuer un virement sur un joueur"));
                }
                return true;
            }
            else
            {
                if("retrait".equals(args[0]) && args.length == 2 && sender.hasPermission("iris.bank.transaction"))
                {
                    int retrait = Integer.valueOf(args[1]);
                    
                    double solde = 0;
                    if (player.hasPermission("iris.horde") && !player.isOp())
                    {
                        solde = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",0);
                    }
                    else
                    {
                        solde = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml","banque",0);
                    }
 
                    if(solde - retrait < 0)
                    {
                        player.sendMessage(formatMsg.format("<green>il ne vous reste que " + solde + " emeraudes sur votre compte, reformulez votre demande"));
                        return true;
                    }
                    
                    solde = solde - retrait;
                    
                    if (player.hasPermission("iris.horde") && !player.isOp())
                    {
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "Horde.banque", solde);
                    }
                    else
                    {
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "banque", solde);
                    }
                    
                    ItemStack emeraude = new ItemStack(Material.EMERALD, retrait);
                    player.getInventory().addItem(emeraude);
                    player.updateInventory();
                    player.sendMessage(formatMsg.format("<green>" + retrait + " emeraudes ont <e_ai>t<e_ai> retir<e_ai> de votre compte et mis dans votre inventaire"));
                    player.sendMessage(formatMsg.format("<italique><yellow>vous avez maintenant un credit de : "+ solde + " emeraudes sur votre compte en banque"));
                    return true;
                }
                else if("depot".equals(args[0]) && args.length == 2 && sender.hasPermission("iris.bank.transaction"))
                {
                    int depot = Integer.valueOf(args[1]);
                    
                    double solde = 0;
                    if (player.hasPermission("iris.horde") && !player.isOp())
                    {
                        solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",0);
                    }
                    else
                    {
                        solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","banque",0);
                    }
                    
                    ItemStack emeraude = new ItemStack(Material.EMERALD, depot);
                    if(player.getInventory().contains(Material.EMERALD,depot))
                    {
                        player.getInventory().removeItem(emeraude);
                        player.updateInventory();
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<green>vous n'avez pas " + depot + " emeraudes dans votre inventaire, reformulez votre demande"));
                        return true;
                    }
                    solde = solde + depot;
                    
                    if (player.hasPermission("iris.horde") && !player.isOp())
                    {
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "Horde.banque", solde);
                    }
                    else
                    {
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "banque", solde);
                    }
                                        
                    player.sendMessage(formatMsg.format("<green>" + depot + " emeraudes ont <e_ai>t<e_ai> ajout<e_ai> <a_gr> votre compte"));
                    player.sendMessage(formatMsg.format("<italique><yellow>vous avez maintenant un credit de : "+ solde + " emeraudes"));
                    return true;
                }
                else if("virej".equals(args[0]) && sender.hasPermission("iris.bank.transaction"))
                {
                    if(args.length == 3 )
                    {
                        Player player2 = Bukkit.getPlayer(args[1]);
                        int virement = Integer.valueOf(args[2]);
                        if (economy.setVersement(player, player, virement) == true)
                        {
                            player.sendMessage(formatMsg.format("<yellow>Virement effectué"));
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<gray>principe : <yellow>/bank virej [joueur] [Nombre_d'emeraude]"));
                    }
                }
                else if("vireh".equals(args[0]) && sender.hasPermission("iris.horde") && !player.isOp())
                {
                    if(args.length == 2 )
                    {
                        int depot = Integer.valueOf(args[1]);
                        int solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",0);
                        solde = solde - depot;
                        conf.setIntYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",solde);
                        
                        int soldeH = conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Bank", 0 );
                        soldeH = soldeH + depot;
                        conf.setIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Bank", soldeH );
                        
                        player.sendMessage(formatMsg.format("<yellow>Vous venez de déposer " + depot + " Emeraude(s) sur le compte de votre Horde"));
                        
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<gray>principe : <yellow>/bank vireh [Nombre_d'emeraude]"));
                    }
                }
                    
                else if("verse".equals(args[0]) && (sender.hasPermission("iris.bank.admin") || sender.isOp()))
                {
                    if(args.length == 3 )
                    {
                        int depot = Integer.valueOf(args[1]);
                        double solde = 0;
                        if (player.hasPermission("iris.horde") && !player.isOp())
                        {
                            solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",0);
                        }
                        else
                        {
                            solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","banque",0);
                        }

                        solde = solde + depot;

                        Player p = Bukkit.getPlayer(args[2]);
                        if (p == null)
                        {
                            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                        }
                        else
                        {                            
                            if (player.hasPermission("iris.horde") && !player.isOp())
                            {
                                conf.setDoubleYAML("userdata",p.getUniqueId() + ".yml", "Horde.banque", solde);
                            }
                            else
                            {
                                conf.setDoubleYAML("userdata",p.getUniqueId() + ".yml", "banque", solde);
                            }

                            player.sendMessage(formatMsg.format("<green>" + depot + " emeraudes ont <e_ai>t<e_ai> ajout<e_ai> <a_gr> votre compte"));
                            player.sendMessage(formatMsg.format("<italique><yellow>vous avez maintenant un credit de : "+ solde + " emeraudes"));
                            return true;
                        }
                    }
                    else
                    {
                        player.sendMessage(formatMsg.format("<gray>principe : <yellow>/bank verse [Nombre_d'emeraude] [nom-du-joueur]"));
                    }
                }
                else if(!sender.hasPermission("iris.bank.transaction"))
                {
                    player.sendMessage(formatMsg.format("<gray>Vous n'avez pas acces <a_gr> cette commande !"));
                    player.sendMessage(formatMsg.format("<gray>Pour retirer/deposer des Emeraudes de votre compte, allez <a_gr> la banque du Lobby"));
                }
                else
                {
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank retrait <green>[Nombre_d'emeraude] <gray>effectuer un retrait d'emeraudes"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank depot <green>[Nombre_d'emeraude] <gray>effectuer un depot d'emeraudes"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank vireh <green>[Nombre_d'emeraude] <gray>effectuer un virement sur votre horde"));
                    player.sendMessage(formatMsg.format("<gray>- <yellow>/bank virej <green>[joueur] [Nombre_d'emeraude] <gray>effectuer un virement sur un joueur"));
                }
            }
        }
        return true;
    }
}
