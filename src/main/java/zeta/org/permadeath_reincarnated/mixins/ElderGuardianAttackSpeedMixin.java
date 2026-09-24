package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(ElderGuardian.class)
public class ElderGuardianAttackSpeedMixin {
   @Inject(method = "getAttackDuration", at = @At("HEAD"), cancellable = true)
   private void permadeath$onGetAttackDuration(CallbackInfoReturnable<Integer> cir) {
      Guardian self = (Guardian)(Object)this;
      if (DayGlobalCount.CURRENT_DAY >= 40 && self.getTags().contains("attackSpeed") && self instanceof ElderGuardian) {
         cir.setReturnValue(30);
      }
   }
}
