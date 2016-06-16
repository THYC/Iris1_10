package net.teraoctet.iris.parcelle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class ParcelleManager 
{
    private static final ArrayList<Parcelle> parcelles = new ArrayList<>();
    private static final ArrayList<Parcelle> jails = new ArrayList<>();
    private static final ArrayList<Member> members = new ArrayList<>();
    private static HashMap<String, ParcelleManager> setts = new HashMap();
    private Location border1;
    private Location border2;
    private Player player;
    
    public ParcelleManager(Player arg0, Location arg1, Location arg2)
    {
        this.player = arg0;
        this.border1 = arg1;
        this.border2 = arg2;
    }
    
    public static ParcelleManager getSett(Player player)
    {
        if (!setts.containsKey(player.getName()))
        {
            ParcelleManager sett = new ParcelleManager(player, null, null);
            setts.put(player.getName(), sett);
            return sett;
        }
        ParcelleManager sett = (ParcelleManager)setts.get(player.getName());
        sett.setPlayer(player);
        return sett;
    }

    public ParcelleManager() 
    {
        
    }
    
    public void setPlayer(Player a)
    {
        this.player = a;
    }
    
    public void loadParcelle()
    {     
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            try 
            {
                ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irisparcelle ORDER BY jail desc");
                String members;
                String owner;

                if (rs != null) 
                {
                    parcelles.clear();
                    
                    while (rs.next()) 
                    {
                        members = "";
                        owner = "";
                        ResultSet member = ConMySQL.executeSelect("SELECT * FROM irisparcellemember "
                                + "where parcelleName = '" + rs.getString("parcelleName") + "'");
                        while (member.next())
                        {
                            if(member.getInt("typeMember") == 1)
                            {
                                owner = member.getString("playerUUID") + ";" + owner;
                            }
                            members = members + member.getString("playerUUID") + ";";                
                        }
                        Parcelle parcelle = new Parcelle(
                                rs.getString("parcelleName"),
                                rs.getString("parent"),
                                rs.getString("world"),
                                rs.getInt("X1"), 
                                rs.getInt("Y1"),
                                rs.getInt("Z1"),
                                rs.getInt("X2"),
                                rs.getInt("Y2"),
                                rs.getInt("Z2"),
                                rs.getInt("jail"),
                                rs.getInt("noEnter"), 
                                rs.getInt("noFly"),
                                rs.getInt("noBuild"),
                                rs.getInt("noBreak"),
                                rs.getInt("noTeleport"),
                                rs.getInt("noInteract"), 
                                rs.getInt("noFire"),
                                rs.getString("message"),
                                rs.getInt("mode"),
                                rs.getInt("noMob"),
                                rs.getInt("noTNT"),
                                owner, 
                                members);
                        if(parcelle.getJail()==1)
                        {
                            jails.add(parcelle);
                        }
                        else
                        {
                            parcelles.add(parcelle);
                        }
                        
                    }
                    loadMember();
                    Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Parcelle active"));
                }
            } 
            catch (SQLException ex) 
            {
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Erreur de lecture Parcelle sur MySQL"));
                Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void loadMember()
    {     
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            try 
            {
                ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irisparcellemember");
                
                if (rs != null) 
                {
                    members.clear();
                    
                    while (rs.next()) 
                    {
                        Member member = new Member(
                                rs.getString("parcelleName"),
                                rs.getString("playerUUID"),
                                rs.getString("playerName"), 
                                rs.getInt("typeMember"));
                        members.add(member);
                    }
                }
            } 
            catch (SQLException ex) 
            {
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Erreur de lecture MemberParcelle sur MySQL"));
                Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
  
    private Parcelle parcelleContainsVector(Location loc, boolean flagJail)
    {
        if (flagJail == true)
        {
            for(Parcelle j : jails)
            {
                if(foundParcelle(loc,j)) 
                {
                    return j;
                }
            }
        }
        else
        {
            for(Parcelle p : parcelles)
            {
                if(foundParcelle(loc,p)) 
                {
                    return p;
                }
            }
        }
        return null;
    }
        
    private boolean foundParcelle(Location loc, Parcelle p)
    {
        if (p.getWorldName().equalsIgnoreCase(loc.getWorld().getName()) == false)
        {
            return false;
        }
        if ((loc.getBlockX() < p.getX1() ) || (loc.getBlockX() > p.getX2())) 
        {
            return false;
        }
        if ((loc.getBlockZ() < p.getZ1()) || (loc.getBlockZ() > p.getZ2())) 
        {
            return false;
        }
        if ((loc.getBlockY() < p.getY1()) || (loc.getBlockY() > p.getY2())) 
        {
            return false;
        }
        return true;
    }
  
    public Parcelle getParcelle(Location loc, boolean flagJail)
    {
        return parcelleContainsVector(loc, flagJail);
    }
    
    public Parcelle getParcelle(Location loc)
    {
        return parcelleContainsVector(loc, false);
    }
    
    public Boolean hasParcelle(String name)
    {
        for(Parcelle p : parcelles)
        {
            if(p.getName().contains(name)) 
            {
                return true;
            }
        }
        return false;
    }
    
    public Boolean hasJail(String name)
    {
        for(Parcelle j : jails)
        {
            if(j.getName().contains(name)) 
            {
                return true;
            }
        }
        return false;
    }
    
    public Parcelle getParcelle(String name)
    {
        for(Parcelle p : parcelles)
        {
            if(p.getName().contains(name)) 
            {
                return p;
            }
        }
        return null;
    }
    
    public String getParcelleOwner(String name)
    {
        for(Parcelle p : parcelles)
        {
            if(p.getName().contains(name)) 
            {
                return p.getuuidOwner();
            }
        }
        return null;
    }
    
    public String getParcelleOwnerName(Parcelle parcelle)
    {
        String listMembers = "";
        for(Member m : members)
        {
            if(m.getParcelleName().contains(parcelle.getName()) && m.getMemberType() == 1) 
            {
                listMembers = listMembers + m.getPlayerName() + ";";
            }
        }
        return listMembers;
    }
    
    public String getParcelleAllowed(String name)
    {
        for(Parcelle p : parcelles)
        {
            if(p.getName().equals(name)) 
            {
                return p.getAllowed();
            }
        }
        return null;
    }
    
    public String getParcelleAllowedName(Parcelle parcelle)
    {
        String listMembers = "";
        for(Member m : members)
        {
            if(m.getParcelleName().contains(parcelle.getName())) 
            {
                listMembers = listMembers + m.getPlayerName() + ";";
            }
        }
        return listMembers;
    }
    
    public void setBorder(int a, Location b)
    {
        if (a == 1) 
        {
            this.border1 = b;
        } else if (a == 2) {
            this.border2 = b;
        }
    }
    
    public Location getBorder1()
    {
        if ((this.border1 != null) && (this.border2 != null))
        {
            World world = this.border1.getWorld();
            int x0 = this.border1.getBlockX();
            int y0 = this.border1.getBlockY();
            int z0 = this.border1.getBlockZ();

            int x1 = this.border2.getBlockX();
            int y1 = this.border2.getBlockY();
            int z1 = this.border2.getBlockZ();

            return new Location(world, Math.min(x0, x1), Math.min(y0, y1), Math.min(z0, z1));
        }
        return this.border1;
    }
  
    public Location getBorder2()
    {
        if ((this.border1 != null) && (this.border2 != null))
        {
            int x0 = this.border1.getBlockX();
            int y0 = this.border1.getBlockY();
            int z0 = this.border1.getBlockZ();

            World world = this.border2.getWorld();
            int x1 = this.border2.getBlockX();
            int y1 = this.border2.getBlockY();
            int z1 = this.border2.getBlockZ();

          return new Location(world, Math.max(x0, x1), Math.max(y0, y1), Math.max(z0, z1));
        }
        return this.border2;
    }
    
    public String getFlag(Parcelle parcelle)
    {
        String flag = "Jail(prison) : " + parcelle.getJail() + " | ";
        flag = flag + "noFly(vol) : " + parcelle.getNoFly() + " | ";
        flag = flag + "noBuild(construire) : " + parcelle.getNoBuild() + " | ";
        flag = flag + "noBreak(casser) : " + parcelle.getNoBreak() + " | ";
        flag = flag + "notp(téléportation) : " + parcelle.getNoTeleport() + " | ";
        flag = flag + "noInterac(Interaction) : " + parcelle.getNoInteract() + " | ";
        flag = flag + "noFire(Incendie) : " + parcelle.getNoFire() + " | ";
        flag = flag + "Gamemode : " + parcelle.getGamemode() + " | ";
        flag = flag + "noMob(monstre) : " + parcelle.getNoMob() + " | ";
        flag = flag + "noTNT(TNT) : " + parcelle.getNoTNT() + " | ";
        return flag;
    }
    
    public void reload()
    {
        loadParcelle();
    }
        
    public boolean setMessage(Player player, Parcelle parcelle, String message)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET message = '" + message
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
            return true;
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
            return false;
        }
    }
    
    public void setNoInteract(Player player, Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noInteract = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoEnter(Player player, Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noEnter = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setJail(Player player, Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET jail = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoFly(Player player, Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noFly = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoBuild(Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noBuild = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoBreak(Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noBreak = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoTeleport(Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noTeleport = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setNoFire(Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noFire = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
    public void setGameMode(Parcelle parcelle, int gamemode)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET mode = '" + gamemode
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
    
        public void setNoMob(Parcelle parcelle, int OnOff)
    {
        if(parcelle.getuuidOwner().contains(player.getUniqueId().toString()))
        {
            String sql = "UPDATE  irisparcelle SET noMob = '" + OnOff
                                + "' WHERE parcelleName = '" + parcelle.getName() + "';";
            ConMySQL.executeInsert(sql);
            reload();
        }
        else
        {
            player.sendMessage(formatMsg.format("<light_purple>Cette parcelle ne t'appartient pas"));
        }
    }
        
    public void listParcelle(Player player, boolean flagJail)
    {
        if (flagJail == true)
        {
            for(Parcelle j : jails)
            {
                player.sendMessage(j.getName());
            }
        }
        else
        {
            for(Parcelle p : parcelles)
            {
                player.sendMessage(p.getName());
            }
        }
    }
    
    public boolean parcelleActive(String world, Location l1, Location l2)
    {                
        Parcelle parcelle = new Parcelle(world,this.border1.getBlockX(),0,l1.getBlockZ(),l2.getBlockX(),500,l2.getBlockZ());
                
        for(Parcelle p : parcelles)
        {
            if(p.getLocation1().getWorld()!=null && p.getLocation2().getWorld()!=null)
            {
                if(foundParcelle(p.getLocation1(),parcelle) || foundParcelle(p.getLocation2(),parcelle) || 
                        foundParcelle(p.getLocation3(),parcelle) || foundParcelle(p.getLocation4(),parcelle) || 
                        foundParcelle(p.getLocation5(),parcelle) || foundParcelle(p.getLocation6(),parcelle) ||
                        foundParcelle(p.getLocation7(),parcelle) || foundParcelle(p.getLocation8(),parcelle)) 
                {
                    return true;
                }
            }
        }
        return false;
    }
}
