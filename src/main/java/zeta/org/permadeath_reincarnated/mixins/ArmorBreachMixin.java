package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import zeta.org.permadeath_reincarnated.PermadeathAttributes;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;

@Mixin(LivingEntity.class)
public class ArmorBreachMixin {
   @WrapOperation(
      method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;FF)F"
      )
   )
   private float armorBreach(LivingEntity entity, float damage, DamageSource source, float armorValue, float armorToughness, Operation<Float> original) {
      LivingEntity attacker = null;
      if (source.getEntity() instanceof LivingEntity living) {
         attacker = living;
      } else if (source.getDirectEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity living) {
         attacker = living;
      }

      float effectiveArmor = armorValue;
      if (attacker != null) {
         float armorPenetration = (float)attacker.getAttributeValue(PermadeathAttributes.ARMOR_BREACH);
         if (attacker.hasEffect(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate())) {
            if (armorValue >= 25.0F && armorValue < 30.0F) {
               armorPenetration += 0.15F;
            } else if (armorValue >= 30.0F && armorValue < 40.0F) {
               armorPenetration += 0.3F;
            } else if (armorValue >= 40.0F && armorValue < 50.0F) {
               armorPenetration += 0.4F;
            } else if (armorValue >= 50.0F) {
               armorPenetration += 0.5F;
            }
         }

         armorPenetration = Math.min(armorPenetration, 1.0F);
         effectiveArmor = Math.max(0.0F, armorValue * (1.0F - armorPenetration));
      }

      return (Float)original.call(new Object[]{entity, damage, source, effectiveArmor, armorToughness});
   }
}
