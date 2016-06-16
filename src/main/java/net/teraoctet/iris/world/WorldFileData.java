package net.teraoctet.iris.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldFileData
{
  private final transient World world;
  private transient File regionFolder = null;
  private transient File[] regionFiles = null;
  private transient Player notifyPlayer = null;
  private final transient Map<CoordXZ, List<Boolean>> regionChunkExistence = Collections.synchronizedMap(new HashMap());
  
  public static WorldFileData create(World world, Player notifyPlayer)
  {
    WorldFileData newData = new WorldFileData(world, notifyPlayer);
    
    newData.regionFolder = new File(newData.world.getWorldFolder(), "region");
    if ((!newData.regionFolder.exists()) || (!newData.regionFolder.isDirectory()))
    {
      File[] possibleDimFolders = newData.world.getWorldFolder().listFiles(new DimFolderFileFilter(null));
        for (File possibleDimFolder : possibleDimFolders) {
            File possible = new File(newData.world.getWorldFolder(), possibleDimFolder.getName() + File.separator + "region");
            if ((possible.exists()) && (possible.isDirectory()))
            {
                newData.regionFolder = possible;
                break;
            }
        }
      if ((!newData.regionFolder.exists()) || (!newData.regionFolder.isDirectory()))
      {
        newData.sendMessage("Could not validate folder for world's region files. Looked in " + newData.world.getWorldFolder().getPath() + " for valid DIM* folder with a region folder in it.");
        return null;
      }
    }
    newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCA"));
    if ((newData.regionFiles == null) || (newData.regionFiles.length == 0))
    {
      newData.regionFiles = newData.regionFolder.listFiles(new ExtFileFilter(".MCR"));
      if ((newData.regionFiles == null) || (newData.regionFiles.length == 0))
      {
        newData.sendMessage("Could not find any region files. Looked in: " + newData.regionFolder.getPath());
        return null;
      }
    }
    return newData;
  }
  
  private WorldFileData(World world, Player notifyPlayer)
  {
    this.world = world;
    this.notifyPlayer = notifyPlayer;
  }
  
  public int regionFileCount()
  {
    return this.regionFiles.length;
  }
  
  public File regionFolder()
  {
    return this.regionFolder;
  }
  
  public File[] regionFiles()
  {
    return this.regionFiles.clone();
  }
  
  public File regionFile(int index)
  {
    if (this.regionFiles.length < index) {
      return null;
    }
    return this.regionFiles[index];
  }
  
  public CoordXZ regionFileCoordinates(int index)
  {
    File regionFile = regionFile(index);
    String[] coords = regionFile.getName().split("\\.");
    try
    {
      int x = Integer.parseInt(coords[1]);
      int z = Integer.parseInt(coords[2]);
      return new CoordXZ(x, z);
    }
    catch (NumberFormatException ex)
    {
      sendMessage("Error! Region file found with abnormal name: " + regionFile.getName());
    }
    return null;
  }
  
  public boolean doesChunkExist(int x, int z)
  {
    CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
    List<Boolean> regionChunks = getRegionData(region);
    
    return regionChunks.get(coordToRegionOffset(x, z));
  }
  
  public boolean isChunkFullyGenerated(int x, int z)
  {
    return (doesChunkExist(x, z)) && (doesChunkExist(x + 1, z)) && (doesChunkExist(x - 1, z)) && (doesChunkExist(x, z + 1)) && (doesChunkExist(x, z - 1));
  }
  
  public void chunkExistsNow(int x, int z)
  {
    CoordXZ region = new CoordXZ(CoordXZ.chunkToRegion(x), CoordXZ.chunkToRegion(z));
    List<Boolean> regionChunks = getRegionData(region);
    regionChunks.set(coordToRegionOffset(x, z), true);
  }
  
  private int coordToRegionOffset(int x, int z)
  {
    x %= 32;
    z %= 32;
    if (x < 0) {
      x += 32;
    }
    if (z < 0) {
      z += 32;
    }
    return x + z * 32;
  }
  
  private List<Boolean> getRegionData(CoordXZ region)
  {
    List<Boolean> data = this.regionChunkExistence.get(region);
    if (data != null) {
      return data;
    }
    data = new ArrayList(1024);
    for (int i = 0; i < 1024; i++) {
      data.add(Boolean.FALSE);
    }
    for (int i = 0; i < this.regionFiles.length; i++)
    {
      CoordXZ coord = regionFileCoordinates(i);
      if (coord.equals(region))
      {
        int counter = 0;
        try
        {
          RandomAccessFile regionData = new RandomAccessFile(regionFile(i), "r");
          for (int j = 0; j < 1024; j++)
          {
            if (regionData.readInt() != 0) {
              data.set(j, Boolean.valueOf(true));
            }
            counter++;
          }
          regionData.close();
        }
        catch (FileNotFoundException ex)
        {
          sendMessage("Error! Could not open region file to find generated chunks: " + regionFile(i).getName());
        }
        catch (IOException ex)
        {
          sendMessage("Error! Could not read region file to find generated chunks: " + regionFile(i).getName());
        }
      }
    }
    this.regionChunkExistence.put(region, data);
    
    return data;
  }
  
  private void sendMessage(String text)
  {
    //ConfigWorld.Log("[WorldData] " + text);
    if ((this.notifyPlayer != null) && (this.notifyPlayer.isOnline())) {
      this.notifyPlayer.sendMessage("[WorldData] " + text);
    }
  }
  
  private static class ExtFileFilter
    implements FileFilter
  {
    String ext;
    
    public ExtFileFilter(String extension)
    {
      this.ext = extension.toLowerCase();
    }
    
    @Override
    public boolean accept(File file)
    {
      return (file.exists()) && (file.isFile()) && (file.getName().toLowerCase().endsWith(this.ext));
    }
  }
  
  private static class DimFolderFileFilter
  implements FileFilter
  {
        private DimFolderFileFilter(Object object) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public boolean accept(File file)
        {
          return (file.exists()) && (file.isDirectory()) && (file.getName().toLowerCase().startsWith("dim"));
        }
  }
  
  public void testImage(CoordXZ region, List<Boolean> data)
  {
    int width = 32;
    int height = 32;
    BufferedImage bi = new BufferedImage(width, height, 2);
    Graphics2D g2 = bi.createGraphics();
    int current = 0;
    g2.setColor(Color.BLACK);
    for (int x = 0; x < 32; x++) {
      for (int z = 0; z < 32; z++)
      {
        //if (data.get(current).booleanValue()) {
          g2.fillRect(x, z, x + 1, z + 1);
        //}
        current++;
      }
    }
    File f = new File("region_" + region.x + "_" + region.z + "_.png");
    //ConfigWorld.Log(f.getAbsolutePath());
    try
    {
      ImageIO.write(bi, "png", f);
    }
    catch (IOException ex)
    {
      //ConfigWorld.Log("[SEVERE]" + ex.getLocalizedMessage());
    }
  }
  
  
}
