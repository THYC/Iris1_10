package net.teraoctet.iris;

import net.teraoctet.iris.listener.GraveListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.teraoctet.iris.inventory.TrocListener;
import net.teraoctet.iris.commands.*;
import net.teraoctet.iris.horde.ChunkManager;
import net.teraoctet.iris.horde.HordeListener;
import net.teraoctet.iris.horde.HordeManager;
import net.teraoctet.iris.listener.BlockListener;
import net.teraoctet.iris.listener.PlayerListener;
import net.teraoctet.iris.listener.PokerListener;
import net.teraoctet.iris.messageauto.MessageAuto;
import net.teraoctet.iris.parcelle.ParcelleListener;
import net.teraoctet.iris.parcelle.ParcelleManager;
import net.teraoctet.iris.portal.PortalManager;
import net.teraoctet.iris.utils.ConnexionMySQL;
import net.teraoctet.iris.utils.FormatMsg;
import net.teraoctet.iris.world.BorderManager;
import net.teraoctet.iris.world.ConfigWorld;
import net.teraoctet.iris.world.MultiWorld;
import net.teraoctet.iris.world.WorldListener;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.broadcastMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Iris 
extends JavaPlugin 
implements Listener
{
    public static Iris plugin;
        
    public static Iris getPlugin()
    {
        return plugin;
    }
    
    private final GraveListener grave = new GraveListener(this);
    private final PlayerListener playerListener = new PlayerListener(this);
    private final TrocListener trocListener = new TrocListener();
    private final PokerListener pokerListener = new PokerListener();
    private final BlockListener blockListener = new BlockListener(this);
    private final DeadMessages deadMessages = new DeadMessages();
    private final HordeListener hordeListener = new HordeListener(this);
    private final ParcelleListener parcelleListener = new ParcelleListener(this);
    public static FormatMsg formatMsg = new FormatMsg();
    public static ConfigFile conf = new ConfigFile();
    public static PortalManager portal = new PortalManager();
    public static ParcelleManager parcelleManager = new ParcelleManager();
    public static HordeManager hordeManager = new HordeManager();
    public static ChunkManager chunkManager = new ChunkManager();
    public static ConnexionMySQL ConMySQL = new ConnexionMySQL();
    public final HashMap<Player, ArrayList<Block>> flyMap = new HashMap(); 
    public InfoBook infoBook = new InfoBook();
    public NamePlates namePlates = new NamePlates();
    public static final Logger log = Logger.getLogger("Minecraft");
    public static Permission permission;
    public MessageAuto messageAuto = new MessageAuto(this);
    public Map<String, Integer> taskID = new HashMap<>();
    public Sign sign;
    public Inventory inv;
    public Inventory invE;
    public static String WorldPVP;
        
    @Override
    public void onEnable() 
    {
        //---------------------------
        // Creation fichier paramètre
        //---------------------------
        
        ConfigFile.dataFolder = this.getDataFolder();
        conf.ConfigFileTXT("message","motd.txt");
        conf.ConfigFileTXT("script","irisMysql.sql");
        conf.ConfigFileYAML("config.yml");
        conf.ConfigFileYAML("warp.yml");
        conf.ConfigFileYAML("messages.yml");
        conf.ConfigFileTXT("message","motd.txt");
        conf.ConfigFileTXT("script","irisMysql.sql");
        conf.ConfigFileYAML("books.yml");
        conf.ConfigFileYAML("border.yml");
        conf.ConfigFileYAML("MultiInventory.yml");
        conf.ConfigFileYAML("horde.yml");
        conf.ConfigFileYAML("world.yml");
        conf.ConfigFileYAML("npc.yml");
        conf.ConfigFileYAML("player.yml");
        conf.paramFile("", "plugins//Iris//message");
        
        
        //---------------------------
        // MySQL
        //---------------------------
        /*    
        try 
        {
            ConMySQL.createSGBD();
        } catch (IOException ex) 
        {
            Logger.getLogger(Iris.class.getName()).log(Level.SEVERE, null, ex);
        }*/
            
        //---------------------------
        // Events
        //---------------------------
        
        loadInventory();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(grave, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(trocListener, this);
        pm.registerEvents(pokerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(deadMessages, this);
        pm.registerEvents(hordeListener, this);
        pm.registerEvents(new DamageEffect(this), this);
        pm.registerEvents(new WorldListener(), this);
        pm.registerEvents(parcelleListener, this);
                
        //---------------------------
        // Enregistrement des commandes
        //---------------------------
        
        getCommand("pos").setExecutor(new Command_Pos());
        getCommand("day").setExecutor(new Command_Day());
        getCommand("night").setExecutor(new Command_Night());
        getCommand("fly").setExecutor(new Command_Fly());
        getCommand("sun").setExecutor(new Command_Sun());
        getCommand("playeffect").setExecutor(new Command_PlayEffect());
        getCommand("tp").setExecutor(new Command_TP());
        getCommand("ci").setExecutor(new Command_CI());
        getCommand("invsee").setExecutor(new Command_Invsee());
        getCommand("kill").setExecutor(new Command_Kill());
        getCommand("back").setExecutor(new Command_Back());
        getCommand("warp").setExecutor(new Command_Warp());
        getCommand("setwarp").setExecutor(new Command_SetWarp());
        getCommand("spawn").setExecutor(new Command_Spawn(this));
        getCommand("playerinfo").setExecutor(new Command_PlayerInfo(this));
        getCommand("infobook").setExecutor(new InfoBook());
        getCommand("home").setExecutor(new Command_Home());
        getCommand("sethome").setExecutor(new Command_SetHome());
        getCommand("setspawn").setExecutor(new Command_SetSpawn());
        getCommand("border").setExecutor(new Command_Border(this));
        getCommand("grave").setExecutor(new Command_Grave());
        getCommand("portal").setExecutor(new Command_Portal(this));
        getCommand("lobby").setExecutor(new Command_Lobby());
        getCommand("lo").setExecutor(new Command_Login());
        getCommand("delock").setExecutor(new Command_Login());
        getCommand("register").setExecutor(new Command_Login());
        getCommand("passchange").setExecutor(new Command_Login());
        getCommand("am").setExecutor(new Command_AutoMessage(this));
        getCommand("test").setExecutor(new Command_Test(this));
        getCommand("scoreboard").setExecutor(new Command_Test(this));
        getCommand("tpa").setExecutor(new Command_TPA(this));
        getCommand("tphere").setExecutor(new Command_TPA(this));
        getCommand("tpaccept").setExecutor(new Command_TPA(this));
        getCommand("tpdeny").setExecutor(new Command_TPA(this));
        getCommand("spawner").setExecutor(new Command_Spawner(this));
        getCommand("reload").setExecutor(new Command_Reload());
        getCommand("head").setExecutor(new Command_head());
        getCommand("parcelle").setExecutor(new Command_Parcelle());
        getCommand("bank").setExecutor(new Command_Bank());
        getCommand("troc").setExecutor(new Command_Troc());
        getCommand("trocpublic").setExecutor(new Command_Troc());
        getCommand("give").setExecutor(new Command_Give());
        getCommand("horde").setExecutor(new Command_Horde(this));
        getCommand("head").setExecutor(new Command_head());
        getCommand("print").setExecutor(new Command_Print());
        getCommand("claim").setExecutor(new Command_Horde(this));
        getCommand("qg").setExecutor(new Command_Horde(this));
        getCommand("iris").setExecutor(new Command_Help());
        getCommand("y").setExecutor(new Command_Horde(this));
        getCommand("yh").setExecutor(new Command_Horde(this));
        getCommand("tracker").setExecutor(new Command_Tracker());
        getCommand("plugin").setExecutor(new Command_Plugin(this));
        getCommand("world").setExecutor(new Command_World(this));
        getCommand("1").setExecutor(new Command_Util(this));
        getCommand("2").setExecutor(new Command_Util(this));
        getCommand("3").setExecutor(new Command_Util(this));
        getCommand("pack").setExecutor(new Command_Pack(this));
        getCommand("jail").setExecutor(new Command_Jail());
        getCommand("dejail").setExecutor(new Command_dejail());
        getCommand("setjail").setExecutor(new Command_SetJail());
        getCommand("lock").setExecutor(new Command_Lock(this));
        getCommand("mf").setExecutor(new Command_MsgFlotant());
        
        ConfigWorld.load(this, false);
        namePlates.load();
        portal.loadPortal();
        parcelleManager.loadParcelle();
        hordeManager.loadHordes();
        chunkManager.LoadChunk();
        blockListener.reforestation();
                
        reloadMessage();
        
        for(Player player : Bukkit.getOnlinePlayers())
        {
            Command_Login.Global.PlayersToRegister.remove(player.getUniqueId());
            Command_Login.Global.PlayersToUnlock.remove(player.getUniqueId());
            Command_Login.Global.MessageSend.remove(player.getUniqueId());
            Command_Login.Global.PlayersMove.add(player.getUniqueId());
        }
        WorldPVP = conf.getStringYAML("config.yml", "HordePVP", "NULL");
        MultiWorld.loadws();
        Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Spigot.1.10.X"));
    }
    
    public void reloadMessage()
    {
        messageAuto.chargeMessageName();
        messageAuto.vfilterMessageName(1);
        messageAuto.Broadcast();
    }
    
    @Override
    public void onDisable()
    {
        GraveListener.removeInventory();
        Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Desactive"));
    }
         
    public BorderManager GetWorldBorder(String worldName)
    {
        return ConfigWorld.Border(worldName);
    }
         
    private boolean isOneBlockHigh(Location[] locs)
    {
        int y1 = locs[0].getBlockY();
        int y2 = locs[1].getBlockY();
        return y1 == y2;
    }
    
    public static long convertSecondsToTicks(long seconds)
    {
        return seconds * 1000;
    }
    
    public void Stat()
    {
        Statistic a = Statistic.MOB_KILLS;
        //Statistic b = Statistic.BREAK_ITEM;
        //Statistic c = stat.mineBlock;
        //Statistic d = Statistic.KILL_ENTITY;
                
        for(Player p : Bukkit.getOnlinePlayers())
        {
            int a1 = p.getStatistic(a);
            int b1 = p.getStatistic(Statistic.BREAK_ITEM,Material.STONE);
            //int c1 = p.getStatistic(c);
            //int d1 = p.getStatistic(d);
            
            broadcastMessage(p.getName() + " mob: " + String.valueOf(a1) + " | break item: " + String.valueOf(b1) );
           // + " | craft item: " + String.valueOf(p.getStatistic(c)) + " | kill : " + String.valueOf(p.getStatistic(d)));
        }  
    }
    
    public void scheduleTP(final Player p, long ticks, final Location loc)
    {
        final int tid = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                p.teleport(loc);
                endTP(p);
            }
        },ticks);

        taskID.put(p.getName(), tid);
    }

    public boolean endTP(Player p)
    {
        if(taskID.containsKey(p.getName()))
        {
            int tid = taskID.get(p.getName());
            getServer().getScheduler().cancelTask(tid);
            taskID.remove(p.getName());
            return true;
        }
        return false;
    }
    
    public void loadInventory()
    {
        ItemStack is1 = new ItemStack(4);
        ItemMeta meta = is1.getItemMeta();
        meta.setDisplayName(formatMsg.format("<green><gras>Home"));
        ArrayList<String> lore = new ArrayList();
        String libelle11 = formatMsg.format("<white><italique>/home");
        String libelle12 = formatMsg.format("<white><italique>TP sur ton home");
        lore.add(0, libelle11);
        lore.add(1, libelle12);
        meta.setLore(lore);
        is1.setItemMeta(meta);
                
        ItemStack is2 = new ItemStack(2);
        ItemMeta meta2 = is2.getItemMeta();
        meta2.setDisplayName(formatMsg.format("<aqua><gras>Spawn"));
        ArrayList<String> lore2 = new ArrayList();
        String libelle21 = formatMsg.format("<white><italique>/spawn");
        String libelle22 = formatMsg.format("<dark_green>TP au spawn");
        lore2.add(0, libelle21);
        lore2.add(1, libelle22);
        meta2.setLore(lore2);
        is2.setItemMeta(meta2);
                
        ItemStack is3 = new ItemStack(46);
        ItemMeta meta3 = is3.getItemMeta();
        meta3.setDisplayName(formatMsg.format("<red><gras>TNT Run"));
        ArrayList<String> lore3 = new ArrayList();
        String libelle31 = formatMsg.format("<white><italique>Rejoins TNT Run");
        lore3.add(0, libelle31);
        meta3.setLore(lore3);
        is3.setItemMeta(meta3);
                
        ItemStack is4 = new ItemStack(332);
        ItemMeta meta4 = is4.getItemMeta();
        meta4.setDisplayName(formatMsg.format("<white><gras>PaintBall"));
        ArrayList<String> lore4 = new ArrayList();
        String libelle41 = formatMsg.format("<white><italique>Rejoins PaintBall");
        lore4.add(0, libelle41);
        meta4.setLore(lore4);
        is4.setItemMeta(meta4);
        
        ItemStack is5 = new ItemStack(276);
        ItemMeta meta5 = is5.getItemMeta();
        meta5.setDisplayName(formatMsg.format("<gold><gras>Horde PVP"));
        ArrayList<String> lore5 = new ArrayList();
        String libelle51 = formatMsg.format("<white><italique>Viens te battre sur Horde PVP");
        String libelle52 = formatMsg.format("<gray><italique>Crée ta horde, construit ta base");
        String libelle53 = formatMsg.format("<gray><italique>harcèle tes ennemis");
        lore5.add(0, libelle51);
        lore5.add(0, libelle52);
        lore5.add(0, libelle53);
        meta5.setLore(lore5);
        is5.setItemMeta(meta5);
        
        //ItemStack is5 = new ItemStack(35,6);
        //ItemStack is6 = new ItemStack(58);
        //ItemStack is7 = new ItemStack(116);
        //ItemStack is8 = new ItemStack(130);
        
        
        
        this.inv = Bukkit.getServer().createInventory(null, 9, "VIP");
        inv.addItem(is1);
        inv.addItem(is2);
        inv.addItem(is3);
        inv.addItem(is4);
        inv.addItem(is5);
        
        //inv.addItem(iss)
        //inv.setItem(i, additem);
    }
}
