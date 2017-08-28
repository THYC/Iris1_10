package net.teraoctet.iris.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command_Reload implements CommandExecutor
{
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (commandLabel.equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("iris.reload")))
        {
            org.bukkit.Bukkit.reload();
            return true;
        }
        return false;
    }
}