package zeta.org.permadeath_reincarnated.systems;

import com.mojang.logging.LogUtils;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.slf4j.Logger;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerShieldUsageHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onRightClickItem(RightClickItem event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day >= 25) {
            if (event.getEntity() instanceof ServerPlayer player) {
               if (player.level() instanceof ServerLevel level) {
                  ItemStack stack = event.getItemStack();
                  if (stack.is(Items.SHIELD)) {
                     if (stack.has(DataComponents.CUSTOM_DATA)) {
                        CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
                        if (data != null) {
                           CompoundTag tag = data.copyTag();
                           if (tag.getInt("permadeath_beg_shield") == 1) {
                              return;
                           }
                        }
                     }

                     int roll = RANDOM.nextInt(100);
                     int threshold = getShieldFailThreshold(day);
                     if (roll >= threshold) {
                        ScheduleInTicks.schedule(
                           () -> {
                              player.sendSystemMessage(
                                 Component.literal("Tu escudo ha fallado. (Probabilidad: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf(roll)).withStyle(ChatFormatting.RED))
                                    .append(Component.literal(" >= " + threshold + ")").withStyle(ChatFormatting.GRAY))
                              );
                              player.stopUsingItem();
                              player.getCooldowns().addCooldown(stack.getItem(), 100);
                              PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_SHIELD_FAIL_ID);
                              level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                              LOGGER.info("[Permadeath Monitor] El escudo de {} falló", player.getName().getString());
                           },
                           1
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private static int getShieldFailThreshold(int day) {
      int chance;
      if (day >= 70) {
         chance = 40;
      } else if (day >= 50) {
         chance = 25;
      } else if (day >= 30) {
         chance = 10;
      } else {
         chance = 5;
      }

      return 100 - chance;
   }
}
