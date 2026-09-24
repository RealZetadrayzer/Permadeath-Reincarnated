package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class ShulkerChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Shulker shulker) {
         if (!shulker.getTags().contains("fromUniversal")) {
            if (shulker.level() instanceof ServerLevel level) {
               if (!shulker.getTags().contains("fromModule")) {
                  if (!event.loadedFromDisk()) {
                     if (!shulker.level().isClientSide) {
                        if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
                           if (day >= 30 && day < 60) {
                              shulker.setCustomName(Component.literal("Shulker Explosivo").withStyle(ChatFormatting.LIGHT_PURPLE));
                              shulker.addTag("dropsShulkerShells");
                              shulker.addTag("shulkerBulletNoLevitation");
                              shulker.addTag("shulkerExplosivo");
                           }

                           if (day >= 60) {
                              if (shulker.level().dimension() == Level.NETHER) {
                                 if (!shulker.getTags().contains("fromExplosivo")) {
                                    shulker.setCustomName(Component.literal("Shulker Criptico").withStyle(ChatFormatting.DARK_GREEN));
                                    shulker.setVariant(Optional.of(DyeColor.GREEN));
                                    shulker.addTag("shulkerBulletNoLevitation");
                                    shulker.addTag("shulkerCriptico");
                                 }
                              } else {
                                 shulker.setCustomName(Component.literal("Shulker Explosivo").withStyle(ChatFormatting.LIGHT_PURPLE));
                                 shulker.addTag("dropsShulkerShells");
                                 shulker.addTag("shulkerBulletNoLevitation");
                                 shulker.addTag("shulkerExplosivo");
                              }
                           }
                        } else if (day >= 30) {
                           shulker.addTag("classShulker");
                           chooseClass(level, shulker);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof Shulker shulker) {
         if (!shulker.level().isClientSide) {
            if (shulker.level() instanceof ServerLevel level) {
               int var10 = DayGlobalCount.CURRENT_DAY;
               boolean isExplosive = shulker.getTags().contains("shulkerExplosivo");
               boolean isCaotic = shulker.getTags().contains("shulkerCaotico");
               boolean isVoid = shulker.getTags().contains("shulkerVoid");
               boolean isPlague = shulker.getTags().contains("shulkerPlaga");
               if (isExplosive) {
                  spawnTnt(level, shulker.blockPosition(), shulker);
               } else if (isCaotic) {
                  float explosionLevel = var10 >= 50 ? 10.0F : 5.0F;
                  level.explode(shulker, shulker.getX(), shulker.getY(), shulker.getZ(), explosionLevel, false, ExplosionInteraction.MOB);
               } else if (isVoid) {
                  spawnBlackHole(level, shulker.blockPosition(), var10 >= 50 ? 1200 : 600);
                  level.playSound(null, shulker.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.5F, 0.6F);
                  level.playSound(null, shulker.blockPosition(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.HOSTILE, 1.2F, 0.5F);
                  level.playSound(null, shulker.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.HOSTILE, 1.0F, 1.0F);
                  level.playSound(null, shulker.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.8F);
                  level.playSound(null, shulker.blockPosition(), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.4F, 0.7F);
               } else if (isPlague) {
                  int duration = var10 >= 50 ? 1200 : 600;
                  spawnAreaCloudEffect(level, shulker.blockPosition(), duration, shulker);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      Entity owner = projectile.getOwner();
      if (owner != null) {
         Level level = projectile.level();
         if (owner instanceof Shulker shulker) {
            int day = DayGlobalCount.CURRENT_DAY;
            boolean isExplosive = owner.getTags().contains("shulkerExplosivo");
            boolean isCryptic = owner.getTags().contains("shulkerCriptico");
            boolean isFireShulker = owner.getTags().contains("shulkerCalcinado");
            boolean isCaotic = owner.getTags().contains("shulkerCaotico");
            boolean isVoid = owner.getTags().contains("shulkerVoid");
            boolean isPlague = owner.getTags().contains("shulkerPlaga");
            if (event.getRayTraceResult().getType() == Type.BLOCK) {
               BlockPos blockPos = ((BlockHitResult)event.getRayTraceResult()).getBlockPos();
               if (isExplosive) {
                  spawnTnt(level, blockPos, shulker);
               } else if (isFireShulker) {
                  projectile.level().explode(owner, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 2.0F, true, ExplosionInteraction.MOB);
               }
            } else if (event.getRayTraceResult().getType() == Type.ENTITY) {
               Entity hitEntity = ((EntityHitResult)event.getRayTraceResult()).getEntity();
               BlockPos pos = hitEntity.blockPosition();
               if (isExplosive) {
                  spawnTnt(level, pos, shulker);
               } else if (isCryptic && hitEntity instanceof LivingEntity living && living.isAffectedByPotions() && !living.isDeadOrDying()) {
                  living.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 3, false, true));
                  living.addEffect(new MobEffectInstance(MobEffects.POISON, 6000, 2, false, true));
                  living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 4, false, true));
               } else if (isFireShulker) {
                  if (!hitEntity.fireImmune()) {
                     int duration = day >= 50 ? 600 : 60;
                     hitEntity.igniteForSeconds(duration);
                  }

                  float explosionLevel = day >= 50 ? 4.0F : 2.0F;
                  projectile.level().explode(owner, hitEntity.getX(), hitEntity.getY(), hitEntity.getZ(), explosionLevel, true, ExplosionInteraction.MOB);
               } else if (isCaotic && hitEntity instanceof LivingEntity living && living.isAffectedByPotions() && !living.isDeadOrDying()) {
                  living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, day >= 50 ? 600 : 200, 0, false, true));
                  living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, day >= 50 ? 600 : 200, 0, false, true));
               } else if (isVoid && hitEntity instanceof LivingEntity living && !living.isDeadOrDying()) {
                  float damage = 1.0F + RANDOM.nextFloat(4.0F);
                  living.hurt(level.damageSources().fellOutOfWorld(), damage);
               } else if (isPlague && hitEntity instanceof LivingEntity living && living.isAffectedByPotions() && !living.isDeadOrDying()) {
                  int duration = day >= 50 ? 800 : 600;
                  living.addEffect(new MobEffectInstance(MobEffects.POISON, duration, day >= 50 ? 9 : 2, false, true));
                  living.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, day >= 50 ? 99 : 9, false, true));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void preventFriendlyFireShulkerCalcinado(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      boolean victimIsFriendly = victim.getTags().contains("fromShulker") || victim.getTags().contains("shulkerCalcinado");
      if (victimIsFriendly) {
         DamageSource source = event.getSource();
         if (!(
            source.getEntity() instanceof LivingEntity livingAttacker
               && (livingAttacker.getTags().contains("fromShulker") || livingAttacker.getTags().contains("shulkerCalcinado"))
         )) {
            if (source.getDirectEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof LivingEntity livingOwner
               && (livingOwner.getTags().contains("fromShulker") || livingOwner.getTags().contains("shulkerCalcinado"))) {
               event.setCanceled(true);
            }
         } else {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void preventFriendlyFireShulkerCaotico(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      boolean victimIsFriendly = victim.getTags().contains("fromShulkerCaotico") || victim.getTags().contains("shulkerCaotico");
      if (victimIsFriendly) {
         DamageSource source = event.getSource();
         if (!(
            source.getEntity() instanceof LivingEntity livingAttacker
               && (livingAttacker.getTags().contains("fromShulkerCaotico") || livingAttacker.getTags().contains("shulkerCaotico"))
         )) {
            if (source.getDirectEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof LivingEntity livingOwner
               && (livingOwner.getTags().contains("fromShulkerCaotico") || livingOwner.getTags().contains("shulkerCaotico"))) {
               event.setCanceled(true);
            }
         } else {
            event.setCanceled(true);
         }
      }
   }

   private static void spawnTnt(Level level, BlockPos pos, Mob shulker) {
      PrimedTnt tnt = new PrimedTnt(level, pos.getX(), pos.getY(), pos.getZ(), shulker);
      tnt.addTag("fromShulker");
      tnt.setFuse(60);
      level.addFreshEntity(tnt);
   }

   private static void spawnAreaCloudEffect(ServerLevel level, BlockPos pos, int duration, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      AreaEffectCloud cloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(level);
      if (cloud != null) {
         cloud.addTag("fromShulkerPlaga");
         cloud.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
         cloud.setParticle(ParticleTypes.SNEEZE);
         cloud.setRadius(4.0F);
         cloud.setDuration(duration);
         cloud.setWaitTime(0);
         cloud.setRadiusPerTick(0.0F);
         cloud.setDurationOnUse(0);
         cloud.setRadiusOnUse(0.0F);
         cloud.setOwner(shulker);
         level.addFreshEntity(cloud);

         for (int i = 0; i < duration; i += 20) {
            ScheduleInTicks.schedule(
               () -> {
                  if (cloud.isAlive()) {
                     double radius = cloud.getRadius();
                     double cx = cloud.getX();
                     double cy = cloud.getY();
                     double cz = cloud.getZ();
                     AABB box = cloud.getBoundingBox().inflate(radius);

                     for (LivingEntity living : level.getEntitiesOfClass(
                        LivingEntity.class, box, livingx -> livingx.isAlive() && livingx.isAffectedByPotions() && !(livingx instanceof Shulker)
                     )) {
                        double dx = living.getX() - cx;
                        double dy = living.getY() - cy;
                        double dz = living.getZ() - cz;
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (distance <= radius) {
                           living.addEffect(new MobEffectInstance(MobEffects.POISON, duration, day >= 50 ? 19 : 4, false, true));
                           living.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, day >= 50 ? 199 : 49, false, true));
                        }
                     }
                  }
               },
               i
            );
         }
      }
   }

   private static void spawnBlackHole(ServerLevel level, BlockPos centerPos, int duration) {
      double centerX = centerPos.getX() + 0.5;
      double centerY = centerPos.getY() + 0.5;
      double centerZ = centerPos.getZ() + 0.5;
      double scale = DayGlobalCount.CURRENT_DAY >= 50 ? 2.0 : 1.0;
      double pullRadius = 24.0 * scale;
      double damageRadius = 3.0 * scale;

      for (int tick = 0; tick < duration; tick++) {
         int currentTick = tick;
         ScheduleInTicks.schedule(
            () -> {
               if (level.isLoaded(centerPos)) {
                  spawnBlackHoleParticles(level, centerX, centerY, centerZ, currentTick, duration, scale);
                  if (currentTick % 40 == 0) {
                     level.playSound(null, centerPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.2F, 0.5F);
                     level.playSound(null, centerPos, SoundEvents.PORTAL_AMBIENT, SoundSource.HOSTILE, 0.5F, 0.7F);
                     level.playSound(null, centerPos, SoundEvents.CONDUIT_AMBIENT, SoundSource.HOSTILE, 0.6F, 0.6F);
                  }

                  for (LivingEntity living : level.getEntitiesOfClass(
                     LivingEntity.class,
                     new AABB(
                        centerX - pullRadius, centerY - pullRadius, centerZ - pullRadius, centerX + pullRadius, centerY + pullRadius, centerZ + pullRadius
                     ),
                     livingx -> livingx.isAlive() && !(livingx instanceof Shulker)
                  )) {
                     if (!(living instanceof ServerPlayer player && (player.isCreative() || player.isSpectator()))
                        && !(living instanceof WitherSkeleton witherSkeleton && witherSkeleton.getTags().contains("fromShulker"))) {
                        double dx = centerX - living.getX();
                        double dy = centerY - living.getY();
                        double dz = centerZ - living.getZ();
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (!(dist < 0.001)) {
                           double normX = dx / dist;
                           double normY = dy / dist;
                           double normZ = dz / dist;
                           double closeness = (pullRadius - Math.min(dist, pullRadius)) / pullRadius;
                           double horizontalStrength = 0.02 + closeness * closeness * 0.2;
                           double verticalStrength = 0.18 + closeness * 0.42;
                           double antiStuckY = living.onGround() ? 0.22 + closeness * 0.35 : 0.05;
                           double burst = dist < 2.0 * scale ? 0.18 : 0.0;
                           if (living.horizontalCollision || living.verticalCollision) {
                              antiStuckY += 0.25;
                           }

                           horizontalStrength *= scale;
                           verticalStrength *= scale;
                           Vec3 current = living.getDeltaMovement();
                           Vec3 newMotion = current.scale(0.78)
                              .add(normX * (horizontalStrength + burst), normY * verticalStrength + antiStuckY, normZ * (horizontalStrength + burst));
                           living.setDeltaMovement(newMotion);
                           living.hurtMarked = true;
                           living.hasImpulse = true;
                           if (dist <= damageRadius && !living.isDeadOrDying()) {
                              float damage = (float)Math.max(1.0, 1.0 + (damageRadius - dist) * 2.0);
                              living.hurt(level.damageSources().fellOutOfWorld(), damage);
                           }
                        }
                     }
                  }
               }
            },
            tick
         );
      }
   }

   private static void spawnBlackHoleParticles(ServerLevel level, double x, double y, double z, int tick, int duration, double scale) {
      spawnDarkSphere(level, x, y, z, 1.55 * scale, (int)(52.0 * scale));
      spawnDarkSphere(level, x, y, z, 1.15 * scale, (int)(36.0 * scale));
      spawnDarkSphere(level, x, y, z, 0.8 * scale, (int)(24.0 * scale));
      level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, 8, 0.12 * scale, 0.12 * scale, 0.12 * scale, 0.0);
      level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, 14, 0.16 * scale, 0.16 * scale, 0.16 * scale, 0.0);
      if (tick % 8 == 0) {
         spawnShinyParticles(level, x, y, z, 6, scale);
      }

      if (tick % 10 == 0) {
         level.sendParticles(ParticleTypes.FLASH, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private static void spawnDarkSphere(ServerLevel level, double cx, double cy, double cz, double radius, int points) {
      for (int i = 0; i < points; i++) {
         double theta = Math.acos(1.0 - 2.0 * (i + 0.5) / points);
         double phi = Math.PI * (1.0 + Math.sqrt(5.0)) * i;
         double px = cx + radius * Math.sin(theta) * Math.cos(phi);
         double py = cy + radius * Math.cos(theta);
         double pz = cz + radius * Math.sin(theta) * Math.sin(phi);
         level.sendParticles(ParticleTypes.SQUID_INK, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private static void spawnShinyParticles(ServerLevel level, double cx, double cy, double cz, int count, double scale) {
      for (int i = 0; i < count; i++) {
         double theta = RANDOM.nextDouble() * Math.PI * 2.0;
         double phi = Math.acos(2.0 * RANDOM.nextDouble() - 1.0);
         double radius = (1.9 + RANDOM.nextDouble() * 0.6) * scale;
         double px = cx + radius * Math.sin(phi) * Math.cos(theta);
         double py = cy + radius * Math.cos(phi);
         double pz = cz + radius * Math.sin(phi) * Math.sin(theta);
         double vx = (RANDOM.nextDouble() - 0.5) * 0.01;
         double vy = (RANDOM.nextDouble() - 0.5) * 0.01;
         double vz = (RANDOM.nextDouble() - 0.5) * 0.01;
         level.sendParticles(ParticleTypes.END_ROD, px, py, pz, 1, vx, vy, vz, 0.0);
      }
   }

   public static void chooseClass(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      int chance = 1 + RANDOM.nextInt(5);
      switch (chance) {
         case 1:
            claseExplosiva(level, shulker);
            break;
         case 2:
            claseCalcinada(level, shulker);
            break;
         case 3:
            claseCaotica(level, shulker);
            break;
         case 4:
            claseVoid(level, shulker);
            break;
         case 5:
            clasePlaga(level, shulker);
      }
   }

   private static void claseExplosiva(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      shulker.setCustomName(Component.literal("Shulker Explosivo").withStyle(ChatFormatting.LIGHT_PURPLE));
      shulker.addTag("dropsShulkerShells");
      shulker.addTag("shulkerBulletNoLevitation");
      shulker.addTag("shulkerExplosivo");
      shulker.setInvulnerable(day >= 50);
   }

   private static void claseCalcinada(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      shulker.setCustomName(Component.literal("Shulker Calcinado").withStyle(ChatFormatting.GOLD));
      shulker.addTag("dropsShulkerShells");
      shulker.addTag("shulkerBulletNoLevitation");
      shulker.addTag("projectileImmune");
      shulker.addTag("shulkerCalcinado");
      shulker.setVariant(Optional.of(DyeColor.ORANGE));
      if (witherSkeleton != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         witherSkeleton.addTag("fromShulker");
         witherSkeleton.addTag("suffocationImmune");
         witherSkeleton.moveTo(shulker.getX(), shulker.getY(), shulker.getZ(), shulker.getYRot(), shulker.getXRot());
         witherSkeleton.setCustomName(Component.literal("Centinela del Vacío").withStyle(ChatFormatting.GOLD));
         RegistryAccess registryAccess = witherSkeleton.level().registryAccess();
         Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.EYE);
         Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.AMETHYST);
         ArmorTrim trim = new ArmorTrim(material, pattern);
         ItemStack helmet = new ItemStack(Items.RESPAWN_ANCHOR);
         ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
         chestplate.set(DataComponents.TRIM, trim);
         chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(262663, false));
         ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
         leggings.set(DataComponents.TRIM, trim);
         leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(262663, false));
         ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
         boots.set(DataComponents.TRIM, trim);
         boots.set(DataComponents.DYED_COLOR, new DyedItemColor(262663, false));
         ItemStack bow = new ItemStack(Items.BOW);
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
         int powerLevel = day >= 50 ? 50 : 30;
         int punchLevel = day >= 50 ? 50 : 30;
         bow.enchant(power, powerLevel);
         bow.enchant(punch, punchLevel);
         Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.SCALE)).setBaseValue(0.5);
         Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(10.0);
         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         witherSkeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
         witherSkeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
         witherSkeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
         witherSkeleton.setItemSlot(EquipmentSlot.FEET, boots);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
         witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
         witherSkeleton.startRiding(shulker);
         level.addFreshEntity(witherSkeleton);
      }
   }

   private static void claseCaotica(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      EndCrystal endCrystal = (EndCrystal)EntityType.END_CRYSTAL.create(level);
      shulker.setCustomName(Component.literal("Shulker Caótico").withStyle(ChatFormatting.YELLOW));
      shulker.addTag("dropsShulkerShells");
      shulker.addTag("shulkerBulletNoLevitation");
      shulker.addTag("explosionImmune");
      shulker.addTag("shulkerCaotico");
      shulker.setVariant(Optional.of(DyeColor.YELLOW));
      if (endCrystal != null) {
         endCrystal.setCustomName(Component.literal("Cristal del End Caótico").withStyle(ChatFormatting.YELLOW));
         endCrystal.addTag("fromShulkerCaotico");
         endCrystal.moveTo(shulker.getX(), shulker.getY(), shulker.getZ(), shulker.getYRot(), shulker.getXRot());
         endCrystal.setShowBottom(false);
         endCrystal.startRiding(shulker);
         level.addFreshEntity(endCrystal);
      }
   }

   private static void claseVoid(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      AttributeInstance shulkerHealth = Objects.requireNonNull(shulker.getAttribute(Attributes.MAX_HEALTH));
      float healthValue = day >= 50 ? 50.0F : 30.0F;
      shulker.setCustomName(Component.literal("Shulker Void").withStyle(ChatFormatting.BLACK));
      shulker.addTag("dropsShulkerShells");
      shulker.addTag("shulkerBulletNoLevitation");
      shulker.addTag("shulkerVoid");
      shulker.setVariant(Optional.of(DyeColor.BLACK));
      shulkerHealth.setBaseValue(healthValue);
      shulker.setHealth(shulker.getMaxHealth());
   }

   private static void clasePlaga(ServerLevel level, Shulker shulker) {
      int day = DayGlobalCount.CURRENT_DAY;
      shulker.setCustomName(Component.literal("Shulker de la Plaga").withStyle(ChatFormatting.GREEN));
      shulker.addTag("dropsShulkerShells");
      shulker.addTag("shulkerBulletNoLevitation");
      shulker.addTag("shulkerPlaga");
      shulker.setVariant(Optional.of(DyeColor.LIME));
      shulker.addEffect(new MobEffectInstance(MobEffects.INFESTED, -1, 0, false, true));
   }
}
