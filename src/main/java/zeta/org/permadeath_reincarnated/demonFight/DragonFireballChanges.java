package zeta.org.permadeath_reincarnated.demonFight;

import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber
public class DragonFireballChanges {
   private static final Random RANDOM = new Random();

   public static void spawnCloud(Level level, BlockPos pos, float radius, float radiusPerTick, int duration, ParticleOptions particle, List<String> tags) {
      AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX(), pos.getY() + 1, pos.getZ());
      cloud.setRadius(radius);
      cloud.setRadiusOnUse(radiusPerTick);
      cloud.setDuration(duration);
      cloud.setParticle(particle);
      if (tags != null) {
         cloud.getTags().addAll(tags);
      }

      level.addFreshEntity(cloud);
   }

   public static void rollCloud(Level level, BlockPos pos, NavigableMap<Double, DragonFireballChanges.CloudData> probabilityMap) {
      double roll = RANDOM.nextDouble();
      DragonFireballChanges.CloudData selected = probabilityMap.ceilingEntry(roll).getValue();
      spawnCloud(level, pos, selected.radius, selected.radiusPerTick, selected.duration, selected.particle, selected.tags);
   }

   public static void rollAttackCloud(Level level, BlockPos pos, double rollAbilities, double rollMetMort) {
      NavigableMap<Double, DragonFireballChanges.CloudData> cloudMap = new TreeMap<>();
      cloudMap.put(0.25, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "spawn_mob_trigger")));
      if (rollAbilities <= 0.15) {
         cloudMap.put(
            0.5,
            new DragonFireballChanges.CloudData(
               6.0F, 0.0F, 600, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 0.0F, 0.0F), List.of("demonFight", "black_cloud")
            )
         );
      } else if (rollAbilities <= 0.3) {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(10.0F, 0.0F, 600, ParticleTypes.GLOW, List.of("demonFight", "rage_cloud")));
      } else if (rollAbilities <= 0.45) {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(5.0F, 0.0F, 600, ParticleTypes.SMOKE, List.of("demonFight", "gray_cloud")));
      } else if (rollAbilities <= 0.6) {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(8.0F, 0.0F, 600, ParticleTypes.CLOUD, List.of("demonFight", "white_cloud")));
      } else if (rollAbilities <= 0.75) {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(10.0F, 0.0F, 600, ParticleTypes.HAPPY_VILLAGER, List.of("demonFight", "green_cloud")));
      } else if (rollAbilities <= 0.9) {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(5.0F, 0.0F, 600, ParticleTypes.WITCH, List.of("demonFight", "purple_cloud")));
      } else {
         cloudMap.put(0.5, new DragonFireballChanges.CloudData(0.01F, 0.0F, 600, ParticleTypes.UNDERWATER, List.of("demonFight", "night_vision_cloud_trigger")));
      }

      if (rollAbilities <= 0.4) {
         cloudMap.put(0.75, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "spawn_tnt_trigger")));
      } else if (rollAbilities <= 0.6) {
         cloudMap.put(0.75, new DragonFireballChanges.CloudData(0.01F, 0.0F, 600, ParticleTypes.UNDERWATER, List.of("demonFight", "thunder_cloud_variant_1")));
      } else if (rollAbilities <= 0.8) {
         cloudMap.put(0.75, new DragonFireballChanges.CloudData(0.01F, 0.0F, 600, ParticleTypes.UNDERWATER, List.of("demonFight", "thunder_cloud_variant_2")));
      } else {
         cloudMap.put(0.75, new DragonFireballChanges.CloudData(0.01F, 0.0F, 600, ParticleTypes.UNDERWATER, List.of("demonFight", "thunder_cloud_variant_3")));
      }

      if (rollAbilities <= 0.25) {
         cloudMap.put(
            1.0, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "block_bedrock_replace_trigger"))
         );
      } else if (rollAbilities <= 0.5) {
         cloudMap.put(1.0, new DragonFireballChanges.CloudData(6.0F, 0.0F, 600, ParticleTypes.SNEEZE, List.of("demonFight", "evocation_cloud")));
      } else if (rollAbilities <= 0.75) {
         if (rollMetMort <= 0.85) {
            cloudMap.put(1.0, new DragonFireballChanges.CloudData(10.0F, 0.0F, 600, ParticleTypes.FLAME, List.of("demonFight", "meteorite_rain_cloud")));
         } else {
            cloudMap.put(
               1.0, new DragonFireballChanges.CloudData(12.0F, 0.0F, 600, ParticleTypes.SOUL_FIRE_FLAME, List.of("demonFight", "enraged_meteorite_rain_cloud"))
            );
         }
      } else {
         cloudMap.put(1.0, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "block_lava_replace_trigger")));
      }

      rollCloud(level, pos, cloudMap);
   }

   public static void rollEnragedAttackCloud(Level level, BlockPos pos, double rollAbilities, double rollMetMort) {
      NavigableMap<Double, DragonFireballChanges.CloudData> cloudMap = new TreeMap<>();
      cloudMap.put(0.35, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "enraged_spawn_mob_trigger")));
      if (rollAbilities <= 0.25) {
         cloudMap.put(
            0.7,
            new DragonFireballChanges.CloudData(
               6.0F, 0.0F, 600, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 0.0F, 0.0F), List.of("demonFight", "black_cloud")
            )
         );
      } else if (rollAbilities <= 0.5) {
         cloudMap.put(0.7, new DragonFireballChanges.CloudData(10.0F, 0.0F, 600, ParticleTypes.GLOW, List.of("demonFight", "rage_cloud")));
      } else if (rollAbilities <= 0.75) {
         cloudMap.put(0.7, new DragonFireballChanges.CloudData(8.0F, 0.0F, 600, ParticleTypes.CLOUD, List.of("demonFight", "white_cloud")));
      } else {
         cloudMap.put(0.7, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "night_vision_cloud_trigger")));
      }

      if (rollAbilities <= 0.5) {
         cloudMap.put(0.85, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "spawn_tnt_trigger")));
      } else {
         cloudMap.put(0.85, new DragonFireballChanges.CloudData(0.01F, 0.0F, 600, ParticleTypes.UNDERWATER, List.of("demonFight", "thunder_cloud_variant_3")));
      }

      if (rollAbilities <= 0.25) {
         cloudMap.put(
            1.0, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "block_bedrock_replace_trigger"))
         );
      } else if (rollAbilities <= 0.5) {
         cloudMap.put(1.0, new DragonFireballChanges.CloudData(8.0F, 0.0F, 600, ParticleTypes.SNEEZE, List.of("demonFight", "enraged_evocation_cloud")));
      } else if (rollAbilities <= 0.75) {
         if (rollMetMort <= 0.7) {
            cloudMap.put(1.0, new DragonFireballChanges.CloudData(10.0F, 0.0F, 600, ParticleTypes.FLAME, List.of("demonFight", "meteorite_rain_cloud")));
         } else {
            cloudMap.put(
               1.0, new DragonFireballChanges.CloudData(12.0F, 0.0F, 600, ParticleTypes.SOUL_FIRE_FLAME, List.of("demonFight", "enraged_meteorite_rain_cloud"))
            );
         }
      } else {
         cloudMap.put(1.0, new DragonFireballChanges.CloudData(0.01F, 0.0F, 20, ParticleTypes.UNDERWATER, List.of("demonFight", "block_lava_replace_trigger")));
      }

      rollCloud(level, pos, cloudMap);
   }

   public static void rollAttack(Entity owner, BlockPos pos) {
      double rollAbilities = RANDOM.nextDouble();
      double rollMetMort = RANDOM.nextDouble();
      Level level = owner.level();
      if (owner.getTags().contains("enraged_demon")) {
         rollEnragedAttackCloud(level, pos, rollAbilities, rollMetMort);
      } else {
         rollAttackCloud(level, pos, rollAbilities, rollMetMort);
      }
   }

   @SubscribeEvent
   public static void onFireballImpact(ProjectileImpactEvent event) {
      if (event.getProjectile() instanceof DragonFireball fireball) {
         HitResult var8 = event.getRayTraceResult();
         Entity owner = fireball.getOwner();
         if (owner != null) {
            if (owner instanceof EnderDragon dragon) {
               if (!dragon.getTags().contains("demonFight")) {
                  return;
               }

               if (var8 instanceof EntityHitResult entityHit) {
                  Entity target = entityHit.getEntity();
                  if (target == dragon || target.getRootVehicle() == dragon) {
                     event.setCanceled(true);
                     return;
                  }

                  if (target instanceof EnderDragonPart part && part.parentMob == dragon) {
                     event.setCanceled(true);
                     return;
                  }
               }

               if (var8 instanceof BlockHitResult blockHit) {
                  rollAttack(owner, blockHit.getBlockPos());
               } else if (var8 instanceof EntityHitResult entityHit) {
                  rollAttack(owner, entityHit.getEntity().blockPosition());
               }
            }
         }
      }
   }

   public record CloudData(float radius, float radiusPerTick, int duration, ParticleOptions particle, List<String> tags) {
   }
}
