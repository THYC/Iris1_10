package net.teraoctet.iris.commands;

import java.util.HashMap;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Spawn
implements CommandExecutor
{
    private final Iris plugin;
    public Player player;
    public World worldInstance;
    public HashMap<String, Long> cooldowns = new HashMap<>();
    
    public Command_Spawn(Iris instance) 
    {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        player = (Player) sender;
        if (player.hasPermission("iris.horde") && !player.isOp())
        {
            int cooldownTime = conf.getIntYAML("config.yml","CoolDownTime",120);
            if(cooldowns.containsKey(player.getDisplayName())) 
            {
                long secondsLeft = ((cooldowns.get(player.getDisplayName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
                if(secondsLeft>0) {
                    player.sendMessage(formatMsg.format("<gray>Vous ne pouvez pas utiliser cette commande avant " + secondsLeft + " secondes!"));
                    return true;
                }
            }
            cooldowns.put(player.getDisplayName(), System.currentTimeMillis());
        }
        
        String world= player.getLocation().getWorld().getName();
        if (!(sender instanceof Player))
        {
            player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdWarpNoPlayer","<dark_aqua>Commande r<e_ai>serv<e_ai> aux joueurs"),player));
            return true;
        }
        else
        {
            if (commandLabel.equalsIgnoreCase("spawn") && (sender.isOp() || sender.hasPermission("iris.spawn")))
            {
                if (args.length == 0)
                {
                    world= player.getLocation().getWorld().getName();
                }
                else if(args.length == 1)
                {
                    if (sender.isOp() || sender.hasPermission("iris.spawn.multiple"))
                    {
                        world = args[0];
                    }
                    else
                    {
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml", "cmdMultiSpawn","<aqua> Vous devez <e_cir>tre V.I.P utilider cette commande !"),player)); 
                        return true;
                    }
                }
                               
                Location lastLocation = player.getLocation();
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());    
                
                worldInstance = Bukkit.getWorld(world);
                Location spawn = new Location(worldInstance,
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.x", worldInstance.getSpawnLocation().getBlockX()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.y", worldInstance.getSpawnLocation().getBlockY()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.z", worldInstance.getSpawnLocation().getBlockZ()));
                
                try
                {
                    if(player.hasPermission("iris.horde") && !sender.isOp())
                    {
                        if(!"NULL".equals(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL")))
                        {
                            player.sendMessage(formatMsg.format("<gray>Tu ne peut pas utiliser cette commande quand tu possèdes la flamme d'un ennemi"));
                            return true;
                        }
                                                
                        player.sendMessage(formatMsg.format("<gray>SPAWN : Vous serez TP dans 20s environ"));
                        plugin.scheduleTP(player, 200L, worldInstance.getSpawnLocation());
                    }
                    else
                    {
                        player.teleport(spawn);
                        player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","cmdSpawn","<dark_aqua>Vous avez demand<e_ai> le spawn ! <smile>"),player));
                    }
                }
                catch(Exception e)
                {
                    player.sendMessage(e.getMessage());
                    player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("messages.yml","ErreurSpawn","<dark_aqua>Monde introuvable !"),player));
                    return false;
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

