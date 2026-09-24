package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class LlamaChanges {
   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      Entity owner = projectile.getOwner();
      if (owner instanceof Llama || owner instanceof TraderLlama) {
         if (event.getRayTraceResult() instanceof EntityHitResult hit) {
            if (hit.getEntity() instanceof LivingEntity target) {
               if (target.isAlive()) {
                  if (!target.level().isClientSide) {
                     if (target.isAffectedByPotions()) {
                        if (DayGlobalCount.CURRENT_DAY >= 50) {
                           double dx = projectile.getX() - target.getX();
                           double dz = projectile.getZ() - target.getZ();
                           target.knockback(3.0, dx, dz);
                           target.hurtMarked = true;
                           target.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 2, false, true));
                           target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, true));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
