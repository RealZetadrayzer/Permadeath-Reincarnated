package zeta.org.permadeath_reincarnated.mixins;

import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(WitherSkull.class)
public abstract class WitherSkullTweaksMixin {
   @Unique
   private static final Random permadeathReincarnated$RANDOM = new Random();

   @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
   private void permadeathreincarnated$witherSkullOnHitEntity(EntityHitResult result, CallbackInfo ci) {
      WitherSkull self = (WitherSkull)(Object)this;
      Entity owner = self.getOwner();
      if (!self.level().isClientSide) {
         if (owner != null && owner.getTags().contains("beginningWither") && !self.level().isClientSide()) {
            Entity target = result.getEntity();
            boolean flag;
            if (owner instanceof LivingEntity livingOwner) {
               DamageSource damageSource = self.damageSources().witherSkull(self, livingOwner);
               flag = target.hurt(damageSource, 15.0F);
               if (flag) {
                  if (target.isAlive()) {
                     EnchantmentHelper.doPostAttackEffects((ServerLevel)self.level(), livingOwner, damageSource);
                  } else {
                     livingOwner.heal(5.0F);
                  }
               }
            } else {
               flag = target.hurt(self.damageSources().magic(), 20.0F);
            }

            if (flag && target instanceof LivingEntity living) {
               int chance = 1 + permadeathReincarnated$RANDOM.nextInt(100);
               if (chance <= 5) {
                  self.level().explode(self, self.getX(), self.getY(), self.getZ(), 3.0F, false, ExplosionInteraction.MOB);
               }
               int i = switch (self.level().getDifficulty()) {
                  case NORMAL -> 10;
                  case HARD -> 40;
                  default -> 0;
               };
               if (i > 0) {
                  living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 2), self.getEffectSource());
               }
            }

            ci.cancel();
         } else if (owner != null && owner.getTags().contains("fromTimer") && !self.level().isClientSide()) {
            Entity target = result.getEntity();
            boolean flag;
            if (owner instanceof LivingEntity livingOwner) {
               DamageSource damageSource = self.damageSources().witherSkull(self, livingOwner);
               flag = target.hurt(damageSource, 30.0F);
               if (flag) {
                  if (target.isAlive()) {
                     EnchantmentHelper.doPostAttackEffects((ServerLevel)self.level(), livingOwner, damageSource);
                  } else {
                     livingOwner.heal(5.0F);
                  }
               }
            } else {
               flag = target.hurt(self.damageSources().magic(), 20.0F);
            }

            if (flag && target instanceof LivingEntity living) {
               int i = switch (self.level().getDifficulty()) {
                  case NORMAL -> 10;
                  case HARD -> 40;
                  default -> 0;
               };
               if (i > 0) {
                  living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 1), self.getEffectSource());
               }
            }

            ci.cancel();
         } else if (owner != null && owner.getTags().contains("fromPlayer") && !self.level().isClientSide()) {
            int day = DayGlobalCount.CURRENT_DAY;
            Entity target = result.getEntity();
            boolean flag;
            if (!(owner instanceof LivingEntity livingOwner)) {
               flag = target.hurt(self.damageSources().magic(), 5.0F);
            } else {
               boolean powered = livingOwner instanceof WitherBoss witherBoss && witherBoss.isPowered();
               DamageSource damageSource = self.damageSources().witherSkull(self, livingOwner);
               float min = powered ? (day >= 40 ? 24.0F : 16.0F) : (day >= 40 ? 16.0F : 8.0F);
               float max = powered ? (day >= 40 ? 30.0F : 24.0F) : (day >= 40 ? 24.0F : 16.0F);
               float damage = min + permadeathReincarnated$RANDOM.nextFloat(max - min);
               flag = target.hurt(damageSource, damage);
               if (flag) {
                  if (target.isAlive()) {
                     EnchantmentHelper.doPostAttackEffects((ServerLevel)self.level(), livingOwner, damageSource);
                  } else {
                     livingOwner.heal(5.0F);
                  }
               }
            }

            if (flag && target instanceof LivingEntity living) {
               int i = switch (self.level().getDifficulty()) {
                  case NORMAL -> 10;
                  case HARD -> 40;
                  default -> 0;
               };
               if (i > 0) {
                  living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * i, 1), self.getEffectSource());
               }
            }

            ci.cancel();
         }
      }
   }
}
