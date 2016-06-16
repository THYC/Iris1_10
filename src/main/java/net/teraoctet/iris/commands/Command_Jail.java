package net.teraoctet.iris.commands;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import static org.bukkit.Bukkit.getOfflinePlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Jail 
implements CommandExecutor
{    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length > 0)
        {
            if(args[0].equalsIgnoreCase("list"))
            {
                sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /warp <warpname>"));
                String warps = "";
                for (String w : conf.getKeysYAML("jail.yml",""))
                {
                    warps = warps + w + " ; ";
                }
                sender.sendMessage(Iris.formatMsg.format("<dark_aqua>" + warps));
                return true;
            }
            Player playerJail = Bukkit.getPlayer(args[0]);
            String playerUUID = "";
            Long temps = 2L;
            String raison = "";
        
            for(Player playerOnline : Bukkit.getServer().getOnlinePlayers())
            {
                if(playerOnline.getDisplayName() == null ? args[0] == null : playerOnline.getDisplayName().equals(args[0]))
                {
                    playerUUID = playerJail.getUniqueId().toString();
                }
                else
                {
                    sender.sendMessage("Joueur absent");
                    playerUUID = getOfflinePlayer(args[0]).getUniqueId().toString();
                }
            }
                        
            String jail = "jail";
                
            if (commandLabel.equalsIgnoreCase("jail") && (sender.isOp() || sender.hasPermission("iris.jail")))
            {
                if (args.length > 1)
                {
                    jail = args[1];
                }
                if (conf.IsConfigYAML("jail.yml",jail))
                {                    
                    for(Player playerOnline : Bukkit.getServer().getOnlinePlayers())
                    {
                        if(playerOnline.getDisplayName() == null ? args[0] == null : playerOnline.getDisplayName().equals(args[0]))
                        {
                            String world = conf.getStringYAML("jail.yml",jail + ".world",playerJail.getWorld().getName());
                            double X = conf.getDoubleYAML("jail.yml",jail + ".X",playerJail.getLocation().getX());
                            double Y = conf.getDoubleYAML("jail.yml",jail + ".Y",playerJail.getLocation().getY());
                            double Z = conf.getDoubleYAML("jail.yml",jail + ".Z",playerJail.getLocation().getZ());

                            Location lastLocation = playerJail.getLocation();
                            conf.setDoubleYAML("userdata",playerUUID + ".yml", "LastLocation.X", lastLocation.getX());
                            conf.setDoubleYAML("userdata",playerUUID + ".yml", "LastLocation.Y", lastLocation.getY());
                            conf.setDoubleYAML("userdata",playerUUID + ".yml", "LastLocation.Z", lastLocation.getZ());
                            conf.setStringYAML("userdata",playerUUID + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   

                            World worldInstance = Bukkit.getWorld(world);
                            Location location = new Location(worldInstance, X, Y, Z);
                            playerJail.teleport(location);
                        }
                    }
                    if (args.length == 3)
                    {
                        temps = Long.valueOf(args[2]);
                    }
                    if (args.length > 3)
                    {
                        for (int i = 3; i < args.length; i++)
                        {
                            if (i != 3) 
                            {
                              raison = raison + ' ';
                            }
                            raison = raison + args[i];
                        } 
                    }
                    else
                    {
                        raison = "non respect du reglement";
                    }
                    
                    temps = temps * 24 * 3600 * 1000;
                    temps = System.currentTimeMillis() + temps;
                    
                    conf.setStringYAML("userdata",playerUUID + ".yml", "jail", jail); 
                    conf.setLongYAML("userdata",playerUUID + ".yml", "timejail", temps);
                    conf.setStringYAML("userdata",playerUUID + ".yml", "raison", raison);
                    broadcastMessage(formatMsg.format("<yellow>" + args[0] + " a ete emprisonné, motif : " + raison));
                }
                else
                {
                  sender.sendMessage(formatMsg.format("<aqua>Pas de jail enregistré !"));
                }
            }
            else
            {
                sender.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
                return true;
            }
        }
        else 
        {
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /jail list <gray>- liste les jails disponible"));
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /jail <player> <gray>- par default prison 'jail' sur 2 jours"));
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /jail <player> <jailName> <gray>- par default sur 2 jours"));
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /jail <player> <jailName> <nombreDeJour>"));
            sender.sendMessage(Iris.formatMsg.format("<dark_aqua>Usage: /jail <player> <jailName> <nombreDeJour> <raison>"));
        }
        return true;
    }
}
