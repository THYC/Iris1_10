package net.teraoctet.iris.utils;

import net.teraoctet.iris.Iris;
import static net.teraoctet.iris.Iris.conf;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.horde.Horde;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Economy 
{
                   
    public double getSolde(Player player)
    {
        double solde;
        if (player.hasPermission("iris.horde") && !player.isOp())
        {
            solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            solde = conf.getIntYAML("userdata",player.getUniqueId() + ".yml","banque",0);
        }
        return solde;
    }
    
    public double getSolde(Player player, Horde horde)
    {
        double solde = 0;
        if (player.hasPermission("iris.horde") && !player.isOp())
        {
            solde = conf.getIntYAML("horde.yml","Horde." + horde.getHordeName() + ".Bank",0);
        }
        return solde;
    }
    
    public double setTransaction(Player Acheteur, Player Vendeur, double achat, String worldName)
    {
        double soldeAcheteur;
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","horde.banque",0);
        }
        else
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","banque",0);
        }
        
        double soldeVendeur;
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","banque",0);
        }
        
        soldeAcheteur = soldeAcheteur - achat;
        soldeAcheteur = Math.round(soldeAcheteur * 100.0) / 100.0;
        
        soldeVendeur = soldeVendeur + achat;
        soldeVendeur = Math.round(soldeVendeur * 100.0) / 100.0;
        
        if (worldName.contains(Iris.WorldPVP))
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "Horde.banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "banque", soldeVendeur);
        }
        else
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "Horde.banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "banque", soldeVendeur);
        }
        return soldeAcheteur;
    }
    
    public double setTransaction(Player Acheteur, OfflinePlayer Vendeur, double achat, String worldName)
    {
        double soldeAcheteur;
        Iris.log.info(Iris.WorldPVP);
        Iris.log.info(worldName);
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","banque",0);
        }
        
        double soldeVendeur;
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","banque",0);
        }
        
        soldeAcheteur = soldeAcheteur - achat;
        soldeAcheteur = Math.round(soldeAcheteur * 100.0) / 100.0;
        
        soldeVendeur = soldeVendeur + achat;
        soldeVendeur = Math.round(soldeVendeur * 100.0) / 100.0;
        
        if (worldName.contains(Iris.WorldPVP))
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "Horde.banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "Horde.banque", soldeVendeur);
        }
        else
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "banque", soldeVendeur);
        }
        
        return soldeAcheteur;
    }
    
    public double setTransaction(OfflinePlayer Acheteur, Player Vendeur, double achat, String worldName)
    {
        double soldeAcheteur;
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","banque",0);
        }
        
        double soldeVendeur;
        if (worldName.contains(Iris.WorldPVP))
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","banque",0);
        }
        
        soldeAcheteur = soldeAcheteur - achat;
        soldeAcheteur = Math.round(soldeAcheteur * 100.0) / 100.0;
        
        soldeVendeur = soldeVendeur + achat;
        soldeVendeur = Math.round(soldeVendeur * 100.0) / 100.0;
        
        if (worldName.contains(Iris.WorldPVP))
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "Horde.banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "Horde.banque", soldeVendeur);
        }
        else
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "banque", soldeAcheteur);
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "banque", soldeVendeur);
        }
        
        return soldeAcheteur;
    }
    
    public double setPrelevement(Player Acheteur, double achat)
    {
        double soldeAcheteur;
        if (Acheteur.hasPermission("iris.horde"))
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeAcheteur = conf.getDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml","banque",0);
        }
        
        soldeAcheteur = soldeAcheteur - achat;
        soldeAcheteur = Math.round(soldeAcheteur * 100.0) / 100.0;

        if (Acheteur.hasPermission("iris.horde"))
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "Horde.banque", soldeAcheteur);
        }
        else
        {
            conf.setDoubleYAML("userdata",Acheteur.getUniqueId() + ".yml", "banque", soldeAcheteur);
        }
        return soldeAcheteur;
    }
    
    public double setPrelevement(Horde horde, double achat)
    {
        double solde;
        solde = conf.getIntYAML("horde.yml","Horde." + horde.getHordeName() + ".Bank",0);
                   
        solde = solde - achat;
        solde = Math.round(solde * 100.0) / 100.0;

        conf.setDoubleYAML("horde.yml","Horde." + horde.getHordeName() + ".Bank",solde);
            
        return solde;
    }
    
    public double setVersement(Player Vendeur, double achat)
    {
        double soldeVendeur;
        if (Vendeur.hasPermission("iris.horde"))
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldeVendeur = conf.getDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml","banque",0);
        }
        
        soldeVendeur = soldeVendeur + achat;
        soldeVendeur = Math.round(soldeVendeur * 100.0) / 100.0;
        
        if (Vendeur.hasPermission("iris.horde"))
        {
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "Horde.banque", soldeVendeur);
        }
        else
        {
            conf.setDoubleYAML("userdata",Vendeur.getUniqueId() + ".yml", "banque", soldeVendeur);
        }
        
        return soldeVendeur;
    }
    
    public boolean setVersement(Player player1, Player player2, double versement)
    {
        if (player2.isOnline() == false)
        {
            player1.sendMessage(formatMsg.format("<yellow>Le joueur doit être en ligne effectuer un versement"));
            return false;
        }
        
        double soldePlayer1;
        if (player1.hasPermission("iris.horde"))
        {
            soldePlayer1 = conf.getDoubleYAML("userdata",player1.getUniqueId() + ".yml","Horde.banque",0);
        }
        else
        {
            soldePlayer1 = conf.getDoubleYAML("userdata",player1.getUniqueId() + ".yml","banque",0);
        }
        
        soldePlayer1 = soldePlayer1 - versement;
        if (soldePlayer1 < 0)
        {
            player1.sendMessage(formatMsg.format("<yellow>Vous n'avez pas assez d'émeraudes"));
            return false;
        }
        
        double soldePlayer2;
        if (player1.hasPermission("iris.horde"))
        {
            soldePlayer2 = conf.getDoubleYAML("userdata",player2.getUniqueId() + ".yml", "Horde.banque", 0);
        }
        else
        {
            soldePlayer2 = conf.getDoubleYAML("userdata",player2.getUniqueId() + ".yml", "banque", 0);
        }
        
        if (player1.hasPermission("iris.horde"))
        {
            conf.setDoubleYAML("userdata",player1.getUniqueId() + ".yml", "Horde.banque", soldePlayer1);
            conf.setDoubleYAML("userdata",player2.getUniqueId() + ".yml", "Horde.banque", soldePlayer2);
        }
        else
        {
            conf.getDoubleYAML("userdata",player1.getUniqueId() + ".yml", "banque", soldePlayer1);
            conf.getDoubleYAML("userdata",player2.getUniqueId() + ".yml", "banque", soldePlayer2);
        }
        
        return true;
    }
    
    public double setVersement(Horde horde, double achat)
    {
        double solde;
        solde = conf.getIntYAML("horde.yml","Horde." + horde.getHordeName() + ".Bank",0);
        
        solde = solde + achat;
        solde = Math.round(solde * 100.0) / 100.0;
        
        conf.setDoubleYAML("horde.yml","Horde." + horde.getHordeName() + ".Bank",solde);
                    
        return solde;
    }
    
}
