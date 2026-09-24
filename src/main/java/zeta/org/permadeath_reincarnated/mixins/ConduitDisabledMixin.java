package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@Mixin(ConduitBlockEntity.class)
public class ConduitDisabledMixin {
   @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
   private static void disableConduitServer(Level level, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 55) {
         AABB box = new AABB(pos).inflate(96.0).expandTowards(0.0, level.getHeight(), 0.0);

         for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, box)) {
            if (!player.isSpectator()) {
               PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_CONDUIT_FAILED_ID);
            }
         }

         ci.cancel();
      }
   }

   @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
   private static void disableConduitClient(Level level, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 55) {
         ci.cancel();
      }
   }
}
