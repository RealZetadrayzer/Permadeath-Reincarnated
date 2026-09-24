package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class GuardianChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Guardian guardian) {
         if (!event.loadedFromDisk()) {
            if (!guardian.level().isClientSide) {
               if (entity.getClass() != ElderGuardian.class) {
                  if (day >= 40) {
                     if (!guardian.getTags().contains("fromSquid")) {
                        guardian.setCustomName(Component.literal("Súper Guardian").withStyle(ChatFormatting.AQUA));
                     }

                     guardian.addTag("attackSpeed");
                     guardian.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, true));
                     guardian.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                  }
               }
            }
         }
      }
   }
}
