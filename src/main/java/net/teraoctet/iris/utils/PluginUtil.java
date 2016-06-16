package net.teraoctet.iris.utils;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class PluginUtil
{
  public static String enable(Plugin plugin)
  {
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    if ((!plugin.isEnabled()) && (plugin != null))
    {
      Bukkit.getPluginManager().enablePlugin(plugin);
      return "Plugin.SuccesEnable";
    }
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    if (plugin.isEnabled()) {
      return "Plugin.AlreadyEnabled";
    }
    return "Plugin.Error";
  }
  
  public static String disable(Plugin plugin)
  {
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    if ((plugin.isEnabled()) && (plugin != null))
    {
      Bukkit.getPluginManager().disablePlugin(plugin);
      return "Plugin.SuccesDisable";
    }
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    if (plugin.isEnabled()) {
      return "Plugin.AlreadyDisabled";
    }
    return "Plugin.Error";
  }
  
  public static String getFormattedName(Plugin plugin)
  {
    return getFormattedName(plugin, false);
  }
  
  public static String getFormattedName(Plugin plugin, boolean includeVersions)
  {
    ChatColor color = plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
    String pluginName = color + plugin.getName();
    if (includeVersions) {
      pluginName = pluginName + " (" + plugin.getDescription().getVersion() + ")";
    }
    return pluginName;
  }
  
  public static Plugin getPluginByName(String[] args, int start)
  {
    return getPluginByName(consolidateStrings(args, start));
  }
  
  public static String consolidateStrings(String[] args, int start)
  {
    String ret = args[start];
    if (args.length > start + 1) {
      for (int i = start + 1; i < args.length; i++) {
        ret = ret + " " + args[i];
      }
    }
    return ret;
  }
  
  public static Plugin getPluginByName(String name)
  {
    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      if (name.equalsIgnoreCase(plugin.getName())) {
        return plugin;
      }
    }
    return null;
  }
  
  public static List<String> getPluginNames(boolean fullName)
  {
    List plugins = new ArrayList();
    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
      plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
    }
    return plugins;
  }
  
  public static String getUsages(Plugin plugin)
  {
    List parsedCommands = new ArrayList();
    
    Map commands = plugin.getDescription().getCommands();
    if (commands != null)
    {
      Iterator commandsIt = commands.entrySet().iterator();
      while (commandsIt.hasNext())
      {
        Map.Entry thisEntry = (Map.Entry)commandsIt.next();
        if (thisEntry != null) {
          parsedCommands.add((String)thisEntry.getKey());
        }
      }
    }
    if (parsedCommands.isEmpty()) {
      return "No commands registered.";
    }
    return Joiner.on(", ").join(parsedCommands);
  }
  
  private static String load(Plugin plugin)
  {
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    return load(plugin.getName());
  }
  
  public static String load(String name)
  {
        Plugin target = null;

        File pluginDir = new File("plugins");
        if (!pluginDir.exists()) 
        {
            return "Plugin.Error";
        }

        File pluginFile = new File("plugins//" + name + ".jar");
        if (!pluginFile.isFile()) 
        {
            for (File f : pluginDir.listFiles()) 
            {
                if (f.getName().endsWith(".jar")) 
                {
                    try
                    {
                        PluginDescriptionFile desc = Bukkit.getPluginManager().getPlugin("Iris").getPluginLoader().getPluginDescription(f);
                        if (desc.getName().equalsIgnoreCase(name)) 
                        {
                            pluginFile = f;
                        }
                    }
                    catch (Exception e)
                    {
                        return "Plugin.NotFound";
                    }
                }
            }
        }
        try
        {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        }
        catch (InvalidDescriptionException e)
        {
            e.printStackTrace();
            return "Plugin.Error";
        }
        catch (InvalidPluginException e)
        {
            e.printStackTrace();
            return "Plugin.Error";
        }
        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);

        return "Plugin.SuccesLoad";
    }
  
  public static String reload(Plugin plugin)
  {
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    if (plugin != null)
    {
      unload(plugin);
      load(plugin);
    }
    return "Plugin.SuccesReload";
  }
  
  public static String unload(Plugin plugin)
  {
    if (plugin == null) {
      return "Plugin.NotFound";
    }
    String name = plugin.getName();
    
    PluginManager pluginManager = Bukkit.getPluginManager();
    
    SimpleCommandMap commandMap = null;
    
    List plugins = null;
    
    Map names = null;
    Map commands = null;
    Map listeners = null;
    
    boolean reloadlisteners = true;
    Field commandMapField;
    if (pluginManager != null) {
      try
      {
        Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        plugins = (List)pluginsField.get(pluginManager);
        
        Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
        lookupNamesField.setAccessible(true);
        names = (Map)lookupNamesField.get(pluginManager);
        try
        {
          Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
          listenersField.setAccessible(true);
          listeners = (Map)listenersField.get(pluginManager);
        }
        catch (Exception e)
        {
          reloadlisteners = false;
        }
        commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        commandMap = (SimpleCommandMap)commandMapField.get(pluginManager);
        
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        commands = (Map)knownCommandsField.get(commandMap);
      }
      catch (NoSuchFieldException e)
      {
        e.printStackTrace();
        return "Plugin.Error";
      }
      catch (IllegalAccessException e)
      {
        e.printStackTrace();
        return "Plugin.Error";
      }
    }
    pluginManager.disablePlugin(plugin);
    if ((plugins != null) && (plugins.contains(plugin))) {
      plugins.remove(plugin);
    }
    if ((names != null) && (names.containsKey(name))) {
      names.remove(name);
    }
    if (commandMap != null) {
      for (Iterator it = commands.entrySet().iterator(); it.hasNext();)
      {
        Map.Entry entry = (Map.Entry)it.next();
        if ((entry.getValue() instanceof PluginCommand))
        {
          PluginCommand c = (PluginCommand)entry.getValue();
          if (c.getPlugin() == plugin)
          {
            c.unregister(commandMap);
            it.remove();
          }
        }
      }
    }
    ClassLoader cl = plugin.getClass().getClassLoader();
    if ((cl instanceof URLClassLoader)) {
      try
      {
        ((URLClassLoader)cl).close();
      }
      catch (IOException ex)
      {
        Logger.getLogger(PluginUtil.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    System.gc();
    
    return "Plugin.SuccesUnload";
  }
}
