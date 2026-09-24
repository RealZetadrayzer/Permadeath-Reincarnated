package zeta.org.permadeath_reincarnated.systems;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start;
import org.slf4j.Logger;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerGoldenAppleHandler {
   private static final Map<UUID, List<MobEffectInstance>> savedEffects = new HashMap<>();
   private static final Logger LOGGER = LogUtils.getLogger();

   @SubscribeEvent
   public static void onStartEat(Start event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide) {
               if (DayGlobalCount.CURRENT_DAY >= 50) {
                  ItemStack stack = event.getItem();
                  if (stack.is(Items.GOLDEN_APPLE)) {
                     savedEffects.put(player.getUUID(), new ArrayList<>(player.getActiveEffects()));
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFinishEat(Finish event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (event.getEntity() instanceof Player player) {
            if (player.level() instanceof ServerLevel level) {
               if (!player.level().isClientSide) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  if (DayGlobalCount.CURRENT_DAY >= 50) {
                     ItemStack stack = event.getItem();
                     if (stack.is(Items.GOLDEN_APPLE)) {
                        int roll = RandomUtil.RANDOM.nextInt(100);
                        int threshold = getGappleFailThreshold(day);
                        if (roll < threshold) {
                           savedEffects.remove(player.getUUID());
                        } else {
                           List<MobEffectInstance> oldEffects = savedEffects.remove(player.getUUID());
                           if (oldEffects != null) {
                              player.sendSystemMessage(
                                 Component.literal("Tu manzana de oro ha fallado. (Probabilidad: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf(roll)).withStyle(ChatFormatting.RED))
                                    .append(Component.literal(" >= " + threshold + ")").withStyle(ChatFormatting.GRAY))
                              );
                              level.playSound(null, player.blockPosition(), SoundEvents.VEX_CHARGE, SoundSource.MASTER, 1.0F, 1.2F);
                              level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.8F, 1.0F);
                              level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 0.7F, 0.7F);
                              player.removeEffect(MobEffects.REGENERATION);
                              player.removeEffect(MobEffects.ABSORPTION);

                              for (MobEffectInstance eff : oldEffects) {
                                 if (eff.getEffect() == MobEffects.REGENERATION || eff.getEffect() == MobEffects.ABSORPTION) {
                                    player.addEffect(new MobEffectInstance(eff));
                                 }
                              }

                              LOGGER.info("[Permadeath Monitor] La manzana de oro de {} fallo", player.getName().getString());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int getGappleFailThreshold(int day) {
      int chance;
      if (day >= 60) {
         chance = 30;
      } else if (day >= 55) {
         chance = 20;
      } else {
         chance = 10;
      }

      return 100 - chance;
   }
}
