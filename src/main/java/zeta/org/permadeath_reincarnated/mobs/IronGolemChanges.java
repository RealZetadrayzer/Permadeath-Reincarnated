package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class IronGolemChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof IronGolem ironGolem) {
         if (!event.loadedFromDisk()) {
            if (!ironGolem.level().isClientSide) {
               if (day >= 30) {
                  ironGolem.setCustomName(Component.literal("Súper Golem de Hierro").withStyle(ChatFormatting.GOLD));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, true));
               }

               if (day >= 40) {
                  ironGolem.setCustomName(Component.literal("Mega Golem de Hierro").withStyle(ChatFormatting.GOLD));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, true));
               }

               if (day >= 50) {
                  ironGolem.setCustomName(Component.literal("Ultra Golem de Hierro").withStyle(ChatFormatting.GOLD));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
               }

               if (day >= 60) {
                  ironGolem.setCustomName(Component.literal("Golem de Hierro Definitivo").withStyle(ChatFormatting.GOLD));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, true));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 3, false, true));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 3, false, true));
                  ironGolem.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, true));
               }
            }
         }
      }
   }
}
