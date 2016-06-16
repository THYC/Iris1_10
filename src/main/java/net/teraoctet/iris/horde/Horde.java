package net.teraoctet.iris.horde;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Horde 
{
    
    private final String hordeName;
    private final String world;
    private final int X1;
    private final int Y1;
    private final int Z1;
    private final String uuid1;
    private final String uuid2;
    private final String uuid3;
    private final String uuid4;
    private final int kill;
    private final int dead;
    
    public Horde(String hordeName, String world, int X1, int Y1, int Z1, String uuid1, String uuid2 , String uuid3, String uuid4)
    {
        
        this.hordeName = hordeName;
        this.world = world;
        this.X1 = X1;
        this.Y1 = Y1;
        this.Z1 = Z1;
        this.uuid1 = uuid1;
        this.uuid2 = uuid2;
        this.uuid3 = uuid3;
        this.uuid4 = uuid4;
        this.kill = 0;
        this.dead = 0;
    }
    
    public Horde(String hordeName, String world, int X1, int Y1, int Z1, String uuid1, String uuid2 , String uuid3, String uuid4, int kill, int dead)
    {
        
        this.hordeName = hordeName;
        this.world = world;
        this.X1 = X1;
        this.Y1 = Y1;
        this.Z1 = Z1;
        this.uuid1 = uuid1;
        this.uuid2 = uuid2;
        this.uuid3 = uuid3;
        this.uuid4 = uuid4;
        this.kill = kill;
        this.dead = dead;
    }
    
    public String getHordeName()
    {
        return this.hordeName;
    }
    
    public String get1()
    {
        return this.uuid1;
    }
    
    public String get2()
    {
        return this.uuid2;
    }
    
    public String get3()
    {
        return this.uuid3;
    }
    
    public String get4()
    {
        return this.uuid4;
    }
    
    public String getWorldName()
    {
        return this.world;
    }
    public int getX()
    {
        return this.X1;
    }
    public int getY()
    {
        return this.Y1;
    }
    
    public int getZ()
    {
        return this.Z1;
    }
    
    public Location getSpawnHorde()
    {
        World worldInstance = Bukkit.getWorld(this.world);
        Location loc = new Location(worldInstance, this.X1,this.Y1, this.Z1);
        
        return loc;
    }
    
    public Location getSpawnHorde(int Y2)
    {
        World worldInstance = Bukkit.getWorld(this.world);
        Location loc = new Location(worldInstance, this.X1,this.Y1 + Y2, this.Z1);
        
        return loc;
    }
}
