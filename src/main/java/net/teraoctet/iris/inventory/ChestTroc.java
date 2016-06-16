package net.teraoctet.iris.inventory;

public class ChestTroc 
{
    String Playername;
    int Type;
    int Data;
    int Qte;
    
    public ChestTroc(String Playername, int Type, int Data, int Qte)
    {
        this.Playername = Playername;
        this.Type = Type;
        this.Data = Data;
        this.Qte = Qte;
    }
    
    public String getPlayername()
    {
        return this.Playername;
    }
    
    public int getQte()
    {
        return this.Qte;
    }
    
    public int getType()
    {
        return this.Type;
    }
    
    public int getData()
    {
        return this.Data;
    }
    
}
