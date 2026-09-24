package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.MaceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MaceItem.class)
public class MaceDamageCapMixin {
   @ModifyReturnValue(method = "getAttackDamageBonus", at = @At("RETURN"))
   private float capMaceDamage(float original) {
      return Math.min(original, 25.0F);
   }
}
