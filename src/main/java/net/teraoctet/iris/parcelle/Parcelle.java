package net.teraoctet.iris.parcelle;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;

public class Parcelle 
{
    private final Location border1 = null;
    private final Location border2 = null;
    private final String parcelleName;
    private final String parent;
    private final String world;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;
    private final int jail;
    private final int noEnter;
    private final int noFly;
    private final int noBuild;
    private final int noBreak;
    private final int noTeleport;
    private final int noInteract;
    private final int noFire;
    private final String message;
    private final int mode;
    private final int noMob;
    private final int noTNT;
    private final String uuidOwner;
    private final String uuidAllowed;
   
    public Parcelle(String parcelleName, String parent, String world, int x1, int y1, int z1, int x2, int y2, int z2, 
            int jail, int noEnter, int noFly, int noBuild, int noBreak, int noTeleport, int noInteract, int noFire, String message, int mode, int noMob, int noTNT,String uuidOwner, String uuidAllowed)
    {
        this.parcelleName = parcelleName;
        this.parent = parent;
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.jail = jail;
        this.noEnter = noEnter;
        this.noFly = noFly;
        this.noBuild = noBuild;
        this.noBreak = noBreak;
        this.noTeleport = noTeleport;
        this.noInteract = noInteract;
        this.noFire = noFire;
        this.message = message;
        this.mode = mode;
        this.uuidOwner = uuidOwner;
        this.uuidAllowed = uuidAllowed;
        this.noMob = noMob;
        this.noTNT = noTNT;
    }
    
    public Parcelle(String world, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.parcelleName = "TMP";
        this.parent = "";
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.jail = 0;
        this.noEnter = 0;
        this.noFly = 0;
        this.noBuild = 0;
        this.noBreak = 0;
        this.noTeleport = 0;
        this.noInteract = 0;
        this.noFire = 0;
        this.message = "";
        this.mode = 0;
        this.uuidOwner = "";
        this.uuidAllowed = "";
        this.noMob = 0;
        this.noTNT = 0;
    }
    
    public String getuuidOwner()
    {
        return this.uuidOwner;
    }
    
    public String getuuidAllowed()
    {
        return this.uuidAllowed;
    }
    
    public int getNoInteract()
    {
        return this.noInteract;
    }
    
    public String getName()
    {
        return this.parcelleName;
    }
    
    public String getWorldName()
    {
        return this.world;
    }
    
    public int getX1()
    {
        return this.x1;
    }
    
    public int getX2()
    {
        return this.x2;
    }
    
    public int getY1()
    {
        return this.y1;
    }
    
    public int getY2()
    {
        return this.y2;
    }
    
    public int getZ1()
    {
        return this.z1;
    }
    
    public int getZ2()
    {
        return this.z2;
    }
    
    public String getOwner()
    {
        return this.uuidOwner;
    }
    
    public Location getBorder1()
    {
        return this.border1;
    }
  
    public Location getBorder2()
    {
        return this.border2;
    }
  
    public String getAllowed()
    {
        return this.uuidAllowed;
    }
    
    public int getNoBreak()
    {
        
        return this.noBreak;
    }
    
    public int getNoBuild()
    {
        return this.noBuild;
    }
    
    public int getNoFire()
    {
        return this.noFire;
    }
    
    public int getNoTeleport()
    {
        return this.noTeleport;
    }
    
    public int getNoFly()
    {
        return this.noFly;
    }
    
    public int getJail()
    {
        return this.jail;
    }
    
    public int getNoEnter()
    {
        return this.noEnter;
    }
    
    public String getMessage()
    {
        return this.message;
    }
  
    public GameMode getGamemode()
    {
        GameMode gamemode = GameMode.getByValue(mode);
        return gamemode;
    } 
    
    public int getNoMob()
    {
        return this.noMob;
    }
    
    public int getNoTNT()
    {
        return this.noTNT;
    }
    
    public Location getLocation1()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x1, this.y1, this.z1);
        return location;
    }
    
    public Location getLocation2()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x2, this.y2, this.z2);
        return location;
    }
    
    public Location getLocation3()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x1, this.y1, this.z2);
        return location;
    }
    
    public Location getLocation4()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x2, this.y2, this.z1);
        return location;
    }
    public Location getLocation5()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x1, this.y2, this.z2);
        return location;
    }
    
    public Location getLocation6()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x2, this.y1, this.z1);
        return location;
    }
    
    public Location getLocation7()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x2, this.y1, this.z2);
        return location;
    }
    
    public Location getLocation8()
    {
        World worldInstance = Bukkit.getWorld(this.getWorldName());
        Location location = new Location(worldInstance, this.x1, this.y2, this.z1);
        return location;
    }
}
