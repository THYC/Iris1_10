package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Command_Invsee
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if ((sender instanceof Player))
        {
            Player player = (Player)sender;
            if (commandLabel.equalsIgnoreCase("invsee") && (sender.isOp() || sender.hasPermission("iris.invsee")))
            {
                if (args.length < 1) 
                {
                    player.sendMessage(Iris.formatMsg.format("<gray>M<e_ai>thode: /invsee <player>",player));
                }
                else
                {
                    Player otherP = Bukkit.getPlayer(args[0]);
                    if (otherP == null)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                    }
                    else
                    {
                        Inventory inv;
                        if (args.length > 1)
                        {
                            inv = player.getPlayer().getServer().createInventory(null, 9, "Equipped");
                            inv.setContents(otherP.getInventory().getArmorContents());
                        }
                        else
                        {
                            inv = otherP.getInventory();
                        }
                        player.closeInventory();
                        player.openInventory(inv);
                    }
                    return true;
                }
                
            }
            else
            {
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
                return true;
            }
        }
        return true;
    }
}
