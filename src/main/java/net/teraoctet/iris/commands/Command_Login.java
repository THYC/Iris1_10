package net.teraoctet.iris.commands;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import net.teraoctet.iris.ConfigFile;
import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.formatMsg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_Login
implements CommandExecutor
{
    private static MessageDigest md;
    private static final ConfigFile conf = new ConfigFile();
        
    public static class Global
    {
        public static final ArrayList<UUID> PlayersToRegister = new ArrayList();
        public static final ArrayList<UUID> PlayersToUnlock = new ArrayList();
        public static final ArrayList<UUID> MessageSend = new ArrayList();
        public static final ArrayList<UUID> PlayersMove = new ArrayList();
    }
  
    public static String cryptWithMD5(String pass)
    {
        try
        {
            md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digested.length; i++) 
            {
                sb.append(Integer.toHexString(0xFF & digested[i]));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException ex)
        {
            Bukkit.getServer().getLogger().severe("Impossible d'encrypter le password!");
        }
        return null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if ((sender instanceof Player))
        {
            Player player = (Player)sender;
            if (cmd.getName().equalsIgnoreCase("register"))
            {
                if (conf.IsConfigYAML("userdata",player.getUniqueId()+ ".yml","password") == false)
                {
                    if (args.length == 0)
                    {
                        player.sendMessage(Iris.formatMsg.format("<green>Pour t'enregistrer tape <yellow>/register <aqua>[tonMotDePasse] [confirme tonMotDePasse]"));
                    }
                    else if (args.length == 2)
                    {
                        String password1 = args[0];
                        String password2 = args[1];
                        if (password1.equals(password2))
                        {
                            String Encrypted = cryptWithMD5(password1);
                            conf.setStringYAML("userdata",player.getUniqueId()+ ".yml", "password", Encrypted);
                            Global.PlayersToRegister.remove(player.getUniqueId());
                            Global.PlayersToUnlock.remove(player.getUniqueId());
                            Global.MessageSend.remove(player.getUniqueId());
                            Global.PlayersMove.add(player.getUniqueId());
                            String txt_newplayer = formatMsg.format(conf.getConfigTXT("//message//newplayer.txt"),player);
                            player.getPlayer().sendMessage(txt_newplayer);
                            player.sendMessage(Iris.formatMsg.format("<yellow>Enregistrement effectué"));
                            player.sendMessage(Iris.formatMsg.format("<green>Pour te logger les fois suivantes tape : <yellow>/delock <aqua>[tonMotDePasse]"));
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "Les password sont differents ! recommencer");
                        }
                    }
                    else
                    {
                        player.sendMessage(Iris.formatMsg.format("<red>Utilisation incorrect! tape <yellow>/register <aqua>[tonMotDePasse] [confirme tonMotDePasse]"));
                    }
                }
                else 
                {
                    player.sendMessage(Iris.formatMsg.format("<red>Vous etes déjà enregistré, pour changer le password tape <yellow>/passchange <aqua>[ancien_motDePasse] [new motDePasse] [confirm motDePasse]"));
                }
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("passchange")) 
            {
                if (args.length != 3)
                {
                    player.sendMessage(Iris.formatMsg.format("<green>Pour changer le password tape <yellow>/passchange <aqua>[ancien_motDePasse] [new motDePasse] [confirm motDePasse]"));
                }
                else if (args.length == 3)
                {
                    String EncryptedOld = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "password","NULL");
                    String GivenPassword = args[0];
                    String EncryptedGiven = cryptWithMD5(GivenPassword);
                    String NewPassword1 = args[1];
                    String NewPassword2 = args[2];
                    if (EncryptedOld.equals(EncryptedGiven))
                    {
                        if (NewPassword1.equals(NewPassword2))
                        {
                            String Encrypted = cryptWithMD5(NewPassword1);
                            conf.setStringYAML("userdata",player.getUniqueId() + ".yml", "password", Encrypted);
                            player.sendMessage(ChatColor.GREEN + "Mot de passe changé !");
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "Votre mot de passe de confirmation n'est pas identique ! recommencer");
                        }
                    }
                    else 
                    {
                        player.sendMessage(ChatColor.RED + "Votre ancien mot de passe n'est pas correct!");
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "Utilisation incorrect, tape: /passchange [ancien_motDePasse] [nouveau_motDePasse] [confirme_motDePasse]");
                }
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("delock")) 
            {
                if (args.length == 0) 
                {
                    player.sendMessage(ChatColor.BLUE + "pour se logger, tape: /delock [tonMotDePasse]");
                } 
                else if (args.length == 1)
                {
                    if (Global.PlayersMove.contains(player.getUniqueId()))
                    {
                        Command_Login.Global.PlayersToUnlock.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "mot de passe correct ! bon jeu :)");
                    }
                    else
                    {
                        String EncryptedOld = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "password","NULL");
                        String GivenPassword = args[0];
                        String EncryptedGiven = cryptWithMD5(GivenPassword);
                        if (EncryptedOld.equals(EncryptedGiven))
                        {
                            Global.PlayersToRegister.remove(player.getUniqueId());
                            Global.PlayersToUnlock.remove(player.getUniqueId());
                            Global.MessageSend.remove(player.getUniqueId());
                            Global.PlayersMove.add(player.getUniqueId());
                            player.sendMessage(ChatColor.GREEN + "mot de passe correct ! bon jeu :)");
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "Password incorrect !");
                        }
                    }
                }
                else 
                {
                    player.sendMessage(ChatColor.RED + "Incorrect usage! Use /delock [tonMotDePasse]");
                }
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("lo")) 
            {
                if (args.length == 0)
                {
                    player.sendMessage(ChatColor.BLUE + "pour vérouiller votre compte taper: /lo [tonMotDePasse]");
                }
                else if (args.length == 1)
                {
                    String EncryptedOld = conf.getStringYAML("userdata",player.getUniqueId() + ".yml", "password","NULL");
                    String GivenPassword = args[0];
                    String EncryptedGiven = cryptWithMD5(GivenPassword);
                    if (EncryptedOld.equals(EncryptedGiven))
                    {
                        Global.PlayersToUnlock.add(player.getUniqueId());
                        Global.PlayersMove.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "Compte vérouillé!");
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + "Mot de passe incorrect!");
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "Usage Incorrect! tappe /lo [TonMotDePasse");
                }
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("ResetPassword") && 
                (player.hasPermission("player.passwords.reset"))) 
            {
                if (args.length == 0)
                {
                    player.sendMessage(ChatColor.BLUE + "To reset a players password use /DelPassword <name> <new password> <confirm new password>. Attention the name is capital sensitive!");
                }
                else if (args.length == 4)
                {
                    String Player = args[1];
                    /*if (this.settings.getPasswords().getString("Players." + Player + ".password") != null)
                    {
                        String Password1 = args[2];
                        String Password2 = args[3];
                        if (Password1.equals(Password2))
                        {
                            String Encrypted = cryptWithMD5(Password1);
                            this.settings.getPasswords().set("Players." + Player + ".password", Encrypted);
                            this.settings.savePasswords();
                            this.settings.reloadPasswords();
                            player.sendMessage(ChatColor.GREEN + "The players password has been reseted! The players password is now " + ChatColor.BLUE + Password1);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED + "The passwords don't match!");
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + "The player doesn't exist or hasn't set a password yet. Attention the name is capital sensitive!");
                    }*/
                }
                else
                {
                    player.sendMessage(ChatColor.RED + "Incorrect usage! Use /slogon DelPassword <name> <new password> <confirm new password>");
                }
            }
        }
        return false;
    }
}
