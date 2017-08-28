package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Command_head 
implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        final Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("head") && player.hasPermission("iris.head"))
        {
            ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
            SkullMeta meta = (SkullMeta)is.getItemMeta();
                
            if(args.length==1)
            {
                switch(args[0])
                {
                    case "cactus":
                        meta.setOwner("MHF_Cactus");
                        break;
                    case "chest":
                        meta.setOwner("MHF_Chest");
                        break;                        
                    case "melon":
                        meta.setOwner("MHF_Melon");
                        break;                            
                    case "tnt":
                        meta.setOwner("MHF_TNT");
                        break;                                
                    case "question":
                        meta.setOwner("MHF_Question");
                        break;
                    default:
                        meta.setOwner(player.getName());
                        break;
                }  
                is.setItemMeta(meta);
                player.getInventory().addItem(is);
                player.updateInventory();
                return true;
            }
            else
            {
                if(args.length == 2 &&  "mhf".equals(args[0]))
                {
                    meta.setOwner(args[1]);
                    is.setItemMeta(meta);
                    player.getInventory().addItem(is);
                    player.updateInventory();
                    return true;
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format("<yellow>usage : /head <green>[cactus|chest|melon|tnt|question]"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>      : /head mhf <green>[non_du_mhf]"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>      : /head <green>[non_du_joueur]"));
                    player.sendMessage(Iris.formatMsg.format("<yellow>liens MHF: <aqua>http://minecraft.gamepedia.com/Mob_head"));
                    return true;
                }
            }
        }
        return true;
    } 
}
