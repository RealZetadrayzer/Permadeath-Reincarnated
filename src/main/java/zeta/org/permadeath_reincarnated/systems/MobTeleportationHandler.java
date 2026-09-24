package zeta.org.permadeath_reincarnated.systems;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;

@EventBusSubscriber
public final class MobTeleportationHandler {
   private static final Random RANDOM = new Random();

   private MobTeleportationHandler() {
   }

   @SubscribeEvent
   public static void onEntityTick(Post event) {
      if (event.getEntity() instanceof LivingEntity livingEntity) {
         if (!livingEntity.level().isClientSide) {
            if (livingEntity.tickCount % 100 == 0) {
               if (hasTag(livingEntity, "ender_quantum") && chance(0.075)) {
                  teleportRandom(livingEntity, 64.0, 32);
               }

               if (hasTag(livingEntity, "definitivo") && chance(0.075)) {
                  teleportRandom(livingEntity, 64.0, 32);
               }

               if (hasTag(livingEntity, "ender") && chance(0.025)) {
                  teleportRandom(livingEntity, 64.0, 32);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (event.getEntity() instanceof Creeper living) {
         if (!living.level().isClientSide) {
            if (hasTeleportTag(living)) {
               if (chance(0.15)) {
                  teleportRandom(living, 64.0, 32);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult) {
         Entity hit = hitResult.getEntity();
         if (hit instanceof LivingEntity living) {
            if (!hit.level().isClientSide) {
               int chance = 1 + RANDOM.nextInt(100);
               if (hasTag(hit, "ender") || hasTag(hit, "ender_quantum") || hasTag(hit, "definitivo") || hasTag(hit, "fromZPiglin")) {
                  if (living instanceof Ghast) {
                     if (hasTag(hit, "fromZPiglin") && chance <= 80) {
                        teleportRandom(living, 64.0, 32);
                        event.setCanceled(true);
                     } else if (chance <= 20) {
                        teleportRandom(living, 64.0, 32);
                        event.setCanceled(true);
                     }
                  } else if (living instanceof Creeper) {
                     teleportRandom(living, 64.0, 32);
                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }

   public static boolean teleportRandom(LivingEntity entity, double range, int yRange) {
      if (!entity.isAlive()) {
         return false;
      }

      if (entity.level().isClientSide) {
         return false;
      }

      Level level = entity.level();

      for (int attempt = 0; attempt < 64; attempt++) {
         double x = entity.getX() + (RANDOM.nextDouble() - 0.5) * range;
         double y = entity.getY() + (RANDOM.nextInt(yRange * 2) - yRange);
         double z = entity.getZ() + (RANDOM.nextDouble() - 0.5) * range;
         MutableBlockPos pos = new MutableBlockPos(x, y, z);

         while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
            pos.move(0, -1, 0);
         }

         BlockState state = level.getBlockState(pos);
         if (state.blocksMotion() && !state.getFluidState().is(FluidTags.WATER)) {
            BlockPos oldPos = entity.blockPosition();
            if (entity.randomTeleport(x, pos.getY() + 1, z, true)) {
               level.playSound(null, oldPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
               entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
               return true;
            }
         }
      }

      return false;
   }

   public static boolean chance(double value) {
      return RANDOM.nextDouble() <= value;
   }

   public static boolean hasTag(Entity entity, String tag) {
      return entity.getTags().contains(tag);
   }

   private static boolean hasTeleportTag(Entity entity) {
      return entity.getTags().contains("ender") || entity.getTags().contains("ender_quantum") || entity.getTags().contains("definitivo");
   }
}
