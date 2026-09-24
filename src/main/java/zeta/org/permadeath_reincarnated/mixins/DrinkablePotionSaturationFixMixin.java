package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PotionItem.class)
public class DrinkablePotionSaturationFixMixin {
   @Redirect(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;isInstantenous()Z"))
   private static boolean permadeath$treatSaturationAsTimed(MobEffect effect) {
      return effect.isInstantenous() && effect != MobEffects.SATURATION.value();
   }
}
