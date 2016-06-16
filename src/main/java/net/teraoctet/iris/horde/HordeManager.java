package net.teraoctet.iris.horde;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.chunkManager;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import static net.teraoctet.iris.Iris.hordeManager;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HordeManager 
{
    private static final ArrayList<Horde> hordes = new ArrayList<>();
    private static final ArrayList<HordeMember> hordeMembers = new ArrayList<>();
    
    public Boolean HasHorde(Player player)
    {
        for(HordeMember member : hordeMembers)
        {
            if(member.getPlayerUUID().equals(player.getUniqueId().toString()))
            {
                return true;
            } 
        }
        return false;
    }
    
    public String listeHorde (String hordeName)
    {
        String liste = "\n";
        String liste1 = listeGrade(hordeName,1);
        String liste2 = listeGrade(hordeName,2);
        String liste3 = listeGrade(hordeName,3);
        String liste4 = listeGrade(hordeName,4);
        
        liste = liste + "<gray>Chef de Horde <star>: <italique><white>" + liste1 + " \n";
        liste = liste + "<gray>Combattant **  : <italique><white>" + liste2 + " \n";
        liste = liste + "<gray>Combattant *** : <italique><white>" + liste3 + " \n";
        liste = liste + "<gray>Recrue         : <italique><white>" + liste4 + " \n";
       
        return liste;
    }
    
    public String listeHorde (Horde horde)
    {
        String liste = "";
        
        for(HordeMember member : hordeMembers)
        {
            if(member.getHordeName().equals(horde.getHordeName()))
            {
                liste = member.getPlayerUUID() + ";" + liste;
            } 
        }
        return liste;
    }
    
    public String listeHorde (String hordeName, int grade)
    {
        String liste = "";
        for(HordeMember member : hordeMembers)
        {
            if(member.getHordeName().equals(hordeName) && member.getTypeMember() == grade)
            {
                liste = liste + ";" + member.getPlayerUUID();
            } 
        }
        return liste;
    }
    
    public String getAllHordes ()
    {
        String liste = "<dark_gray><gras>------- HORDES --------- \n";
        for(Horde horde : hordes)
        {
            liste = liste + horde.getHordeName() + "\n";
        }
        return liste;
    }
    
    public String listeGrade (String hordeName, int grade)
    {
        String liste = " ";
        for(HordeMember member : hordeMembers)
        {
            if(member.getHordeName().contains(hordeName) && member.getTypeMember() == grade)
            {
                liste = liste + " - " + member.getPlayerName();
            } 
        }
        return liste;
    }
    
    public int getCountGrade (String hordeName, int grade)
    {
        int count = 0;
        for(HordeMember member : hordeMembers)
        {
            if(member.getHordeName().equals(hordeName) && member.getTypeMember() == grade)
            {
                count++;
            } 
        }
        return count;
    }
    
    public int getCountMember(Horde h)
    {
        int count = 0;
        for(HordeMember member : hordeMembers)
        {
            if(member.getHordeName().equals(h.getHordeName()))
            {
                count++;
            } 
        }
        return count;
    }
    
    public int getGrade(Player player)
    {
        int grade = 4;
        for(HordeMember member : hordeMembers)
        {
            if(member.getPlayerUUID().equalsIgnoreCase(player.getUniqueId().toString()))
            {
                return member.getTypeMember();
            } 
        }
        return grade;
    }
    
    public void setGrade(Player player, String hordeName, int grade)
    {
        String sql = "REPLACE INTO irishordemember (playerUUID, playerName, hordeName, typeMember)" + 
                        "VALUES ('" + player.getUniqueId().toString() + "','" + player.getDisplayName() + "','" + hordeName  + "'," + grade + ");" ;
        ConMySQL.executeInsert(sql);
        reload();
        player.sendMessage(formatMsg.format("<yellow>Vous avez maintenant le grade " + grade + " dans la horde " + hordeName));
    }
    
    public void setGrade(String UUID, int grade)
    {
        String sql = "UPDATE irishordemember SET typeMember = " + grade + " WHERE playerUUID = '" + UUID + "';" ;           
        ConMySQL.executeInsert(sql);
        reload();
    }
    
    public String getHordeName(Player player)
    {                        
        String hordeName = null;
        
        for(HordeMember member : hordeMembers)
        {
            if(member.getPlayerUUID().equalsIgnoreCase(player.getUniqueId().toString()))
            {
                return member.getHordeName();
            } 
        }
        return hordeName;
    }
    
    public void printInfoPlayer(Player player, String playerName)
    {           
        String hordeName = hordeManager.getHordeName(playerName);
        Horde horde = hordeManager.getHorde(hordeName);
        double droit = chunkManager.droitChunk(horde);
        int nbChunk = chunkManager.getCountChunk(horde) - 1;
        
                        
        player.sendMessage(Iris.formatMsg.format("<gold>Stats '" + hordeName + "'"));
        player.sendMessage(Iris.formatMsg.format("<gray>-----------------------------"));
        player.sendMessage(Iris.formatMsg.format("<gray>Force : <gold>" + conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Force", 0 ) + "  <gray>%Domage : <gold>" + hordeManager.getDamage(hordeName)));
        player.sendMessage(Iris.formatMsg.format("<gray>Membre mort : <red>" + conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Dead", 0 ) + "  <gray>Ennemi tué : <green>" + conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Kill", 0 )));
        player.sendMessage(Iris.formatMsg.format("<gray>-----------------------------"));
        player.sendMessage(Iris.formatMsg.format("<gray>Banque : " + conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Bank", 0 )));
        player.sendMessage(Iris.formatMsg.format("<gray>Nb territoire acquis : " + nbChunk));
        player.sendMessage(Iris.formatMsg.format("<gray>Droit(claim) restant : " + droit + " territoire(s)"));
        player.sendMessage(Iris.formatMsg.format("<gray>-----------------------------"));
    }
    
    public String getHordeNameToName(String HordeName)
    {                        
        String hordeName = null;
        
        for(Horde horde : hordes)
        {
            if(horde.getHordeName().contains(HordeName))
            {
                return horde.getHordeName();
            } 
        }
        return hordeName;
    }
    
    public String getHordeName(String playerName)
    {                
        String hordeName = null;
        for(HordeMember member : hordeMembers)
        {
            if(member.getPlayerName().contains(playerName))
            {
                return member.getHordeName();
            } 
        }
        return "Vagabond";
    }
    
    public Horde getHorde(Player player)
    {                
        for(Horde horde : hordes)
        {
            if(horde.get4().contains(player.getUniqueId().toString()) || horde.get3().contains(player.getUniqueId().toString()) 
                    || horde.get2().contains(player.getUniqueId().toString()) || horde.get1().contains(player.getUniqueId().toString()))
            {
                return horde;
            } 
        }
        return null;
    }
    
    public Horde getHorde(String hordeName)
    {                
        for(Horde horde : hordes)
        {
            if(horde.getHordeName().contains(hordeName))
            {
                return horde;
            } 
        }
        return null;
    }
    
    public void loadHordes()
    {     
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            try 
            {
                ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irishorde");
                String members1;
                String members2;
                String members3;
                String members4;
                
                if (rs != null) 
                {
                    hordes.clear();
                    
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
                        Horde horde = new Horde(
                                    rs.getString("hordeName"),
                                    rs.getString("world"),
                                    rs.getInt("X1"), 
                                    rs.getInt("Y1"), 
                                    rs.getInt("Z1"),
                                    members1,
                                    members2,
                                    members3,
                                    members4);
                        hordes.add(horde);
                    }
                    loadHordeMember();
                    Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Horde actif"));
                }
            } 
            catch (SQLException ex) 
            {
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<red>[Iris] Erreur de lecture Horde sur MySQL"));
                Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void loadHordeMember() //throws SQLException//throws SQLException
    {
        if(ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw")))
        {
            ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irishordemember");

            if (rs != null) 
            {
                hordeMembers.clear();
                try {
                    while (rs.next())
                    {
                        HordeMember member = new HordeMember
                        (
                                rs.getString("playerUUID"),
                                rs.getString("playerName"),
                                rs.getString("hordeName"),
                                rs.getInt("typeMember"),
                                rs.getInt("power"));

                        hordeMembers.add(member);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(HordeManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public double getDamage(String hordeName)
    {
        int force = conf.getIntYAML("horde.yml", "Horde." + hordeName + ".Force", 0);
        //if (force < 0) force = 0;
        double damage = 0;
        force = force / 10;
        if(force > 0)
        {
            damage = 1.00 + (force / 20.00);
            if (damage > 1.50) {damage = 1.50;}
        }
        else if(force == 0)
        {
            damage = 0;
        }
        else if(force < 0)
        {
            damage = 1.00 - (force / 10.00);
            if(damage < 0.70){damage = 0.70;}
        }
        conf.setDoubleYAML("horde.yml", "Horde." + hordeName + ".Damage", damage);
        
        return damage ;
    }
  
    public Boolean MembersHasOnline(Horde horde)
    {
        if (horde == null)return false;
        for(HordeMember member : hordeMembers)
        {       
            if(member.getHordeName() == null ? horde.getHordeName() == null : member.getHordeName().equals(horde.getHordeName()))
            {
                for(Player player :Bukkit.getOnlinePlayers())
                {
                    if(player.getUniqueId().toString().contains(member.getPlayerUUID()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public Horde getHEnnemi(Player p)
    {
        Horde targ = null;
        for (Horde horde : hordes) 
        {
            if ((horde.getWorldName().equals(p.getWorld().getName())) && (horde != getHorde(p)) && 
              ((targ == null) || (p.getLocation().distance(horde.getSpawnHorde()) < targ.getSpawnHorde().distance(p.getLocation()))) && 
              (horde.getSpawnHorde().distance(p.getLocation()) < 100.0D)) 
            {
                targ = horde;
            }
        }
        return targ;
    }
    
    public void reload()
    {
        loadHordes();
    }
}
