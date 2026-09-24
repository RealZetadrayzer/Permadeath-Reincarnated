package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
   @Invoker("getStrikePosition")
   public abstract BlockPos permadeathReincarnated$getStrikePosition();

   @Shadow
   public abstract void setDamage(float var1);

   @Inject(method = "tick", at = @At("HEAD"))
   private void permadeathReincarnated$lightningBoltDamage(CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         LightningBolt bolt = (LightningBolt)(Object)this;
         bolt.setDamage(50.0F);
         bolt.getHitEntities().forEach(entity -> {
            if (entity instanceof ServerPlayer player) {
               if (player.isSpectator()) {
                  return;
               }

               PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_LIGHTNING_DAMAGE_RECEIVED_ID);
            }
         });
      }
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;powerLightningRod()V", shift = Shift.AFTER))
   private void permadeathReincarnated$breakRod(CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         LightningBolt bolt = (LightningBolt)(Object)this;
         if (bolt.level() instanceof ServerLevel serverLevel) {
            BlockPos strikePos = this.permadeathReincarnated$getStrikePosition();
            BlockState state = serverLevel.getBlockState(strikePos);
            if (state.getBlock() instanceof LightningRodBlock && RandomUtil.RANDOM.nextFloat() < 0.1F) {
               serverLevel.destroyBlock(strikePos, true);
            }
         }
      }
   }
}
