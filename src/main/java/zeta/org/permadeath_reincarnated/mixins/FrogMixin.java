package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Frog.class)
public class FrogMixin {
   @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
   private static void allowPlayerTargeting(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof Player) {
         if (DayGlobalCount.CURRENT_DAY >= 20) {
            cir.setReturnValue(true);
         } else {
            cir.setReturnValue(false);
         }
      }
   }
}
