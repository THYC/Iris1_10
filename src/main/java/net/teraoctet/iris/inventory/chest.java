package net.teraoctet.iris.inventory;

import org.bukkit.inventory.Inventory;

public class chest
{
    public double locationX;
    public double locationZ;
    public double locationY;
    public String world;
            
    public String chestName;
    public Inventory grave;
    
    public chest(Inventory grave, String name, double locationX, double locationZ, double locationY, String world )
    {
        this.grave = grave;
        this.chestName = name;
        this.locationX = locationX;
        this.locationZ = locationZ;
        this.locationY = locationY;
        this.world = world;
    } 
}

  