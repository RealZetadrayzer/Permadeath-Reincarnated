package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class BlazeChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Blaze blaze) {
                  if (!event.loadedFromDisk()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50) {
                        Objects.requireNonNull(blaze.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(200.0);
                        blaze.setHealth(blaze.getMaxHealth());
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFireballHit(ProjectileImpactEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (event.getProjectile() instanceof SmallFireball fireball) {
            if (fireball.level() instanceof ServerLevel level) {
               if (!fireball.level().isClientSide) {
                  if (fireball.getOwner() instanceof Blaze blaze) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 60) {
                        float powerExplosion = 4.0F + RandomUtil.RANDOM.nextInt(3);
                        level.explode(blaze, fireball.getX(), fireball.getY(), fireball.getZ(), powerExplosion, true, ExplosionInteraction.MOB);
                        fireball.discard();
                     }
                  }
               }
            }
         }
      }
   }
}
