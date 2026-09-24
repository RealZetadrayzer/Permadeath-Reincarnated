package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Witch;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class WitchChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Witch witch) {
         if (!witch.level().isClientSide) {
            if (!event.loadedFromDisk()) {
               if (day >= 40) {
                  double baseHealth = Objects.requireNonNull(witch.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                  Objects.requireNonNull(witch.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                  witch.setHealth(witch.getMaxHealth());
                  witch.setCustomName(Component.literal("Bruja Imposible").withStyle(ChatFormatting.DARK_PURPLE));
                  witch.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                  witch.addTag("impossibleWitch");
               }
            }
         }
      }
   }
}
