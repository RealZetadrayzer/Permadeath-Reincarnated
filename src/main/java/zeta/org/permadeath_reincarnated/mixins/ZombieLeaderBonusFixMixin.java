package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieLeaderBonusFixMixin {
   @Inject(method = "handleAttributes", at = @At("TAIL"))
   private void applyHealthBonus(float difficulty, CallbackInfo ci) {
      LivingEntity self = (LivingEntity)(Object)this;
      self.setHealth(self.getMaxHealth());
   }
}
