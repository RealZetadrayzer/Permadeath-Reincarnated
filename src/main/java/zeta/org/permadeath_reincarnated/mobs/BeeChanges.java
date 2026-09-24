package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class BeeChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Bee bee) {
                  if (!event.loadedFromDisk()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50) {
                        Objects.requireNonNull(bee.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(30.0);
                     }
                  }
               }
            }
         }
      }
   }
}
