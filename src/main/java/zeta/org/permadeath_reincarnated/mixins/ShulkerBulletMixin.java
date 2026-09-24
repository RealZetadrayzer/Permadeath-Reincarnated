package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBullet.class)
public class ShulkerBulletMixin {
   @Inject(
      method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Direction$Axis;)V",
      at = @At("TAIL")
   )
   private void permadeath$bulletOwnerCheck(Level level, LivingEntity owner, Entity finalTarget, Axis axis, CallbackInfo ci) {
      if (owner != null && owner.getTags().contains("shulkerBulletNoLevitation")) {
         ((ShulkerBullet)(Object)this).addTag("shulkerBulletNoLevitation");
      }
   }

   @Redirect(
      method = "onHitEntity",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private boolean permadeath$bulletNoLevitation(LivingEntity target, MobEffectInstance effect, Entity source) {
      ShulkerBullet bullet = (ShulkerBullet)(Object)this;
      return bullet.getTags().contains("shulkerBulletNoLevitation") ? false : target.addEffect(effect, source);
   }
}
