package net.teraoctet.iris.world;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BorderCheckTask
implements Runnable
{
    private static final ConfigFile conf = new ConfigFile();
    @Override
    public void run()
    {
        if (ConfigWorld.KnockBack() == 0.0D) {
            return;
        }
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            checkPlayer(player, null, false, true);
        }
    }

    private static final Set<String> handlingPlayers = Collections.synchronizedSet(new LinkedHashSet());

    public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly, boolean notify)
    {
        if ((player == null) || (!player.isOnline())) 
        {
            return null;
        }
        Location loc = targetLoc == null ? player.getLocation().clone() : targetLoc;
        if (loc == null) 
        {
            return null;
        }
        World world = loc.getWorld();
        if (world == null) 
        {
            return null;
        }
        BorderManager border = ConfigWorld.Border(world.getName());
        if (border == null) 
        {
            return null;
        }
        if (border.insideBorder(loc.getX(), loc.getZ(), ConfigWorld.ShapeRound())) 
        {
            return null;
        }
        if ((ConfigWorld.isPlayerBypassing(player.getName())) || (handlingPlayers.contains(player.getName().toLowerCase()))) 
        {
            return null;
        }
        handlingPlayers.add(player.getName().toLowerCase());

        Location newLoc = newLocation(player, loc, border, notify);
        boolean handlingVehicle = false;
        if (player.isInsideVehicle())
        {
            Entity ride = player.getVehicle();
            player.leaveVehicle();
            if (ride != null)
            {
                double vertOffset = (ride instanceof LivingEntity) ? 0.0D : ride.getLocation().getY() - loc.getY();
                Location rideLoc = newLoc.clone();
                rideLoc.setY(newLoc.getY() + vertOffset);
                if ((ride instanceof Boat))
                {
                    ride.remove();
                    ride = world.spawnEntity(rideLoc, EntityType.BOAT);
                }
                else
                {
                    ride.setVelocity(new Vector(0, 0, 0));
                    ride.teleport(rideLoc);
                }
                if (ConfigWorld.RemountTicks() > 0)
                {
                    setPassengerDelayed(ride, player, player.getName(), ConfigWorld.RemountTicks());
                    handlingVehicle = true;
                }
            }
        }
        ConfigWorld.showWhooshEffect(loc);
        if (!returnLocationOnly) 
        {
            player.teleport(newLoc);
        }
        if (!handlingVehicle) 
        {
            handlingPlayers.remove(player.getName().toLowerCase());
        }
        if (returnLocationOnly) 
        {
            return newLoc;
        }
        return null;
    }

    public static Location checkPlayer(Player player, Location targetLoc, boolean returnLocationOnly)
    {
        return checkPlayer(player, targetLoc, returnLocationOnly, true);
    }

    private static Location newLocation(Player player, Location loc, BorderManager border, boolean notify)
    {
        Location newLoc = border.correctedPosition(loc, ConfigWorld.ShapeRound(), player.isFlying());
        if (newLoc == null)
        {
            if (ConfigWorld.getIfPlayerKill())
            {
                player.setHealth(0.0D);
                return null;
            }
            
            Location spawn = new Location(player.getLocation().getWorld(),
                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.x", player.getLocation().getWorld().getSpawnLocation().getBlockX()),
                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.y", player.getLocation().getWorld().getSpawnLocation().getBlockY()),
                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.z", player.getLocation().getWorld().getSpawnLocation().getBlockZ()));
                        
            newLoc = spawn;
        }
        if (notify) 
        {
            player.sendMessage(conf.getStringYAML("border.yml", "message",Iris.formatMsg.format("<yellow>vous n'<e_gr>tes pas autoris<e_ai> à d<e_ai>passer cette limite.",player)));
        }
        return newLoc;
    }

    private static void setPassengerDelayed(final Entity vehicle, final Player player, final String playerName, long delay)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Iris.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                BorderCheckTask.handlingPlayers.remove(playerName.toLowerCase());
                if ((vehicle == null) || (player == null)) 
                {
                    return;
                }
                vehicle.setPassenger(player);
            }
        }, delay);
    }
}

