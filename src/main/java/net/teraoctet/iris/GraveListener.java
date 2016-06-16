package net.teraoctet.iris;

import static java.lang.Math.rint;
import java.util.ArrayList;
import net.teraoctet.iris.utils.FormatMsg;
import net.teraoctet.iris.inventory.chest;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.createInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import static org.bukkit.Material.BEDROCK;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GraveListener
  implements Listener
{
    Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
    
    public GraveListener(Iris plugin)
    {
        this.plugin = plugin;
    }
  
    private final FormatMsg formatMsg = new FormatMsg();
    private String NomDuJoueur;
    public static ArrayList<chest> inventorys = new ArrayList<>();

    GraveListener() 
    {
        
    }
       
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final Player player = event.getEntity();
        NomDuJoueur = player.getName();
     
        if (player.hasPermission("iris.grave"))
        {
            /*  Création du panneaux */
            Location location = player.getLocation();
            location.setY(rint(location.getY()));
            location = controlBlock(location.add(0, -1, 0));
            World newBlock = location.getWorld();
            final Block nb = newBlock.getBlockAt(location);
            final Block nb2 = nb.getRelative(0, 1, 0);
            
            player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "graveSpawn"),player));
            player.sendMessage(formatMsg.format("<aqua>Trouve ta tombe represente par un block de bedrock et fait un click droit dessus pour reprendre ton stuff"));
            player.sendMessage(formatMsg.format("<yellow>position de ta tombe : xyz  " + nb.getLocation().getBlockX() + " " + nb.getLocation().getBlockY() + " " + + nb.getLocation().getBlockZ()));
            
            try
            {                
                nb.setType(Material.BEDROCK);            
                nb2.setType(Material.SIGN_POST);            
                String playerName = player.getName();
                Sign sign = (Sign)nb2.getState();

                String Line1 = formatMsg.format(conf.getStringYAML("messages.yml", "graveLine1"), player);
                String Line3 = formatMsg.format(conf.getStringYAML("messages.yml", "graveLine3"), player);
                String Line4 = formatMsg.format(conf.getStringYAML("messages.yml", "graveLine4"), player);
                sign.setLine(0, Line1);
                sign.setLine(1, playerName);
                sign.setLine(2, Line3);
                sign.setLine(3, Line4);
                sign.update();
                
                /* création inventaire */
                Inventory grave = createInventory(null, 54, "Tombe de " + NomDuJoueur);
                putInventoryInChests(player,grave);
                chest invent = new chest(grave,player.getName(),rint(nb.getX()),rint(nb.getZ()), rint(nb.getY()), nb.getWorld().getName());
                inventorys.add(invent);
                event.getDrops().clear(); 
            }
            catch(Exception ex)
            {
                player.sendMessage(formatMsg.format("<red>ERREUR <yellow>Votre tombe n'a pu <e_cir>tre plac<e_ai>"));
            }
                        
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    nb.getLocation().getBlock().setType(Material.AIR);
                    nb2.getLocation().getBlock().setType(Material.AIR);
                    player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "graveDespawn"),player));
                    if (GraveListener.inventorys.size() > 0)
                    {
                        inventorys.remove(0);
                    }
                }
            }, 10000);
        }
    }
       
    @EventHandler
    public void onGraveInteract(PlayerInteractEvent event)
    {
        final Player player = (Player) event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(BEDROCK))
        {
            if (player.hasPermission("iris.grave"))
            {
                Block b = event.getClickedBlock();
                for(chest grave : inventorys)
                {
                    if (grave.locationX == rint(b.getLocation().getBlockX()) && grave.locationZ == rint(b.getLocation().getBlockZ()))
                    {
                        player.openInventory(grave.grave);
                    }
                }               
            }
            else
            {
                player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "noPermission"), player));
            }
        }
     }
    
    public void putInventoryInChests(Player player, Inventory grave)
    {
        PlayerInventory players_inventory = player.getInventory();
        ItemStack[] items = players_inventory.getContents();
        for (ItemStack item : items) 
        {
            if (item != null) 
            {
                grave.addItem(new ItemStack[] { item });
            }
        }
        players_inventory.clear();
        player.getEquipment().clear();
    }
    
    public static void removeInventory()
    {
        for(chest grave : inventorys)
        {
            double X = grave.locationX;
            double Y = grave.locationY;
            double Z = grave.locationZ;
                
            World worldInstance = Bukkit.getWorld(grave.world);
            Location location = new Location(worldInstance, X, Y, Z);
            Block block = worldInstance.getBlockAt(location);
            block.setType(Material.AIR);
        }  
        inventorys.removeAll(inventorys);
    }
    
    public Location controlBlock(Location location)
    {                
        if (!location.getBlock().getType().equals(Material.AIR) 
                && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                && !location.getBlock().getType().equals(Material.SNOW))
        {
            location.setY(location.getY()+1);
            if (!location.getBlock().getType().equals(Material.AIR) 
                && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                && !location.getBlock().getType().equals(Material.SNOW))
            {
                return location;
            }
            else
            {
                location.setY(location.getY()-1);
                location.setX(location.getX()+1);
                if (!location.getBlock().getType().equals(Material.AIR) 
                    && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                    && !location.getBlock().getType().equals(Material.SNOW))
                {
                    return location;
                }
                else
                {
                    location.setX(location.getX()-1);
                    location.setZ(location.getZ()+1);
                    if (!location.getBlock().getType().equals(Material.AIR) 
                        && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                        && !location.getBlock().getType().equals(Material.SNOW))
                    {
                        return location;
                    }
                    else
                    {
                        location.setX(location.getX()-1);
                        location.setZ(location.getZ()-1);
                        if (!location.getBlock().getType().equals(Material.AIR) 
                            && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                            && !location.getBlock().getType().equals(Material.SNOW))
                        {
                            return location;
                        }
                        else
                        {
                            location.setX(location.getX()+1);
                            location.setZ(location.getZ()-1);
                            if (!location.getBlock().getType().equals(Material.AIR) 
                                && !location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) 
                                && !location.getBlock().getType().equals(Material.SNOW))
                            {
                                return location;
                            }
                            else
                            {
                                location.setY(location.getY()+1);
                                location.setZ(location.getZ()+1);
                                controlBlock(location);
                                return location;
                            }
                        }
                    }
                }
            }
        }
        return location;
    }
}
