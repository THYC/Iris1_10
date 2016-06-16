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

public class Command_Lobby 
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdWarpNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        else
        {
            if (commandLabel.equalsIgnoreCase("lobby") && (sender.isOp() || sender.hasPermission("iris.lobby")))
            {
                Location lastLocation = player.getLocation();
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());    
                try
                {
                    String world = conf.getStringYAML("warp.yml","LOBBY.world",player.getWorld().getName());
                    double X = conf.getDoubleYAML("warp.yml","LOBBY.X",player.getLocation().getX());
                    double Y = conf.getDoubleYAML("warp.yml","LOBBY.Y",player.getLocation().getY());
                    double Z = conf.getDoubleYAML("warp.yml","LOBBY.Z",player.getLocation().getZ());

                    World worldInstance = Bukkit.getWorld(world);
                    Location location = new Location(worldInstance, X, Y, Z);
                    player.teleport(location);
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdLobby","<dark_aqua>Bienvenue au LOBBY Teraoctet !"),player));
                }
                catch(Exception e)
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","ErreurLobby","<dark_aqua>Location LOBBY non d<e_ai>finit, /setwarp LOBBY pour d<e_ai>finir le Lobby"),player));
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
