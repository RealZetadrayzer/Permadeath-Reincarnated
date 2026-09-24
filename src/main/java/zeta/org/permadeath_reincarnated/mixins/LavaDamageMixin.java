package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Entity.class)
public class LavaDamageMixin {
   @ModifyArg(
      method = "lavaHurt()V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      index = 1
   )
   private float increaseLavaDamage(float amount) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return amount;
      }

      int day = DayGlobalCount.CURRENT_DAY;
      return day < 40 ? amount : 50.0F;
   }
}
