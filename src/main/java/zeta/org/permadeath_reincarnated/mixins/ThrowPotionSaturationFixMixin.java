package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownPotion.class)
public class ThrowPotionSaturationFixMixin {
   @Redirect(method = "applySplash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;isInstantenous()Z"))
   private boolean permadeath$treatSaturationAsTimed(MobEffect effect) {
      return effect.isInstantenous() && effect != MobEffects.SATURATION.value();
   }
}
