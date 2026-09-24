package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class RavagerChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof Ravager ravager) {
         if (!entity.level().isClientSide) {
            if (!event.loadedFromDisk()) {
               int day = DayGlobalCount.CURRENT_DAY;
               if (!ravager.getTags().contains("ravager")) {
                  ravager.addTag("ravager");
                  if (day >= 25 && day < 50) {
                     if (!ravager.getTags().contains("fromAnimal") && !ravager.getTags().contains("fromZPiglin") && ravager.getClass() != CustomRavager.class) {
                        ravager.setCustomName(Component.literal("Súper Ravager").withStyle(ChatFormatting.GOLD));
                     }

                     if (ravager.getClass() == CustomRavager.class && !ravager.getTags().contains("fromZPiglin")) {
                        ravager.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                        ravager.addTag("fromAnimal");
                        ravager.setCanJoinRaid(false);
                        ravager.setCurrentRaid(null);
                        ravager.setWave(0);
                        ravager.setTicksOutsideRaid(0);
                     }

                     ravager.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
                     ravager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, true));
                  } else if (day >= 50) {
                     if (!ravager.getTags().contains("fromAnimal") && !ravager.getTags().contains("fromZPiglin") && ravager.getClass() != CustomRavager.class) {
                        ravager.setCustomName(Component.literal("Ultra Ravager").withStyle(ChatFormatting.GOLD));
                     }

                     if (ravager.getClass() == CustomRavager.class && !ravager.getTags().contains("fromZPiglin")) {
                        ravager.setCustomName(Component.literal("Ultra Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                        ravager.addTag("fromAnimal");
                        ravager.setCanJoinRaid(false);
                        ravager.setCurrentRaid(null);
                        ravager.setWave(0);
                        ravager.setTicksOutsideRaid(0);
                     }

                     Objects.requireNonNull(ravager.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(500.0);
                     ravager.setHealth(ravager.getMaxHealth());
                     ravager.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
                     ravager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                  }
               }
            }
         }
      }
   }
}
