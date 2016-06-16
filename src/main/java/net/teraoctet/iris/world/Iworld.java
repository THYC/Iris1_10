package net.teraoctet.iris.world;

import java.io.File;
import static net.teraoctet.iris.Iris.conf;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;

public class Iworld
{
    World base;
  
    public Iworld(World w)
    {
        this.base = w;
    }
  
    public Iworld(String world)
    {
        try
        {
            if ((world == null) || (Bukkit.getWorld(world) == null)) 
            {
                throw new NullPointerException("World not found");
            }
            this.base = new Iworld(Bukkit.getWorld(world)).getWorld();
        }
        catch (NullPointerException ex)
        {
            throw new NullPointerException("World not found");
        }
    }
  
    public World getWorld()
    {
        return this.base;
    }
  
    public static enum WorldFlag
    {
        MONSTER,  ANIMAL,  PVP;
    }
  
    public File getDataFile()
    {
        return new File(Bukkit.getPluginManager().getPlugin("Iris").getDataFolder(), "worlds.yml");
    }
  
    public boolean isFlagDenied(WorldFlag f)
    {
        return !conf.getBooleanYAML("world.yml", "worlds." + getWorld().getName() + "." + f.toString().toLowerCase(),true);
    }
  
    public boolean isFlagAllowed(WorldFlag f)
    {
        return !isFlagDenied(f);
    }
  
    public void setFlagAllowed(WorldFlag f)
    {
        conf.setBooleanYAML("world.yml", "worlds." + getWorld().getName() + "." + f.toString().toLowerCase(),true);
        
        if (f.equals(WorldFlag.ANIMAL)) 
        {
            getWorld().setAnimalSpawnLimit(15);
        }
        if (f.equals(WorldFlag.MONSTER)) 
        {
            getWorld().setMonsterSpawnLimit(70);
        }
        if (f.equals(WorldFlag.PVP)) 
        {
            getWorld().setPVP(true);
        }
    }
  
    public void setFlagDenied(WorldFlag f)
    {
        conf.setBooleanYAML("world.yml", "worlds." + getWorld().getName() + "." + f.toString().toLowerCase(),false);
        
        if (f.equals(WorldFlag.ANIMAL)) 
        {
            getWorld().setAnimalSpawnLimit(0);
        }
        if (f.equals(WorldFlag.MONSTER)) 
        {
            getWorld().setMonsterSpawnLimit(0);
        }
        if (f.equals(WorldFlag.PVP)) 
        {
            getWorld().setPVP(false);
        }
    }
  
    public void setGamemode(int gm)
    {
      conf.setIntYAML("world.yml", "worlds." + getWorld().getName() + ".gamemode",gm);
    }
  
    public GameMode getGamemode()
    {
        GameMode gm = GameMode.getByValue(conf.getIntYAML("world.yml", "worlds." + getWorld().getName() + ".gamemode"));
        return gm;
    }
}

