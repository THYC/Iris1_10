package net.teraoctet.iris.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldTrimTask
  implements Runnable
{
    private transient Server server = null;
    private transient World world = null;
    private transient WorldFileData worldData = null;
    private transient BorderManager border = null;
    private transient boolean readyToGo = false;
    private transient boolean paused = false;
    private transient int taskID = -1;
    private transient Player notifyPlayer = null;
    private transient int chunksPerRun = 1;
    private transient int currentRegion = -1;
    private transient int regionX = 0;
    private transient int regionZ = 0;
    private transient int currentChunk = 0;
    private transient List<CoordXZ> regionChunks = new ArrayList(1024);
    private transient List<CoordXZ> trimChunks = new ArrayList(1024);
    private transient int counter = 0;
    private transient long lastReport = ConfigWorld.Now();
    private transient int reportTarget = 0;
    private transient int reportTotal = 0;
    private transient int reportTrimmedRegions = 0;
    private transient int reportTrimmedChunks = 0;
  
    public WorldTrimTask(Server theServer, Player player, String worldName, int trimDistance, int chunksPerRun)
    {
        this.server = theServer;
        this.notifyPlayer = player;
        this.chunksPerRun = chunksPerRun;

        this.world = this.server.getWorld(worldName);
        if (this.world == null)
        {
            if (worldName.isEmpty()) 
            {
                sendMessage("You must specify a world!");
            } 
            else 
            {
                sendMessage("World \"" + worldName + "\" not found!");
            }
            stop();
            return;
        }
        this.border = (ConfigWorld.Border(worldName) == null ? null : ConfigWorld.Border(worldName).copy());
        if (this.border == null)
        {
            sendMessage("No border found for world \"" + worldName + "\"!");
            stop();
            return;
        }
        this.border.setRadiusX(this.border.getRadiusX() + trimDistance);
        this.border.setRadiusZ(this.border.getRadiusZ() + trimDistance);

        this.worldData = WorldFileData.create(this.world, this.notifyPlayer);
        if (this.worldData == null)
        {
            stop();
            return;
        }
        this.reportTarget = (this.worldData.regionFileCount() * 3072);
        if (!nextFile()) {
            return;
        }
        this.readyToGo = true;
    }

    public void setTaskID(int ID)
    {
        this.taskID = ID;
    }

    @Override
    public void run()
    {
        if ((this.server == null) || (!this.readyToGo) || (this.paused)) {
            return;
        }
        this.readyToGo = false;

        long loopStartTime = ConfigWorld.Now();

        this.counter = 0;
        while (this.counter <= this.chunksPerRun)
        {
            if (this.paused) 
            {
                return;
            }
            long now = ConfigWorld.Now();
            if (now > this.lastReport + 5000L) 
            {
                reportProgress();
            }
            if (now > loopStartTime + 45L)
            {
                this.readyToGo = true;
                return;
            }
            if (this.regionChunks.isEmpty())
            {
                addCornerChunks();
            }
            else if (this.currentChunk == 4)
            {
                if (this.trimChunks.isEmpty())
                {
                  this.counter += 4;
                  nextFile();
                  continue;
                }
                addEdgeChunks();
                addInnerChunks();
            }
            else
            {
                if ((this.currentChunk == 124) && (this.trimChunks.size() == 124))
                {
                    this.counter += 16;
                    this.trimChunks = this.regionChunks;
                    unloadChunks();
                    this.reportTrimmedRegions += 1;
                    File regionFile = this.worldData.regionFile(this.currentRegion);
                    if (!regionFile.delete())
                    {
                      sendMessage("Error! Region file which is outside the border could not be deleted: " + regionFile.getName());
                      wipeChunks();
                    }
                    nextFile();
                    continue;
                }
                if (this.currentChunk == 1024)
                {
                    this.counter += 32;
                    unloadChunks();
                    wipeChunks();
                    nextFile();
                    continue;
                }
            }
            CoordXZ chunk = this.regionChunks.get(this.currentChunk);
            if (!isChunkInsideBorder(chunk)) 
            {
                this.trimChunks.add(chunk);
            }
            this.currentChunk += 1;
            this.counter += 1;
        }
        this.reportTotal += this.counter;


        this.readyToGo = true;
    }

    private boolean nextFile()
    {
        this.reportTotal = (this.currentRegion * 3072);
        this.currentRegion += 1;
        this.regionX = (this.regionZ = this.currentChunk = 0);
        this.regionChunks = new ArrayList(1024);
        this.trimChunks = new ArrayList(1024);
        if (this.currentRegion >= this.worldData.regionFileCount())
        {
            this.paused = true;
            this.readyToGo = false;
            finish();
            return false;
        }
        this.counter += 16;


        CoordXZ coord = this.worldData.regionFileCoordinates(this.currentRegion);
        if (coord == null) {
            return false;
        }
        this.regionX = coord.x;
        this.regionZ = coord.z;
        return true;
    }

    private void addCornerChunks()
    {
        this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX), CoordXZ.regionToChunk(this.regionZ)));
        this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + 31, CoordXZ.regionToChunk(this.regionZ)));
        this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX), CoordXZ.regionToChunk(this.regionZ) + 31));
        this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + 31, CoordXZ.regionToChunk(this.regionZ) + 31));
    }

    private void addEdgeChunks()
    {
        int chunkX = 0;
        int chunkZ = 0;
        for (chunkZ = 1; chunkZ < 31; chunkZ++) 
        {
            this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + chunkX, CoordXZ.regionToChunk(this.regionZ) + chunkZ));
        }
        chunkX = 31;
        for (chunkZ = 1; chunkZ < 31; chunkZ++) 
        {
            this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + chunkX, CoordXZ.regionToChunk(this.regionZ) + chunkZ));
        }
        chunkZ = 0;
        for (chunkX = 1; chunkX < 31; chunkX++) 
        {
            this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + chunkX, CoordXZ.regionToChunk(this.regionZ) + chunkZ));
        }
        chunkZ = 31;
        for (chunkX = 1; chunkX < 31; chunkX++) 
        {
            this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + chunkX, CoordXZ.regionToChunk(this.regionZ) + chunkZ));
        }
        this.counter += 4;
    }

    private void addInnerChunks()
    {
      for (int chunkX = 1; chunkX < 31; chunkX++) 
      {
            for (int chunkZ = 1; chunkZ < 31; chunkZ++) 
            {
                this.regionChunks.add(new CoordXZ(CoordXZ.regionToChunk(this.regionX) + chunkX, CoordXZ.regionToChunk(this.regionZ) + chunkZ));
             }
      }
      this.counter += 32;
    }

    private void unloadChunks()
    {
      for (CoordXZ unload : this.trimChunks) 
      {
            if (this.world.isChunkLoaded(unload.x, unload.z)) 
            {
                this.world.unloadChunk(unload.x, unload.z, false, false);
            }
      }
      this.counter += this.trimChunks.size();
    }

    private void wipeChunks()
    {
        File regionFile = this.worldData.regionFile(this.currentRegion);
        if (!regionFile.canWrite())
        {
            regionFile.setWritable(true);
            if (!regionFile.canWrite())
            {
                sendMessage("Error! region file is locked and can't be trimmed: " + regionFile.getName());
                return;
            }
        }
        int offsetX = CoordXZ.regionToChunk(this.regionX);
        int offsetZ = CoordXZ.regionToChunk(this.regionZ);
        long wipePos = 0L;
        int chunkCount = 0;
        try
        {
            RandomAccessFile unChunk = new RandomAccessFile(regionFile, "rwd");
            for (CoordXZ wipe : this.trimChunks) 
            {
                if (this.worldData.doesChunkExist(wipe.x, wipe.z))
                {
                    wipePos = 4 * (wipe.x - offsetX + (wipe.z - offsetZ) * 32);
                    unChunk.seek(wipePos);
                    unChunk.writeInt(0);
                    chunkCount++;
                }
            }
            unChunk.close();
            this.reportTrimmedChunks += chunkCount;
        }
        catch (FileNotFoundException ex)
        {
            sendMessage("Error! Could not open region file to wipe individual chunks: " + regionFile.getName());
        }
        catch (IOException ex)
        {
            sendMessage("Error! Could not modify region file to wipe individual chunks: " + regionFile.getName());
        }
        this.counter += this.trimChunks.size();
    }

    private boolean isChunkInsideBorder(CoordXZ chunk)
    {
        return this.border.insideBorder(CoordXZ.chunkToBlock(chunk.x) + 8, CoordXZ.chunkToBlock(chunk.z) + 8);
    }

    public void finish()
    {
        this.reportTotal = this.reportTarget;
        reportProgress();
        sendMessage("task successfully completed!");
        stop();
    }

    public void cancel()
    {
        stop();
    }

    private void stop()
    {
        if (this.server == null) 
        {
            return;
        }
        this.readyToGo = false;
        if (this.taskID != -1) 
        {
            this.server.getScheduler().cancelTask(this.taskID);
        }
        this.server = null;
    }

    public boolean valid()
    {
        return this.server != null;
    }

    public void pause()
    {
        pause(!this.paused);
    }

    public void pause(boolean pause)
    {
        this.paused = pause;
        if (pause) 
        {
            reportProgress();
        }
    }

    public boolean isPaused()
    {
        return this.paused;
    }

    private void reportProgress()
    {
        this.lastReport = ConfigWorld.Now();
        double perc = this.reportTotal / this.reportTarget * 100.0D;
        sendMessage(this.reportTrimmedRegions + " entire region(s) and " + this.reportTrimmedChunks + " individual chunk(s) trimmed so far (" + ConfigWorld.coord.format(perc) + "% done" + ")");
    }

    private void sendMessage(String text)
    {
        //ConfigWorld.Log("[Trim] " + text);
        if (this.notifyPlayer != null) 
        {
            this.notifyPlayer.sendMessage("[Trim] " + text);
        }
    }
}
