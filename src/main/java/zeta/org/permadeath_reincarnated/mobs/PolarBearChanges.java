package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class PolarBearChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.tickCount % 10 == 0) {
            if (entity instanceof PolarBear bear) {
               if (!bear.getTags().contains("fromStray")) {
                  if (bear.isAlive()) {
                     if (DayGlobalCount.CURRENT_DAY >= 50) {
                        if (!bear.getPersistentData().getBoolean("Exploded")) {
                           LivingEntity target = bear.getTarget();
                           if (target != null) {
                              if (target.isAlive()) {
                                 if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                                    double distance = bear.distanceToSqr(target);
                                    if (!(distance > 9.0)) {
                                       if (target.canBeSeenAsEnemy()) {
                                          bear.getPersistentData().putBoolean("Exploded", true);
                                          level.explode(bear, bear.getX(), bear.getY(), bear.getZ(), 10.0F, true, ExplosionInteraction.MOB);
                                          bear.discard();
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
