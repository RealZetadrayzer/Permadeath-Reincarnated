package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class WardenShieldMixin {
   @Inject(method = "canDisableShield", at = @At("HEAD"), cancellable = true)
   private void permadeath$superWardenNoShieldDisable(CallbackInfoReturnable<Boolean> callback) {
      Warden warden = (Warden)(Object)this;
      if (warden.getTags().contains("superWarden")) {
         callback.setReturnValue(false);
      }
   }
}
