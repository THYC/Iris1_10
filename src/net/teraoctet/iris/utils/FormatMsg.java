package net.teraoctet.iris.utils;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

    public class FormatMsg 
    {        
        /**
        * Création d'un fichier de configuration YAML
        * 
        * @param msg message à formater [String]
        * 
        * @return retourne le message formaté [String]
        */
        public String format(String msg)
        {
            if(msg == null)
            {
                return "";
            }
            //-----------------------------
            //      Caractère spéciaux
            //-----------------------------
            msg = msg.replaceAll("\\<a_ai\\>", "á");
            msg = msg.replaceAll("\\<a_gr\\>", "à");
            msg = msg.replaceAll("\\<e_ai\\>", "é");
            msg = msg.replaceAll("\\<e_gr\\>", "è");
            msg = msg.replaceAll("\\<e_cir\\>", "ê");
            msg = msg.replaceAll("\\<u_cir\\>", "û");
            msg = msg.replaceAll("\\<u_gr\\>", "ù");
            msg = msg.replaceAll("\\<2points\\>", ":");
            //-----------------------------
            //            Icone
            //-----------------------------
            msg = msg.replaceAll("\\<mort\\>", "☠");
            msg = msg.replaceAll("\\<smile\\>","☺");
            msg = msg.replaceAll("\\<soleil\\>","☼");
            msg = msg.replaceAll("\\<heart\\>","❤");
            msg = msg.replaceAll("\\<heart2\\>","♥");
            msg = msg.replaceAll("\\<star\\>","★");
            //-----------------------------
            //            Variable
            //-----------------------------
            msg = msg.replaceAll("\\<online\\>", String.valueOf(numberOfOnlinePlayers()));
            //-----------------------------
            //           Couleur
            //-----------------------------
            msg = msg.replaceAll("\\<dark_red\\>","\u00A74");
            msg = msg.replaceAll("\\<red\\>","\u00A7c");
            msg = msg.replaceAll("\\<gold\\>","\u00A76");
            msg = msg.replaceAll("\\<yellow\\>","\u00A7e");
            msg = msg.replaceAll("\\<dark_green\\>","\u00A72");
            msg = msg.replaceAll("\\<green\\>","\u00A7a");
            msg = msg.replaceAll("\\<aqua\\>","\u00A7b");
            msg = msg.replaceAll("\\<dark_aqua\\>","\u00A73");
            msg = msg.replaceAll("\\<dark_blue\\>","\u00A71");
            msg = msg.replaceAll("\\<blue\\>","\u00A71");
            msg = msg.replaceAll("\\<light_purple\\>","\u00A7d");
            msg = msg.replaceAll("\\<dark_purple\\>","\u00A75");
            msg = msg.replaceAll("\\<white\\>","\u00A7f");
            msg = msg.replaceAll("\\<gray\\>","\u00A77");
            msg = msg.replaceAll("\\<dark_gray\\>","\u00A78");
            msg = msg.replaceAll("\\<black\\>","\u00A70");
            //-----------------------------
            //           Style
            //-----------------------------
            msg = msg.replaceAll("\\<magic\\>","\u00A7k");
            msg = msg.replaceAll("\\<italique\\>","\u00A7o");
            msg = msg.replaceAll("\\<gras\\>","\u00A7l");
            msg = msg.replaceAll("\\<barre\\>","\u00A7m");
            msg = msg.replaceAll ("\\<underline\\>", "\u00A7n");
            return msg;
        }  
        
        public String format(String msg, Player player)
        {
            if(msg == null)
            {
                msg = "";
                return msg;
            }
            msg = format(msg);
            //-----------------------------
            //            Variable
            //-----------------------------
            msg = msg.replaceAll("\\<joueur\\>", player.getName());
            msg = msg.replaceAll("\\<PLAYER\\>", player.getName());
            msg = msg.replaceAll("\\<player\\>", player.getName());
            msg = msg.replaceAll("\\<world\\>", player.getWorld().getName());
            msg = msg.replaceAll("\\<time\\>", timeToString(player.getWorld().getTime()));
            msg = msg.replaceAll("\\<displayname\\>", String.valueOf(player.getDisplayName()));
            
            return msg;
        }       

        public static int numberOfOnlinePlayers()
        {
            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
            return onlinePlayers.size();
        }
        
        public String timeToString(long time) 
        {
            int hours = (int) ((Math.floor(time / 1000.0) + 8) % 24); // '8' is the offset
            int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
            return String.format("%02d:%02d", hours, minutes);
        }
        
        public long convertSecondeToTick(int minute) 
        {
            long tick = (long) Math.floor((minute * 1000) * 1000 / 60);
            return tick;
        }
        
        public static Long parseTime(String timeString)
        throws NumberFormatException
        {
            int weeks = 0;
            int days = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;

            Pattern p = Pattern.compile("\\d+[a-z]{1}");
            Matcher m = p.matcher(timeString);
            boolean result = m.find();
            while (result)
            {
                String argument = m.group();
                if (argument.endsWith("w")) {
                  weeks = Integer.parseInt(argument.substring(0, argument.length() - 1));
                } else if (argument.endsWith("d")) {
                  days = Integer.parseInt(argument.substring(0, argument.length() - 1));
                } else if (argument.endsWith("h")) {
                  hours = Integer.parseInt(argument.substring(0, argument.length() - 1));
                } else if (argument.endsWith("m")) {
                  minutes = Integer.parseInt(argument.substring(0, argument.length() - 1));
                } else if (argument.endsWith("s")) {
                  seconds = Integer.parseInt(argument.substring(0, argument.length() - 1));
                }
                result = m.find();
            }
            long time = seconds;
            time += minutes * 60;
            time += hours * 3600;
            time += days * 86400;
            time += weeks * 604800;
            time *= 1000L;

            return time;
        }
    }
