package zeta.org.permadeath_reincarnated.systems;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.slf4j.Logger;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerBrokenTridentHandler {
   private static final Logger LOGGER = LogUtils.getLogger();

   @SubscribeEvent
   public static void onTridentHit(ProjectileImpactEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (event.getProjectile() instanceof ThrownTrident trident) {
            if (trident.getOwner() instanceof ServerPlayer player) {
               if (player.level() instanceof ServerLevel level) {
                  if (!trident.level().isClientSide) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 60) {
                        int roll = RandomUtil.RANDOM.nextInt(100);
                        int threshold = getTridentFailThreshold(day);
                        if (roll >= threshold) {
                           ScheduleInTicks.schedule(
                              () -> {
                                 player.sendSystemMessage(
                                    Component.literal("Tu tridende se ha roto. (Probabilidad: ")
                                       .withStyle(ChatFormatting.GRAY)
                                       .append(Component.literal(String.valueOf(roll)).withStyle(ChatFormatting.RED))
                                       .append(Component.literal(" >= " + threshold + ")").withStyle(ChatFormatting.GRAY))
                                 );
                                 level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                                 PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_TRIDENT_BREAK_ID);
                                 trident.discard();
                                 LOGGER.info("[Permadeath Monitor] El tridente de {} se ha roto", player.getName().getString());
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
   }

   private static int getTridentFailThreshold(int day) {
      int chance;
      if (day >= 75) {
         chance = 50;
      } else {
         chance = 20;
      }

      return 100 - chance;
   }
}
