package net.teraoctet.iris.messageauto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.ConMySQL;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageAuto
{
    public static final ArrayList<Message> messages = new ArrayList<>();
    public static final ArrayList<Message> filterMessage = new ArrayList<>();
    private static final ConfigFile conf = new ConfigFile();
    private int index = 0;
    private String txt = "";
    private String perm = "";
    private static int taskID = 0;
    
    Iris plugin;
    
    public MessageAuto(Iris plugin)
    {
        this.plugin = plugin;
    }
           
    public void Broadcast()
    {
        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (Bukkit.getOnlinePlayers().size() < 2)return;
                    Message msgName = (filterMessage.get(index));
                    index += 1;
                    if (index == filterMessage.size()) index = 0;
                    txt = "";
                                        
                    txt = formatMsg.format(conf.getConfigTXT("//message//" + msgName.messagename + ".txt"));
                    perm = "iris.automessage.receive." + msgName.groupeperm;
                    
                }
                catch(Exception ex)
                {
                    plugin.getServer().getScheduler().cancelTask(taskID);
                    Iris.log.info("IRIS: Message auto OFF");
                }
                org.bukkit.Bukkit.broadcast(Iris.formatMsg.format(txt),perm);
            }
        }, conf.getIntYAML("config.yml", "IntervalMessageAuto",1200), conf.getIntYAML("config.yml", "DelaisMessageAuto",4000));
    }
      
    public void chargeMessageName()
    {            
        ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
        try 
        {
            ResultSet rs = ConMySQL.executeSelect("SELECT * FROM irismessagename ORDER BY messagename");
            if (rs != null) 
            {
                while (rs.next()) 
                {
                    Message message = new Message(rs.getString("messagename"),rs.getString("groupeperm"),rs.getInt("actif"));
                    messages.add(message);
                }
                Bukkit.getConsoleSender().sendMessage(formatMsg.format("<green>[Iris] Entetes messages active"));
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(ConnexionMySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public void vfilterMessageName(int actif)
    {
        filterMessage.clear();
        for (Message msg : messages)
        {
            if(msg.actif == actif)
            {
                filterMessage.add(msg);
            }
        }
    } 
    
    public void StopMessageAuto(Player player)
    {
        this.plugin.getServer().getScheduler().cancelTask(taskID);
        player.sendMessage(Iris.formatMsg.format("<aqua>MessageAuto stop<e_ai>"));
    }
}

