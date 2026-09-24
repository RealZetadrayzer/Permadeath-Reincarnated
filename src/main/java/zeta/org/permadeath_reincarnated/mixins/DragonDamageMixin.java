package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragon.class)
public class DragonDamageMixin {
   @Redirect(
      method = "knockBack",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
   )
   private boolean modifyKnockBackDamage(Entity instance, DamageSource source, float amount) {
      EnderDragon dragon = (EnderDragon)(Object)this;
      float newDamage = amount;
      if (dragon.getTags().contains("demonFight")) {
         newDamage = 20.0F;
      }

      boolean result = instance.hurt(source, newDamage);
      if (dragon.level() instanceof ServerLevel serverLevel) {
         EnchantmentHelper.doPostAttackEffects(serverLevel, instance, source);
      }

      return result;
   }

   @Redirect(
      method = "hurt*",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
   )
   private boolean modifyHurtDamage(Entity instance, DamageSource source, float amount) {
      EnderDragon dragon = (EnderDragon)(Object)this;
      float newDamage = amount;
      if (dragon.getTags().contains("demonFight")) {
         newDamage = 20.0F;
      }

      boolean result = instance.hurt(source, newDamage);
      if (dragon.level() instanceof ServerLevel serverLevel) {
         EnchantmentHelper.doPostAttackEffects(serverLevel, instance, source);
      }

      return result;
   }
}
