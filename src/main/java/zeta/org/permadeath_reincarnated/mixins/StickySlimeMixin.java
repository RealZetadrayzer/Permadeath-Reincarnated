package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Slime.class)
public class StickySlimeMixin {
   @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
   private void permadeath$doubleDamageForStickySlimes(CallbackInfoReturnable<Float> cir) {
      Slime slime = (Slime)(Object)this;
      if (slime.getTags().contains("fromOozingStickySlime")) {
         int day = DayGlobalCount.CURRENT_DAY;
         float multiplier = day >= 50 ? 4.0F : 2.0F;
         cir.setReturnValue((float)slime.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier);
      }
   }
}
