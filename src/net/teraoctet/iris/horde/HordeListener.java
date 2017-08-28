package net.teraoctet.iris.horde;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.chunkManager;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.hordeManager;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class HordeListener implements Listener 
{
    private final Iris plugin;
    private final HashMap<Player, Player> compassTargets;
  
    public HordeListener(Iris plugin)
    {
        this.plugin = plugin;
        this.compassTargets = new HashMap();
    }
    
    @EventHandler
    public void onPlayerInteractTracker(PlayerInteractEvent e)
    {
        Player ply = e.getPlayer();
        Player targ = null;
                       
        if (((e.getAction().equals(Action.RIGHT_CLICK_AIR)) || (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) 
                && (ply.getItemInHand().getType().equals(Material.COMPASS)))
        {
            if(!ply.getItemInHand().hasItemMeta())
            {
                this.compassTargets.remove(ply);
                return;
            }
            ItemMeta Tracker = ply.getItemInHand().getItemMeta();
            if (!Tracker.getDisplayName().contains("§c§lTRACKER"))
            {
                this.compassTargets.remove(ply);
                return;
            }
            
            if (!ply.hasPermission("iris.tracker") || ply.isOp()) 
            {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) 
            {
                if ((ply.getWorld() == player.getWorld()) && 
                (ply != player) && 
                ((targ == null) || (ply.getLocation().distance(player.getLocation()) < targ.getLocation().distance(ply.getLocation()))) && 
                (player.getLocation().distance(ply.getLocation()) < 200.0D)) {
                targ = player;
              }
            }
            if (targ == null)
            {
                ply.sendMessage(formatMsg.format("<gray>Pas d'ennemi proche !"));
                return;
            }
            ply.sendMessage(formatMsg.format("<gray>Le radar à identifié <gold>" + targ.getName() + "!"));
            this.compassTargets.put(ply, targ);
        }
        else if (((e.getAction().equals(Action.LEFT_CLICK_AIR)) || (e.getAction().equals(Action.LEFT_CLICK_BLOCK))) && (ply.getItemInHand().getType().equals(Material.COMPASS)))
        {
            if(!e.getPlayer().getItemInHand().hasItemMeta())
            {
                this.compassTargets.remove(e.getPlayer());
                return;
            }
            ItemMeta Tracker = e.getPlayer().getItemInHand().getItemMeta();
            if (!Tracker.getDisplayName().contains("§c§lTRACKER"))
            {
                this.compassTargets.remove(e.getPlayer()    );
                return;
            }
            
            if (!ply.hasPermission("iris.tracker") || ply.isOp()) 
            {
                return;
            }
            if (this.compassTargets.get(ply) != null) 
            {
                int distance = (int)ply.getLocation().distance(((Player)this.compassTargets.get(ply)).getLocation());
                ply.sendMessage(formatMsg.format("<gray>Ennemi potentiel identifé à " + distance + " blocks environ"));
            } 
            else 
            {
                ply.sendMessage(formatMsg.format("<gray>Votre boussole n'a rien repéré!"));
            }
        }
    }

    @EventHandler
    public void onTrackerMove(PlayerMoveEvent e)
    {
        if(!e.getPlayer().getItemInHand().getType().equals(Material.COMPASS))return;
        if(!e.getPlayer().getItemInHand().hasItemMeta())
        {
            this.compassTargets.remove(e.getPlayer());
            return;
        }
        ItemMeta Tracker = e.getPlayer().getItemInHand().getItemMeta();
        if (!Tracker.getDisplayName().contains("§c§lTRACKER"))
        {
            this.compassTargets.remove(e.getPlayer()    );
            return;
        }
            
        if (this.compassTargets.get(e.getPlayer()) != null) 
        {
            e.getPlayer().setCompassTarget(((Player)this.compassTargets.get(e.getPlayer())).getLocation());
        }
        for (Player ply : Bukkit.getOnlinePlayers())
        {
            Player compassTarget = (Player)this.compassTargets.get(ply);
            if (compassTarget == e.getPlayer()) 
            {
                if (ply.getWorld() == e.getPlayer().getWorld())
                {
                    if (ply.getLocation().distance(e.getPlayer().getLocation()) >= 200.0D)
                    {
                        ply.sendMessage(formatMsg.format("<gray>Ennemi potentiel hors de portée"));
                        this.compassTargets.remove(ply);
                    }
                    ply.setCompassTarget(e.getPlayer().getLocation());
                }
                else
                {
                    ply.sendMessage(formatMsg.format("<gray>L'ennemi potentiel a changé de monde, et il n'est plus traçable"));
                    this.compassTargets.remove(ply);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropHead(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        if(event.getItemDrop().getItemStack().getType() == Material.SKULL_ITEM)
        {            
            if(chunkManager.ChunkExist(event.getItemDrop().getLocation()))
            {
                ItemStack skull;
                skull = event.getItemDrop().getItemStack();
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                try
                {
                    if(!skull.hasItemMeta())return;
                    
                    String[] head = meta.getOwner().split("#");
                    //Player playerHead = Bukkit.getPlayer(head[0]);
                    String hordeHead = hordeManager.getHordeName(head[0]);
                    int force = Integer.valueOf(head[1]);
                    int forced = force + 1;

                    Horde horde = hordeManager.getHorde(player);
                    String hordePlayer = horde.getHordeName();
                    
                    if(!"NULL".equals(conf.getStringYAML("horde.yml", "Horde." + horde.getHordeName() + ".Autel", "NULL")))
                    {
                        player.sendMessage(formatMsg.format("<gray>Tu doit retrouver la flamme de l'Autel pour invoquer les dieux !"));
                        return;
                    }
                    else
                    {                        
                        if(!hordeHead.contains(hordePlayer) && chunkManager.hasQG(player) &&
                            (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + hordeHead + ".ally", 0) != 1 &&
                            conf.getIntYAML("horde.yml", "Horde." + hordeHead + "." + hordeManager.getHordeName(player) + ".ally", 0) != 1))
                        {
                            player.sendMessage(formatMsg.format("<yellow>**** Victoire :"));
                            player.sendMessage(formatMsg.format(hordePlayer + "<gray>: +" + force + " Forces"));
                            player.sendMessage(formatMsg.format(hordeHead + "<gray>: -" + forced + " Forces"));

                            int forceWin = conf.getIntYAML("horde.yml", "Horde." + hordePlayer + ".Force",0) + force;
                            conf.setIntYAML("horde.yml", "Horde." + hordePlayer + ".Force", forceWin);

                            int forceLos = conf.getIntYAML("horde.yml", "Horde." + hordeHead + ".Force",0) - forced;
                            conf.setIntYAML("horde.yml", "Horde." + hordeHead + ".Force", forceLos);

                            ItemStack os = new ItemStack(Material.BONE, 3);
                            event.getItemDrop().setItemStack(os);
                            event.getItemDrop().getWorld().strikeLightning(horde.getSpawnHorde());
                        }
                        else if(hordeHead.contains(hordePlayer) && chunkManager.hasQG(player))
                        {
                            ItemStack os = new ItemStack(Material.BONE, 3);
                            event.getItemDrop().setItemStack(os);
                            event.getItemDrop().getWorld().strikeLightningEffect(horde.getSpawnHorde());
                        }
                    }
                }
                catch(NumberFormatException ex){
                 
                }
            }
        }
    }
        
    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerDead(PlayerDeathEvent event) throws SQLException
    {
        if(event.getEntityType() == EntityType.PLAYER)
        {
            Player playerDead = event.getEntity().getPlayer();

            if(playerDead.hasPermission("iris.horde.head") && !playerDead.isOp())
            {
                Player playerKiller = event.getEntity().getKiller();
                int force = 2;
                
                if(hordeManager.getGrade(playerDead) == 1)
                {
                    force = 3;
                }
                if(chunkManager.hasQG(playerDead))
                {
                    force = 5;
                }
                if(hordeManager.getGrade(playerDead) == 1 && chunkManager.hasQG(playerDead))
                {
                    force = 6;
                }
                if(!hordeManager.HasHorde(playerDead))
                {
                    force = 0;
                }
                
                dropHeadPlayer(playerDead, force);
                String hordeName = hordeManager.getHordeName(playerDead);

                if(event.getEntity().getKiller() instanceof org.bukkit.entity.Player)
                {
                    String hordeKiller = hordeManager.getHordeName(playerKiller);
                    int kill = conf.getIntYAML("horde.yml", "Horde." + hordeKiller + ".Kill",0) + 1;
                    conf.setIntYAML("horde.yml", "Horde." + hordeKiller + ".Kill", kill);
                    int dead = conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Dead",0) + 1;
                    conf.setIntYAML("horde.yml", "Horde." + hordeName + ".Dead", dead);
                }
                broadcastMessage(formatMsg.format("<red><star><gold> une tête de la horde " + hordeName + " vient de tomber !")); 
            }
            
            if(!"NULL".equals(conf.getStringYAML("userdata",playerDead.getUniqueId()+ ".yml", "Autel","NULL")))
            {
                Horde horde = hordeManager.getHorde(conf.getStringYAML("userdata",playerDead.getUniqueId()+ ".yml", "Autel","NULL"));
                conf.setStringYAML("horde.yml", "Horde." + horde.getHordeName() + ".Autel", "NULL");
                conf.setStringYAML("userdata",playerDead.getUniqueId()+ ".yml", "Autel","NULL");
                Location beacon = horde.getSpawnHorde();
                beacon.getBlock().setType(Material.BEACON);
                playerDead.sendMessage(formatMsg.format("<gray>Vous avez perdu la flamme de la horde " + horde.getHordeName()));
                String perm = "iris.horde.message." + horde.getHordeName();
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<yellow>L'ennemi qui a osé nous défier est mort, notre flamme est enfin revenu"),perm);
            }
        }
    }
    
    private void dropHeadPlayer(Player player, int force)
    {
        //String hordeName = hordeManager.getHordeName(player);
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        
        String tag = player.getDisplayName() + "#" + force;
        meta.setOwner(tag);
        skull.setItemMeta(meta);
        player.getLocation().getWorld().dropItem(player.getLocation(), skull);                
    } 
    
    @EventHandler
    public void onPlayerAutel(PlayerInteractEvent event)
    {

        if(event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            Player player = event.getPlayer();
            Block interactedBlock = event.getClickedBlock();
            if (interactedBlock == null) 
            {
                return;
            }
            
            Material blockMat = interactedBlock.getType();
            if (blockMat != Material.BEACON)
            {
                return;
            }
                        
            Horde horde = chunkManager.getHorde(interactedBlock.getChunk());
            if(hordeManager.MembersHasOnline(horde) == false)
            {
                player.sendMessage(formatMsg.format("<gray>Tu ne peux attaquer cette ennemi sans défense, attends qu'un joueur se connecte"));
                return;
            }
                        
            if(!hordeManager.HasHorde(player))return;
            
            if(horde.getHordeName().contains(hordeManager.getHordeName(player)) ||
                    (conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + "." + horde.getHordeName() + ".ally", 0) == 1 &&
                    conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + "." + hordeManager.getHordeName(player) + ".ally", 0) == 1))
            {
                if("NULL".equals(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL")))
                {
                    player.sendMessage(formatMsg.format("<gray>Vous n'avez pas de flamme d'Autel ennemi"));
                    return;
                }
                
                Horde hordeE = hordeManager.getHorde(conf.getStringYAML("userdata",event.getPlayer().getUniqueId()+ ".yml", "Autel","NULL"));
                conf.setStringYAML("horde.yml", "Horde." + hordeE.getHordeName() + ".Autel", "NULL");
                conf.setStringYAML("userdata",event.getPlayer().getUniqueId()+ ".yml", "Autel","NULL");
                Location beacon = hordeE.getSpawnHorde();
                beacon.getBlock().setType(Material.BEACON);
                String perm = "iris.horde.message." + hordeE.getHordeName();
                player.sendMessage(formatMsg.format("<gray>Bravo ton courage rapporte 15 points de force + 20 émeraudes à ta Horde !"));
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<yellow>L'ennemi à gagner, notre flamme est revenu"),perm);
                
                int forceD = conf.getIntYAML("horde.yml", "Horde." + hordeE.getHordeName() + ".Force",0);
                forceD = forceD - 15;
                conf.setIntYAML("horde.yml", "Horde." + hordeE.getHordeName() + ".Force", forceD);

                int forceA = conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Force",0);
                forceA = forceA + 15;
                conf.setIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Force", forceA);
                int gain = conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Bank", 0);
                gain = gain + 20;
                conf.setIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Bank", gain);
            }
            else
            {
                if(!"NULL".equals(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL")))
                {
                    player.sendMessage(formatMsg.format("<gray>Vous avez déjà une flamme d'Autel"));
                    return;
                }
                conf.setStringYAML("horde.yml", "Horde." + horde.getHordeName() + ".Autel", player.getName());
                conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel",horde.getHordeName());

                player.sendMessage(formatMsg.format("<yellow>**** Bravo tu viens de voler la flamme de l'Autel de la horde " + horde.getHordeName()));
                player.sendMessage(formatMsg.format("<gray>Cela te rapportera 15 points de force + 20 émeraudes à ta Horde si tu la ramène sur ton Autel!"));
                player.sendMessage(formatMsg.format("<gray>Ton ennemi ne pourra plus valider ces têtes sur son autel tant que tu es en vie"));
                player.sendMessage(formatMsg.format("<gray>Prends garde à toi, ta tête est maintenant mise à prix !"));
                
                interactedBlock.setType(Material.GLASS);
                String perm = "iris.horde.message." + horde.getHordeName();
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<red>**** Alerte : <gray>" + player.getDisplayName() + " vient de voler la flamme de notre Autel !"),perm);
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<gray>Nous ne pouvons plus valider nos têtes tant que ce misérable est en vie !"),perm);
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<gray>Nous devons impérativement le tuer pour rétablir la flamme sur l'Autel !"),perm);
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<gray>Tape <yellow>/horde wanted <gray>pour connaitre la position de " + player.getDisplayName()),perm);
            }
        }

    }
    
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e)
    {
        try 
        {
            Player attaquant = (Player) e.getDamager();
            Player defenseur = (Player) e.getEntity();
            if(plugin.endTP(attaquant)==true)attaquant.sendMessage(formatMsg.format("TP annulée, vous êtes en combat"));
            if(plugin.endTP(defenseur)==true)defenseur.sendMessage(formatMsg.format("TP annulée, vous êtes en combat"));
        } 
        catch(Exception ex) 
        {

        }
    }
    
    @EventHandler
    public void onSacrifice(EntityDeathEvent event)
    {
        Location loc = event.getEntity().getLocation();
        
        HChunk chunk = chunkManager.getChunk(loc);
        if (chunk != null)
        {
            if (chunk.getTypeMember() == 1)
            {
                switch (event.getEntity().getType().name())
                {
                    case "COW":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
                        if(getrandom(15) == 1)
                        {
                            gainSacrifice(chunk,5);
                        }
                        break;
                    case "HORSE":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                        
                        if(getrandom(5) == 1)
                        {
                            gainSacrifice(chunk,6);
                        }
                        break;                            
                    case "OCELOT":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                        
                        if(getrandom(3) == 1)
                        {
                            gainSacrifice(chunk,6);
                        }
                        break;                        
                    case "PIG":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                        
                        if(getrandom(15) == 1)
                        {
                            gainSacrifice(chunk,5);
                        }
                        break;                        
                    case "MUSHROOM_COW":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                        
                        if(getrandom(2) == 1)
                        {
                            gainSacrifice(chunk,5);
                        }
                        break;                        
                    case "SHEEP":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                       
                        if(getrandom(15) == 1)
                        {
                            gainSacrifice(chunk,4);
                        }
                        break; 
                    case "CHICKEN":
                        event.getDrops().clear();
                        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());                        
                        if(getrandom(30) == 25)
                        {
                            gainSacrifice(chunk,2);
                        }
                        break;                        
                }
            }
        }
    }
    
    private void gainSacrifice(HChunk chunk, int mise)
    {
        int force = getrandom(mise);
        Horde horde = hordeManager.getHorde(chunk.getHordeName());
        int max = hordeManager.getCountMember(horde) * 110;
        String perm = "iris.horde.message." + chunk.getHordeName();
        
        int forceA = conf.getIntYAML("horde.yml", "Horde." + chunk.getHordeName() + ".Force",0);
        
        if (forceA > max)
        {
            Bukkit.broadcast(formatMsg.format("<gold>Votre Horde à atteint le maximum de force en sacrifice"), perm);
            Bukkit.broadcast(formatMsg.format("<gold>Recrutez des joueurs, ou partez en guerre !"), perm);
            return;
        }
        forceA = forceA + force;
        conf.setIntYAML("horde.yml", "Horde." + chunk.getHordeName() + ".Force", forceA);
        Bukkit.broadcast(formatMsg.format("<gold>Les dieux nous donnent " + force + " points de force pour ces sacrifices"), perm);
    }
    private int getrandom(int max)
    {
        Random rand = new Random();
        int nombre = rand.nextInt(max - 1 + 1) + 1;
        return nombre;
    }
    
}
