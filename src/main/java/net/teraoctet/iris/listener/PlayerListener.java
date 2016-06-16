package net.teraoctet.iris.listener;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.teraoctet.iris.InfoBook;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.NamePlates;
import net.teraoctet.iris.commands.Command_Login;
import net.teraoctet.iris.horde.HChunk;
import net.teraoctet.iris.horde.Horde;
import net.teraoctet.iris.parcelle.Parcelle;
import net.teraoctet.iris.utils.ConnexionMySQL;
import net.teraoctet.iris.utils.Economy;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
    
    public class PlayerListener 
    implements Listener 
    {
        private final Economy economy = new Economy();
        InfoBook infoBook = new InfoBook();
        NamePlates namePlates = new NamePlates();
        Command_Login login;
        Iris plugin;
        private Player p;
        public Map<Player, Long> lastTimes = new HashMap();

        public PlayerListener(Iris plugin)
        {
            this.plugin = plugin;
        }
          
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) 
        {
            Player player = event.getPlayer();
            p = player;
            
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getDisplayName() + " times 10 80 10");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getDisplayName() + " subtitle {\"text\":\"Inscrivez vous sur notre forum \",\"color\":\"green\",\"extra\":[{\"text\":\" http://Craft.Teraoctet.net\",\"color\":\"yellow\"}]}");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getDisplayName() + " title {\"text\":\"Bienvenue sur\",\"color\":\"green\",\"extra\":[{\"text\":\" Craft.Teraoctet\",\"color\":\"yellow\"}]}");
                            
            conf.setStringYAML("player.yml", player.getDisplayName() + ".uuid" , player.getUniqueId().toString());
            conf.setStringYAML("player.yml", player.getDisplayName() + ".ip" , player.getAddress().toString());
                                    
            event.setJoinMessage(formatMsg.format(conf.getStringYAML("messages.yml","joinMsg"),event.getPlayer()));
            String txt = formatMsg.format(conf.getConfigTXT("//message//motd.txt"),event.getPlayer());
            event.getPlayer().sendMessage(txt);
                        
            boolean newPlayer;
                                        
            newPlayer = conf.paramFile(player.getUniqueId() + ".yml", "plugins//Iris//userdata");
            if(newPlayer == true)
            {
                String MapLobby = conf.getStringYAML("config.yml", "FistMapSpawn", player.getWorld().getName());
                World world = Bukkit.getWorld(MapLobby);
                
                Location spawn = new Location(world,
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.x", world.getSpawnLocation().getBlockX()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.y", world.getSpawnLocation().getBlockY()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.z", world.getSpawnLocation().getBlockZ()));
                player.teleport(spawn);
                
                economy.setVersement(player, conf.getIntYAML("config.yml", "NbEmeraudeForNew", 10));                             
                conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "PlayerName",player.getName());
                Firework f = (Firework)player.getWorld().spawn(player.getLocation(), Firework.class);
      
                FireworkMeta fm = f.getFireworkMeta();
                fm.addEffect(FireworkEffect.builder()
                    .flicker(true)
                    .trail(true)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withColor(Color.RED)
                    .withFade(Color.YELLOW)
                    .build());
                fm.setPower(1);
                f.setFireworkMeta(fm);
                
                broadcastMessage(formatMsg.format(conf.getStringYAML("messages.yml", "newPlayer","<dark_aqua>Un nouveau joueur <a_gr> rejoint notre serveur, bienvenue <a_gr> <joueur> !"),event.getPlayer()));
                event.getPlayer().sendMessage(formatMsg.format("<yellow>Veuillez lire le reglement sur notre forum : http://craft.teraoctet.net",event.getPlayer()));
                            
                List<String> onlogin = conf.getListYAML("books.yml","onlogin");
                if ((onlogin != null) && (!onlogin.isEmpty())) 
                {
                    for (String bookName : onlogin)
                    {
                        ItemStack book = infoBook.createDefaultBook(bookName,player);
                        if (book != null) 
                        {
                            player.getInventory().addItem(new ItemStack[] {book});
                        }
                    }
                }
                if (conf.getBooleanYAML("config.yml", "enableLogin", false))
                {
                    Command_Login.Global.PlayersToRegister.add(player.getUniqueId());
                    player.sendMessage(formatMsg.format("<green><star><star> IMPORTANT <star><star> :<yellow> Ce serveur utilise une protection pour éviter que d'autres joueurs utilisent votre compte, "
                            + "vous devez vous enregistrer pour crafter en tapant <aqua>/register <yellow>[MotDePasse] [ConfirmezMotDePasse] <green><star><star>"));
                }
            }
            else
            {
                if (conf.getBooleanYAML("config.yml", "enableLogin", false))
                {
                    Command_Login.Global.PlayersMove.remove(player.getUniqueId());
                    Command_Login.Global.PlayersToUnlock.add(player.getUniqueId());
                    player.sendMessage(formatMsg.format("<gras><gold>----------------------------------------------------------"));
                    player.sendMessage(formatMsg.format("<green><star><star> LOGIN <star><star> : <yellow>pour te logger tape : <aqua>/delock <yellow>[TonMotDePasse]<green> <star><star>"));
                    player.sendMessage(formatMsg.format("<gras><gold>----------------------------------------------------------"));
                }
                                
                if(!"null".equals(conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "jail","null")))
                {
                    String jail = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "jail","null");
                    Long temps = conf.getLongYAML("userdata",player.getUniqueId() + ".yml", "timejail", 0);
                    
                    if (temps < System.currentTimeMillis())
                    {
                        conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "jail", "null"); 
                        conf.setLongYAML("userdata",player.getUniqueId() + ".yml", "timejail", 0);
                        conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "raison", "");
                        broadcastMessage(formatMsg.format("<yellow><player> a été libéré de prison",player));
                    }
                    else
                    {
                        if (conf.IsConfigYAML("jail.yml",jail))
                        {
                            String world = conf.getStringYAML("jail.yml",jail + ".world",player.getWorld().getName());
                            double X = conf.getDoubleYAML("jail.yml",jail + ".X",player.getLocation().getX());
                            double Y = conf.getDoubleYAML("jail.yml",jail + ".Y",player.getLocation().getY());
                            double Z = conf.getDoubleYAML("jail.yml",jail + ".Z",player.getLocation().getZ());
                            String raison = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "raison", "non respect du reglement");

                            World worldInstance = Bukkit.getWorld(world);
                            Location location = new Location(worldInstance, X, Y, Z);

                            player.teleport(location);
                            broadcastMessage(formatMsg.format("<yellow><player> a ete emprisonné, motif : " + raison,player));
                        }
                    }
                }
            }
            
            Date now = new Date();
            Date d = new Date(conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "ConnectTime", 0));
            
            long disconnectTime = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "DisconnectTime",0L); 
            long connectTime = System.currentTimeMillis();
            long tpsDernCon = (connectTime - disconnectTime)/1000;
            
            if (tpsDernCon > 172800)
            {
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal7", 0);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal6", 0);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal5", 0);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal4", 0);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal3", 0);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal2", 0); 
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsMoyen", 0); 
            }
            
            if(hordeManager.HasHorde(player))
            {
                Long time = conf.getLongYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".time",0L);
                time = (System.currentTimeMillis() - time)/1000;
                if( time > 172800 && conf.getIntYAML("horde.yml", "Horde." + hordeManager.getHordeName(player) + ".Force",0) > 0)
                {
                    Horde horde = hordeManager.getHorde(player);
                    for(String horder : hordeManager.listeHorde(horde).split(";"))
                    {
                        disconnectTime = conf.getLongYAML("userdata",horder + ".yml", "DisconnectTime",0L);
                        if (disconnectTime == 0L) 
                        {
                            conf.setLongYAML("userdata",horder + ".yml", "DisconnectTime",System.currentTimeMillis());
                            disconnectTime = System.currentTimeMillis();
                        }
                        
                        connectTime = System.currentTimeMillis();
                        tpsDernCon = (connectTime - disconnectTime)/1000;
                        if (tpsDernCon > 172800)
                        {
                            int force = ((int)tpsDernCon / 172800) * 5;
                            force = conf.getIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force",0) - force;
                            if(force < 0)
                            {
                                force = 0;
                                conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", (int)force);
                            }
                            else
                            {
                                conf.setIntYAML("horde.yml", "Horde." + horde.getHordeName() + ".Force", (int)force);
                                player.sendMessage(formatMsg.format("<gold>Des joueurs de votre horde sont longuement absent, votre horde a donc des pénalités"));
                            }
                        }
                    }
                    conf.setLongYAML("horde.yml", "Horde." + horde.getHordeName() + ".time", System.currentTimeMillis());
                }
            }
            
            if (now.getMonth() != d.getMonth() || now.getDay() != d.getDay())
            {
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "ConnectTime", connectTime); 
            }
            
            long moyenne = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsMoyen", 0L); 
            
            if (moyenne > 1800.00)
            {
                ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addGroup("vip");
            }
            else if (moyenne < 1801.00)
            {
                ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).removeGroup("vip");
            }
        }
        
        /*@EventHandler(priority=EventPriority.LOWEST)
        public void onBookDrop(PlayerDropItemEvent e)
        {
            Player player = e.getPlayer();
            Item item = e.getItemDrop();
            List<String> list = conf.getListYAML("books.yml","protected");
            if ((list != null) && (!list.isEmpty()) && (player != null) && (item != null))
            {
                ItemStack book = item.getItemStack();
                if ((book != null) && (book.getType() == Material.WRITTEN_BOOK))
                {
                    BookMeta bookMeta = (BookMeta)book.getItemMeta();
                    for (String titre : list) 
                    {
                        if (conf.getStringYAML("books.yml",titre + ".title").equalsIgnoreCase(bookMeta.getTitle()))
                        {
                            player.sendMessage("Vous ne pouvez pas jeter ce livre!");
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }*/
        
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) 
        {
            Player player = event.getPlayer();
            event.setQuitMessage(formatMsg.format(conf.getStringYAML("messages.yml","quitMsg"),player));
            
            if(!"NULL".equals(conf.getStringYAML("userdata",event.getPlayer().getUniqueId()+ ".yml", "Autel","NULL")))
            {
                Horde horde = hordeManager.getHorde(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL"));
                conf.setStringYAML("horde.yml", "Horde." + horde.getHordeName() + ".Autel", "NULL");
                conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "Autel","NULL");
                Location beacon = horde.getSpawnHorde();
                beacon.getBlock().setType(Material.BEACON);
                String perm = "iris.horde.message." + horde.getHordeName();
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format("<yellow>L'ennemi qui a osé nous défier a déserté, notre flamme est enfin revenu"),perm);
            }
                                    
            long disconnectTime = System.currentTimeMillis();
            conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "DisconnectTime", disconnectTime);  
            
            long tpsPasseTotal7 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal7",0L);
            long tpsPasseTotal6 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal6",0L);
            long tpsPasseTotal5 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal5",0L);
            long tpsPasseTotal4 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal4",0L);
            long tpsPasseTotal3 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal3",0L);
            long tpsPasseTotal2 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal2",0L);
            long tpsPasseTotal1 = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal1",0L);
            
            Date now = new Date();
            Date d = new Date(conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "ConnectTime", 0));            
            if (now.getMonth() != d.getMonth() || now.getDay() != d.getDay())
            {
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal7", tpsPasseTotal6);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal6", tpsPasseTotal5);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal5", tpsPasseTotal4);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal4", tpsPasseTotal3);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal3", tpsPasseTotal2);   
                conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal2", tpsPasseTotal1);   
            } 
            
            long connectTime = conf.getLongYAML("userdata",player.getUniqueId()+ ".yml", "ConnectTime",0L); 
            long tpsPasseTotal = (disconnectTime - connectTime)/1000;
            conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsPasseTotal1", tpsPasseTotal); 
            
            long moyenne = (tpsPasseTotal7+ tpsPasseTotal6 + tpsPasseTotal5 + tpsPasseTotal4 + tpsPasseTotal3 + tpsPasseTotal2 + tpsPasseTotal1)/7;
            conf.setLongYAML("userdata",player.getUniqueId()+ ".yml", "tpsMoyen", moyenne); 
        }
                
        @EventHandler
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
        {
            Player player = event.getPlayer();
            
            if (conf.getBooleanYAML("config.yml", "enableLogin", false))
            {                            
                if (Command_Login.Global.PlayersToRegister.contains(player.getUniqueId()) 
                        || (Command_Login.Global.PlayersToUnlock.contains(player.getUniqueId())))
                {
                    if (!event.getMessage().contains("/delock"))
                    {                
                        if (!event.getMessage().contains("/register"))
                        {
                            player.sendMessage(formatMsg.format("<red><star><star> RAPPEL <star><star> :<aqua> Ce serveur utilise une protection, vous ne pouvez pas taper des commandes,"
                                + " et vous ne pouvez pas crafter, vous devez vous logger pour pouvoir jouer <green><star> C'est obligatoire et gratuit <star>"));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
            
            if (event.getMessage().contains("/reload") && (player.hasPermission("iris.reload") || player.isOp()))
            {
                broadcastMessage(formatMsg.format("<aqua>** ATTENTION ** : <yellow>Le serveur fait actuellement un reload, "));
                broadcastMessage(formatMsg.format("<yellow>vous devrez donc peut être vous relogger en tapant <green>/delock [votre_mot_de_passe]"));
            }
            
            if (player.hasPermission("iris.horde") && !"NULL".equals(conf.getStringYAML("userdata",event.getPlayer().getUniqueId()+ ".yml", "Autel","NULL")))
            {
                if (event.getMessage().contains("/lobby") || event.getMessage().contains("/qg") 
                        || event.getMessage().contains("/spawn") || event.getMessage().contains("/h rancon") || event.getMessage().contains("/horde rancon"))
                {
                    player.sendMessage(formatMsg.format("<light_purple>Tu ne peut taper ces commandes quand tu as une flamme ennemi, tu dois rentrer par tes "
                            + "propres moyens, si tu es coincés, suicide toi en tapant <yellow>/h kill"));
                    event.setCancelled(true);
                    return;
                }                 
            }
            
            Parcelle parcelle = parcelleManager.getParcelle(player.getLocation());
            if (parcelle != null && !parcelle.getuuidAllowed().contains(player.getUniqueId().toString()) && !player.isOp())
            {
                if (parcelle.getName().contains("|off|"))
                {
                    return;
                }
                if (!event.getMessage().contains("/delock"))
                {                
                    if (!event.getMessage().contains("/register"))
                    {
                        if (!event.getMessage().contains("/h"))
                        {
                            if (!event.getMessage().contains("/y"))
                            {
                                if (!event.getMessage().contains("/qg"))
                                {
                                    if (!event.getMessage().contains("/troc"))
                                    {
                                        if (!event.getMessage().contains("/ma"))
                                        {
                                            if (!event.getMessage().contains("/parcelle info"))
                                            {
                                                if (!event.getMessage().contains("/bank"))
                                                {
                                                    player.sendMessage(formatMsg.format("<light_purple>Vous ne pouvez pas taper de commande ici"));
                                                    event.setCancelled(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
                        
            HChunk chunk = chunkManager.getChunk(player.getLocation());
            if (chunk != null)
            {
                if (!event.getMessage().contains("/delock"))
                {                
                    if (!event.getMessage().contains("/register"))
                    {
                        if (!event.getMessage().contains("/h"))
                        {
                            if(!chunk.getHordeName().equals(hordeManager.getHordeName(player)) && !event.getMessage().contains("/claim"))
                            {
                                if (!event.getMessage().contains("/bank"))
                                {
                                    player.sendMessage(formatMsg.format("<light_purple>Ce lieu est envouté et vous empèche de taper des commande"));
                                    event.setCancelled(true);
                                }
                            }
                            if(chunk.getHordeName().equals(hordeManager.getHordeName(player)))
                            {
                                if (event.getMessage().contains("/spawn"))
                                {
                                    player.sendMessage(formatMsg.format("<gray>Vous ne pouvez pas taper cette commande chez vous"));
                                    event.setCancelled(true);
                                }
                            }
                        }
                        if(event.getMessage().contains("/horde create") || event.getMessage().contains("/horde setqg"))
                        {
                            player.sendMessage(formatMsg.format("<light_purple>Ce lieu est envouté et vous empèche de taper cette commande ici"));
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
                
        @EventHandler(priority=EventPriority.NORMAL)
        public void onPlayerChat(AsyncPlayerChatEvent event)
        {
            Player player = event.getPlayer();
            UUID playerID = event.getPlayer().getUniqueId();
             /*           
            if(player.hasPermission("iris.chatradius") && !player.isOp())
            {
                long radius = conf.getLongYAML("config.yml", "Chat.radius", 20L);
                radius *= radius;

                for (Player players : Bukkit.getServer().getOnlinePlayers()) 
                {
                    if(players.getLocation().distance(player.getLocation()) < radius) 
                    {
                        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerID);               
                        String prefix = user.getPrefix(player.getWorld().getName());
                        String suffix = user.getSuffix(player.getWorld().getName());
                        String hordeName = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "horde");
                        //players.sendMessage(formatMsg.format(suffix + prefix + "<PLAYER>: <white>" + event.getMessage(), player));
                        //return;
                    }
                }
            }*/
            
            //else 
            if (event.getMessage().contains("delock") && !event.getMessage().contains("/delock"))
            {
                player.sendMessage(formatMsg.format("<light_purple>Tu as oubli<e_ai> le '/', recommences :)"));
                event.setCancelled(true);
                return;
            }
            
            if ((event.getMessage().length() > 0))
            {                
                PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerID);               
                String prefix = user.getPrefix(player.getWorld().getName());
                String suffix = user.getSuffix(player.getWorld().getName());
                String suffixWorld = conf.getStringYAML("world.yml", "worlds." + player.getLocation().getWorld().getName() + ".Name","");
                event.setFormat(formatMsg.format(suffixWorld + suffix + prefix + "<PLAYER>: <white>" + event.getMessage(), player));
                
                if(!"null".equals(conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "jail","null")))
                {
                    event.setFormat(formatMsg.format("<gray>[PRISONNIER] <PLAYER>: <white>" + event.getMessage(), player));
                }
            }
        }
                
        @EventHandler
        public void ClickNPC(PlayerInteractEntityEvent event)
        {
            final Player player = event.getPlayer();
            Entity entity = event.getRightClicked();
                        
            /*if(event.getPlayer().isOp())
            {
                
                //final LivingEntity entity = event.getRightClicked();
                //entity.setCustomName("Gardien du portail");
		//entity.setCustomNameVisible(true);
                //entity.getType().getName()
                //MetadataValue mv = null;
                //mv.asString();
                //Location[] locs = plugin.getPlayerSelection(player);
                //entity.setPassenger(event.getPlayer())
                               
                
                if(player.isOp())
                {
                    player.sendMessage(String.valueOf(entity.getEntityId()));
                    player.sendMessage(String.valueOf(entity.getUniqueId()));
                    player.sendMessage(String.valueOf(entity.getMetadata("name")));
                }
                
                Set<String> listNPC = conf.getKeysYAML("npc.yml","NPC");
                for (String npc : listNPC)
                {
                    if(entity.getUniqueId().toString().contains(npc))
                    {
                        player.sendMessage(formatMsg.format(conf.getStringYAML("npc.yml","NPC." + npc + ".quete","<gray>Bonjour <player>"),player));
                        //player.sendMessage("fait un clique droit sur le panneaux pour entrer.");
                    }      
                }
                
                //entity.setPassenger(player);
                //entity.setMetadata("Beber", mv);
                
            }*/
            Parcelle parcelle = parcelleManager.getParcelle(entity.getLocation());
            if (parcelle != null && !parcelle.getOwner().contains(player.getUniqueId().toString()) && !player.isOp())
            {
                if(entity instanceof org.bukkit.entity.ItemFrame)
                {  
                    ItemFrame frame = (ItemFrame)entity;
                    event.setCancelled(true);
                }  
            }
            
            if (player.getItemInHand().getType() == Material.STICK) 
            {
                if (player.getItemInHand().hasItemMeta())
                {
                    if (player.getItemInHand().getItemMeta().getDisplayName().contains("eleveur"))
                    {
                        if(entity instanceof Animals)
                        {     
                            Animals animal = (Animals)entity;
                            if ((entity instanceof Cow))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short) 92);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                            else if ((animal instanceof Chicken))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short)93);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                            else if ((animal instanceof Sheep))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short)91);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                            else if ((animal instanceof Pig))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short)90);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                            else if ((animal instanceof Horse))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short)100);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                            else if ((animal instanceof Rabbit))
                            {
                                if (!animal.isAdult()) 
                                {
                                    ItemStack eggs = new ItemStack(Material.MONSTER_EGG, 1, (short)101);
                                    entity.getLocation().getWorld().dropItem(entity.getLocation(), eggs);             
                                    entity.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
                
        @EventHandler(priority=EventPriority.NORMAL)
        public void playerMoved(PlayerMoveEvent event)
        {
            Player player = event.getPlayer();
            if (conf.getBooleanYAML("config.yml", "enableLogin", false))
            {
                if ("NULL".equals(conf.getStringYAML("userdata",player.getUniqueId()+ ".yml","password","NULL")))
                {
                    player.sendMessage(formatMsg.format("<green><star><star> IMPORTANT <star><star> :<yellow> Ce serveur utilise une protection, "
                            + "vous devez vous enregistrer pour crafter en tapant <aqua>/register <yellow>[MotDePasse] [ConfirmezMotDePasse] <green><star><star>",player));
                }
            }
            portal.playerMoved(event.getPlayer());
        }
                        
        @EventHandler
        public void onPlayerHelp(PlayerInteractEvent event)
        {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign)
            {
                Player player = event.getPlayer();
                Sign sign = (Sign) event.getClickedBlock().getState();
                                
                if (sign.getLine(0).contains(formatMsg.format("[?]")) && !sign.getLine(3).isEmpty())
                {
                    try
                    {
                        String txt = formatMsg.format(conf.getConfigTXT("//aide//" + sign.getLine(3) + ".txt"));
                        player.sendMessage(txt);  
                    }
                    catch(Exception ex)
                    {
                        player.sendMessage(formatMsg.format("<red>ERREUR : Le panneau est mal format<e_ai>"));  
                    }
                }
            }
        }
               
        @EventHandler
        public void onPlayerInteractLog(PlayerInteractEvent event)
        {
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
        }
                
        @EventHandler
        public void onPlayerAchievementEvent(PlayerAchievementAwardedEvent event)
        {
            Player player = event.getPlayer();
            //PermissionUser user = PermissionsEx.getUser(player);
            //String world = null;

            if(conf.getBooleanYAML("config.yml", "promotionActif",false)== false)return;
                        
            String group = conf.getStringYAML("config.yml", event.getAchievement().name() + ".group","NULL");
            
            if(!"NULL".equals(group))
            {
                player.sendMessage(Iris.formatMsg.format(conf.getStringYAML("config.yml", event.getAchievement().name() + ".message","<gold>F<e_ai>licitation <player> ! ton succ<e_gr>s te permet de monter en grade !"),player));
                
                List<String> list = plugin.getConfig().getStringList(event.getAchievement().name() + ".world");
                if(!list.isEmpty()){
                    for (String w : list)
                    {
                        ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addGroup(group,w);
                    }
                }
                else
                {
                    ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player).addGroup(group);
                }
                
                ItemStack item = new ItemStack(Material.valueOf(conf.getStringYAML("config.yml", event.getAchievement().name() + ".items","AIR")));
                player.getInventory().addItem(item);
                player.updateInventory();
            }
            else
            {
                ItemStack item = new ItemStack(Material.valueOf(conf.getStringYAML("config.yml", event.getAchievement().name() + ".items","AIR")));
                if(item.getType() == Material.AIR)
                {
                    return;
                }
                player.sendMessage(formatMsg.format(conf.getStringYAML("config.yml", event.getAchievement().name() + ".message","<aqua>F<e_ai>licitation <player> ! ton succ<e_gr>s te raporte un petit cadeau"),player));
                player.getInventory().addItem(item); 
                player.updateInventory();
            }       
        }
        
        @EventHandler
        public void onPlayerInteractBlock(PlayerInteractEvent event) 
        {
            Player player = event.getPlayer();
            if (player.getItemInHand().getType() == Material.BLAZE_ROD) 
            {
                if (player.getItemInHand().hasItemMeta())
                {
                    if (player.getItemInHand().getItemMeta().getDisplayName().contains("Zeus"))
                    {
                        //doThrow((Fireball)player.launchProjectile(Fireball.class));
                        //this.lastTimes.put(player, System.currentTimeMillis());
                        player.sendMessage("Bouuuuum .... ffffff");
                    }
                // Creates a bolt of lightning at a given location. In this case, that location is where the player is looking.
                // Can only create lightning up to 200 blocks away.
                //player.getWorld().strikeLightning(player.getTargetBlock(null, 200).getLocation());
                //player.getWorld().createExplosion(player.getTargetBlock(null, 200).getLocation(), 15.0F);
                
                }
            }
        }
        
        private void doThrow(Fireball ball)
        {
            if (ball.getType().toString().equals("FIREBALL"))
            {
                ball.setIsIncendiary(true);
                ball.setYield(2);
            }
        }
                
        /*@EventHandler(priority=EventPriority.NORMAL)
        public void onProjectileHit(ProjectileHitEvent e) 
        {
            //Bukkit.broadcastMessage(e.getEntity().getType().getName());
            if (((e.getEntity() instanceof Arrow)) && 
              ((((Arrow)e.getEntity()).getShooter() instanceof Player))) 
            {
                Player theShooter = (Player)((Arrow)e.getEntity()).getShooter();
                Entity theArrow = (Arrow)e.getEntity();
                //if (ExplosionBow.Explosionbow.containsKey(theShooter.getName())) 
                //{
                if(theShooter.getName().equalsIgnoreCase("thyc82")){
                    //Entity ent = theArrow.getLocation().
                    theArrow.getWorld().createExplosion(theArrow.getLocation(), 10.0F);
                    theArrow.remove();
                    
                }
                //}
            }
        }*/
                
        @EventHandler
        public void onPlayerRespawn(PlayerRespawnEvent event) 
        {
            Player player = event.getPlayer();
            
            if(!"null".equals(conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "jail","null")))
            {
                String jail = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "jail","null");
                if (conf.IsConfigYAML("jail.yml",jail))
                {
                    String world = conf.getStringYAML("jail.yml",jail + ".world",player.getWorld().getName());
                    double X = conf.getDoubleYAML("jail.yml",jail + ".X",player.getLocation().getX());
                    double Y = conf.getDoubleYAML("jail.yml",jail + ".Y",player.getLocation().getY());
                    double Z = conf.getDoubleYAML("jail.yml",jail + ".Z",player.getLocation().getZ());

                    World worldInstance = Bukkit.getWorld(world);
                    Location location = new Location(worldInstance, X, Y, Z);

                    event.setRespawnLocation(location);
                    player.sendMessage(formatMsg.format("<yellow>Tu n'a pas finit de purger ta peine"));
                }
                return;
            }
            
            
            String home = "home";
            String world= player.getLocation().getWorld().getName();
            if (conf.IsConfigYAML("userdata",player.getUniqueId() + ".yml","Home." + world + "." + home))
            {
                double X = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".X", player.getLocation().getX());
                double Y = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".Y", player.getLocation().getY());
                double Z = conf.getDoubleYAML("userdata",player.getUniqueId() + ".yml", "Home." + world + "." + home + ".Z", player.getLocation().getZ());
                
                World worldInstance = Bukkit.getWorld(world);
                Location homeLocation = new Location(worldInstance, X, Y, Z);
                
                Location lastLocation = player.getLocation();
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
                conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
                conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());   
                
                event.setRespawnLocation(homeLocation);
            }
            else
            {
                Location spawn = new Location(player.getLocation().getWorld(),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.x", player.getLocation().getWorld().getSpawnLocation().getBlockX()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.y", player.getLocation().getWorld().getSpawnLocation().getBlockY()),
                                conf.getIntYAML("world.yml", "worlds." + player.getWorld().getName() + ".spawn.z", player.getLocation().getWorld().getSpawnLocation().getBlockZ()));
                player.teleport(spawn);
                event.setRespawnLocation(spawn);
            }
            if (player.hasPermission("iris.horde")) player.setPassenger(player.getWorld().spawn(player.getLocation(), Snowball.class));
        }
        
        @EventHandler(priority=EventPriority.LOWEST)
        public void ondeath(final PlayerDeathEvent e)
        {
            if (!Iris.conf.getBooleanYAML("config.yml","AutoRespawn",true)) 
            {
                return;
            }
            if (!Iris.conf.getBooleanYAML("config.yml","uc.fastrespawn",true)) 
            {
                return;
            }
            
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    Player p = e.getEntity();
                    try
                    {
                        Object nmsPlayer = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
                        Object packet = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".PacketPlayInClientCommand").newInstance();
                        Class<?> enumClass = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EnumClientCommand");
                        for (Object ob : enumClass.getEnumConstants()) 
                        {
                            if (ob.toString().equals("PERFORM_RESPAWN")) 
                            {
                                packet = packet.getClass().getConstructor(new Class[] { enumClass }).newInstance(new Object[] { ob });
                            }
                        }
                        Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                        con.getClass().getMethod("a", new Class[] { packet.getClass() }).invoke(con, new Object[] { packet });
                    }
                    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException | NoSuchFieldException t)
                    {
                    }
                }
            }, 2L);
        }
        
        @EventHandler(priority=EventPriority.NORMAL)
        public void InfoPlayerBreak(PlayerInteractEvent event)
        {
            Player player = event.getPlayer();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.ARROW)
            {
                
                Block blk = event.getClickedBlock();
                Location loc = blk.getLocation();

                if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        
                try 
                {
                    String msg = "<green>Joueurs ayant cassés un bloc a cette position :\n";
                    ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irismouchard WHERE location = '" + loc.toString() + "'");

                    if (rs != null) 
                    {
                        while (rs.next()) 
                        {
                            msg = msg + 
                                    "<gray>Joueur : <yellow>" + rs.getString("playerName") + "\n" +
                                    "<gray>IP : <yellow>" + rs.getString("adresseIP") + "\n" +
                                    "<gray>TypeBlock : <yellow>" + rs.getString("blockBreak") + "\n" +
                                    "<gray>Date : <yellow>" + rs.getString("id_time") + "\n" +
                                    "<gray>--------------------------------------\n";
                        }
                    }
                    else
                    {
                        msg = "<yellow> pas d'enregistrement sur cette position";
                    }
                    player.sendMessage(formatMsg.format(msg));
                } 
                catch (SQLException ex) 
                {
                    Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Erreur de lecture MemberParcelle sur MySQL"));
                    Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        @EventHandler()
        public final void onInvVIPClickEvent(InventoryClickEvent event)
        {
            if(!"VIP".equals(event.getInventory().getName()))
            {
                return;
            }
            
            if (event.isShiftClick())
            {
                event.setCancelled(true);
                return;
            }
            
            //if(event.getAction().NOTHING)return;
            
            Player player = (Player)event.getWhoClicked();
            
            if(event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
            {
                if(event.getRawSlot() < 9)
                {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);   
                    return;
                }
            }

            Inventory invChest = event.getView().getTopInventory();
            Inventory invPlayer = event.getView().getBottomInventory();
                                        
            if(invChest.getType() == InventoryType.CHEST && invPlayer.getType() == InventoryType.PLAYER)
            {  
            
                if (event.getInventory().getItem(0) == null) 
                {
                    return;
                }
                if (event.getInventory().getItem(0).hasItemMeta() == false) 
                {
                    return;
                }
                if (event.getInventory().getItem(0).getItemMeta().hasLore() == false) 
                {
                    return;
                }
                if (event.getCurrentItem() != null)
                {
                    if (event.getCurrentItem().hasItemMeta())
                    {
                        if (event.getCurrentItem().getItemMeta().getDisplayName() == null ? player.getName() == null : event.getCurrentItem().getItemMeta().getDisplayName().equals(player.getName()))
                        {
                            return;
                        }
                    }
                }
                
                if (event.getInventory().getItem(0).getItemMeta().getDisplayName() == null ? player.getName() == null : event.getInventory().getItem(0).getItemMeta().getDisplayName().equals(player.getName()))
                {
                    return;
                }
                
                if(event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == InventoryAction.HOTBAR_SWAP)
                {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);   
                    return;
                }
                        
                //Les items sont pris pour un déplacement
                if(event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_ONE || event.getAction() == InventoryAction.PICKUP_SOME)
                {                 
                    //Les items ont été pris dans le coffre
                    if(event.getRawSlot() == event.getSlot())
                    {
                        if(event.getRawSlot() < 9)
                        {
                            if(event.getInventory().getItem(event.getRawSlot()).hasItemMeta())
                            {
                                player.sendMessage(formatMsg.format("<gras><red>Ce bloc n'est pas a vendre"));
                                event.setCancelled(true);
                                event.setResult(Event.Result.DENY);   
                                return;
                            }
                        
                        
                            player.sendMessage(formatMsg.format("<yellow>le bloc <aqua>" + event.getCursor().getData().getItemTypeId() + ":" + event.getCursor().getData().getData() + "<yellow> n'est pas a vendre"));
                            event.setCancelled(true);
                            event.setResult(Event.Result.DENY);
                        }
                        else //Les items ont juste été déplacé à l'intérieur de l'inventaire du joueur, on sort
                        {
                        }
                    } 
                }  
            }
        }
        
        //@EventHandler
        //public void onPotionSplash(PotionSplashEvent event)
        //{
            //Bukkit.getConsoleSender().sendMessage(formatMsg.format("<aqua>[Iris]" + event.getAffectedEntities()));
        //}
        
        /*@EventHandler//Stops placement of lava
        public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) 
        {
            Player player = event.getPlayer();
            if(player.hasPermission("iris.lava"))
            {
                return;
            }
            if (event.getBucket() == Material.LAVA_BUCKET) 
            {
                event.setCancelled(true);
                player.sendMessage(formatMsg.format("<red>La lave est desactive !"));
            }
        }*/

        /*@EventHandler //Stops lava pickup
        public void onPlayerBucketFill(PlayerBucketFillEvent event) 
        {
            Player player = event.getPlayer();
            if(player.hasPermission("iris.lava"))
            {
                return;
            }
            if ((event.getBlockClicked().getType() == Material.STATIONARY_LAVA))
            {
                event.setCancelled(true);
                player.sendMessage(formatMsg.format("<red>La lave est desactive !"));
            }
            
            if ((event.getBlockClicked().getType() == Material.LAVA))
            {
                event.setCancelled(true);
                player.sendMessage(formatMsg.format("<red>La lave est desactive !"));
            }
        }*/
    }


