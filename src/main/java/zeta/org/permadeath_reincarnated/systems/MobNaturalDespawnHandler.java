package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

@EventBusSubscriber
public class MobNaturalDespawnHandler {
   @SubscribeEvent
   public static void onEntityTickCheckForDespawn(Post event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.level() instanceof ServerLevel level) {
               if (mob.getTags().contains("shouldNaturallyDespawn")) {
                  if (!mob.isPersistenceRequired()) {
                     if (mob.tickCount % 100 == 0) {
                        int chance = 1 + level.random.nextInt(100);
                        Player nearest = level.getNearestPlayer(mob, 128.0);
                        if (nearest == null) {
                           removeRideStack(mob);
                        } else {
                           double distanceSq = nearest.distanceToSqr(mob);
                           double minSafeDistanceSq = 1024.0;
                           double hardDespawnDistanceSq = 16384.0;
                           if (!(distanceSq <= minSafeDistanceSq)) {
                              if (distanceSq >= hardDespawnDistanceSq) {
                                 removeRideStack(mob);
                              } else {
                                 if (chance <= 10) {
                                    removeRideStack(mob);
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

   private static void removeRideStack(Entity entity) {
      Entity root = entity.getRootVehicle();
      removeEntityAndPassengers(root);
   }

   private static void removeEntityAndPassengers(Entity entity) {
      for (Entity passenger : entity.getPassengers()) {
         removeEntityAndPassengers(passenger);
      }

      entity.remove(RemovalReason.DISCARDED);
   }
}
