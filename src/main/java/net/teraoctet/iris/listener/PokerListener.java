package net.teraoctet.iris.listener;

import java.util.Random;
import static net.teraoctet.iris.Iris.formatMsg;
import net.teraoctet.iris.utils.Economy;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import static org.bukkit.Material.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class PokerListener 
implements Listener 
{
    private final Economy economy = new Economy();
    
    @EventHandler
    public void onPlayerPoker(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign)
        {
            final Sign sign = (Sign) event.getClickedBlock().getState();
            if (sign.getLine(0).equals(formatMsg.format("<gras>[POKER]")))
            {
                final Player player = event.getPlayer();
                if(player.getItemInHand().getType() == EMERALD)
                {
                    final int mise = player.getItemInHand().getAmount();
                    
                    if(player.getItemInHand().getAmount() == 1)
                    {
                        ItemStack is = new ItemStack(40);
                        player.getInventory().setItemInHand(is); 
                    }
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
                    //player.setItemInHand(null);
                    
                    player.updateInventory(); 
                    poker(player,1,sign);
                }
            }   
        }
    }
    
    private int poker(Player player, int mise, Sign sign)
    {
        int gain = mise * 64;
        int c1 = getrandom();
        int c2 = getrandom();
        int c3 = getrandom();
        if(c1 == c2 && c2 == c3)
        {
            player.sendMessage(formatMsg.format("<gras>Vous avez gagné"));
            sign.setLine(2,formatMsg.format("<green>" + String.valueOf(c1) + ":" + String.valueOf(c2) + ":" + String.valueOf(c3)));
            sign.update();

            Firework f = (Firework)player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.RED)
                .withFade(Color.YELLOW)
                .build());
            fm.setPower(1);
            f.setFireworkMeta(fm);
            
            economy.setVersement(player,64);  
            player.sendMessage(formatMsg.format("<yellow>64 émeraudes ont été versé sur votre compte"));
            
        }
        else
        {
            player.sendMessage(formatMsg.format("<gray>Vous avez perdu"));
            sign.setLine(2,formatMsg.format(String.valueOf(c1) + ":" + String.valueOf(c2) + ":" + String.valueOf(c3)));
            sign.update();
        }
        return gain;
    }
    
    private int getrandom()
    {
        Random rand = new Random();
        int nombre = rand.nextInt(10 - 1 + 1) + 1;
        return nombre;
    }
}
