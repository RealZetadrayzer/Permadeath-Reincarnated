package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.level.block.MagmaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(MagmaBlock.class)
public class MagmaBlockDamageMixin {
   @ModifyArg(
      method = "stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      index = 1
   )
   private float increaseMagmaDamage(float amount) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return amount;
      }

      int day = DayGlobalCount.CURRENT_DAY;
      return day < 40 ? amount : 20.0F;
   }
}
