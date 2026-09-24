package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;

@EventBusSubscriber
public class MobFasterDrowningHandler {
   @SubscribeEvent
   public static void onDrown(LivingDrownEvent event) {
      if (event.getEntity().level() instanceof ServerLevel level) {
         if (!level.isClientSide) {
            int day = DayGlobalCount.CURRENT_DAY;
            if (day >= 50) {
               float damage = day >= 60 ? 20.0F : 10.0F;
               event.setDamageAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onBreathe(LivingBreatheEvent event) {
      LivingEntity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (day >= 50) {
         int baseAir = entity.getMaxAirSupply();
         int baseConsume = day >= 60 ? 10 : 5;
         AttributeInstance attribute = entity.getAttribute(Attributes.OXYGEN_BONUS);
         double oxygenBonus = attribute != null ? attribute.getValue() : 0.0;
         int totalAir = baseAir + (int)(oxygenBonus * 10.0);
         float finalConsume = baseConsume * ((float)baseAir / totalAir);
         event.setConsumeAirAmount((int)finalConsume);
      }
   }
}
