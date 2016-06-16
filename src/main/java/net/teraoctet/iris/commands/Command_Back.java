package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Back
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("back") && (sender.isOp() || sender.hasPermission("iris.back")))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("Vous ne pouvez pas utiliser cette commande ici");
                return true;
            }
            Location lastLocation = player.getLocation();
                            
            double X = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", player.getLocation().getX());
            double Y = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", player.getLocation().getY());
            double Z = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", player.getLocation().getZ());
            String world = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", player.getLocation().getWorld().getName());
            
            World worldInstance = Bukkit.getWorld(world);
            Location location = new Location(worldInstance, X, Y, Z);
            player.teleport(location);
            
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdTP"),player));
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
            conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());    
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
            return true;
        }
        return true;
    }
}
