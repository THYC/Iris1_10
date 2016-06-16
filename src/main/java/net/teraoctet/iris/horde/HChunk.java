package net.teraoctet.iris.horde;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class HChunk 
{
    private final String playerUUID;
    private final String hordename;
    private final String world;
    private final int X1;
    private final int Z1;
    private final int typeMember;
    private final int jail;
    private final int noBuild;
    private final int noBreak;
    private final int noInteract;
    private final int noFire;
    private final String message;
    private final int noPVP;
    private final int noKillAnimal;
    private final String uuid1;
    private final String uuid2;
    private final String uuid3;
    private final String uuid4;
    
    public HChunk(String playerUUID, String hordename, String world, int X1, int Z1, int typeMember, int jail, 
            int noBuild, int noBreak, int noInteract, int noFire, String message, int noPVP, int noKillAnimal,
            String uuid1, String uuid2, String uuid3, String uuid4)
    {
        this.playerUUID = playerUUID;
        this.hordename = hordename;
        this.world = world;
        this.X1 = X1;
        this.Z1 = Z1;
        this.typeMember = typeMember;
        this.jail = jail;
        this.noBuild = noBuild;
        this.noBreak = noBreak;
        this.noInteract = noInteract;
        this.noFire = noFire;
        this.message = message;
        this.noPVP = noPVP;
        this.noKillAnimal = noKillAnimal;
        this.uuid1 = "";
        this.uuid2 = "";
        this.uuid3 = "";
        this.uuid4 = "";
    }
    
    public HChunk(String playerUUID, String hordename, String world, int X1, int Z1, int typeMember)
    {
        this.playerUUID = playerUUID;
        this.hordename = hordename;
        this.world = world;
        this.X1 = X1;
        this.Z1 = Z1;
        this.typeMember = typeMember;
        this.jail = 0;
        this.noBuild = 1;
        this.noBreak = 1;
        this.noInteract = 0;
        this.noFire = 0;
        this.message = "";
        this.noPVP = 0;
        this.noKillAnimal = 1;
        this.uuid1 = "";
        this.uuid2 = "";
        this.uuid3 = "";
        this.uuid4 = "";
    }
    
        public HChunk(String playerUUID, String hordename, String world, int X1, int Z1)
    {
        this.playerUUID = playerUUID;
        this.hordename = hordename;
        this.world = world;
        this.X1 = X1;
        this.Z1 = Z1;
        this.typeMember = 0;
        this.jail = 0;
        this.noBuild = 1;
        this.noBreak = 1;
        this.noInteract = 0;
        this.noFire = 0;
        this.message = "";
        this.noPVP = 0;
        this.noKillAnimal = 1;
        this.uuid1 = "";
        this.uuid2 = "";
        this.uuid3 = "";
        this.uuid4 = "";
    }
    
    public String getPlayerUUID()
    {
        return this.playerUUID;
    }
    
    public String getHordeName()
    {
        return this.hordename;
    }
    
    public String getWorldName()
    {
        return this.world;
    }
    
    public int getX()
    {
        return this.X1;
    }
    
    public int getZ()
    {
        return this.Z1;
    }
    
    public Chunk getChunk()
    {
        World w = Bukkit.getWorld(this.world);
        Chunk chunk = w.getChunkAt(this.X1,this.Z1);
        return chunk;
    }
        
    public int getTypeMember()
    {
        return this.typeMember;
    }
    
    public int getJail()
    {
        return this.jail;
    }
    
    public int getNoBuild()
    {
        return this.noBuild;
    }
    
    public int getNoBreak()
    {
        return this.noBreak;
    }
    
    public int getNoInteract()
    {
        return this.noInteract;
    }
    
    public int getNoFire()
    {
        return this.noFire;
    }
    
    public String getMessage()
    {
        return this.message;
    }
    
    public int getNoPVP()
    {
        return this.noPVP;
    }
    
    public int getNoKillAnimal()
    {
        return this.noKillAnimal;
    }
    
    public Boolean isEmpty()
    {
        return "".equals(this.playerUUID) && "".equals(this.hordename)  && "".equals(this.world);
    }
}

