package net.teraoctet.iris.portal;

public class Portal
{
    public String portalName;
    public String world;
    public int x1;
    public int y1;
    public int z1;
    public int x2;
    public int y2;
    public int z2;
    public String toworld;
    public int tox1;
    public int toy1;
    public int toz1;
    public String bookName;
    public String message;
    public int mode;
            
    public Portal(String portalName, String world, int x1, int y1, int z1, int x2, int y2, int z2,String toworld, int tox1, int toy1, int toz1, String bookName,String message, int mode )
    {
        this.portalName = portalName;
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.toworld = toworld;
        this.tox1 = tox1;
        this.toy1 = toy1;
        this.toz1 = toz1;
        this.bookName = bookName;
        this.message = message;
        this.mode = mode;
    } 
}