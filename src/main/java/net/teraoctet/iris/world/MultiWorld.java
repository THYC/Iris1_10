package net.teraoctet.iris.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class MultiWorld 
{
    static Plugin plugin;
  
    public MultiWorld(Plugin instance)
    {
        plugin = instance;
    }
  
    public static void loadws()
    {
        if (conf.getKeysYAML("world.yml","worlds") == null)
        {
            Iris.log.info(formatMsg.format("<aqua>[IRIS] World Desactive"));
            return;
        }
        
        for(String worldName : conf.getKeysYAML("world.yml","worlds"))
        {
            try
            {
            WorldCreator w = new WorldCreator(worldName);
            w.environment(World.Environment.valueOf(conf.getStringYAML("world.yml","worlds." + worldName + ".Env")));
            Bukkit.createWorld(w);
            }
            catch(Exception ex)
            {
                plugin.getLogger().log(Level.INFO, "[Iris] Erreur de lecture du monde {0}", worldName);
            }
        }
    }
  
    public static void create(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.create") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        
        if (args.length < 3)
        {
            sender.sendMessage(formatMsg.format("<aqua>Usage: /world create <Name> [Type]"));
            sender.sendMessage(formatMsg.format("<aqua>Types: flat, custom, largebiomes, amplified, normal, nether, end, nostructures, s:[SEED]"));
            return;
        }
        
        World.Environment env = World.Environment.NORMAL;
        
        if (Bukkit.getWorld(args[1]) != null)
        {
          sender.sendMessage(formatMsg.format("<aqua>Ce monde existe déjà"));
          return;
        }
        if (!args[1].replaceAll("[a-zA-Z0-9]", "").replaceAll("_", "").equalsIgnoreCase(""))
        {
            sender.sendMessage(formatMsg.format("<aqua>Des caractère ne sont pas conformes"));
            return;
        }
        WorldCreator settings = new WorldCreator(args[1]);
        for (int i = 0; i < args.length + 3; i++) 
        {
            if ((args[2].equalsIgnoreCase("flat")) || (args[2].equalsIgnoreCase("flatland")))
            {
                settings.type(WorldType.FLAT);
            }
            else if ((args[2].equalsIgnoreCase("large")) || (args[2].replaceAll("_", "").equalsIgnoreCase("largebiomes")))
            {
                settings.type(WorldType.LARGE_BIOMES);
            }
            else if (args[2].equalsIgnoreCase("amplified"))
            {
                settings.type(WorldType.AMPLIFIED);
            }
            else if (args[2].equalsIgnoreCase("custom"))
            {
                settings.type(WorldType.CUSTOMIZED);
            }
            else if (args[2].equalsIgnoreCase("normal"))
            {
                env = World.Environment.NORMAL;
            }
            else if (args[2].equalsIgnoreCase("nether"))
            {
                settings.environment(World.Environment.NETHER);
                env = World.Environment.NETHER;
            }
            else if (args[2].equalsIgnoreCase("end"))
            {
                settings.environment(World.Environment.THE_END);
                env = World.Environment.THE_END;
            }
            else if (args[2].equalsIgnoreCase("nostructures"))
            {
                settings.generateStructures(false);
            }
            else if (isNumber(args[2]))
            {
                settings.seed(Long.parseLong(args[2]));
            }
            else if (args[2].startsWith("s:"))
            {
                if (isAlphaNumeric(args[2]))
                {
                    String seed1 = args[2].replaceFirst("s:", "");
                    settings.seed((long) seed1.hashCode());
                }
            }
            else if (args[2].startsWith("g:"))
            {
                String generator = args[2].replaceFirst("g:", "");
                settings.generator(generator);
            }            
        }
        sender.sendMessage(formatMsg.format("<aqua>Création du monde " + args[1] + " ..."));
        Bukkit.createWorld(settings);
        try
        {
            conf.setStringYAML("world.yml","worlds." + settings.name() + ".Env", env.name());
        }
        catch (NullPointerException e)
        {
            
        }
        sender.sendMessage(formatMsg.format("<aqua>Création du monde " + args[1] + " terminé"));
    }
  
    public static void importw(CommandSender sender, String[] args)
    {
        try
        {
            if (!sender.hasPermission("iris.admin.world.import") && !sender.isOp())
            {
                sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
                return;
            }
            if (args.length < 2)
            {
                sender.sendMessage(formatMsg.format("<aqua>Usage: /world import <Name> [Normal/Nether/End]"));
                return;
            }
            if (!new File(args[1]).exists())
            {
                sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[1] + " n'a pas été trouvé"));
                return;
            }

            WorldCreator settings = new WorldCreator(args[1]);
            for (int i = 0; i < 10; i++)
            {
                if (!args[2].equalsIgnoreCase("normal")) 
                {
                    if (args[2].equalsIgnoreCase("nether")) 
                    {
                        settings.environment(World.Environment.NETHER);
                    } else if (args[2].equalsIgnoreCase("end")) 
                    {
                        settings.environment(World.Environment.THE_END);
                    }
                }
            }
            sender.sendMessage(formatMsg.format("<aqua>Importation du monde " + args[2] + " ..."));
            Bukkit.createWorld(settings);
            
            try
            {
                conf.setStringYAML("world.yml","worlds." + settings.name() + ".Env", settings.type().toString());
            }
            catch (NullPointerException e){}
        }
        catch(Exception ex)
        {
            sender.sendMessage(formatMsg.format("<gray>-<green>/world create <yellow>[NomDuMonde] [Type] <gray>Création d'un nouveau monde"));
            sender.sendMessage(formatMsg.format("<gray>-<green>Types: flat, largebiomes, amplified, normal, nether, end, nostructures, [SEED]"));
            return;
        }
        
        sender.sendMessage(formatMsg.format("<aqua>Importation du monde " + args[2] + " terminé"));
    }
  
    public static void list(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.list") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        
        if (conf.getKeysYAML("world.yml","worlds") == null)
        {
            sender.sendMessage(formatMsg.format("<aqua>Aucun monde enregistré, module désactivé"));
            return;
        }
        
        for(String key : conf.getKeysYAML("world.yml","worlds"))
        {
            sender.sendMessage(key);
        }
    }
  
    public static void remove(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.remove") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        if (args.length < 2)
        {
            sender.sendMessage(formatMsg.format("<aqua>Usage: /world remove <Name>"));
            return;
        }
        
        World world = Bukkit.getWorld(args[1]);
        if (world == null)
        {
            sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[0] + " n'a pas été trouvé"));
            return;
        }
        for (Player pl : Bukkit.getOnlinePlayers()) 
        {
            if (pl.getWorld().equals(world))
            {
                World w2 = (World)Bukkit.getWorlds().get(0);
                Location spawn = new Location(w2,
                                conf.getIntYAML("world.yml", "worlds." + w2.getName() + ".spawn.x", w2.getSpawnLocation().getBlockX()),
                                conf.getIntYAML("world.yml", "worlds." + w2 + ".spawn.y", w2.getSpawnLocation().getBlockY()),
                                conf.getIntYAML("world.yml", "worlds." + w2.getName() + ".spawn.z", w2.getSpawnLocation().getBlockZ()));
                pl.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
        Bukkit.getServer().unloadWorld(world, true);
        WorldCreator settings = new WorldCreator(args[1]);
        try
        {
            conf.delNodeYAML("world.yml", "worlds." + world.getName());
        }
        catch (NullPointerException e)
        {
            
        }
        sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[1] + " a été supprimé"));
    }
          
    public static void reset(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.reset") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        
        World world = Bukkit.getWorld(args[1]);
        if (world == null)
        {
            sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[1] + " n'a pas été trouvé"));
            return;
        }
        if (world.getPlayers().size() > 0)
        {
            sender.sendMessage(formatMsg.format("<aqua>Impossible de redémarrer ce monde si des joueurs sont dessus"));
            return;
        }
        sender.sendMessage(formatMsg.format("<aqua>Redémarrage du monde " + world.getName() + " ..."));
        
        String i = "";
        while (new File(world.getWorldFolder().getName() + "_OLD" + i).exists()) 
        {
            if (i.equalsIgnoreCase(""))
            {
              i = "1";
            }
            else
            {
              Integer a = Integer.parseInt(i);
              a = a + 1;
              i = String.valueOf(a);
            }
        }
        File f = new File(world.getWorldFolder().getName() + "_OLD" + i);
        try
        {
            copy(world.getWorldFolder(), f);
        }
        catch (IOException e)
        {
        }
        resetAll(world);
        sender.sendMessage(formatMsg.format("<aqua>Redémarrage du monde " + world.getName() + " terminé"));
    }
  
    private static void copy(File source, File dest)
      throws IOException
    {
    	if(!source.exists())
        {
 
           Iris.log.log(Level.INFO, "IRIS : Le dossier {0} n''existe pas", source);
        }
        else
        {
           try
           {
        	copyFolder(source,dest);
           }
           catch(IOException e)
           {
           }
        }
    }
  
    private static void clear(File dir)
    {
        for (File file : dir.listFiles()) 
        {
            if (!file.getName().toLowerCase().contains("player"))
            {
                if (file.isDirectory()) 
                {
                    clear(file);
                }
                file.delete();
            }
        }
    }
  
    private static void resetAll(World world)
    {
        world.save();
        Bukkit.unloadWorld(world, true);
        File dir = world.getWorldFolder();
        clear(dir);
        WorldCreator creator = new WorldCreator(world.getName());
        creator.seed(world.getSeed());
        creator.environment(world.getEnvironment());
        creator.generator(world.getGenerator());
        creator.generateStructures(world.canGenerateStructures());
        creator.type(world.getWorldType());
        World world2 = Bukkit.createWorld(creator);
        world2.save();
    }
  
    public static void tp(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.tp") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        if (args.length != 2)
        {
            sender.sendMessage(formatMsg.format("<aqua>Usage: /world tp <Name>"));
            return;
        }
        
        World world = Bukkit.getWorld(args[1]);
        if (world == null)
        {
            Iris.log.info(args[1]);
            Iris.log.info(formatMsg.format("<aqua>Le monde " + args[1] + " n'a pas été trouvé"));
            sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[1] + " n'a pas été trouvé"));
            return;
        }
        if(sender instanceof Player)
        {
            try
            {
                Player p = (Player)sender;
                Location spawn = new Location(world,
                                conf.getIntYAML("world.yml", "worlds." + p.getWorld().getName() + ".spawn.x", world.getSpawnLocation().getBlockX()),
                                conf.getIntYAML("world.yml", "worlds." + p.getWorld().getName() + ".spawn.y", world.getSpawnLocation().getBlockY()),
                                conf.getIntYAML("world.yml", "worlds." + p.getWorld().getName() + ".spawn.z", world.getSpawnLocation().getBlockZ()));
                p.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            catch (Exception ex)
            {
                sender.sendMessage(formatMsg.format("<aqua>Le monde " + args[1] + " n'a pas été trouvé"));
            }
        }
    }
  
    public static void flag(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("iris.admin.world.flag") && !sender.isOp())
        {
            sender.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml","noPermission")));
            return;
        }
        if (args.length != 4)
        {
            sender.sendMessage(formatMsg.format("<aqua>Usage: /world flag <World> <Flag> <Allow/Deny>"));
            sender.sendMessage(formatMsg.format("<gray>       Flags: MONSTER,  ANIMAL,  PVP, GAMEMODE"));
            return;
        }
      
        Iworld world = new Iworld(Bukkit.getWorld(args[1]));
        String flag = args[2];
        String value = args[3];
                                
        if ((flag.equalsIgnoreCase("monster")) || (flag.equalsIgnoreCase("monsterspawn")))
        {
            if (value.equalsIgnoreCase("deny"))
            {
                for (Entity en : world.getWorld().getEntities()) 
                {
                    if ((en instanceof Monster)) 
                    {
                        en.remove();
                    }
                }
                world.setFlagDenied(Iworld.WorldFlag.MONSTER);
                sender.sendMessage(formatMsg.format("<gray>Monster for " + world.getWorld().getName() + " set to " + value));
            }
            else if (value.equalsIgnoreCase("allow"))
            {
                world.setFlagAllowed(Iworld.WorldFlag.MONSTER);
                sender.sendMessage(formatMsg.format("<gray>Flag monster for " + world.getWorld().getName() + " set to " + value));
            }
            else
            {
                sender.sendMessage(formatMsg.format("<aqua>Usage: /world flag <World> <Flag> <Allow/Deny>"));
                sender.sendMessage(formatMsg.format("<gray>       Flags: MONSTER,  ANIMAL,  PVP, GAMEMODE"));
            }
        }
        else if ((flag.equalsIgnoreCase("animal")) || (flag.equalsIgnoreCase("animalspawn")))
        {
            if (value.equalsIgnoreCase("deny"))
            {
                for (Entity en : world.getWorld().getEntities()) 
                {
                    if ((en instanceof Animals)) 
                    {
                        en.remove();
                    }
                }
                world.setFlagDenied(Iworld.WorldFlag.ANIMAL);
                sender.sendMessage(formatMsg.format("<gray>Flag animal for " + world.getWorld().getName() + " set to " + value));
            }
            else if (value.equalsIgnoreCase("allow"))
            {
                world.setFlagAllowed(Iworld.WorldFlag.ANIMAL);
                sender.sendMessage(formatMsg.format("<gray>Flag animal for " + world.getWorld().getName() + " set to " + value));
            }
            else
            {
                sender.sendMessage(formatMsg.format("<aqua>Usage: /world flag <World> <Flag> <Allow/Deny>"));
                sender.sendMessage(formatMsg.format("<gray>       Flags: MONSTER,  ANIMAL,  PVP, GAMEMODE"));
            }
        }
        else if (flag.equalsIgnoreCase("pvp"))
        {
            if (value.equalsIgnoreCase("deny"))
            {
                world.setFlagDenied(Iworld.WorldFlag.PVP);
                sender.sendMessage(formatMsg.format("<gray>Flag pvp for " + world.getWorld().getName() + " set to " + value));
            }
            else if (value.equalsIgnoreCase("allow"))
            {
                world.setFlagAllowed(Iworld.WorldFlag.PVP);
                sender.sendMessage(formatMsg.format("<gray>Flag pvp for " + world.getWorld().getName() + " set to " + value));
            }
            else
            {
                sender.sendMessage(formatMsg.format("<aqua>Usage: /world flag <World> <Flag> <Allow/Deny>"));
                sender.sendMessage(formatMsg.format("<gray>       Flags: MONSTER,  ANIMAL,  PVP, GAMEMODE"));
            }
        }
        else
        {
            sender.sendMessage(formatMsg.format("<aqua>Usage: /world flag <World> <Flag> <Allow/Deny>"));
            sender.sendMessage(formatMsg.format("<gray>       Flags: MONSTER,  ANIMAL,  PVP, GAMEMODE"));
        }
    }
        
    public static boolean isNumber(String check)
    {
        try
        {
            Integer.parseInt(check);
            return true;
        }
        catch (NumberFormatException e) {}
        return false;
    }
    
    public static boolean isAlphaNumeric(String s)
    {
        String pattern = "[a-zA-Z0-9]";
        if (s.matches(pattern)) 
        {
            return true;
        }
        return false;
    }
    
    public static void copyFolder(File src, File dest)
    	throws IOException
    {
 
    	if(src.isDirectory())
        {
            if(!dest.exists())
            {
               dest.mkdir();
               System.out.println("Directory copied from " 
                          + src + "  to " + dest);
            }
            String files[] = src.list();

            for (String file : files) 
            {
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               copyFolder(srcFile,destFile);
            }
 
    	}else{
            OutputStream out;
                try 
                ( 
                    InputStream in = new FileInputStream(src)) {
                    out = new FileOutputStream(dest);
                    byte[] buffer = new byte[1024];
                    int length;
        
                    while ((length = in.read(buffer)) > 0){
                        out.write(buffer, 0, length);
                    }
                }
    	        out.close();
    	        System.out.println("IRIS: File copied from " + src + " to " + dest);
    	}
    }
  
}
