package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(LivingEntity.class)
public class FallDamageMixin {
   @ModifyArg(
      method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      index = 1
   )
   private float increaseFallDamage(float amount) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return amount;
      }

      int day = DayGlobalCount.CURRENT_DAY;
      if (day < 40) {
         return amount;
      }

      float multiplier = day >= 60 ? 8.0F : (day >= 55 ? 4.0F : (day >= 50 ? 2.0F : 1.25F));
      return amount * multiplier;
   }
}
