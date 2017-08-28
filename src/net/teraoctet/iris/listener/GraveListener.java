package net.teraoctet.iris.listener;

import static java.lang.Math.rint;
import java.util.Random;
import java.util.Set;
import net.teraoctet.iris.utils.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.parcelleManager;
import net.teraoctet.iris.utils.FormatMsg;
import net.teraoctet.iris.parcelle.Parcelle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;

public class GraveListener
  implements Listener
{
    private final Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
    private final FormatMsg formatMsg = new FormatMsg();
    private boolean graveCrypte = false;
    
    public GraveListener(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final Player player = event.getEntity();
        graveCrypte = false;
     
        if (player.hasPermission("iris.grave"))
        {
            Location graveLoc = player.getLocation();
            
            graveLoc.setY(rint(graveLoc.getY()));
            graveLoc = controlBlock(graveLoc);
            graveLoc = controlBlockBas(graveLoc);
            Location graveChest = graveLoc;
            
            if(graveCrypte == false)
            {
                Parcelle parcelle = parcelleManager.getParcelle(graveLoc);
                if (parcelle == null){
                    graveChest = graveChest.add(0, -2, 0);
                }
                Location graveChest2 = new Location(graveChest.getWorld(),graveChest.getBlockX()+1,graveChest.getBlockY(),graveChest.getBlockZ());
                                
                //player.sendMessage(graveChest.toString());
                //player.sendMessage(graveChest2.toString());
                                 
                player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "graveSpawn"),player));
                player.sendMessage(formatMsg.format("<aqua>Une tombe contenant ton stuff a été créé"));
                player.sendMessage(formatMsg.format("<yellow>position de ta tombe : xyz  " + graveLoc.getBlockX() + " " + graveLoc.getBlockY() + " " + + graveLoc.getBlockZ()));

                World world = player.getWorld();
                Inventory inventory = player.getInventory();
                ItemStack[] drops = inventory.getContents();

                event.getDrops().clear();
                String type = graveChest.getBlock().getType().name();
                world.getBlockAt(graveChest).setType(Material.CHEST);
                world.getBlockAt(graveChest2).setType(Material.CHEST);

                Chest chest = (Chest)world.getBlockAt(graveChest).getState();
                for (ItemStack itemStack : drops)
                {
                    if (itemStack != null) 
                    {
                        chest.getInventory().addItem(new ItemStack[] { itemStack });
                    }
                }
                String loc = graveChest.getWorld() + "-" + graveChest.getBlockX() + "-" + graveChest.getBlockY() + "-" + graveChest.getBlockZ();
                conf.setStringYAML("grave.yml",loc + ".block",type);
                
                ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                SkullMeta meta = (SkullMeta)is.getItemMeta();
                meta.setOwner(player.getName());
                is.setItemMeta(meta);
                                    
                double coef = 57.295779513082323D;
                ArmorStand as = (ArmorStand)graveLoc.add(0, 0, 0).getWorld().spawn(graveLoc.add(0, 0, 0), ArmorStand.class);
                as.setVisible(false);
                as.setCanPickupItems(false);
                as.setRemoveWhenFarAway(false);
                as.setArms(false);
                as.setBasePlate(false);
                as.setCustomName(ChatColor.DARK_PURPLE + "Ci git : " + player.getName());
                as.setCustomNameVisible(true);
                as.setGravity(false);
                as.setSmall(true);
                as.setInvulnerable(true);
                as.setCanPickupItems(false);
                as.setHelmet(is);
                as.setGlowing(false);
                //as.setItemInHand(new ItemStack(Material.WOOD_AXE, 1, (byte)0));
                //as.setRightArmPose(new EulerAngle(-40.0D / coef, 42.0D / coef, -17.0D / coef));
                as.setHeadPose(new EulerAngle(-40.0D / coef, 52.0D / coef, -17.0D / coef));
                as.setLeftLegPose(new EulerAngle(-40.0D / coef, 42.0D / coef, -17.0D / coef));
                
                this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        as.remove();
                    }
                }, 5000);
            
            }else{
                player.sendMessage(formatMsg.format(conf.getStringYAML("messages.yml", "graveSpawn"),player));
                player.sendMessage(formatMsg.format("<aqua>Ta tombe n'a pas peut être placé, les restes de ton corp on été mis dans la crypte"));
                player.sendMessage(formatMsg.format("<yellow>La crypte se trouve au lobby, court chercher ton stuff"));
                
                World world = graveLoc.getWorld();
                Inventory inventory = player.getInventory();
                ItemStack[] drops = inventory.getContents();
      
                event.getDrops().clear();
                Chest chest = (Chest)world.getBlockAt(graveLoc).getState();
                for (ItemStack itemStack : drops)
                {
                    if (itemStack != null) 
                    {
                        chest.getInventory().addItem(new ItemStack[] { itemStack });
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void CloseGrave(InventoryCloseEvent event) 
    {
        Player p = (Player)event.getPlayer();
        if(event.getInventory().getHolder() instanceof DoubleChest)
        {
            Location locChest = event.getInventory().getLocation();
            String locChestString = locChest.getWorld() + "-" + locChest.getBlockX() + "-" + locChest.getBlockY() + "-" + locChest.getBlockZ();
            if(conf.IsConfigYAML("grave.yml",locChestString))breakGrave(locChest,locChestString);
        }
    }
    
    private void breakGrave(Location locChest, String locChestString){
        locChest.getBlock().breakNaturally();
        Location loc = new Location(locChest.getWorld(),locChest.getBlockX()+1,locChest.getBlockY(),locChest.getBlockZ());
        loc.getBlock().breakNaturally();
        locChest.getBlock().getLocation().getWorld().playEffect(locChest.getBlock().getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
        Material mat = Material.getMaterial(conf.getStringYAML("grave.yml",locChestString + ".block","DIRT"));
        locChest.getBlock().setType(mat);
        loc.getBlock().setType(mat);
        conf.delNodeYAML("grave.yml",locChestString);
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
                                location = controlBlock(location);
                                return location;
                            }
                        }
                    }
                }
            }
        }
        return location;
    }
    
    public Location controlBlockBas(Location location)
    {                
        if(location.getY() < 0 || location.getBlock().getType().equals(Material.STATIONARY_LAVA)){
            graveCrypte = true;
            location = getCrypte();
            return location;
        }
        if (location.getBlock().getType().equals(Material.AIR) 
                || location.getBlock().getType().equals(Material.SNOW)
                || location.getBlock().getType().equals(Material.STATIONARY_WATER))
        { 
            location.setY(location.getY()-1);
            location = controlBlockBas(location);

            return location;
        }
        location.setY(location.getY()+1);
        return location;
    }
    
    public Location controlBlockRive(Location location)
    {                
        location = location.add(1, 0, 0);
        if (location.getBlock().getType().equals(Material.AIR) && location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) && location.add(0, 2, 0).getBlock().getType().equals(Material.AIR))
        {
            return location;
        }else{
            location = location.add(-1, 0, 1);
            if (location.getBlock().getType().equals(Material.AIR)&& location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) && location.add(0, 2, 0).getBlock().getType().equals(Material.AIR))
            {
                return location;
            }
            else
            {
                location = location.add(-1, 0, -1);
                if (location.getBlock().getType().equals(Material.AIR)&& location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) && location.add(0, 2, 0).getBlock().getType().equals(Material.AIR))
                {
                    return location;
                }
                else
                {
                    location = location.add(1, 0, -1);
                    if (location.getBlock().getType().equals(Material.AIR)&& location.add(0, 1, 0).getBlock().getType().equals(Material.AIR) && location.add(0, 2, 0).getBlock().getType().equals(Material.AIR))
                    {
                        return location;
                    }
                }
            }
        }
        return location;
    }
    
    public Location getCrypte()
    {
        int X = 0;
        int Y = 100;
        int Z = 0;
        String world = "azycko";
        
        Set<String> crypteList = conf.getKeysYAML("crypte.yml","crypte");
        if(!crypteList.isEmpty())
        {
            int count = crypteList.size();
            Random rand = new Random();
            int pos = rand.nextInt(count - 1 + 1) + 1;

            X = conf.getIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".X",0);
            Y = conf.getIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Y",0);
            Z = conf.getIntYAML("crypte.yml","crypte." + String.valueOf(pos) +".Z",0);
            world = conf.getStringYAML("crypte.yml","crypte." + String.valueOf(pos) + ".world","azycko");
        }
      
        World worldInstance = Bukkit.getWorld(world);
        Location spawnCrypte = new Location(worldInstance, X, Y, Z);
        return spawnCrypte;
    }
}
