package net.teraoctet.iris.parcelle;

import java.sql.SQLException;
import java.util.UUID;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.horde.HChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class ParcelleListener
implements Listener 
{    
    Iris plugin;
        
    public ParcelleListener(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority=EventPriority.HIGH)
    @SuppressWarnings("empty-statement")
    public void playerMoved(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location fromLoc = event.getFrom();
        Location toLoc = event.getTo();
        
        Parcelle parcelle = parcelleManager.getParcelle(toLoc);
        Parcelle jail = parcelleManager.getParcelle(fromLoc,true);
                
        if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
        {
            if(player.isFlying() && parcelle.getNoFly()==1 && !player.isOp()) 
            {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.sendMessage(formatMsg.format("<light_purple>Cette terre ne peut être survolée"));
            }
            if(parcelleManager.getParcelle(fromLoc) == null)
            {
                player.sendMessage(formatMsg.format(parcelle.getMessage(),player));
                if(parcelle.getNoEnter() == 1 && !player.hasPermission("iris.parcelle.enter"))
                {
                    player.teleport(fromLoc);
                    player.sendMessage(formatMsg.format("<light_purple>Vous n'avez pas la permission de rentrer ici"));
                    return;
                }
            }
            
        }
        
        if(jail == null && parcelleManager.getParcelle(toLoc,true) != null)
        {
            player.sendMessage(formatMsg.format(parcelleManager.getParcelle(toLoc,true).getMessage(),player));
        }
        
        if(parcelleManager.getParcelle(toLoc,true) == null && jail != null)
        {
            if(!player.isOp() && !"null".equals(conf.getStringYAML("userdata",player.getUniqueId().toString() + ".yml", "jail","null")))
            {
                player.teleport(fromLoc);
                player.sendMessage(formatMsg.format("<light_purple>Vous êtes emprisonné, vous ne pouvez pas sortir d'ici"));
                return;
            }
        }
            
        if (fromLoc.getChunk() != toLoc.getChunk())
        {
            HChunk chunk = chunkManager.getChunk(toLoc);
            if (chunk != null)
            {
                if(!chunk.getHordeName().equals(hordeManager.getHordeName(player)))
                {
                    if(player.isFlying()) 
                    {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                    if(!"NULL".equals(chunk.getMessage())) player.sendMessage(formatMsg.format(chunk.getMessage()));
                }
                else
                {             
                    player.sendMessage(formatMsg.format("<gray>=="));
                }
            }
        }
        else
        {
            
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    @SuppressWarnings("empty-statement")
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Block blk = event.getClickedBlock();
        Location loc = blk.getLocation();
    
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign)
        {
            Sign sign = (Sign) event.getClickedBlock().getState();
            //player.sendMessage(sign.getLine(0));
            if (sign.getLine(0).contains(formatMsg.format("<gras>PARCELLE")))
            {
                int cout =  Integer.parseInt(sign.getLine(2).substring(6));
                ItemStack a = new ItemStack(Material.EMERALD, cout);
                if(player.getInventory().contains(Material.EMERALD,cout))
                {
                    Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
                    if (!parcelle.getuuidOwner().contains("NULL"))
                    {
                        player.getInventory().removeItem(a);
                        player.updateInventory();
                        conf.setIntYAML("userdata",parcelle.getuuidOwner() + ".yml", "banque", cout);
                        
                        Player vendeur = Bukkit.getPlayer(UUID.fromString(parcelle.getuuidOwner()));
                        vendeur.sendMessage(formatMsg.format("<gras><star><yellow><player> vient d'acheter votre parcelle",player));
                        vendeur.sendMessage(formatMsg.format("<green>" + cout + " emeraudes ont <e_ai>t<e_ai> ajout<e_ai> <a_gr> votre compte"));
                        vendeur.sendMessage(formatMsg.format("<italique><yellow>/bank <green>pour consulter votre compte"));
                    }
                                        
                    String sqlMember = "UPDATE irisparcellemember SET playerUUID = '" + player.getUniqueId().toString()+ "' "
                                 + "WHERE parcelleName = '" + parcelle.getName() + "'; ";

                    ConMySQL.executeInsert(sqlMember);
                    parcelleManager.reload();
                    
                   player.sendMessage(formatMsg.format("<aqua>Vous <e_cir>tes maintenant propi<e_ai>taire de ce bien !"));
                   event.getClickedBlock().setType(Material.AIR);  
                   return;
                } 
                else
                {
                    player.sendMessage(formatMsg.format("<aqua>Vous n'avez pas assez d'<e_ai>meraudes pour la transaction, vous devez les avoir dans votre inventaire"));
                }
            }
        }
        Parcelle parcelle = parcelleManager.getParcelle(loc);
        if (parcelle != null && player.getItemInHand().getTypeId() == 269)
        {
            player.sendMessage(formatMsg.format("<dark_red>Vous êtes sur une parcelle protégée : " + parcelle.getName()));
            player.sendMessage(formatMsg.format("<dark_gray>Propriétaire : " + parcelleManager.getParcelleOwnerName(parcelle)));
            player.sendMessage(formatMsg.format("<dark_gray>Habitant : " + parcelleManager.getParcelleAllowedName(parcelle)));
        }
        if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && parcelle.getNoInteract()==1 && !player.isOp())
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle est prot<e_ai>g<e_ai>e par un sort magique"));
            event.setCancelled(true);
            return;
        }
        if (player.getItemInHand().getTypeId() == 269)
        {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
                
            {
                ParcelleManager parcelleManager = ParcelleManager.getSett(player);
                parcelleManager.setBorder(2, loc);
                player.sendMessage(formatMsg.format("<green>Parcelle angle2 : <white>" + String.format("%d %d %d", new Object[] { loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() })));
                event.setCancelled(true);
            }
        } 
        if (player.getItemInHand().getTypeId() == 269)
        {
            if(event.getAction() == Action.LEFT_CLICK_BLOCK) 
                
            {
                ParcelleManager parcelleManager = ParcelleManager.getSett(player);
                parcelleManager.setBorder(1, loc);
                player.sendMessage(formatMsg.format("<green>Parcelle angle1 : <white>" + String.format("%d %d %d", new Object[] { loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() })));
                event.setCancelled(true);
            }
        }
    }
            
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) throws SQLException
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Parcelle parcelle = parcelleManager.getParcelle(block.getLocation());
        if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && parcelle.getNoBreak()==1 && !player.isOp())
        {
            event.setCancelled(true);
            player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas casser de bloc ici, cette parcelle est prot<e_ai>g<e_ai>"));
            return;
        } 
        
        HChunk chunk = chunkManager.getChunk(block.getLocation());
        if (chunk != null)
        {
            if(chunk.getHordeName().equals(hordeManager.getHordeName(player)) || 
                    (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) == 1 &&
                    conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) == 1))
            
            {
                switch (chunk.getTypeMember())
                {
                    case 1:
                        if (event.getBlock().getType() == Material.DIAMOND_BLOCK || event.getBlock().getType() == Material.BEACON)
                        {
                            player.sendMessage(formatMsg.format("<gray>Tu ne pas détruire l'Autel!"));
                            event.setCancelled(true);
                            return;
                        }
                        
                        if(hordeManager.getGrade(player) > 2)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de casser ce bloc"));
                        }
                        else
                        {
                            return;
                        }
                    case 2:
                        if(hordeManager.getGrade(player) > 2)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de casser ce bloc"));
                        }
                        else
                        {
                            return;
                        }
                    case 3:
                        if(hordeManager.getGrade(player) > 3)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de casser ce bloc"));
                        }
                        else
                        {
                        }
                    default:
                }
            }
            else
            {
                if (chunk.getTypeMember() == 1)
                {
                    if (event.getBlock().getType() == Material.DIAMOND_BLOCK || event.getBlock().getType() == Material.BEACON)
                    {
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getBlock().getTypeId() == 65 || event.getBlock().getTypeId() == 46 || event.getBlock().getTypeId() == 106) 
                    {
                        return;
                    }
                
                    Boolean breakBlock = chunkManager.BreakBlockQG(event.getBlock().getLocation(), chunk);
                    String perm = "iris.horde.message." + chunk.getHordeName();
                    org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<red>ALERTE : <gray>Des ennemis sont sur nos terres !"),perm);
                    player.sendMessage(formatMsg.format("<light_purple>Prends garde à toi ! tu es en territoire ennemi"));
                    event.setCancelled(breakBlock);
                    return;
                }
                if (event.getBlock().getTypeId() == 65 || event.getBlock().getTypeId() == 46 || event.getBlock().getTypeId() == 106) return;
                event.setCancelled(true);
                String perm = "iris.horde.message." + chunk.getHordeName();
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<red>ALERTE : <gray>Des ennemis sont sur nos terres !"),perm);
                player.sendMessage(formatMsg.format("<light_purple>Prends garde à toi ! tu es en territoire ennemi"));
            }
        }  
    }
  
    @EventHandler(priority=EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) throws SQLException
    {
        Player player = event.getPlayer();
        Block block = event.getBlock();
    
        Parcelle parcelle = parcelleManager.getParcelle(block.getLocation());        
        if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && parcelle.getNoBuild() == 1 && !player.isOp())
        {
            event.setCancelled(true);
            player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas construire ici, cette parcelle est prot<e_ai>g<e_ai>"));
            return;
        }
        
        HChunk chunk = chunkManager.getChunk(block.getLocation());
        if (chunk != null)
        {
            if (event.getBlock().getType() == Material.BEACON && chunk.getTypeMember() == 1) 
            {
                event.setCancelled(true);
                return;
            }
                            
            if(chunk.getHordeName().equals(hordeManager.getHordeName(player)) ||
                    (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) == 1 &&
                    conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) == 1))
            {
                switch (chunk.getTypeMember())
                {
                    case 1:
                        if(hordeManager.getGrade(player) > 2)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de construire ici"));
                        }
                        else
                        {
                            return;
                        }
                    case 2:
                        if(hordeManager.getGrade(player) > 2)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de construire ici"));
                        }
                        else
                        {
                            return;
                        }
                    case 3:
                        if(hordeManager.getGrade(player) > 3)
                        {
                            if (event.getBlock().getTypeId() == 65) return;
                            event.setCancelled(true);
                            player.sendMessage(formatMsg.format("<gray>Désolé ! ton grade ne te permet pas de construire ici"));
                        }
                        else
                        {
                        }
                    default:
                }
            }
            else
            {
                if (event.getBlock().getTypeId() == 65 || event.getBlock().getTypeId() == 46 || event.getBlock().getTypeId() == 106) return;
                event.setCancelled(true);
                String perm = "iris.horde.message." + chunk.getHordeName();
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<red>ALERTE : <gray>Des ennemis sont sur nos terres !"),perm);
                player.sendMessage(formatMsg.format("<light_purple>Prends garde à toi ! tu es en territoire ennemi"));
            }
        }
    }
  
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockBurn(BlockBurnEvent event)
    {
        Block block = event.getBlock();
        Material mat = block.getType();

        Parcelle parcelle = parcelleManager.getParcelle(block.getLocation());        
        if (parcelle != null && parcelle.getNoFire()==1)
        {
            event.setCancelled(true);
        }
        
        HChunk chunk = chunkManager.getChunk(block.getLocation());
        if (chunk != null)
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        from = new Location(from.getWorld(), from.getBlockX(), from.getBlockY(), from.getBlockZ(), from.getYaw(), from.getPitch());
        Location to = event.getTo();
        to = new Location(to.getWorld(), to.getBlockX(), to.getBlockY(), to.getBlockZ(), to.getYaw(), to.getPitch());

        Parcelle fparcelle = parcelleManager.getParcelle(from);
        Parcelle tparcelle = parcelleManager.getParcelle(to);
        if (tparcelle != null && !tparcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && tparcelle.getNoTeleport()==1 && !player.isOp())
        {
            player.sendMessage(formatMsg.format("<light_purple>TP interdit ici, cette parcelle est prot<e_ai>g<e_ai>"));
            event.setCancelled(true);
            return;
        }
        if (fparcelle != null && !fparcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && fparcelle.getNoTeleport()==1 && !player.isOp())
        {
            player.sendMessage(formatMsg.format("<light_purple>utilisation TP interdit ici, cette parcelle est prot<e_ai>g<e_ai>"));
            event.setCancelled(true);
            return;
        }
        if (tparcelle != null && !tparcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && tparcelle.getNoFly()==1 && !player.isOp())
        {
            player.setFlying(false);
        }
        if ((fparcelle != tparcelle) && (fparcelle != null) && !fparcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && fparcelle.getJail()==1 && !player.isOp())
        {
            player.sendMessage(formatMsg.format("<light_purple>JAIL"));
        }
        if (fparcelle != null && (player.getServer().getDefaultGameMode() != fparcelle.getGamemode()) && !player.isOp())
        {
            player.setGameMode(fparcelle.getGamemode());
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onAnimalDamage(EntityDamageByEntityEvent event) 
    {
        Player player;   
        Parcelle parcelle = parcelleManager.getParcelle(event.getEntity().getLocation());
        //HChunk chunk = chunkManager.getChunk(event.getEntity().getLocation());

        if (parcelle != null)
        {
            if (parcelle.getName().contains("|off|"))
            {
                return;
            }
            if(event.getEntity() instanceof org.bukkit.entity.Animals)
            {     

                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        event.setCancelled(true);
                        if (player.getHealth() > 0) player.setHealth(player.getHealth()-1);
                    }
                }

            }
            if(event.getEntity() instanceof org.bukkit.entity.Villager)
            {     

                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        event.setCancelled(true);

                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {  
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if(!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
                    {   
                        player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas tuer un animal sur une parcelle prot<e_ai>g<e_ai>"));
                        player.setHealth(player.getHealth()-1);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
        
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) 
    {
        Parcelle parcelle = parcelleManager.getParcelle(event.getEntity().getLocation());
        if (parcelle != null)
        {
            if (!parcelle.getName().contains("|protect|")) return;
            if (parcelle.getJail() == 1) return;
            if (event.getEntity() instanceof org.bukkit.entity.Player && event instanceof EntityDamageByEntityEvent) 
            {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

                if (damageEvent.getDamager() instanceof Player) 
                {
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
                if (damageEvent.getDamager() instanceof org.bukkit.entity.Zombie) 
                {	
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
                if (damageEvent.getDamager() instanceof org.bukkit.entity.Skeleton) 
                {	
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
                if (damageEvent.getDamager() instanceof org.bukkit.entity.Creeper) 
                {	
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
                if (damageEvent.getDamager() instanceof org.bukkit.entity.PigZombie) 
                {	
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
                if (damageEvent.getDamager() instanceof org.bukkit.entity.Spider) 
                {	
                    event.setCancelled(true); // cancel damage event by caused by other players
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent e)
    {
        try 
        {
            if(e.getDamager() instanceof org.bukkit.entity.Monster || e.getEntity() instanceof org.bukkit.entity.Monster)return;
            Player player = (Player) e.getDamager();
            if (chunkManager.ChunkExist(player.getLocation()))
            {
                HChunk chunk = chunkManager.getChunk(player.getLocation());
                if(!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                {
                    e.setDamage(e.getDamage()* 0.7);
                }
                else
                {
                    e.setDamage(e.getDamage()* hordeManager.getDamage(hordeManager.getHordeName(player)));
                }
            }
            else
            {
                if(player.hasPermission("iris.horde"))
                {
                    Player playerd = (Player)e.getEntity();
                    if(hordeManager.getHordeName(playerd).equals(hordeManager.getHordeName(player)) || 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + hordeManager.getHordeName(playerd) + ".enemy", 0) == 0 &&
                        conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(playerd) + "." + hordeManager.getHordeName(player) + ".enemy", 0) == 0))
                    {
                        e.setCancelled(true);
                    }
                }
            }
        } 
        catch(Exception ex) 
        {

        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onAnimalDamageChunk(EntityDamageByEntityEvent event) 
    {
        Player player;
        HChunk chunk = chunkManager.getChunk(event.getEntity().getLocation());

        if (chunk != null)
        {
            if(event.getEntity() instanceof org.bukkit.entity.Animals)
            {     

                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    if (((Projectile)event.getDamager()).getShooter() instanceof Player) 
                    {
                        player = (Player)((Projectile)event.getDamager()).getShooter();
                        if(player == null) return;
                        if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                            (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                            conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                        {
                            player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                            event.setCancelled(true);
                        }
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }

            }
            if(event.getEntity() instanceof org.bukkit.entity.Villager)
            {     

                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                    if(player == null) return;
                    if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if (!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && 
                        (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + chunk.getHordeName() + ".ally", 0) != 1 ||
                        conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.NORMAL)
    public void onAnimalDamageParcelle(EntityDamageByEntityEvent event) 
    {
        Player player;
        Parcelle parcelle = parcelleManager.getParcelle(event.getEntity().getLocation());

        if (parcelle != null)
        {
            if(event.getEntity() instanceof org.bukkit.entity.Animals)
            {     
                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;                    
                    if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    if (((Projectile)event.getDamager()).getShooter() instanceof Player) 
                    {
                        player = (Player)((Projectile)event.getDamager()).getShooter();
                        if(player == null) return;
                        if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                        {
                            player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                            event.setCancelled(true);
                        }
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer d'animaux ici, tu es sur un territoire ennemi"));
                        event.setCancelled(true);
                    }
                }

            }
            if(event.getEntity() instanceof org.bukkit.entity.Villager)
            {     

                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        player.setHealth(1);
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                    if(player == null) return;
                    if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        player.setHealth(1);
                        event.setCancelled(true);
                    }
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    if (!parcelle.getuuidAllowed().contains(player.getUniqueId().toString()))
                    {
                        player.sendMessage(formatMsg.format("<light_purple>Tu ne peux pas tuer de villageois ici, tu es sur un territoire ennemi"));
                        player.setHealth(1);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
        
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Parcelle parcelle = parcelleManager.getParcelle(event.getLocation());
        if (parcelle != null)
        {
            if (parcelle.getNoMob() == 1)
            {
                if(event.getEntity() instanceof org.bukkit.entity.Villager || event.getEntity() instanceof org.bukkit.entity.Animals 
                        || event.getEntity() instanceof org.bukkit.entity.PigZombie)
                {     
                
                }
                else
                {
                    event.setCancelled(true);
                }
            }
        } 
    }
    
    @EventHandler
    public void onBreakFrame(EntityDamageEvent event)
    {
        //Bukkit.getConsoleSender().sendMessage(event.getEntityType().name());
        //Bukkit.getConsoleSender().sendMessage(event.getCause().name());
    }
    
    @EventHandler
    public void onBreakFrame(EntityDamageByEntityEvent event)
    {
        //Bukkit.getConsoleSender().sendMessage(event.getDamager().getName());
        try
        {
            Player player = (Player) event.getDamager();
            Parcelle parcelle = parcelleManager.getParcelle(event.getEntity().getLocation());
            if (parcelle.getName().contains("|off|")) return;
            if (parcelle == null || parcelle.getOwner().contains(player.getUniqueId().toString()) || parcelle.getNoBreak() == 0 || player.isOp())
            {
                return;
            }
            if(event.getEntity() instanceof org.bukkit.entity.ItemFrame)
            {  
                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    event.setCancelled(true);
                }
                if(event.getDamager() instanceof org.bukkit.entity.Projectile)
                {
                    player = (Player)((Projectile)event.getDamager()).getShooter();
                    if(player == null) return;
                    event.setCancelled(true);
                }
                if(event.getDamager() instanceof org.bukkit.entity.Player)
                {
                    player = (Player)((Player)event.getDamager());
                    if(player == null) return;
                    event.setCancelled(true);
                }
                if(event.getDamager() instanceof org.bukkit.entity.Arrow)
                {
                    player = (Player)((Arrow)event.getDamager()).getShooter();
                    if(player == null) return;
                    event.setCancelled(true);
                }
            }  
        }
        catch(Exception ex){}
    }
                 
}
