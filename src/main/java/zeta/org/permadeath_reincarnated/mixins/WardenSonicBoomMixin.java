package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SonicBoom.class)
public class WardenSonicBoomMixin {
   @Redirect(
      method = "lambda$tick$2",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      require = 1
   )
   private static boolean permadeath$sonicBoomExplosionOnHit(LivingEntity livingEntity, DamageSource damageSource, float damageAmount) {
      if (damageSource.getEntity() instanceof Warden warden) {
         boolean isSuper = warden.getTags().contains("superWarden");
         boolean isDefinitive = warden.getTags().contains("definitiveWarden");
         if (isDefinitive) {
            damageAmount *= 2.0F;
         }

         boolean didDamage = livingEntity.hurt(damageSource, damageAmount);
         if (!didDamage) {
            return false;
         }

         if (!isSuper) {
            return true;
         }

         if (livingEntity.level() instanceof ServerLevel serverLevel) {
            if (!EventHooks.canEntityGrief(serverLevel, warden)) {
               return true;
            }

            float explosionLevel = isDefinitive ? 6.0F : 3.0F;
            Vec3 impactPosition = livingEntity.position().add(0.0, 0.8, 0.0);
            serverLevel.playSound(
               null, livingEntity.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2.4F, 0.75F + serverLevel.random.nextFloat() * 0.08F
            );
            serverLevel.playSound(null, livingEntity.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 2.0F, 0.55F);
            serverLevel.playSound(null, livingEntity.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.2F, 1.8F);
            serverLevel.playSound(
               null, livingEntity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.8F, 0.6F + serverLevel.random.nextFloat() * 0.2F
            );
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, impactPosition.x, impactPosition.y, impactPosition.z, 18, 0.25, 0.25, 0.25, 0.0);
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, impactPosition.x, impactPosition.y, impactPosition.z, 70, 0.9, 0.45, 0.9, 0.1);
            serverLevel.sendParticles(ParticleTypes.FLASH, impactPosition.x, impactPosition.y, impactPosition.z, 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.explode(warden, impactPosition.x, impactPosition.y, impactPosition.z, explosionLevel, ExplosionInteraction.MOB);
            return true;
         } else {
            return true;
         }
      } else {
         return livingEntity.hurt(damageSource, damageAmount);
      }
   }
}
