package net.teraoctet.iris.commands;

import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command_AutoMessage implements CommandExecutor
{    
    Iris plugin;
    private final ConfigFile conf;
    
    public Command_AutoMessage(Iris plugin)
    {
        this.conf = new ConfigFile();
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return true;
    }
}
