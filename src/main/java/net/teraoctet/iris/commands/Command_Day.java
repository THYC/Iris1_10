package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Day
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("day") && (sender.isOp() || sender.hasPermission("iris.weather.day")))
        {
            player.getWorld().setTime(0L);
            broadcastMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdDay"),player));
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
    }
}
