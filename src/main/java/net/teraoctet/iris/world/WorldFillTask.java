package net.teraoctet.iris.world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldFillTask
implements Runnable
{
  private transient Server server = null;
  private transient World world = null;
  private transient BorderManager border = null;
  private transient WorldFileData worldData = null;
  private transient boolean readyToGo = false;
  private transient boolean paused = false;
  private transient boolean pausedForMemory = false;
  private transient int taskID = -1;
  private transient Player notifyPlayer = null;
  private transient int chunksPerRun = 1;
  private transient boolean continueNotice = false;
  private transient boolean forceLoad = false;
  private transient int fillDistance = 208;
  private transient int tickFrequency = 1;
  private transient int refX = 0;
  private transient int lastLegX = 0;
  private transient int refZ = 0;
  private transient int lastLegZ = 0;
  private transient int refLength = -1;
  private transient int refTotal = 0;
  private transient int lastLegTotal = 0;
  private transient int x = 0;
  private transient int z = 0;
  private transient boolean isZLeg = false;
  private transient boolean isNeg = false;
  private transient int length = -1;
  private transient int current = 0;
  private transient boolean insideBorder = true;
  private List<CoordXZ> storedChunks = new LinkedList();
  private Set<CoordXZ> originalChunks = new HashSet();
  private transient CoordXZ lastChunk = new CoordXZ(0, 0);
  private transient long lastReport = ConfigWorld.Now();
  private transient long lastAutosave = ConfigWorld.Now();
  private transient int reportTarget = 0;
  private transient int reportTotal = 0;
  private transient int reportNum = 0;
  
  public WorldFillTask(Server theServer, Player player, String worldName, int fillDistance, int chunksPerRun, int tickFrequency, boolean forceLoad)
  {
    this.server = theServer;
    this.notifyPlayer = player;
    this.fillDistance = fillDistance;
    this.tickFrequency = tickFrequency;
    this.chunksPerRun = chunksPerRun;
    this.forceLoad = forceLoad;
    
    this.world = this.server.getWorld(worldName);
    if (this.world == null)
    {
      if (worldName.isEmpty()) {
        sendMessage("You must specify a world!");
      } else {
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
    this.worldData = WorldFileData.create(this.world, this.notifyPlayer);
    if (this.worldData == null)
    {
      stop();
      return;
    }
    this.border.setRadiusX(this.border.getRadiusX() + fillDistance);
    this.border.setRadiusZ(this.border.getRadiusZ() + fillDistance);
    this.x = CoordXZ.blockToChunk((int)this.border.getX());
    this.z = CoordXZ.blockToChunk((int)this.border.getZ());
    
    int chunkWidthX = (int)Math.ceil((this.border.getRadiusX() + 16) * 2 / 16.0D);
    int chunkWidthZ = (int)Math.ceil((this.border.getRadiusZ() + 16) * 2 / 16.0D);
    int biggerWidth = chunkWidthX > chunkWidthZ ? chunkWidthX : chunkWidthZ;
    this.reportTarget = (biggerWidth * biggerWidth + biggerWidth + 1);
    Chunk[] originals = this.world.getLoadedChunks();
    for (Chunk original : originals) {
      this.originalChunks.add(new CoordXZ(original.getX(), original.getZ()));
    }
    this.readyToGo = true;
  }
  
  public WorldFillTask(Server theServer, Player player, String worldName, int fillDistance, int chunksPerRun, int tickFrequency)
  {
    this(theServer, player, worldName, fillDistance, chunksPerRun, tickFrequency, false);
  }
  
  public void setTaskID(int ID)
  {
    if (ID == -1) {
      stop();
    }
    this.taskID = ID;
  }
  
  @Override
  public void run()
  {
    if (this.continueNotice)
    {
      this.continueNotice = false;
      sendMessage("World map generation task automatically continuing.");
      sendMessage("Reminder: you can cancel at any time with \"wb fill cancel\", or pause/unpause with \"wb fill pause\".");
    }
    if (this.pausedForMemory)
    {
      if (ConfigWorld.AvailableMemory() < 500) {
        return;
      }
      this.pausedForMemory = false;
      this.readyToGo = true;
      sendMessage("Available memory is sufficient, automatically continuing.");
    }
    if ((this.server == null) || (!this.readyToGo) || (this.paused)) {
      return;
    }
    this.readyToGo = false;
    
    long loopStartTime = ConfigWorld.Now();
    for (int loop = 0; loop < this.chunksPerRun; loop++)
    {
      if ((this.paused) || (this.pausedForMemory)) {
        return;
      }
      long now = ConfigWorld.Now();
      if (now > this.lastReport + 5000L) {
        reportProgress();
      }
      if (now > loopStartTime + 45L)
      {
        this.readyToGo = true;
        return;
      }
      while (!this.border.insideBorder(CoordXZ.chunkToBlock(this.x) + 8, CoordXZ.chunkToBlock(this.z) + 8)) {
        if (!moveToNext()) {
          return;
        }
      }
      this.insideBorder = true;
      if (!this.forceLoad) {
        while (this.worldData.isChunkFullyGenerated(this.x, this.z))
        {
          this.insideBorder = true;
          if (!moveToNext()) {
            return;
          }
        }
      }
      this.world.loadChunk(this.x, this.z, true);
      this.worldData.chunkExistsNow(this.x, this.z);
      


      int popX = !this.isZLeg ? this.x : this.x + (this.isNeg ? -1 : 1);
      int popZ = this.isZLeg ? this.z : this.z + (!this.isNeg ? -1 : 1);
      this.world.loadChunk(popX, popZ, false);
      if ((!this.storedChunks.contains(this.lastChunk)) && (!this.originalChunks.contains(this.lastChunk)))
      {
        this.world.loadChunk(this.lastChunk.x, this.lastChunk.z, false);
        this.storedChunks.add(new CoordXZ(this.lastChunk.x, this.lastChunk.z));
      }
      this.storedChunks.add(new CoordXZ(popX, popZ));
      this.storedChunks.add(new CoordXZ(this.x, this.z));
      while (this.storedChunks.size() > 8)
      {
        CoordXZ coord = this.storedChunks.remove(0);
        if (!this.originalChunks.contains(coord)) {
          this.world.unloadChunkRequest(coord.x, coord.z);
        }
      }
      if (!moveToNext()) {
        return;
      }
    }
    this.readyToGo = true;
  }
  
  public boolean moveToNext()
  {
    if ((this.paused) || (this.pausedForMemory)) {
      return false;
    }
    this.reportNum += 1;
    if ((!this.isNeg) && (this.current == 0) && (this.length > 3)) {
      if (!this.isZLeg)
      {
        this.lastLegX = this.x;
        this.lastLegZ = this.z;
        this.lastLegTotal = (this.reportTotal + this.reportNum);
      }
      else
      {
        this.refX = this.lastLegX;
        this.refZ = this.lastLegZ;
        this.refTotal = this.lastLegTotal;
        this.refLength = (this.length - 1);
      }
    }
    if (this.current < this.length)
    {
      this.current += 1;
    }
    else
    {
      this.current = 0;
      this.isZLeg ^= true;
      if (this.isZLeg)
      {
        this.isNeg ^= true;
        this.length += 1;
      }
    }
    this.lastChunk.x = this.x;
    this.lastChunk.z = this.z;
    if (this.isZLeg) {
      this.z += (this.isNeg ? -1 : 1);
    } else {
      this.x += (this.isNeg ? -1 : 1);
    }
    if ((this.isZLeg) && (this.isNeg) && (this.current == 0))
    {
      if (!this.insideBorder)
      {
        finish();
        return false;
      }
      this.insideBorder = false;
    }
    return true;
  }
  
  public void finish()
  {
    this.paused = true;
    reportProgress();
    this.world.save();
    sendMessage("task successfully completed!");
    stop();
  }
  
  public void cancel()
  {
    stop();
  }
  
  private void stop()
  {
    if (this.server == null) {
      return;
    }
    this.readyToGo = false;
    if (this.taskID != -1) {
      this.server.getScheduler().cancelTask(this.taskID);
    }
    this.server = null;
    while (!this.storedChunks.isEmpty())
    {
      CoordXZ coord = this.storedChunks.remove(0);
      if (!this.originalChunks.contains(coord)) {
        this.world.unloadChunkRequest(coord.x, coord.z);
      }
    }
  }
  
  public boolean valid()
  {
    return this.server != null;
  }
  
  public void pause()
  {
    if (this.pausedForMemory) {
      pause(false);
    } else {
      pause(!this.paused);
    }
  }
  
  public void pause(boolean pause)
  {
    if ((this.pausedForMemory) && (!pause)) {
      this.pausedForMemory = false;
    } else {
      this.paused = pause;
    }
    if (this.paused)
    {
      ConfigWorld.StoreFillTask();
      reportProgress();
    }
    else
    {
      ConfigWorld.UnStoreFillTask();
    }
  }
  
  public boolean isPaused()
  {
    return (this.paused) || (this.pausedForMemory);
  }
  
  private void reportProgress()
  {
    this.lastReport = ConfigWorld.Now();
    double perc = (this.reportTotal + this.reportNum) / this.reportTarget * 100.0D;
    if (perc > 100.0D) {
      perc = 100.0D;
    }
    sendMessage(this.reportNum + " more chunks processed (" + (this.reportTotal + this.reportNum) + " total, ~" + ConfigWorld.coord.format(perc) + "%" + ")");
    this.reportTotal += this.reportNum;
    this.reportNum = 0;
    if ((ConfigWorld.FillAutosaveFrequency() > 0) && (this.lastAutosave + ConfigWorld.FillAutosaveFrequency() * 1000 < this.lastReport))
    {
      this.lastAutosave = this.lastReport;
      sendMessage("Saving the world to disk, just to be on the safe side.");
      this.world.save();
    }
  }
  
  private void sendMessage(String text)
  {
    int availMem = ConfigWorld.AvailableMemory();
    
    //ConfigWorld.Log("[Fill] " + text + " (free mem: " + availMem + " MB)");
    if (this.notifyPlayer != null) {
      this.notifyPlayer.sendMessage("[Fill] " + text);
    }
    if (availMem < 200)
    {
      this.pausedForMemory = true;
      ConfigWorld.StoreFillTask();
      text = "Available memory is very low, task is pausing. A cleanup will be attempted now, and the task will automatically continue if/when sufficient memory is freed up.\n Alternatively, if you restart the server, this task will automatically continue once the server is back up.";
      //ConfigWorld.Log("[Fill] " + text);
      if (this.notifyPlayer != null) {
        this.notifyPlayer.sendMessage("[Fill] " + text);
      }
      System.gc();
    }
  }
  
  public void continueProgress(int x, int z, int length, int totalDone)
  {
    this.x = x;
    this.z = z;
    this.length = length;
    this.reportTotal = totalDone;
    this.continueNotice = true;
  }
  
  public int refX()
  {
    return this.refX;
  }
  
  public int refZ()
  {
    return this.refZ;
  }
  
  public int refLength()
  {
    return this.refLength;
  }
  
  public int refTotal()
  {
    return this.refTotal;
  }
  
  public int refFillDistance()
  {
    return this.fillDistance;
  }
  
  public int refTickFrequency()
  {
    return this.tickFrequency;
  }
  
  public int refChunksPerRun()
  {
    return this.chunksPerRun;
  }
  
  public String refWorld()
  {
    return this.world.getName();
  }
  
  public boolean refForceLoad()
  {
    return this.forceLoad;
  }
 
}
