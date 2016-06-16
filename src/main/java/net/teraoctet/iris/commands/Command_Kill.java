package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Kill
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        
        if (commandLabel.equalsIgnoreCase("kill") && (sender.isOp() || sender.hasPermission("iris.kill"))) 
        {
            if (args.length == 0)
            {
                Location lastLocation = player.getLocation();
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   
                player.setHealth(0);
                return true;
            }
            else if (args.length == 1)
            {
                Player target = sender.getServer().getPlayer(args[0]);
                if (target == null)
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                }
                else
                {                        
                    Player targetPlayer = player.getServer().getPlayer(args[0]);
                    targetPlayer.setHealth(0);
                }
            }
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
    }
}