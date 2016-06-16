package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_TP
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        
        if (commandLabel.equalsIgnoreCase("tp") && (sender.isOp() || sender.hasPermission("iris.tp")))
        {
            Player otherPlayer;
            switch(args.length)
            {
                case 0:
                    player.sendMessage(Iris.formatMsg.format("<gray>M<e_ai>thode: /tp <player> or /tp <player1> <player2> or /tp x y z",player));
                    break;
                case 1:
                    otherPlayer = sender.getServer().getPlayer(args[0]);
                    if (otherPlayer == null)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                    }
                    else
                    {
                        Location lastLocation = player.getLocation();
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                        conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                        conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   

                        otherPlayer = player.getServer().getPlayer(args[0]);
                        Location otherPlayerLocation = otherPlayer.getLocation();
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdTP"),player));
                        player.teleport(otherPlayerLocation);
                    }
                    break;
                case 2:
                    otherPlayer = sender.getServer().getPlayer(args[0]);
                    Player otherPlayer2 = sender.getServer().getPlayer(args[1]);
                    if (otherPlayer == null || otherPlayer2 == null)
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "offlinePlayer"),player));
                    }
                    else
                    {
                        otherPlayer = player.getServer().getPlayer(args[0]);
                        otherPlayer2 = player.getServer().getPlayer(args[1]);
                        Location otherPlayer2Location = otherPlayer2.getLocation();
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdTP"),player));
                        otherPlayer.teleport(otherPlayer2Location);
                    }
                    break;
                case 3:
                    Location lastLocation = player.getLocation();
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                    conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   

                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdTP"),player));
                    player.teleport(new Location(player.getWorld(), x, y, z));
                    break;                  
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
        
    
