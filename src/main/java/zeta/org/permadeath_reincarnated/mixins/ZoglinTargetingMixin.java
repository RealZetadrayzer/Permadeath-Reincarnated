package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zoglin.class)
public class ZoglinTargetingMixin {
   @Inject(method = "isTargetable", at = @At("HEAD"), cancellable = true)
   private void permadeath$restrictTargets(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
      Zoglin self = (Zoglin)(Object)this;
      if (self.getTags().contains("fromArmadillo")) {
         if (!self.level().isClientSide) {
            if (!(target instanceof ServerPlayer) && !(target instanceof IronGolem)) {
               cir.setReturnValue(false);
            } else {
               cir.setReturnValue(true);
            }
         }
      }
   }
}
