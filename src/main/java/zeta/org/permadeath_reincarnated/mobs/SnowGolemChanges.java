package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.entities.CustomSnowGolem;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class SnowGolemChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof CustomSnowGolem snowGolem) {
         if (!event.loadedFromDisk()) {
            if (!snowGolem.level().isClientSide) {
               snowGolem.setCustomName(Component.literal("Golem de Nieve Hostil").withStyle(ChatFormatting.GOLD));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      int day = DayGlobalCount.CURRENT_DAY;
      if (day >= 50) {
         Projectile projectile = event.getProjectile();
         if (projectile.getOwner() != null) {
            if (projectile.getOwner() instanceof SnowGolem snowGolem) {
               HitResult hit = event.getRayTraceResult();
               if (hit.getType() == Type.ENTITY) {
                  EntityHitResult entityHitResult = (EntityHitResult)hit;
                  if (entityHitResult.getEntity() instanceof LivingEntity target) {
                     if (target.isAlive()) {
                        Level level = projectile.level();
                        if (!level.isClientSide()) {
                           double dx = projectile.getX() - target.getX();
                           double dz = projectile.getZ() - target.getZ();
                           target.knockback(0.5, dx, dz);
                           target.hurtMarked = true;
                           target.hurt(level.damageSources().mobProjectile(projectile, snowGolem), 30.0F);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
