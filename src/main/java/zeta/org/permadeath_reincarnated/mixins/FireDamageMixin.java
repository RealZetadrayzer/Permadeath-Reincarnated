package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Entity.class)
public class FireDamageMixin {
   @ModifyArg(
      method = "baseTick()V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      index = 1
   )
   private float increaseFireDamage(float amount) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return amount;
      }

      int day = DayGlobalCount.CURRENT_DAY;
      return day < 40 ? amount : amount * 5.0F;
   }
}
