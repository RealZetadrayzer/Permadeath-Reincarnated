package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PlayerTouchBedrockLevitationHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            ServerLevel level = player.serverLevel();
            if (player.onGround()) {
               if (level.dimension().equals(Level.END)) {
                  if (DayGlobalCount.CURRENT_DAY < 30) {
                     return;
                  }

                  BlockPos below = player.blockPosition().below();
                  if (!level.getBlockState(below).is(Blocks.BEDROCK)) {
                     return;
                  }

                  double feetY = player.getBoundingBox().minY;
                  double blockTopY = below.getY() + 1.0;
                  if (feetY - blockTopY > 0.001) {
                     return;
                  }

                  player.setDeltaMovement(0.0, 0.5, 0.0);
                  player.hurtMarked = true;
                  player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 9, false, true));
               }

               if (level.dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                  BlockPos below = player.blockPosition().below();
                  if (!level.getBlockState(below).is(Blocks.BEDROCK)) {
                     return;
                  }

                  double feetY = player.getBoundingBox().minY;
                  double blockTopY = below.getY() + 1.0;
                  if (feetY - blockTopY > 0.001) {
                     return;
                  }

                  player.setDeltaMovement(0.0, 0.5, 0.0);
                  player.hurtMarked = true;
                  player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 9, false, true));
               }
            }
         }
      }
   }
}
