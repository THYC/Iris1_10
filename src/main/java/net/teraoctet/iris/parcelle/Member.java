package net.teraoctet.iris.parcelle;

public class Member 
{
    private final String parcelleName;
    private final String playerUUID;
    private final String playerName;
    private final int typeMember;
    
    public Member(String parcelleName, String playerUUID, String playerName, int typeMember)
    {
        this.parcelleName = parcelleName;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.typeMember = typeMember;
    }
    
    public Member(String parcelleName, String playerUUID, String playerName)
    {
        this.parcelleName = parcelleName;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.typeMember = 0;
    }
    
    public String getParcelleName()
    {
        return this.parcelleName;
    }
    
    public String getPlayerUUID()
    {
        return this.playerUUID;
    }
    
    public String getPlayerName()
    {
        return this.playerName;
    }
    
    public int getMemberType()
    {
        return this.typeMember;
    }
}
