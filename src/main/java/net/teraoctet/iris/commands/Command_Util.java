package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Util  implements CommandExecutor
{
    Iris plugin;
        
    public Command_Util(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    { 
        Player player = (Player)sender;
        if ("1".equals(commandLabel) && (player.hasPermission("iris.vip.1") || player.isOp()))
        {           
            player.openInventory(plugin.inv);
            return true;
        }
        if ("2".equals(commandLabel) && (player.hasPermission("iris.vip.2") || player.isOp()))
        {           
           player.openWorkbench(player.getLocation(), true);
           return true;
        }
        if ("3".equals(commandLabel) && (player.hasPermission("iris.vip.3") || player.isOp()))
        {           
           player.openEnchanting(player.getLocation(), true);
           return true;
        }
        return true;
    }
    
}