package net.teraoctet.iris.commands;

import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
//import static net.teraoctet.iris.Iris.ooldowns;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Home
implements CommandExecutor
{
    private final ConfigFile conf = new ConfigFile();
    
    public Command_Home(Iris instance) 
    {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;
        String home = "home";
        String world= player.getLocation().getWorld().getName();
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs")));
            return true;
        }
        
        int cooldownTime = conf.getIntYAML("config.yml","CoolDownTime",60);
        if(plugin.cooldowns.containsKey(player.getDisplayName())) 
        {
            long secondsLeft = ((plugin.cooldowns.get(player.getDisplayName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
            if(secondsLeft>0) {
                player.sendMessage(formatMsg.format("<gray>Vous ne pouvez pas utiliser cette commande avant " + secondsLeft + " secondes!"));
                return true;
            }
        }
        plugin.cooldowns.put(player.getDisplayName(), System.currentTimeMillis());
            
        switch (args.length) {
            case 0:
                home = "home";
                world= player.getLocation().getWorld().getName();
                break;
            case 1:
                if (sender.isOp() || sender.hasPermission("iris.home.multiple"))
                {
                    home = args[0];
                    world= player.getLocation().getWorld().getName();
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdMultiHome","<aqua> Vous devez <e_cir>tre V.I.P pour avoir plusieurs HOME !")));
                    return true;
                }   break;
            case 2:
                if (sender.isOp() || sender.hasPermission("iris.home.multiple"))
                {
                    home = args[0];
                    world= args[1];
                }
                else
                {
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdMultiHome","<aqua> Vous devez <e_cir>tre V.I.P pour avoir plusieurs HOME !"),player));
                    return true;
                }   break;
            default:
                break;
        }
        
        if (commandLabel.equalsIgnoreCase("home") && (sender.isOp() || sender.hasPermission("iris.home")))
        {
            if (conf.IsConfigYAML("userdata",player.getUniqueId() + ".yml","Home." + world + "." + home))
            {
                double X = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".X", player.getLocation().getX());
                double Y = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".Y", player.getLocation().getY());
                double Z = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".Z", player.getLocation().getZ());
                
                World worldInstance = Bukkit.getWorld(world);
                Location homeLocation = new Location(worldInstance, X, Y, Z);
                
                player = (Player) sender;
                Location lastLocation = player.getLocation();
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   
                
                player.sendMessage(Iris.formatMsg.format("<aqua>DELAI IMPOSE : Vous serez TP sur votre home dans environ 10s environ."));
                plugin.scheduleTP(player, 200L, homeLocation);
                //player.teleport(homeLocation);
                //player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdHome"))); 
            }
            else
            {
              player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdErreurHome","<dark_red>Ce home n'<e_ai>xiste pas")));
            }
        }
        else
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return true;
        }
        return true;   
    }
}

