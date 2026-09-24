package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class ServerDeathMessagesMixin {
   @WrapWithCondition(
      method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
      at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
   )
   private boolean permadeathreincarnated$suppressNamedEntityDeath(Logger logger, String format, Object arg1, Object arg2) {
      return false;
   }
}
