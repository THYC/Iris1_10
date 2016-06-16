package net.teraoctet.iris.commands;

import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.*;
import net.teraoctet.iris.messageauto.Message;
import net.teraoctet.iris.messageauto.MessageAuto;
import static net.teraoctet.iris.messageauto.MessageAuto.messages;
import net.teraoctet.iris.utils.ConnexionMySQL;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_AutoMessage implements CommandExecutor
{    
    Iris plugin;
    private static final ConfigFile conf = new ConfigFile();
    
    public Command_AutoMessage(Iris plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player)sender;
        if (commandLabel.equalsIgnoreCase("am") && (sender.isOp() || sender.hasPermission("iris.automessage.admin")))
        {
            ConnexionMySQL.getInstance(
                conf.getStringYAML("config.yml","dbURL","jdbc:mysql://localhost//iris"),  
                conf.getStringYAML("config.yml","user","root"), 
                conf.getStringYAML("config.yml","password","sqlirispw"));
            
            if("stop".equalsIgnoreCase(args[0]))
            {
                plugin.messageAuto.StopMessageAuto(player);
                player.sendMessage(Iris.formatMsg.format("<aqua>MessageAuto stop<e_ai>e"));
                return true;
            }

            if("run".equalsIgnoreCase(args[0]))
            {
                plugin.messageAuto.Broadcast();
                player.sendMessage(Iris.formatMsg.format("<aqua>MessageAuto demarr<e_ai>e"));
                return true;
            }

            if("add".equalsIgnoreCase(args[0]))
            {
                if(args.length == 4)
                {                        
                    String sql = "INSERT INTO irismessagename (messagename, groupeperm, actif)" + 
                                "VALUES ('" + args[1] + "','" + args[2] + "'," + Integer.parseInt(args[3]) + "); ";

                    ConMySQL.executeInsert(sql);
                    player.sendMessage(formatMsg.format("<aqua>Groupe de message [ " + args[1] + " <aqua>] ajout<e_ai>"));
                }
                else
                {
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am add <aqua>[Groupe] [permission] [actif: 0/1]"));
                }
                return true;
            }

            if("del".equalsIgnoreCase(args[0]))
            {
                if(args.length == 2)
                { 
                    String sql = "DELETE FROM irismessagename WHERE messagename = '" + args[1] + "'; ";
                    ConMySQL.executeUpdate(sql);
                
                    player.sendMessage(formatMsg.format("<yellow>Message <green>[ " + args[1] + " <yellow>] supprim<e_ai>"));
                }
                else
                {
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am del <aqua>[NomMessage]"));
                }
                return true;
            }

            if("list".equalsIgnoreCase(args[0]))
            {
                if(args.length == 1)
                { 
                    String txt = "";
                    for (Message msg : messages) 
                    {
                        txt = txt + msg.messagename + " [" + msg.groupeperm + "] [" + msg.actif + "]\n";
                    }
                    player.sendMessage(formatMsg.format(txt));
                    return true;
                }

                if(args.length == 2)
                { 
                    String txt = formatMsg.format(conf.getConfigTXT("//message//" + args[1] + ".txt"));
                    player.sendMessage(formatMsg.format(txt));
                }
                else
                {
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am list <aqua>[Message] <yellow>- pour voir le contenu du message"));
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am list <yellow>- pour lister toutes les messages"));
                }
                return true;
            }

            if("off".equalsIgnoreCase(args[0]))
            {
                if(args.length == 2)
                { 
                    String sql = "UPDATE irismessagename SET actif = 0 where messagename = '" + args[1] + "';";
                    ConMySQL.executeInsert(sql);
                    
                    player.sendMessage(formatMsg.format("<grey>Message désactivé"));
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am off <yellow>[Message]"));
                }
            }

            if("on".equalsIgnoreCase(args[0]))
            {     
                if(args.length == 2)
                { 
                    String sql = "UPDATE irismessagename SET actif = 1 where messagename = '" + args[1] + "';";
                    ConMySQL.executeInsert(sql);

                    player.sendMessage(formatMsg.format("<grey>Message actif"));
                    return true;
                }
                else
                {
                    player.sendMessage(formatMsg.format("<yellow>Usage: <green>/am on <yellow>[Message]"));
                }
            }

            if("reload".equalsIgnoreCase(args[0]))
            {
                MessageAuto.messages.clear();
                MessageAuto.filterMessage.clear();
                plugin.messageAuto.chargeMessageName();
                plugin.messageAuto.vfilterMessageName(1);
                player.sendMessage(formatMsg.format("<aqua>Message reload complete"));
                return true;
            }
        }
        return true;
    }
}
