package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;

@EventBusSubscriber
public class PlayerFoodEffectsHandler {
   @SubscribeEvent
   public static void onFoodEaten(Finish event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.level().isClientSide) {
            if (player.level() instanceof ServerLevel) {
               int day = DayGlobalCount.CURRENT_DAY;
               if (day >= 50) {
                  if (event.getItem().getItem() == Items.SPIDER_EYE) {
                     player.addEffect(new MobEffectInstance(MobEffects.POISON, -1, 0, false, true));
                  }

                  if (event.getItem().getItem() == Items.ROTTEN_FLESH) {
                     player.addEffect(new MobEffectInstance(MobEffects.HUNGER, -1, 0, false, true));
                  }

                  if (event.getItem().getItem() == Items.POISONOUS_POTATO) {
                     player.addEffect(new MobEffectInstance(MobEffects.POISON, -1, 1, false, true));
                  }

                  if (event.getItem().getItem() == Items.PUFFERFISH) {
                     player.addEffect(new MobEffectInstance(MobEffects.HUNGER, -1, 2, false, true));
                     player.addEffect(new MobEffectInstance(MobEffects.POISON, -1, 1, false, true));
                     player.addEffect(new MobEffectInstance(MobEffects.SATURATION, -1, 0, false, true));
                  }
               }

               if (event.getItem().getItem() == Items.PUMPKIN_PIE) {
                  if (day >= 50 && day < 60) {
                     player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 100, 0, false, true));
                  } else if (day >= 60) {
                     player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 3, false, true));
                  }
               }
            }
         }
      }
   }
}
