package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerTouchSoulSandSlownessHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            ServerLevel level = player.serverLevel();
            if (player.onGround()) {
               BlockPos below = player.blockPosition().below();
               if (level.getBlockState(below).is(Blocks.SOUL_SAND)
                  || level.getBlockState(below).is(Blocks.SOUL_SOIL) && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
                  double feetY = player.getBoundingBox().minY;
                  double blockTopY = below.getY() + 1.0;
                  if (!(feetY - blockTopY > 0.001)) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50) {
                        int duration = day >= 60 ? 600 : 200;
                        int amplifier = day >= 60 ? 2 : 1;
                        player.hurtMarked = true;
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier, false, true));
                     }
                  }
               }
            }
         }
      }
   }
}
