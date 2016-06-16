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

public class Command_Warp
implements CommandExecutor
{
    private static final ConfigFile conf = new ConfigFile();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        if (args.length == 1)
        {
            if (commandLabel.equalsIgnoreCase("warp") && (sender.isOp() || sender.hasPermission("iris.warp")))
            {
                if (conf.IsConfigYAML("warp.yml",args[0]))
                {
                    String world = conf.getStringYAML("warp.yml",args[0] + ".world",player.getWorld().getName());
                    double X = conf.getDoubleYAML("warp.yml",args[0] + ".X",player.getLocation().getX());
                    double Y = conf.getDoubleYAML("warp.yml",args[0] + ".Y",player.getLocation().getY());
                    double Z = conf.getDoubleYAML("warp.yml",args[0] + ".Z",player.getLocation().getZ());
                    
                    Location lastLocation = player.getLocation();
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                    conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                    conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   
        
                    World worldInstance = Bukkit.getWorld(world);
                    Location location = new Location(worldInstance, X, Y, Z);
                    player.teleport(location);
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdWarp"),player));
                }
                else
                {
                  player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdErreurWarp"),player));
                }
            }
            else
            {
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission"),player));
                return true;
            }
        }
        else 
        {
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /warp <warpname>",player));
            String warps = "";
            for (String w : conf.getKeysYAML("warp.yml",""))
            {
                warps = warps + w + " ; ";
            }
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>" + warps,player));
        }
        return true;
    }
}
