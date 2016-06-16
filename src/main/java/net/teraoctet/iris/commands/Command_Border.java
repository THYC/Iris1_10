package net.teraoctet.iris.commands;

import java.util.Iterator;
import java.util.Set;
import net.teraoctet.iris.Iris;
import net.teraoctet.iris.parcelle.ParcelleManager;
import net.teraoctet.iris.world.BorderManager;
import net.teraoctet.iris.world.ConfigWorld;
import net.teraoctet.iris.world.CoordXZ;
import net.teraoctet.iris.world.WorldFillTask;
import net.teraoctet.iris.world.WorldTrimTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Border
implements CommandExecutor
{
    private final Iris plugin;

    public Command_Border(Iris plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player)sender;
        if (args.length == 1 && args[0].equalsIgnoreCase("set"))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.set")) 
            {
                return true;
                
            }
            
            ParcelleManager parcelleManager = ParcelleManager.getSett(player);
            Location[] locs = null;
            locs[0] = parcelleManager.getBorder1();
            locs[1] = parcelleManager.getBorder2();
            ConfigWorld.setBorderCorners(locs[0].getWorld().getName(), locs[0].getX(), locs[0].getBlockZ(),locs[1].getX(),locs[1].getZ(), true, false);
            sender.sendMessage(Iris.formatMsg.format("<aqua>Bordure cr<e_ai>er",player));
            return true;
        }
        
        else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("radius"))
        {
            if (!player.hasPermission("iris.border.radius") && !player.isOp()) 
            {
                return true;
            }
            String world = player.getWorld().getName();
            BorderManager border = ConfigWorld.Border(world);
            if (border == null)
            {
                return true;
            }
            double x = border.getX();
            double z = border.getZ();
            int radiusX;
            int radiusZ;
            try
            {
                radiusX = Integer.parseInt(args[1]);
                if (args.length == 3) 
                {
                    radiusZ = Integer.parseInt(args[2]);
                } 
                else 
                {
                    radiusZ = radiusX;
                }
            }
            catch (NumberFormatException ex)
            {
                return true;
            }
            ConfigWorld.setBorder(world, radiusX, radiusZ, x, z);
            sender.sendMessage("Radius has been set. " + ConfigWorld.BorderDescription(world));
        }
        
        else if ((args.length == 2) && (args[1].equalsIgnoreCase("clear")))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.clear")) 
            {
                return true;
            }
            String world = args[0];
            BorderManager border = ConfigWorld.Border(world);
            if (border == null)
            {
                sender.sendMessage("The world you specified (\"" + world + "\") does not have a border set.");
                return true;
            }
            ConfigWorld.removeBorder(world);
            if (player != null) 
            {
                sender.sendMessage("Border cleared for world \"" + world + "\".");
            }
        }
    
        else if ((args.length == 1) && (args[0].equalsIgnoreCase("clear")) && (player != null))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.clear")) 
            {
                return true;
            }
            String world = player.getWorld().getName();
            BorderManager border = ConfigWorld.Border(world);
            if (border == null)
            {
                return true;
            }
            ConfigWorld.removeBorder(world);
            sender.sendMessage("Border cleared for world \"" + world + "\".");
        }
        
        else if ((args.length == 2) && (args[0].equalsIgnoreCase("clear")) && (args[1].equalsIgnoreCase("all")))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.clear")) 
            {
                return true;
            }
            ConfigWorld.removeAllBorders();
            if (player != null) 
            {
                sender.sendMessage("All borders cleared for all worlds.");
            }
        }
        
    else if ((args.length == 1) && (args[0].equalsIgnoreCase("list")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.list")) {
        return true;
      }
      sender.sendMessage("Default border shape for all worlds is \"" + ConfigWorld.ShapeName() + "\".");
      
      Set<String> list = ConfigWorld.BorderDescriptions();
      if (list.isEmpty())
      {
        sender.sendMessage("There are no borders currently set.");
        return true;
      }
      Iterator listItem = list.iterator();
      while (listItem.hasNext()) {
        sender.sendMessage((String)listItem.next());
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("shape")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.shape")) {
        return true;
      }
      if ((args[1].equalsIgnoreCase("rectangular")) || (args[1].equalsIgnoreCase("square")))
      {
        ConfigWorld.setShape(false);
      }
      else if ((args[1].equalsIgnoreCase("elliptic")) || (args[1].equalsIgnoreCase("round")))
      {
        ConfigWorld.setShape(true);
      }
      else
      {
        sender.sendMessage("You must specify a shape of \"elliptic\"/\"round\" or \"rectangular\"/\"square\".");
        return true;
      }
      if (player != null) {
        sender.sendMessage("Default border shape for all worlds is now set to \"" + ConfigWorld.ShapeName() + "\".");
      }
    }
    else if ((args.length == 1) && (args[0].equalsIgnoreCase("getmsg")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.getmsg")) {
        return true;
      }
      sender.sendMessage("Border message is currently set to:");
      
      sender.sendMessage("Formatted border message:");
      
    }
    else if ((args.length >= 2) && (args[0].equalsIgnoreCase("setmsg")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.setmsg")) {
        return true;
      }
      String message = "";
      for (int i = 1; i < args.length; i++)
      {
        if (i != 1) {
          message = message + ' ';
        }
        message = message + args[i];
      }
      ConfigWorld.setMessage(message);
      if (player != null)
      {
        sender.sendMessage("Border message is now set to:");
        
        sender.sendMessage("Formatted border message:");
        
      }
    }
    else if ((args.length == 1) && (args[0].equalsIgnoreCase("reload")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.reload")) {
        return true;
      }
      if (player != null) {
        //ConfigWorld.Log("Reloading config file at the command of player \"" + player.getName() + "\".");
      }
      ConfigWorld.load(this.plugin, true);
      if (player != null) {
        sender.sendMessage("WorldBorder configuration reloaded.");
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("whoosh")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.whoosh")) {
        return true;
      }
      ConfigWorld.setWhooshEffect(strAsBool(args[1]));
      if (player != null)
      {
        sender.sendMessage("\"Whoosh\" knockback effect " );
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("knockback")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.knockback")) {
        return true;
      }
      double numBlocks = 0.0D;
      try
      {
        numBlocks = Double.parseDouble(args[1]);
      }
      catch (NumberFormatException ex)
      {
        return true;
      }
      if ((numBlocks < 0.0D) || ((numBlocks > 0.0D) && (numBlocks < 1.0D)))
      {
        return true;
      }
      ConfigWorld.setKnockBack(numBlocks);
      if (player != null) {
        sender.sendMessage("Knockback set to " + numBlocks + " blocks inside the border.");
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("delay")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.delay")) {
        return true;
      }
      int delay = 0;
      try
      {
        delay = Integer.parseInt(args[1]);
      }
      catch (NumberFormatException ex)
      {
        return true;
      }
      if (delay < 1)
      {
        return true;
      }
      ConfigWorld.setTimerTicks(delay);
      if (player != null) {
        sender.sendMessage("Timer delay set to " + delay + " tick(s). That is roughly " + delay * 50 + "ms.");
      }
    }
    
    
    else if ((args.length == 3) && (args[0].equalsIgnoreCase("wrap")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.wrap")) {
        return true;
      }
      String world = args[1];
      BorderManager border = ConfigWorld.Border(world);
      if (border == null)
      {
        sender.sendMessage("The world you specified (\"" + world + "\") does not have a border set.");
        return true;
      }
      boolean wrap = strAsBool(args[2]);
      border.setWrapping(wrap);
      ConfigWorld.setBorder(world, border);
      if (player != null) {
        sender.sendMessage("Border for world \"" + world + "\" is now set to " + (wrap ? "" : "not ") + "wrap around.");
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("wrap")) && (player != null))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.wrap")) {
        return true;
      }
      String world = player.getWorld().getName();
      BorderManager border = ConfigWorld.Border(world);
      if (border == null)
      {
        sender.sendMessage("This world (\"" + world + "\") does not have a border set.");
        return true;
      }
      boolean wrap = strAsBool(args[1]);
      border.setWrapping(wrap);
      ConfigWorld.setBorder(world, border);
      
      sender.sendMessage("Border for world \"" + world + "\" is now set to " + (wrap ? "" : "not ") + "wrap around.");
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("portal")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.portal")) {
        return true;
      }
      ConfigWorld.setPortalRedirection(strAsBool(args[1]));
      if (player != null)
      {
        sender.sendMessage("Portal redirection " );
      }
    }
    else if ((args.length >= 2) && (args[1].equalsIgnoreCase("fill")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.fill")) {
        return true;
      }
      boolean cancel = false;boolean confirm = false;boolean pause = false;
      String frequency = "";
      if (args.length >= 3)
      {
        cancel = (args[2].equalsIgnoreCase("cancel")) || (args[2].equalsIgnoreCase("stop"));
        confirm = args[2].equalsIgnoreCase("confirm");
        pause = args[2].equalsIgnoreCase("pause");
        if ((!cancel) && (!confirm) && (!pause)) {
          frequency = args[2];
        }
      }
      String pad = args.length >= 4 ? args[3] : "";
      String forceLoad = args.length >= 5 ? args[4] : "";
      
      String world = args[0];
      
      cmdFill(sender, player, world, confirm, cancel, pause, pad, frequency, forceLoad);
    }
    else if ((args.length >= 1) && (args[0].equalsIgnoreCase("fill")))
    {
      if (!ConfigWorld.HasPermission(player, "fill")) {
        return true;
      }
      boolean cancel = false;boolean confirm = false;boolean pause = false;
      String frequency = "";
      if (args.length >= 2)
      {
        cancel = (args[1].equalsIgnoreCase("cancel")) || (args[1].equalsIgnoreCase("stop"));
        confirm = args[1].equalsIgnoreCase("confirm");
        pause = args[1].equalsIgnoreCase("pause");
        if ((!cancel) && (!confirm) && (!pause)) {
          frequency = args[1];
        }
      }
      String pad = args.length >= 3 ? args[2] : "";
      String forceLoad = args.length >= 4 ? args[3] : "";
      
      String world = "";
      if ((player != null) && (!cancel) && (!confirm) && (!pause)) {
        world = player.getWorld().getName();
      }
      if ((!cancel) && (!confirm) && (!pause) && (world.isEmpty()))
      {
        
        return true;
      }
      cmdFill(sender, player, world, confirm, cancel, pause, pad, frequency, forceLoad);
    }
    else if ((args.length >= 2) && (args[1].equalsIgnoreCase("trim")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.trim")) {
        return true;
      }
      boolean cancel = false;boolean confirm = false;boolean pause = false;
      String pad = "";String frequency = "";
      if (args.length >= 3)
      {
        cancel = (args[2].equalsIgnoreCase("cancel")) || (args[2].equalsIgnoreCase("stop"));
        confirm = args[2].equalsIgnoreCase("confirm");
        pause = args[2].equalsIgnoreCase("pause");
        if ((!cancel) && (!confirm) && (!pause)) {
          frequency = args[2];
        }
      }
      if (args.length >= 4) {
        pad = args[3];
      }
      String world = args[0];
      
      cmdTrim(sender, player, world, confirm, cancel, pause, pad, frequency);
    }
    else if ((args.length >= 1) && (args[0].equalsIgnoreCase("trim")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.trim")) {
        return true;
      }
      boolean cancel = false;boolean confirm = false;boolean pause = false;
      String pad = "";String frequency = "";
      if (args.length >= 2)
      {
        cancel = (args[1].equalsIgnoreCase("cancel")) || (args[1].equalsIgnoreCase("stop"));
        confirm = args[1].equalsIgnoreCase("confirm");
        pause = args[1].equalsIgnoreCase("pause");
        if ((!cancel) && (!confirm) && (!pause)) {
          frequency = args[1];
        }
      }
      if (args.length >= 3) {
        pad = args[2];
      }
      String world = "";
      if ((player != null) && (!cancel) && (!confirm) && (!pause)) {
        world = player.getWorld().getName();
      }
      if ((!cancel) && (!confirm) && (!pause) && (world.isEmpty()))
      {
        
        return true;
      }
      cmdTrim(sender, player, world, confirm, cancel, pause, pad, frequency);
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("remount")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.remount")) {
        return true;
      }
      int delay = 0;
      try
      {
        delay = Integer.parseInt(args[1]);
        if (delay < 0) {
          throw new NumberFormatException();
        }
      }
      catch (NumberFormatException ex)
      {
        
        return true;
      }
      ConfigWorld.setRemountTicks(delay);
      if (player != null) {
        if (delay == 0)
        {
          sender.sendMessage("Remount delay set to 0. Players will be left dismounted when knocked back from the border while on a vehicle.");
        }
        else
        {
          sender.sendMessage("Remount delay set to " + delay + " tick(s). That is roughly " + delay * 50 + "ms / " + delay * 50.0D / 1000.0D + " seconds. Setting to 0 would disable remounting.");
          if (delay < 10) {
            
          }
        }
      }
    }
    else if ((args.length == 2) && (args[0].equalsIgnoreCase("fillautosave")))
    {
      if (!ConfigWorld.HasPermission(player, "iris.border.fillautosave")) {
        return true;
      }
      int seconds = 0;
      try
      {
        seconds = Integer.parseInt(args[1]);
        if (seconds < 0) {
          throw new NumberFormatException();
        }
      }
      catch (NumberFormatException ex)
      {
        
        return true;
      }
      ConfigWorld.setFillAutosaveFrequency(seconds);
      if (player != null) {
        if (seconds == 0)
        {
          sender.sendMessage("World autosave frequency during Fill process set to 0, disabling it.");
          sender.sendMessage("Note that much progress can be lost this way if there is a bug or crash in the world generation process from Bukkit or any world generation plugin you use.");
        }
        else
        {
          sender.sendMessage("World autosave frequency during Fill process set to " + seconds + " seconds (rounded to a multiple of 5).");
          sender.sendMessage("New chunks generated by the Fill process will be forcibly saved to disk this often to prevent loss of progress due to bugs or crashes in the world generation process.");
        }
      }
    }
        else if ((args.length >= 2) && (args[0].equalsIgnoreCase("bypass")))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.bypass"))
            {
                return true;
            }
            String sPlayer = args[1];

            boolean bypassing = !ConfigWorld.isPlayerBypassing(sPlayer);
            if (args.length > 2) 
            {
                bypassing = strAsBool(args[2]);
            }
            ConfigWorld.setPlayerBypass(sPlayer, bypassing);

            Player target = Bukkit.getPlayer(sPlayer);
            if ((target != null) && (target.isOnline())) 
            {
                target.sendMessage("Border bypass is now " );
            }
            if ((player != null) && (player != target)) 
            {
                sender.sendMessage("Border bypass for player \"" + sPlayer + "\" is " );
            }
        }
        
        else if ((args.length == 1) && (args[0].equalsIgnoreCase("bypass")) && (player != null))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.bypass")) 
            {
                return true;
            }
            String sPlayer = player.getName();
            boolean bypassing = !ConfigWorld.isPlayerBypassing(sPlayer);
            ConfigWorld.setPlayerBypass(sPlayer, bypassing);
            sender.sendMessage("Border bypass is now " );
        }
       
        else if ((args.length == 1) && (args[0].equalsIgnoreCase("bypasslist")))
        {
            if (!ConfigWorld.HasPermission(player, "iris.border.bypasslist")) 
            {
                return true;
            }
            sender.sendMessage("Players with border bypass enabled: " + ConfigWorld.getPlayerBypassList());
        }
        return false;
    }
  
  private boolean strAsBool(String str)
  {
    str = str.toLowerCase();
    if ((str.startsWith("y")) || (str.startsWith("t")) || (str.startsWith("on")) || (str.startsWith("+")) || (str.startsWith("1"))) {
      return true;
    }
    return false;
  }
  
  private boolean cmdSet(CommandSender sender, String world, String[] data, int offset, boolean oneRadius)
  {
    int radiusX;
    int radiusZ;
    double x;
    double z;
    try
    {
      radiusX = Integer.parseInt(data[offset]);
      if (oneRadius)
      {
        radiusZ = radiusX;
        offset--;
      }
      else
      {
        radiusZ = Integer.parseInt(data[(offset + 1)]);
      }
      x = Double.parseDouble(data[(offset + 2)]);
      z = Double.parseDouble(data[(offset + 3)]);
    }
    catch (NumberFormatException ex)
    {
      
      return false;
    }
    ConfigWorld.setBorder(world, radiusX, radiusZ, x, z);
    return true;
  }
  
  private String fillWorld = "";
  private int fillFrequency = 20;
  private int fillPadding = CoordXZ.chunkToBlock(13);
  private boolean fillForceLoad = false;
  
  private void fillDefaults()
  {
    this.fillWorld = "";
    this.fillFrequency = 20;
    

    this.fillPadding = CoordXZ.chunkToBlock(13);
    this.fillForceLoad = false;
  }
  
  private boolean cmdFill(CommandSender sender, Player player, String world, boolean confirm, boolean cancel, boolean pause, String pad, String frequency, String forceLoad)
  {
    if (cancel)
    {
      
      fillDefaults();
      ConfigWorld.StopFillTask();
      return true;
    }
    if (pause)
    {
      if ((ConfigWorld.fillTask == null) || (!ConfigWorld.fillTask.valid()))
      {
        
        return true;
      }
      ConfigWorld.fillTask.pause();
      
      return true;
    }
    if ((ConfigWorld.fillTask != null) && (ConfigWorld.fillTask.valid()))
    {
      
      return true;
    }
    try
    {
      if (!pad.isEmpty()) {
        this.fillPadding = Math.abs(Integer.parseInt(pad));
      }
      if (!frequency.isEmpty()) {
        this.fillFrequency = Math.abs(Integer.parseInt(frequency));
      }
    }
    catch (NumberFormatException ex)
    {
      
      fillDefaults();
      return false;
    }
    if (this.fillFrequency <= 0)
    {
      
      fillDefaults();
      return false;
    }
    if (!forceLoad.isEmpty()) {
      this.fillForceLoad = strAsBool(forceLoad);
    }
    if (!world.isEmpty()) {
      this.fillWorld = world;
    }
    if (confirm)
    {
      if (this.fillWorld.isEmpty())
      {
        
        return false;
      }
      if (player != null) {
        //ConfigWorld.Log("Filling out world to border at the command of player \"" + player.getName() + "\".");
      }
      int ticks = 1;int repeats = 1;
      if (this.fillFrequency > 20) {
        repeats = this.fillFrequency / 20;
      } else {
        ticks = 20 / this.fillFrequency;
      }
      ConfigWorld.fillTask = new WorldFillTask(this.plugin.getServer(), player, this.fillWorld, this.fillPadding, repeats, ticks, this.fillForceLoad);
      if (ConfigWorld.fillTask.valid())
      {
        int task = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, ConfigWorld.fillTask, ticks, ticks);
        ConfigWorld.fillTask.setTaskID(task);
        sender.sendMessage("WorldBorder map generation for world \"" + this.fillWorld + "\" task started.");
      }
      else
      {
       
      }
      fillDefaults();
    }
    else
    {
      if (this.fillWorld.isEmpty())
      {
        
        return false;
      }
      
    }
    return true;
  }
  
  private String trimWorld = "";
  private int trimFrequency = 5000;
  private int trimPadding = CoordXZ.chunkToBlock(13);
  
  private void trimDefaults()
  {
    this.trimWorld = "";
    this.trimFrequency = 5000;
    this.trimPadding = CoordXZ.chunkToBlock(13);
  }
  
  private boolean cmdTrim(CommandSender sender, Player player, String world, boolean confirm, boolean cancel, boolean pause, String pad, String frequency)
  {
    if (cancel)
    {
      sender.sendMessage("Cancelling the world map trimming task.");
      trimDefaults();
      ConfigWorld.StopTrimTask();
      return true;
    }
    if (pause)
    {
      if ((ConfigWorld.trimTask == null) || (!ConfigWorld.trimTask.valid()))
      {
        sender.sendMessage("The world map trimming task is not currently running.");
        return true;
      }
      ConfigWorld.trimTask.pause();
      sender.sendMessage("The world map trimming task is now " + (ConfigWorld.trimTask.isPaused() ? "" : "un") + "paused.");
      return true;
    }
    if ((ConfigWorld.trimTask != null) && (ConfigWorld.trimTask.valid()))
    {
      sender.sendMessage("The world map trimming task is already running.");
      return true;
    }
    try
    {
      if (!pad.isEmpty()) {
        this.trimPadding = Math.abs(Integer.parseInt(pad));
      }
      if (!frequency.isEmpty()) {
        this.trimFrequency = Math.abs(Integer.parseInt(frequency));
      }
    }
    catch (NumberFormatException ex)
    {
      sender.sendMessage("The frequency and padding values must be integers.");
      trimDefaults();
      return false;
    }
    if (this.trimFrequency <= 0)
    {
      sender.sendMessage("The frequency value must be greater than zero.");
      trimDefaults();
      return false;
    }
    if (!world.isEmpty()) {
      this.trimWorld = world;
    }
    if (confirm)
    {
      if (this.trimWorld.isEmpty())
      {
        sender.sendMessage("You must first use this command successfully without confirming.");
        return false;
      }
      if (player != null) {
        //ConfigWorld.Log("Trimming world beyond border at the command of player \"" + player.getName() + "\".");
      }
      int ticks = 1;int repeats = 1;
      if (this.trimFrequency > 20) {
        repeats = this.trimFrequency / 20;
      } else {
        ticks = 20 / this.trimFrequency;
      }
      ConfigWorld.trimTask = new WorldTrimTask(this.plugin.getServer(), player, this.trimWorld, this.trimPadding, repeats);
      if (ConfigWorld.trimTask.valid())
      {
        int task = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, ConfigWorld.trimTask, ticks, ticks);
        ConfigWorld.trimTask.setTaskID(task);
        sender.sendMessage("WorldBorder map trimming task for world \"" + this.trimWorld + "\" started.");
      }
      else
      {
        sender.sendMessage("The world map trimming task failed to start.");
      }
      trimDefaults();
    }
    else
    {
      if (this.trimWorld.isEmpty())
      {
        sender.sendMessage("You must first specify a valid world.");
        return false;
      }
      
    }
    return true;
  }
}

