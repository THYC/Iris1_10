package net.teraoctet.iris.world;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ConfigWorld 
{
    private static Iris plugin;
    public static DecimalFormat coord = new DecimalFormat("0.0");
    private static int borderTask = -1;
    public static WorldFillTask fillTask;
    public static WorldTrimTask trimTask;
    private static final Runtime rt = Runtime.getRuntime();
    private static boolean shapeRound = true;
    private static final Map<String, BorderManager> borders = Collections.synchronizedMap(new LinkedHashMap());
    private static Set<String> bypassPlayers = Collections.synchronizedSet(new LinkedHashSet());
    private static String message;   
    private static double knockBack = 3.0D;
    private static int timerTicks = 4;
    private static boolean whooshEffect = false;
    private static boolean portalRedirection = true;
    private static int remountDelayTicks = 0;
    private static boolean killPlayer = false;
    private static boolean denyEnderpearl = false;
    private static int fillAutosaveFrequency = 30;
    private static final ConfigFile conf = new ConfigFile();
  
    public static long Now()
    {
        return System.currentTimeMillis();
    }

    public static void setBorder(String world, BorderManager border)
    {
        borders.put(world, border);
        save(true);
    }

    public static void setBorder(String world, int radiusX, int radiusZ, double x, double z)
    {
        BorderManager old = Border(world);
        Boolean oldShape = old == null ? null : old.getShape();
        boolean oldWrap = old == null ? false : old.getWrapping();
        setBorder(world, new BorderManager(x, z, radiusX, radiusZ, oldShape, oldWrap));
    }

    public static void setBorder(String world, int radius, double x, double z)
    {
          setBorder(world, radius, radius, x, z);
    }
 
    public static void setBorderCorners(String world, double x1, double z1, double x2, double z2, Boolean shapeRound, boolean wrap)
    {
        double radiusX = Math.abs(x1 - x2) / 2.0D;
        double radiusZ = Math.abs(z1 - z2) / 2.0D;
        double x = (x1 < x2 ? x1 : x2) + radiusX;
        double z = (z1 < z2 ? z1 : z2) + radiusZ;
        setBorder(world, new BorderManager(x, z, (int)Math.round(radiusX), (int)Math.round(radiusZ), shapeRound, wrap));
    }
  
    public static void setBorderCorners(String world, double x1, double z1, double x2, double z2, Boolean shapeRound)
    {
        setBorderCorners(world, x1, z1, x2, z2, shapeRound, false);
    }
  
    public static void setBorderCorners(String world, double x1, double z1, double x2, double z2)
    {
        BorderManager old = Border(world);
        Boolean oldShape = old == null ? null : old.getShape();
        boolean oldWrap = old == null ? false : old.getWrapping();
        setBorderCorners(world, x1, z1, x2, z2, oldShape, oldWrap);
    }
  
    public static void removeBorder(String world)
    {
        borders.remove(world);
        save(true);
    }
  
    public static void removeAllBorders()
    {
        borders.clear();
        save(true);
    }
  
    public static String BorderDescription(String world)
    {
        BorderManager border = borders.get(world);
        if (border == null) 
        {
            return "Aucune limite de bordure trouvée sur \"" + world + "\".";
        }
        return "World \"" + world + "\" limite bordure : " + border.toString();
    }
  
    public static Set<String> BorderDescriptions()
    {
        Set<String> output = new HashSet();

        Iterator world = borders.keySet().iterator();
        while (world.hasNext()) 
        {
            output.add(BorderDescription((String)world.next()));
        }
        return output;
    }
  
    public static BorderManager Border(String world)
    {
        return borders.get(world);
    }
  
    public static Map<String, BorderManager> getBorders()
    {
        return new LinkedHashMap(borders);
    }
  
    public static void setMessage(String msg)
    {
        updateMessage(msg);
        save(true);
    }
  
    public static void updateMessage(String msg)
    {
        message = msg;
    }
  
    public static void setShape(boolean round)
    {
        shapeRound = round;
        save(true);
    }
  
    public static boolean ShapeRound()
    {
        return shapeRound;
    }
  
    public static String ShapeName()
    {
        return ShapeName(shapeRound);
    }
  
    public static String ShapeName(boolean round)
    {
        return round ? "elliptic/round" : "rectangular/square";
    }
  
    public static void setWhooshEffect(boolean enable)
    {
        whooshEffect = enable;
        save(true);
    }
  
    public static boolean whooshEffect()
    {
        return whooshEffect;
    }
  
    public static boolean getIfPlayerKill()
    {
        return killPlayer;
    }
  
    public static boolean getDenyEnderpearl()
    {
        return denyEnderpearl;
    }
  
    public static void showWhooshEffect(Location loc)
    {
        if (!whooshEffect()) 
        {
            return;
        }
        World world = loc.getWorld();
        world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
        world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
        world.playEffect(loc, Effect.SMOKE, 4);
        world.playEffect(loc, Effect.SMOKE, 4);
        world.playEffect(loc, Effect.SMOKE, 4);
        world.playEffect(loc, Effect.GHAST_SHOOT, 0);
    }
  
    public static void setPortalRedirection(boolean enable)
    {
        portalRedirection = enable;
        save(true);
    }
  
    public static boolean portalRedirection()
    {
        return portalRedirection;
    }
  
    public static void setKnockBack(double numBlocks)
    {
        knockBack = numBlocks;
        save(true);
    }
  
    public static double KnockBack()
    {
        return knockBack;
    }
  
    public static void setTimerTicks(int ticks)
    {
        timerTicks = ticks;
        StartBorderTimer();
        save(true);
    }
  
    public static int TimerTicks()
    {
        return timerTicks;
    }
  
    public static void setRemountTicks(int ticks)
    {
        remountDelayTicks = ticks;
        save(true);
    }
  
    public static int RemountTicks()
    {
        return remountDelayTicks;
    }
  
    public static void setFillAutosaveFrequency(int seconds)
    {
        fillAutosaveFrequency = seconds;
        save(true);
    }
  
    public static int FillAutosaveFrequency()
    {
        return fillAutosaveFrequency;
    }

    public static void setPlayerBypass(String player, boolean bypass)
    {
        if (bypass) 
        {
            bypassPlayers.add(player.toLowerCase());
        } 
        else 
        {
            bypassPlayers.remove(player.toLowerCase());
        }
    }

    public static boolean isPlayerBypassing(String player)
    {
        return bypassPlayers.contains(player.toLowerCase());
    }

    public static void togglePlayerBypass(String player)
    {
        setPlayerBypass(player, !isPlayerBypassing(player));
    }

    public static String getPlayerBypassList()
    {
        if (bypassPlayers.isEmpty()) 
        {
            return "<none>";
        }
        String newString = bypassPlayers.toString();
        return newString.substring(1, newString.length() - 1);
    }

    public static boolean isBorderTimerRunning()
    {
        if (borderTask == -1) 
        {
            return false;
        }
        return (plugin.getServer().getScheduler().isQueued(borderTask)) || (plugin.getServer().getScheduler().isCurrentlyRunning(borderTask));
    }

    public static void StartBorderTimer()
    {
        StopBorderTimer();
        borderTask = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BorderCheckTask(), timerTicks, timerTicks);
    }

    public static void StopBorderTimer()
    {
        if (borderTask == -1) 
        {
            return;
        }
        plugin.getServer().getScheduler().cancelTask(borderTask);
        borderTask = -1;
    }

    public static void StopFillTask()
    {
        if ((fillTask != null) && (fillTask.valid())) 
        {
            fillTask.cancel();
        }
    }

    public static void StoreFillTask()
    {
        save(false, true);
    }

    public static void UnStoreFillTask()
    {
        save(false);
    }

    public static void RestoreFillTask(String world, int fillDistance, int chunksPerRun, int tickFrequency, int x, int z, int length, int total, boolean forceLoad)
    {
        fillTask = new WorldFillTask(plugin.getServer(), null, world, fillDistance, chunksPerRun, tickFrequency, forceLoad);
        if (fillTask.valid())
        {
            fillTask.continueProgress(x, z, length, total);
            int task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, fillTask, 20L, tickFrequency);
            fillTask.setTaskID(task);
        }
    }

    public static void RestoreFillTask(String world, int fillDistance, int chunksPerRun, int tickFrequency, int x, int z, int length, int total)
    {
        RestoreFillTask(world, fillDistance, chunksPerRun, tickFrequency, x, z, length, total, false);
    }

    public static void StopTrimTask()
    {
        if ((trimTask != null) && (trimTask.valid())) 
        {
            trimTask.cancel();
        }
    }

    public static int AvailableMemory()
    {
        return (int)((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / 1048576L);
    }

    public static boolean HasPermission(Player player, String request)
    {
        return HasPermission(player, request, true);
    }

    public static boolean HasPermission(Player player, String request, boolean notify)
    {
        if (player == null) 
        {
            return true;
        }
        if (player.hasPermission("iris.border." + request)) 
        {
            return true;
        }
        if (notify) 
        {
            player.sendMessage("You do not have sufficient permissions.");
        }
        return false;
    }
    
    public static void load(Iris master, boolean logIt)
    {
        plugin = master;
        shapeRound = conf.getBooleanYAML("border.yml", "round-border", true);
        whooshEffect = conf.getBooleanYAML("border.yml", "whoosh-effect", false);
        portalRedirection = conf.getBooleanYAML("border.yml", "portal-redirection", true);
        knockBack = conf.getDoubleYAML("border.yml", "knock-back-dist", 3.0D);
        timerTicks = conf.getIntYAML("border.yml", "timer-delay-ticks", 5);
        remountDelayTicks = conf.getIntYAML("border.yml", "remount-delay-ticks", 0);
        killPlayer = conf.getBooleanYAML("border.yml", "player-killed-bad-spawn", false);
        denyEnderpearl = conf.getBooleanYAML("border.yml", "deny-enderpearl", false);
        fillAutosaveFrequency = conf.getIntYAML("border.yml", "fill-autosave-frequency", 30);
        bypassPlayers = Collections.synchronizedSet(new LinkedHashSet(conf.getListYAML("border.yml","bypass-list")));
        StartBorderTimer();
        borders.clear();
     
        ConfigurationSection worlds = conf.getConfigurationSection("border.yml","worlds");
        if (worlds != null)
        {
            Set<String> worldNames = worlds.getKeys(false);
            for (String worldName : worldNames)
            {
                Boolean overrideShape = conf.getBooleanYAML("border.yml","shape-round",true);
                boolean wrap = conf.getBooleanYAML("border.yml","wrapping", false);
                BorderManager border = new BorderManager(conf.getDoubleYAML("border.yml", "worlds." + worldName + ".x",0.0D), 
                conf.getDoubleYAML("border.yml", "worlds." + worldName + ".z",0.0D), 
                conf.getIntYAML("border.yml", "worlds." + worldName + ".radiusX",0),
                conf.getIntYAML("border.yml", "worlds." + worldName + ".radiusZ",0), overrideShape, wrap);
                borders.put(worldName, border);
            }
        }
        
        if (!"".equals(conf.getStringYAML("border.yml","fillTask","")))
        {
            String worldName = conf.getStringYAML("border.yml","fillTask.world");
            if (worldName == null)
            {
                return;
            }
            int fillDistance = conf.getIntYAML("border.yml","fillTask.fillDistance", 176);
            int chunksPerRun = conf.getIntYAML("border.yml","fillTask.chunksPerRun", 5);
            int tickFrequency = conf.getIntYAML("border.yml","fillTask.tickFrequency", 20);
            int fillX = conf.getIntYAML("border.yml","fillTask.x", 0);
            int fillZ = conf.getIntYAML("border.yml","fillTask.z", 0);
            int fillLength = conf.getIntYAML("border.yml","fillTask.length", 0);
            int fillTotal = conf.getIntYAML("border.yml","fillTask.total", 0);
            boolean forceLoad = conf.getBooleanYAML("border.yml","fillTask.forceLoad", false);
            RestoreFillTask(worldName, fillDistance, chunksPerRun, tickFrequency, fillX, fillZ, fillLength, fillTotal, forceLoad);
            save(false);
        }
    }
  
    public static void save(boolean logIt)
    {
        save(logIt, false);
    }
  
    public static void save(boolean logIt, boolean storeFillTask)
    {
        conf.setStringYAML("border.yml", "message", message);
        conf.setBooleanYAML("border.yml","round-border", shapeRound);
        conf.setBooleanYAML("border.yml","whoosh-effect", whooshEffect);
        conf.setBooleanYAML("border.yml","portal-redirection", portalRedirection);
        conf.setDoubleYAML("border.yml","knock-back-dist", knockBack);
        conf.setIntYAML("border.yml","timer-delay-ticks", timerTicks);
        conf.setIntYAML("border.yml","remount-delay-ticks", remountDelayTicks);
        conf.setBooleanYAML("border.yml","player-killed-bad-spawn", killPlayer);
        conf.setBooleanYAML("border.yml","deny-enderpearl", denyEnderpearl);
        conf.setIntYAML("border.yml","fill-autosave-frequency", fillAutosaveFrequency);
        conf.setListYAML("border.yml","bypass-list", new ArrayList(bypassPlayers));

        Iterator world = borders.entrySet().iterator();
        while (world.hasNext())
        {
            Map.Entry wdata = (Map.Entry)world.next();
            String name = ((String)wdata.getKey()).replace(".", "<");
            BorderManager bord = (BorderManager)wdata.getValue();

            conf.setDoubleYAML("border.yml","worlds." + name + ".x", bord.getX());
            conf.setDoubleYAML("border.yml","worlds." + name + ".z", bord.getZ());
            conf.setIntYAML("border.yml","worlds." + name + ".radiusX", bord.getRadiusX());
            conf.setIntYAML("border.yml","worlds." + name + ".radiusZ", bord.getRadiusZ());
            conf.setBooleanYAML("border.yml","worlds." + name + ".wrapping", bord.getWrapping());
            if (bord.getShape() != null) {
              conf.setBooleanYAML("border.yml","worlds." + name + ".shape-round", bord.getShape());
            }
        }
        if ((storeFillTask) && (fillTask != null) && (fillTask.valid()))
        {
            conf.setStringYAML("border.yml","fillTask.world", fillTask.refWorld());
            conf.setIntYAML("border.yml","fillTask.fillDistance", fillTask.refFillDistance());
            conf.setIntYAML("border.yml","fillTask.chunksPerRun", fillTask.refChunksPerRun());
            conf.setIntYAML("border.yml","fillTask.tickFrequency", fillTask.refTickFrequency());
            conf.setIntYAML("border.yml","fillTask.x", fillTask.refX());
            conf.setIntYAML("border.yml","fillTask.z", fillTask.refZ());
            conf.setIntYAML("border.yml","fillTask.length", fillTask.refLength());
            conf.setIntYAML("border.yml","fillTask.total", fillTask.refTotal());
            conf.setBooleanYAML("border.yml","fillTask.forceLoad", fillTask.refForceLoad());
        }
        else
        {
            conf.setStringYAML("border.yml","fillTask", "");
        }
    }
}
