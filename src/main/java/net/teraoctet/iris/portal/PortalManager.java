package net.teraoctet.iris.portal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import net.teraoctet.iris.*;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.horde.Horde;
import net.teraoctet.iris.horde.HordeManager;
import net.teraoctet.iris.utils.ConnexionMySQL;
import net.teraoctet.iris.utils.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PortalManager 
{
    InfoBook infoBook = new InfoBook();
    private static final ArrayList<Portal> portals = new ArrayList<>();
    private Location locplayer = null;
    private final Economy eco = new Economy();
    HordeManager hordeManager = new HordeManager();

    public void playerMoved(Player player)
    {
        locplayer = player.getLocation();
        Portal p = containsVector(locplayer);
        if(p != null && player.hasPermission("iris.portal.tp." + p.portalName))
        { 
            if(player.hasPermission("iris.horde") && !player.isOp())
            {
                if (hordeManager.HasHorde(player))
                {
                    String hordeName = hordeManager.getHordeName(player);
                    Horde horde = hordeManager.getHorde(player);
                
                    if(conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Bank", 0) < 1)
                    {
                        player.sendMessage(formatMsg.format("<aqua>Le compte en banque de ta horde est vide"));
                        player.sendMessage(formatMsg.format("<aqua>tape <yellow>/bank vireh <aqua>pour effectuer un virement de ton compte sur ta horde"));
                        return;
                    }
                    else
                    {
                        eco.setPrelevement(horde, 1);
                        player.sendMessage(formatMsg.format("<aqua>1 Emeraude a été prélevé sur le compte de ta Horde pour le passage de ce portail"));
                    }
                }
            }
            
            Location lastLocation = player.getLocation();

            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.X", lastLocation.getX());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Y", lastLocation.getY());
            conf.setDoubleYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.Z", lastLocation.getZ());
            conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "LastLocation.World", lastLocation.getWorld().getName());    
            try
            {
                double X = p.tox1;
                double Y = p.toy1;
                double Z = p.toz1;
                World worldInstance = Bukkit.getWorld(p.toworld);
                
                if(worldInstance == null)
                {
                    player.sendMessage(Iris.formatMsg.format("<aqua>Ce monde est introuvable"));
                    return;
                }
                
                Location portalLoc = new Location(worldInstance, X, Y, Z);
                player.getPlayer().teleport(portalLoc);
                switch(p.mode)
                {
                    case 1:
                        player.setGameMode(GameMode.CREATIVE);
                        break;
                    case 2:
                        player.setGameMode(GameMode.ADVENTURE);
                        break;
                    default:
                        player.setGameMode(GameMode.SURVIVAL);
                        break;
                }
               
                player.playSound(player.getPlayer().getLocation(),Sound.AMBIENT_CAVE,250,4);
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400, 4));
            }
            catch(Exception e)
            {
                player.sendMessage(formatMsg.format("<aqua>point d'arriv<e_ai> non d<e_ai>clar<e_ai> pour ce portail"));
            }

            if (p.message != null && "null".equals(p.message)==false && !"".equals(p.message))
            {
                player.sendMessage(Iris.formatMsg.format(p.message,player.getPlayer()));
            }

            if (p.bookName != null && "null".equals(p.bookName)==false && !"".equals(p.bookName))
            {
                ItemStack book = infoBook.createDefaultBook(p.bookName,player);
                if (book != null) 
                {
                    player.getInventory().addItem(new ItemStack[] {book});
                } 
            }
        }
    }
        
    private Portal containsVector(Location loc)
    {
        for(Portal p : portals)
        {
            if(foundPortal(loc,p)) 
            {
                return p;
            }
        }
        return null;
    }
        
    private boolean foundPortal(Location loc, Portal p)
    {
        if (p.world.equalsIgnoreCase(loc.getWorld().getName()) == false)
        {
            return false;
        }
        if ((loc.getBlockX() < p.x1 ) || (loc.getBlockX() > p.x2)) 
        {
            return false;
        }
        if ((loc.getBlockZ() < p.z1) || (loc.getBlockZ() > p.z2)) 
        {
            return false;
        }
        if ((loc.getBlockY() < p.y1) || (loc.getBlockY() > p.y2)) 
        {
            return false;
        }
        return true;
    }
        
    public void loadPortal()
    {            
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            try 
            {
                ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irisportal");
                if (rs != null) 
                {
                    portals.clear();

                    while (rs.next()) 
                    {
                        Portal portal = new Portal(
                            rs.getString("portalName"),
                            rs.getString("world"),
                            rs.getInt("X1"), 
                            rs.getInt("Y1"),
                            rs.getInt("Z1"),
                            rs.getInt("X2"),
                            rs.getInt("Y2"),
                            rs.getInt("Z2"),
                            rs.getString("toworld"),
                            rs.getInt("toX1"),
                            rs.getInt("toY1"),
                            rs.getInt("toZ1"),
                            rs.getString("bookName"),
                            rs.getString("message"),
                            rs.getInt("mode"));
                        portals.add(portal);
                    }
                    Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Portail active"));
                }
            } 
            catch (SQLException ex) 
            {
                Iris.log.log(Level.SEVERE, ex.getMessage());
            }
        } 
    }                
}


