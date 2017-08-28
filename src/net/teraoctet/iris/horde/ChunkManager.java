package net.teraoctet.iris.horde;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.hordeManager;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChunkManager
{
    private static final ArrayList<HChunk> chunks = new ArrayList();
    //public static final ArrayList<UUID> Visiteurs = new ArrayList();
 
    public ChunkManager() 
    {
        
    }
    
    public void LoadChunk()
    {
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            try 
            {
                ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irischunk ");
                String members1;
                String members2;
                String members3;
                String members4;
                
                if (rs != null) 
                {
                    chunks.clear();
                    
                    while (rs.next()) 
                    {
                        members1 = "";
                        members2 = "";
                        members3 = "";
                        members4 = "";
                        
                        ResultSet member = ConMySQL.executeSelect("SELECT * FROM irishordemember "
                                + "where hordeName = '" + rs.getString("hordeName") + "'");
                        while (member.next())
                        {
                            if(member.getInt("typeMember") == 1)
                            {
                                members1 = members1 + ";" + member.getString("playerUUID");
                            }
                            if(member.getInt("typeMember") == 2)
                            {
                                members2 = members2 + ";" + member.getString("playerUUID");
                            }
                            if(member.getInt("typeMember") == 3)
                            {
                                members3 = members3 + ";" + member.getString("playerUUID");
                            }
                            if(member.getInt("typeMember") == 4)
                            {
                                members4 = members4 + ";" + member.getString("playerUUID");
                            }
                        } 
                        
                        HChunk hchunk = new HChunk(
                            rs.getString("playerUUID"),
                            rs.getString("hordename"),
                            rs.getString("world"),
                            rs.getInt("X1"), 
                            rs.getInt("Z1"),
                            rs.getInt("typeMember"),    
                            rs.getInt("jail"),
                            rs.getInt("noBuild"),
                            rs.getInt("noBreak"),
                            rs.getInt("noInteract"), 
                            rs.getInt("noFire"),
                            rs.getString("message"),
                            rs.getInt("noPVP"),
                            rs.getInt("noKillAnimal"),
                            members1,
                            members2,
                            members3,
                            members4);
                        chunks.add(hchunk);
                        
                        
                    }
                    Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] HChunk actif"));
                }
            } 
            catch (SQLException ex) 
            {
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Erreur de lecture HChunk sur MySQL"));
                Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
  
    private HChunk hasHChunk(Location loc)
    {
        
        for(HChunk c : chunks)
        {
            if(foundChunk(loc,c)) 
            {
                return c;
            }
        }
        return null;
    }
    
    private HChunk hasHChunk(Chunk chunk)
    {
        
        for(HChunk c : chunks)
        {
            if(chunk.getX() == c.getX() && chunk.getZ() == c.getZ() && chunk.getWorld().getName().equals(c.getWorldName())) 
            {
                return c;
            }
        }
        return null;
    }
    
    public Boolean haveQG(Player player)
    {
        for(HChunk c : chunks)
        {
            if(c.getHordeName().equalsIgnoreCase(hordeManager.getHordeName(player)) && c.getTypeMember() == 1) 
            {
                return true;
            }
        }
        return false;
    }
    
    public Boolean hasQG(Player player)
    {
        for(HChunk c : chunks)
        {
            if(c.getHordeName().equalsIgnoreCase(hordeManager.getHordeName(player)) && c.getTypeMember() == 1) 
            {
                if (c.getChunk() == player.getLocation().getChunk())return true;
            }
        }
        return false;
    }
    
    public Boolean hasQG(Location loc)
    {
        HChunk c = hasHChunk(loc);
        if (c == null)
        {
            return false;
        }
        else
        {
            if (c.getTypeMember() == 1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
        
    private boolean foundChunk(Location loc, HChunk c)
    {
        int X = loc.getChunk().getX();
        int Z = loc.getChunk().getZ();
        
        if (c.getWorldName().equalsIgnoreCase(loc.getWorld().getName()) == false)
        {
            return false;
        }
        if ( X != c.getX()) 
        {
            return false;
        }
        if ( Z != c.getZ())
        {
            return false;
        }
        
        return true;
    }
    
    public boolean isChunkEnnemi(Chunk chunk, Horde horde)
    {
        for(HChunk c : chunks)
        {
            if((chunk.getX()-2 == c.getX() || chunk.getX()-1 == c.getX() || chunk.getX()== c.getX() || chunk.getX()+1 == c.getX() || chunk.getX()+2 == c.getX()) && 
                    (chunk.getZ()-2 == c.getZ() || chunk.getZ()-1 == c.getZ() || chunk.getZ()== c.getZ() || chunk.getZ()+1 == c.getZ() || chunk.getZ()+2 == c.getZ()) && 
                    chunk.getWorld().getName().equalsIgnoreCase(c.getWorldName())) 
            {
                if(!c.getHordeName().equals(horde.getHordeName())) return true;
            }
        }
        return false;
    }
        
    public void claim(Player player, int grade) throws SQLException
    {
        String horde = hordeManager.getHordeName(player);
        if(horde == null)
        {
            player.sendMessage(formatMsg.format("<light_purple>Vous devez faire partie d'une HORDE pour utiliser cette commande"));
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        
        String sql = "REPLACE INTO irischunk (playerUUID, hordename, world, X1, Z1, typeMember, "
                + "jail, noBuild, noBreak, noInteract, noFire, noPVP, noKillAnimal, message) " 
                + "VALUES ('" + player.getUniqueId().toString() + "','" + horde + "','" + chunk.getWorld().getName() + "'," 
                + chunk.getX() + "," + chunk.getZ() + "," + grade + ",0,1,1,0,1,0,1,'NULL'); ";
        ConMySQL.executeInsert(sql);
        reload();
    }
    
    public void claim(Player player, Chunk chunk, int grade) throws SQLException
    {
        String horde = hordeManager.getHordeName(player);
        if(horde == null)
        {
            return;
        }
                
        String sql = "REPLACE INTO irischunk (playerUUID, hordename, world, X1, Z1, typeMember, "
                + "jail, noBuild, noBreak, noInteract, noFire, noPVP, noKillAnimal, message) " 
                + "VALUES ('" + player.getUniqueId().toString() + "','" + horde + "','" + chunk.getWorld().getName() + "'," 
                + chunk.getX() + "," + chunk.getZ() + "," + grade + ",0,1,1,0,1,0,1,'NULL'); ";
        ConMySQL.executeInsert(sql);
    }
    
    public void setQG(Player player) throws SQLException
    {
        Chunk chunk = player.getLocation().getChunk();
        int y = player.getLocation().getBlockY();
        
        Location locQG = chunk.getBlock(7, y, 7).getLocation();
        
        String sql = "replace into irishorde (hordeName, world, X1, Y1, Z1)" +
            "VALUES ('" + hordeManager.getHordeName(player) + "','" + locQG.getWorld().getName() + "'," + locQG.getBlockX() + "," + locQG.getBlockY() + ","
                            + locQG.getBlockZ() + ");";
        ConMySQL.executeInsert(sql);
                     
        claim(player,1);
                
        /*Location loc = new Location(locQG.getWorld(), locQG.getBlockX()-16, 0, locQG.getBlockZ());
        claim(player,loc.getChunk(),2);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+16, 0, locQG.getBlockZ());
        claim(player,loc.getChunk(),2);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()-16, 0, locQG.getBlockZ()+16);
        claim(player,loc.getChunk(),2);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX(), 0, locQG.getBlockZ()+16);
        claim(player,loc.getChunk(),2); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+16, 0, locQG.getBlockZ()+16);
        claim(player,loc.getChunk(),2);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()-16, 0, locQG.getBlockZ()-16);
        claim(player,loc.getChunk(),2);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX(), 0, locQG.getBlockZ()-16);
        claim(player,loc.getChunk(),2); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+16, 0, locQG.getBlockZ()-16);
        claim(player,loc.getChunk(),2);*/
        
        reload();
        
        World newBlock = locQG.getWorld();
        Block nb = newBlock.getBlockAt(locQG);
        nb.setType(Material.BEACON); 
        
        Location loc = new Location(locQG.getWorld(), locQG.getBlockX(), locQG.getBlockY()-1, locQG.getBlockZ());
        Block nb1 = newBlock.getBlockAt(loc);
        nb1.setType(Material.DIAMOND_BLOCK);
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()-1, locQG.getBlockY()-1, locQG.getBlockZ());
        Block nb2 = newBlock.getBlockAt(loc);
        nb2.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+1, locQG.getBlockY()-1, locQG.getBlockZ());
        Block nb3 = newBlock.getBlockAt(loc);
        nb3.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX(), locQG.getBlockY()-1, locQG.getBlockZ()+1);
        Block nb4 = newBlock.getBlockAt(loc);
        nb4.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+1, locQG.getBlockY()-1, locQG.getBlockZ()+1);
        Block nb5 = newBlock.getBlockAt(loc);
        nb5.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()-1, locQG.getBlockY()-1, locQG.getBlockZ()+1);
        Block nb6 = newBlock.getBlockAt(loc);
        nb6.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX(), locQG.getBlockY()-1, locQG.getBlockZ()-1);
        Block nb7 = newBlock.getBlockAt(loc);
        nb7.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()+1, locQG.getBlockY()-1, locQG.getBlockZ()-1);
        Block nb8 = newBlock.getBlockAt(loc);
        nb8.setType(Material.DIAMOND_BLOCK); 
        
        loc = new Location(locQG.getWorld(), locQG.getBlockX()-1, locQG.getBlockY()-1, locQG.getBlockZ()-1);
        Block nb9 = newBlock.getBlockAt(loc);
        nb9.setType(Material.DIAMOND_BLOCK); 
                            
    }
    
    public boolean setMessage(Player player, String message) throws SQLException
    {
        String horde = hordeManager.getHordeName(player);
        if(horde.isEmpty())
        {
            player.sendMessage(formatMsg.format("<light_purple>Vous devez faire partie d'une HORDE pour utiliser cette commande"));
            return false;
        }
        HChunk chunk = hasHChunk(player.getLocation());
        if(chunk == null)
        {
            player.sendMessage(formatMsg.format("<light_purple>Ce terrain n'est pas revendiqué"));
            return false;
        }
        if(hordeManager.getHordeName(player).contains(chunk.getHordeName()))
        {
            String sql = "UPDATE  irischunk SET message = '" + message 
                                + "' WHERE hordename = '" + hordeManager.getHordeName(player) + "' AND world = '" + player.getLocation().getWorld().getName() + "' AND " +
                    "X1 = " + player.getLocation().getChunk().getX() + " AND Z1 = " + player.getLocation().getChunk().getZ() + ";";
            ConMySQL.executeInsert(sql);
            reload();
            return true;
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette terre ne t'appartient pas, part pendant qu'il est encore temps !"));
            return false;
        }
    }
    
    public int getCountChunk(Horde horde)
    {
        int count = 0;
        for(HChunk c : chunks)
        {
            if(c.getHordeName().equalsIgnoreCase(horde.getHordeName())) 
            {
                count++;
            }
        }
        return count;
    }
    
    public HChunk getChunk(Location loc)
    {
        return hasHChunk(loc);
    }
    
    public Horde getHorde(Chunk chunk)
    {
        HChunk c = hasHChunk(chunk);
        if (c == null) return null;
        Horde horde = hordeManager.getHorde(c.getHordeName());
        return horde;
    }
    
    public double droitChunk(Player player)
    {
        Horde horde = hordeManager.getHorde(player);
        int nbChunk = getCountChunk(horde) - 1;
        String hordeName = horde.getHordeName();
        double force = conf.getDoubleYAML("horde.yml", "Horde." + hordeName + ".Force", 0 );
                
        double droit = 0;
        double x = force / 10.0;
        
        droit = Math.floor(x);
        droit = droit - nbChunk;
        return droit;
    }
    
    public double droitChunk(Horde horde)
    {
        int nbChunk = getCountChunk(horde) - 1;
        String hordeName = horde.getHordeName();
        double force = conf.getDoubleYAML("horde.yml", "Horde." + hordeName + ".Force", 0 );
        
        double droit = 0;
        double x = force / 10.0;
        
        droit = Math.floor(x);
        droit = droit - nbChunk;
        return droit;
    }
    
    public Boolean ChunkExist(Location loc)
    {
        
        for(HChunk c : chunks)
        {
            if(foundChunk(loc,c)) 
            {
                return true;
            }
        }
        return false;
    }
            
    public boolean BreakBlockQG(Location block, HChunk chunk)
    {
        String hordeName = chunk.getHordeName();
        Horde horde = hordeManager.getHorde(hordeName);
        Location QG = horde.getSpawnHorde();
        
        Location G0 = new Location(block.getWorld(), block.getBlockX()-4, QG.getBlockY(), block.getBlockZ());
        Location D0 = new Location(block.getWorld(), block.getBlockX()+4, QG.getBlockY(), block.getBlockZ());
        Location H0 = new Location(block.getWorld(), block.getBlockX(), QG.getBlockY(), block.getBlockZ()-4);
        Location B0 = new Location(block.getWorld(), block.getBlockX(), QG.getBlockY(), block.getBlockZ()+4);
        
        if(getChunk(G0) == chunk && block.getBlockY() < QG.getBlockY()+ 2 && block.getBlockY() > QG.getBlockY()-1)
        {
            if(getChunk(D0) == chunk && block.getBlockY() < QG.getBlockY()+ 2 && block.getBlockY() > QG.getBlockY()-1)
            {
                if(getChunk(H0) == chunk && block.getBlockY() < QG.getBlockY()+ 2 && block.getBlockY() > QG.getBlockY()-1)
                {
                    if(getChunk(B0) == chunk && block.getBlockY() < QG.getBlockY()+ 2 && block.getBlockY() > QG.getBlockY()-1){return false;}
                }
            }
        }
        return true;
    }
    
    public void reload()
    {
        LoadChunk();
    }
}

