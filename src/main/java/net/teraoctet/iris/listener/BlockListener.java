package net.teraoctet.iris.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.chunkManager;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.hordeManager;
import static net.teraoctet.iris.Iris.parcelleManager;
import net.teraoctet.iris.Sapling;
import net.teraoctet.iris.commands.Command_Login;
import net.teraoctet.iris.horde.HChunk;
import net.teraoctet.iris.horde.Horde;
import net.teraoctet.iris.parcelle.Parcelle;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockListener 
implements Listener 
{
    public int task;
    private final Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
    public static List<Sapling> saplings;
    public static ConcurrentHashMap<Item, Sapling> liveSaplings;
        
    public BlockListener(Iris instance) {
        plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockInterditPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if (conf.getBooleanYAML("config.yml", "enableLogin", false))
        {
            if (!Command_Login.Global.PlayersMove.contains(player.getUniqueId()))
            {
                if (Command_Login.Global.PlayersToRegister.contains(player.getUniqueId()))
                {
                      player.sendMessage(formatMsg.format("<light_purple>Vous n'<e_cir>tes pas encore entregistr<e_ai> !"));
                      event.setCancelled(true);
                }
                else if (Command_Login.Global.PlayersToUnlock.contains(player.getUniqueId()))
                {
                    player.sendMessage(formatMsg.format("<light_purple>Je crois que tu as oubli<e_ai> de te logger ! <smile>"));
                    event.setCancelled(true);
                }
                event.setCancelled(true);
            }
        }
        
        if (player.hasPermission("iris.horde")) 
        {
            if(event.getBlock().getType() == Material.ENDER_CHEST)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(formatMsg.format("<yellow>le bloc <green> " + event.getBlock().getType().name() + " <yellow>est interdit!!! :/ "));
            }      
            return;
        }

        plugin.getConfig().getList("BlocInterdit");
        List<Integer> list = plugin.getConfig().getIntegerList("BlocInterdit");
        for (int s : list)
        {
            Parcelle parcelle = parcelleManager.getParcelle(event.getBlock().getLocation());
            if (parcelle != null)
            {
                if(parcelle.getNoTNT() == 0)
                {
                    return;
                }
            }
            if(event.getBlock().getTypeId()== s)
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(formatMsg.format("<yellow>le bloc <green> " + event.getBlock().getType().name() + " <yellow>est interdit!!! :/ "));
            }      
        }
    }
    
    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent e)
    {
        if (((e.getEntity() instanceof Enderman)) && ((conf.getBooleanYAML("config.yml","Degats.Enderman",true) == true)))
        {
            e.setCancelled(true);
        }
        else if (((e.getEntity() instanceof Silverfish)) && ((conf.getBooleanYAML("config.yml","Degats.Silverfish",false) == true)))
        {
            e.setCancelled(true);
        }
        else if (((e.getEntity() instanceof Sheep)) && ((conf.getBooleanYAML("config.yml","Degats.Sheep",false) == true)))
        {
            e.setCancelled(true);
            ((Sheep)e.getEntity()).setSheared(false);
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onExplosionS(EntityExplodeEvent e)
    {       
        Parcelle p = parcelleManager.getParcelle(e.getLocation());
        if (p != null)
        {
            if(p.getNoTNT() == 0)
            {
                return;
            } 
        }
            
        String WorldPVP = conf.getStringYAML("config.yml", "HordePVP", "NULL");
        
        if (!WorldPVP.contains("NULL"))
        {
            if (e.getEntity().getWorld().getName().equalsIgnoreCase(WorldPVP))
            {
                boolean c = false;
                ArrayList<Block>blocks = new ArrayList();
                List<String> matBreak = conf.getListYAML("config.yml", "HordeBlockBreakTNT");
                Location TNTloc = e.getLocation();
                long radius = 5L;
                radius *= radius;

                for(Block b : e.blockList())
                {
                    HChunk chunk = chunkManager.getChunk(b.getLocation());
                    if (chunk != null)
                    {
                        Horde horde = chunkManager.getHorde(b.getLocation().getChunk());
                        if(hordeManager.MembersHasOnline(horde) == true)
                        {
                            if (chunk.getTypeMember() == 1)
                            {
                                for (String mat : matBreak)
                                {            
                                    if(b.getLocation().distance(TNTloc) < radius && !chunkManager.hasQG(TNTloc))blocks.add(b);
                                    if(b.getType() == Material.getMaterial(mat))blocks.add(b);
                                }
                            }
                            else
                            {
                                blocks.add(b);
                            }
                        }
                        else
                        {
                            c = true;
                        }
                    }
                    else
                    {
                        blocks.add(b);
                    }
                } 
                e.blockList().clear();
                for(Block b : blocks)
                {
                    e.blockList().add(b);
                }

                if(c == true)
                {
                    broadcastMessage(formatMsg.format("<gray>Les explosions sont neutralisées pour les hordes sans défenses"));
                }

                Parcelle parcelle = parcelleManager.getParcelle(e.getLocation());
                if (parcelle != null)
                {
                    e.blockList().clear();
                } 

                // Block étant exclu du dispersement après explosion
                ArrayList<Material> disallowedBlocks = new ArrayList<>();
                disallowedBlocks.clear();
                disallowedBlocks.add(Material.CHEST);
                disallowedBlocks.add(Material.DISPENSER);
                disallowedBlocks.add(Material.BURNING_FURNACE);
                disallowedBlocks.add(Material.PISTON_BASE);
                disallowedBlocks.add(Material.PISTON_EXTENSION);
                disallowedBlocks.add(Material.PISTON_MOVING_PIECE);
                disallowedBlocks.add(Material.PISTON_STICKY_BASE);
                disallowedBlocks.add(Material.TNT);

                if (e.isCancelled()){return;}
                if (e.blockList().isEmpty()){return;}
                e.setYield(0F);
                double x = 0;
                double y = 0;
                double z = 0;
                Location eLoc = e.getLocation();
                World w = eLoc.getWorld();

                for (int i = 0; i < e.blockList().size();i++)
                {
                        Block b = e.blockList().get(i);
                        Location bLoc =b.getLocation();
                        if (disallowedBlocks.contains(b.getType())){continue;}
                        x = bLoc.getX() - eLoc.getX();
                        y = bLoc.getY() - eLoc.getY() + .5;
                        z = bLoc.getZ() - eLoc.getZ();
                        FallingBlock fb = w.spawnFallingBlock(bLoc, b.getType(), (byte)b.getData());
                        fb.setDropItem(false);
                        fb.setVelocity(new Vector(x,y,z));
                }
                return;
            }
        }
                        
        if ((e.getEntity() instanceof TNTPrimed))
        {
            if (conf.getBooleanYAML("config.yml","Degats.TNT",true) == true)
            {
                e.blockList().clear();
            }
        }
        else if (conf.getBooleanYAML("config.yml","Degats.MobExplosion",true) == true)
        {
            e.blockList().clear();
        }
    }
    
    @EventHandler
    public void onPaintingBreak(HangingBreakEvent e)
    {
        String WorldPVP = conf.getStringYAML("config.yml", "HordePVP", "NULL");
        
        if (e.getEntity().getWorld().getName().equalsIgnoreCase(WorldPVP)) return;
        
        if ((e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) && ((e.getEntity() instanceof TNTPrimed)))
        {
            if (conf.getBooleanYAML("config.yml","Degats.TNT",true) == true)
            {
                e.setCancelled(true);
            }
        }
        else if ((e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) && (conf.getBooleanYAML("config.yml","Degats.MobExplosion",true) == true))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityForm(EntityBlockFormEvent e)
    {
        if (conf.getBooleanYAML("config.yml","Degats.SnowMan",true) == true)
        {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerBuild(EntityBlockFormEvent e)
    {
        if (conf.getBooleanYAML("config.yml","Degats.SnowMan",true) == true)
        {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if (conf.getBooleanYAML("config.yml", "enableLogin", false))
        {
            if (!Command_Login.Global.PlayersMove.contains(player.getUniqueId()))
            {
                if (Command_Login.Global.PlayersToRegister.contains(player.getUniqueId()))
                {
                      player.sendMessage(formatMsg.format("<light_purple>Vous n'<e_cir>tes pas encore entregistr<e_ai> !"));
                      Command_Login.Global.MessageSend.add(player.getUniqueId());
                      event.setCancelled(true);
                }
                else if ((Command_Login.Global.PlayersToUnlock.contains(player.getUniqueId())) && 
                  (!Command_Login.Global.MessageSend.contains(player.getUniqueId())))
                {
                    player.sendMessage(formatMsg.format("<light_purple>Je crois que tu as oubli<e_ai> de te logger ! <smile>"));
                    Command_Login.Global.MessageSend.add(player.getUniqueId());
                    event.setCancelled(true);
                }
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void SaveonBlockBreak(BlockBreakEvent event)
    {
        if(conf.getBooleanYAML("config.yml", "EnableMouchard", true) == true)
        {
            
            Player player = event.getPlayer();
            
            
            List<Integer> list = conf.getListIntYAML("config.yml", "Mouchard");
            for (int s : list)
            {
                if(event.getBlock().getTypeId()== s)
                {
                    String sql = "INSERT INTO irismouchard (playerName, adresseIP, blockBreak, location)" + 
                    "VALUES ('" + player.getDisplayName() + "','" + player.getAddress() + "'," +
                    event.getBlock().getTypeId() + ",'" + event.getBlock().getLocation().toString() + "'); ";
                    ConMySQL.executeInsert(sql);
                }      
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    private void onSpawnerPlace(final BlockPlaceEvent event)
    {
        if (event.getBlock().getType() == Material.MOB_SPAWNER)
        {
            if (event.getItemInHand().hasItemMeta() == false) return;
            final String spawnerName = event.getItemInHand().getItemMeta().getLore().get(0);
            if (spawnerName == null) return;
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    CreatureSpawner s = (CreatureSpawner)event.getBlock().getState();
                    EntityType type = EntityType.valueOf(spawnerName.toUpperCase());
                    s.setSpawnedType(type);
                }
            }, 0L);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTplace(BlockPlaceEvent event) 
    {
        Player player = event.getPlayer();
                
        if (player.hasPermission("iris.horde")) 
        {
            if(event.getBlock().getType() == Material.TNT)
            {
                Location locTNT = event.getBlockPlaced().getLocation();
                World w = locTNT.getWorld();
                int X = locTNT.getBlockX();
                int Y = locTNT.getBlockY();
                int Z = locTNT.getBlockZ();
                
                Location Ly2 = new Location(w, X, Y + 2, Z);
                Location Ly1 = new Location(w, X, Y + 1, Z);
                Location Ly_1 = new Location(w, X, Y - 1, Z);
                Location Ly_2 = new Location(w, X, Y - 2, Z);
                
                if(Ly2.getBlock().getType() == Material.TNT && Ly1.getBlock().getType() == Material.TNT)
                {
                    player.sendMessage(formatMsg.format("<yellow>L'empillage de TNT est interdit!!!"));
                    event.setCancelled(true);
                    return;
                }
                
                if(Ly1.getBlock().getType() == Material.TNT && Ly_1.getBlock().getType() == Material.TNT)
                {
                    player.sendMessage(formatMsg.format("<yellow>L'empillage de TNT est interdit!!!"));
                    event.setCancelled(true);
                    return;
                }
                
                if(Ly_2.getBlock().getType() == Material.TNT && Ly_1.getBlock().getType() == Material.TNT)
                {
                    player.sendMessage(formatMsg.format("<yellow>L'empillage de TNT est interdit!!!"));
                    event.setCancelled(true);
                }  
            }      
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event)
    {
        if(event.getLine(0).equalsIgnoreCase("TROC"))event.setLine(0, "[TROC]");
        if(event.getLine(0).equalsIgnoreCase("TRO"))event.setLine(0, "[TROC]");
        if(event.getLine(0).equalsIgnoreCase("TR"))event.setLine(0, "[TROC]");
        if(event.getLine(0).equalsIgnoreCase("T"))event.setLine(0, "[TROC]");
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void pyromane(BlockIgniteEvent event)
    {
        if (event.getCause() == IgniteCause.FLINT_AND_STEEL)
        {
            if (event.getIgnitingEntity().getType() == EntityType.PLAYER)
            {
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<aqua>[Iris] PYRO :" + event.getIgnitingEntity().getUniqueId().toString() + "-" + event.getIgnitingEntity().getLocation().toString()));
                conf.setStringYAML("player.yml", "PYRO",event.getIgnitingEntity().getUniqueId().toString() , event.getIgnitingEntity().getLocation().toString());
            }
        }
    }
        
    @EventHandler(priority=EventPriority.HIGH)
    public void onSaplingDrop(ItemSpawnEvent event)
    {
        if ((event != null) && (event.getEntity() != null) && (event.getEntity().getItemStack() != null))
        {
            for (Sapling stack : saplings) 
            {
                if ((event.getEntity().getItemStack().getType() == stack.item) && (event.getEntity().getItemStack().getDurability() == stack.itemMeta))
                {
                    liveSaplings.put(event.getEntity(), stack);
                    break;
                }
            }
        }
    }
    
    public void reforestation()
    {   
        liveSaplings = new ConcurrentHashMap();
        List<Sapling> saplingList = new ArrayList();
        
        saplingList.add(new Sapling(Material.SAPLING, (short)0, Material.SAPLING, (byte)0));
        saplingList.add(new Sapling(Material.SAPLING, (short)1, Material.SAPLING, (byte)1));
        saplingList.add(new Sapling(Material.SAPLING, (short)2, Material.SAPLING, (byte)2));
        saplingList.add(new Sapling(Material.SAPLING, (short)3, Material.SAPLING, (byte)3));
        saplingList.add(new Sapling(Material.SAPLING, (short)4, Material.SAPLING, (byte)4));
        saplingList.add(new Sapling(Material.SAPLING, (short)5, Material.SAPLING, (byte)5));
                
        saplings = saplingList;
        
        task = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                if (!liveSaplings.isEmpty()) 
                {
                    try
                    {
                        checkSapling();
                    }
                    catch (Exception e)
                    {
                        Iris.log.warning("[IRIS] : erreur reforestation");
                    }
                }
            }
        }, 60L, 200L);
    }
      
    public void checkSapling()
    {
        Iterator<Map.Entry<Item, Sapling>> i = liveSaplings.entrySet().iterator();
        while (i.hasNext())
        {
            Map.Entry<Item, Sapling> entry = (Map.Entry)i.next();
            Item item = (Item)entry.getKey();
            Sapling stack = (Sapling)entry.getValue();
            if ((stack == null) || (item == null) || (item.getItemStack().getAmount() <= 0) || (!item.isValid()))
            {
                i.remove();
            }
            else
            {
                int xCoord = item.getLocation().getBlockX();
                int yCoord = item.getLocation().getBlockY();
                int zCoord = item.getLocation().getBlockZ();

                Block block = item.getWorld().getBlockAt(item.getLocation());
                Material blockMaterial = block == null ? null : block.getType();

                Block lowerBlock = item.getWorld().getBlockAt(xCoord, yCoord - 1, zCoord);
                
                if ((block != null) && (blockMaterial == Material.AIR) && (lowerBlock != null) && (!lowerBlock.isEmpty()) && 
                        (lowerBlock.getType() == Material.DIRT || lowerBlock.getType() == Material.GRASS || lowerBlock.getType() == Material.SOIL))
                {
                    if (item.getWorld() != null) 
                    {
                        block.setType(stack.block);  
                        block.setData(stack.blockMeta, true);
                        
                        ItemStack itemStack = item.getItemStack();
                        if (itemStack.getAmount() > 1)
                        {
                            itemStack.setAmount(itemStack.getAmount() - 1);   
                            item.setItemStack(itemStack);
                        }
                        else
                        {
                            item.remove();
                        }
                    }  
                    i.remove();    
                }
            }
        }
    }
}
