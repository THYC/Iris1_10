package net.teraoctet.iris.listener;

import java.sql.SQLException;
import java.util.logging.Level;
import net.teraoctet.iris.inventory.PlayersInventory;
import net.teraoctet.iris.Iris;
import net.teraoctet.iris.world.BorderCheckTask;
import net.teraoctet.iris.world.ConfigWorld;
import net.teraoctet.iris.world.Iworld;
import net.teraoctet.iris.world.MobType;
import static net.teraoctet.iris.Iris.conf;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldListener
implements Listener
{
    private final PlayersInventory invent = new PlayersInventory();
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (ConfigWorld.KnockBack() == 0.0D) 
        {
            return;
        }
        if ((event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && (ConfigWorld.getDenyEnderpearl()))
        {
            event.setCancelled(true);
            return;
        }
        Location newLoc = BorderCheckTask.checkPlayer(event.getPlayer(), event.getTo(), true, true);
        if (newLoc != null) 
        {
            event.setTo(newLoc);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        if ((ConfigWorld.KnockBack() == 0.0D) || (!ConfigWorld.portalRedirection())) 
        {
            return;
        }
        Location newLoc = BorderCheckTask.checkPlayer(event.getPlayer(), event.getTo(), true, false);
        if (newLoc != null) 
        {
            event.setTo(newLoc);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event)
    {
        if (ConfigWorld.isBorderTimerRunning()) 
        {
            return;
        }
        Iris.log.log(Level.INFO, "[Iris] La protection de generation de chunk n'est pas active !");
        ConfigWorld.StartBorderTimer();
    }
    
    @EventHandler(priority=EventPriority.LOW)
    public void mobSpawn(CreatureSpawnEvent e)
    {
        if ((e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) || (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))) 
        {
            return;
        }
        try
        {
            if (((e.getEntity() instanceof Monster)) || (MobType.fromBukkitType(e.getEntityType()).type.equals(MobType.Enemies.ENEMY)) || (e.getEntityType().equals(EntityType.GHAST)) || (e.getEntityType().equals(EntityType.SLIME)))
            {
                Iworld w = new Iworld(e.getLocation().getWorld());
                if (w.isFlagDenied(Iworld.WorldFlag.MONSTER)) 
                {
                    e.setCancelled(true);
                }
            }
        }
        catch (Exception ex) 
        {
            Bukkit.broadcast(ex.getLocalizedMessage(), "iris.debug");
        }
        
        try
        {
            if (((e.getEntity() instanceof Animals)) || (MobType.fromBukkitType(e.getEntityType()).type.equals(MobType.Enemies.FRIENDLY)) || (MobType.fromBukkitType(e.getEntityType()).type.equals(MobType.Enemies.NEUTRAL)) || (e.getEntityType().equals(EntityType.SQUID)))
            {
                Iworld w = new Iworld(e.getLocation().getWorld());
                if (w.isFlagDenied(Iworld.WorldFlag.ANIMAL)) 
                {
                    e.setCancelled(true);
                }
            }
        }
        catch (Exception ex) 
        {
            Bukkit.broadcast(ex.getLocalizedMessage(), "iris.debug");
        }
    }
  
    /*@EventHandler(priority=EventPriority.HIGH)
    public void pvp(EntityDamageByEntityEvent e)
    {
        if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Player)))
        {
            Iworld w = new Iworld(e.getEntity().getWorld());
            if (w.isFlagDenied(Iworld.WorldFlag.PVP)) 
            {
                e.setCancelled(true);
            }
        }
        if(e.getEntity() instanceof org.bukkit.entity.Player)
        {   
            if(e.getDamager() instanceof org.bukkit.entity.Projectile)
            {
                Entity player = (Entity)((Projectile)e.getDamager()).getShooter();
                if(player instanceof org.bukkit.entity.Player) e.setCancelled(true);
            }
        }
    }*/
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) throws SQLException
    {
        try
        {
            String FromWorld = event.getFrom().getName();
            String ToWorld = event.getPlayer().getWorld().getName();
            Player player = event.getPlayer();

            String FromWorldInv = conf.getStringYAML("MultiInventory.yml", FromWorld,"NULL");
            String ToWorldInv = conf.getStringYAML("MultiInventory.yml", ToWorld,"NULL");

            if(!FromWorldInv.equals(ToWorldInv) || FromWorldInv.equals("NULL") || ToWorldInv.equals("NULL"))
            {  
                invent.SaveInventory(player, FromWorldInv);
                invent.LoadInventory(player, ToWorldInv);
                player.setGameMode(GameMode.getByValue(conf.getIntYAML("world.yml", "worlds." + ToWorld + ".GAMEMODE", 0)));
                if(conf.getIntYAML("world.yml", "worlds." + ToWorld + ".GAMEMODE", 0) == 0)
                {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                }
            }
            else
            {
                Iris.log.info("[Iris] Fichier MultiInventory.yml non configur<e_ai>");
            }  
        }
        catch(SQLException ex)
        {
            Iris.log.info("[Iris] Erreur OnPlayerChangeWorld");
            Iris.log.info(ex.getLocalizedMessage());
        }
    }
}

