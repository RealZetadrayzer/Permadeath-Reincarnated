package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionParticlesFixMixin {
   @Shadow
   @Final
   private float radius;
   @Shadow
   @Final
   private ParticleOptions smallExplosionParticles;
   @Shadow
   @Final
   private ParticleOptions largeExplosionParticles;
   @Shadow
   @Final
   private Level level;
   @Shadow
   @Final
   private double x;
   @Shadow
   @Final
   private double y;
   @Shadow
   @Final
   private double z;

   @Inject(method = "finalizeExplosion", at = @At("HEAD"))
   private void changeParticle(boolean spawnParticles, CallbackInfo ci) {
      ParticleOptions particle = this.radius < 2.0F ? this.smallExplosionParticles : this.largeExplosionParticles;
      if (spawnParticles) {
         this.level.addParticle(particle, this.x, this.y, this.z, 1.0, 0.0, 0.0);
      }
   }
}
