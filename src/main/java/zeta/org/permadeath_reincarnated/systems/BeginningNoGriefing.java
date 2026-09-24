package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class BeginningNoGriefing {
   @SubscribeEvent
   public static void onMobGriefing(EntityMobGriefingEvent event) {
      int day = DayGlobalCount.CURRENT_DAY;
      boolean flag = day >= 55 && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
      if (!flag) {
         if (event.getEntity().level() instanceof ServerLevel level) {
            if (level.dimension().location().equals(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "the_beginning"))) {
               event.setCanGrief(false);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onExplosionDetonate(Detonate event) {
      int day = DayGlobalCount.CURRENT_DAY;
      boolean flag = day >= 55 && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
      if (!flag) {
         if (event.getLevel() instanceof ServerLevel level) {
            if (level.dimension().location().equals(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "the_beginning"))) {
               event.getAffectedBlocks().clear();
            }
         }
      }
   }
}
