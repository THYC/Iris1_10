package net.teraoctet.iris;

import org.bukkit.Effect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEffect
implements Listener
{
    Iris plugin;

    public DamageEffect(Iris plugin)
    {
      this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityBleed(EntityDamageEvent event)
    {
        if ((this.plugin.getConfig().getBoolean("BloodEffect.activated")))
        {
            if (!(event.getEntity() instanceof LivingEntity)) 
            {
                return;
            }

            if ((this.plugin.getConfig().getBoolean("BloodEffect.onPlayer") == true && (event.getEntityType() == EntityType.PLAYER))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }

            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onVillager") == true && (event.getEntityType() == EntityType.VILLAGER))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }

            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onZombie") == true && (event.getEntityType() == EntityType.ZOMBIE))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }

            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onSkeleton") == true && (event.getEntityType() == EntityType.SKELETON))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }

            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onCreeper") == true && (event.getEntityType() == EntityType.CREEPER))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onSpider") == true && (event.getEntityType() == EntityType.SPIDER))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onSpider") == true && (event.getEntityType() == EntityType.CAVE_SPIDER))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onEnderman") == true && (event.getEntityType() == EntityType.ENDERMAN))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onBlaze") == true && (event.getEntityType() == EntityType.BLAZE))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onMagmaCube") == true && (event.getEntityType() == EntityType.MAGMA_CUBE))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onGhast") == true && (event.getEntityType() == EntityType.GHAST))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
            else if ((this.plugin.getConfig().getBoolean("BloodEffect.onPigZombie") == true && (event.getEntityType() == EntityType.PIG_ZOMBIE))) 
            {
                event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, this.plugin.getConfig().getInt("BloodEffect.blockID"));
            }
        }
    }
}
