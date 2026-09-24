package zeta.org.permadeath_reincarnated.mixins;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSittingFlamingPhase.class)
public abstract class DragonSittingFlamingPhaseMixin extends AbstractDragonPhaseInstance {
   @Shadow
   @Nullable
   private AreaEffectCloud flame;

   public DragonSittingFlamingPhaseMixin(EnderDragon dragon) {
      super(dragon);
   }

   @Inject(
      method = "doServerTick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = Shift.AFTER)
   )
   private void replaceFlameCloud(CallbackInfo ci) {
      if (this.dragon.getTags().contains("demonFight")) {
         AreaEffectCloud oldCloud = this.flame;
         if (oldCloud != null && !oldCloud.level().isClientSide) {
            oldCloud.remove(RemovalReason.DISCARDED);
            AreaEffectCloud centerAttackCloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(oldCloud.level());
            if (centerAttackCloud != null) {
               centerAttackCloud.setPos(oldCloud.getX(), oldCloud.getY(), oldCloud.getZ());
               centerAttackCloud.setRadius(5.0F);
               centerAttackCloud.setDuration(600);
               centerAttackCloud.addTag("demonFight");
               if (Math.random() <= 0.75) {
                  centerAttackCloud.setParticle(ParticleTypes.WITCH);
                  centerAttackCloud.addTag("purple_cloud");
               } else {
                  centerAttackCloud.setParticle(ParticleTypes.SNEEZE);
                  centerAttackCloud.addTag("evocation_cloud");
               }

               oldCloud.level().addFreshEntity(centerAttackCloud);
               this.flame = centerAttackCloud;
            }
         }
      }
   }
}
