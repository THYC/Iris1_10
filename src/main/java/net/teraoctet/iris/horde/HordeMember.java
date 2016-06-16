package net.teraoctet.iris.horde;

public class HordeMember 
{
    private final String playerUUID;
    private final String playerName;
    private final String hordeName;
    private final int typeMember;
    private final int power;
        
    public HordeMember(String playerUUID, String playerName, String hordeName, int typeMember, int power)
    {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.hordeName = hordeName;
        this.typeMember = typeMember;
        this.power = power;
    }
    
    public String getPlayerUUID()
    {
        return this.playerUUID;
    }
    
    public String getPlayerName()
    {
        return this.playerName;
    }
    
    public String getHordeName()
    {
        return this.hordeName;
    }
    
    public int getTypeMember()
    {
        return this.typeMember;
    }  
    
    public int getPower()
    {
        return this.power;
    }  
}
