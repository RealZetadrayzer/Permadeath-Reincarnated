package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.Nullable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class ZombieHorseChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof ZombieHorse horse) {
                  if (!horse.getTags().contains("plainsZombieHorse")) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        horse.addTag("plainsZombieHorse");
                        if (day >= 30) {
                           horse.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                           horse.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, true));
                        }

                        Zombie zombie = (Zombie)EntityType.ZOMBIE.create(level);
                        if (zombie != null) {
                           setupZombieRider(level, horse, zombie, day);
                           level.addFreshEntity(zombie);
                        }

                        applyHorseVariant(horse);
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
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof ZombieHorse horse) {
               if (horse.getTags().contains("plainsZombieHorse")) {
                  level.explode(horse, horse.getX(), horse.getY(), horse.getZ(), 4.0F, false, ExplosionInteraction.MOB);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void commanderReinforcements(Post event) {
      Entity entity = event.getEntity();
      if (entity.level() instanceof ServerLevel level) {
         if (!level.isClientSide) {
            if (entity instanceof Zombie zombie) {
               if (zombie.getTags().contains("zombieDeathRider")) {
                  if (zombie.isAlive()) {
                     if (zombie.tickCount % 60 == 0) {
                        if (zombie.getTarget() != null) {
                           int nearbyAllies = level.getEntitiesOfClass(
                                 Zombie.class, zombie.getBoundingBox().inflate(16.0), z -> z.isAlive() && z.getTags().contains("fromDeathZombie")
                              )
                              .size();
                           if (nearbyAllies < 4) {
                              int chance = 1 + RANDOM.nextInt(100);
                              int chanceThreshold = zombie.getTarget() instanceof ServerPlayer ? 40 : 20;
                              int amount = zombie.getTarget() instanceof ServerPlayer ? 2 + RANDOM.nextInt(3) : 1 + RANDOM.nextInt(2);
                              if (chance <= chanceThreshold) {
                                 for (int i = 0; i < amount; i++) {
                                    int delay = i * 10;
                                    ScheduleInTicks.schedule(
                                       () -> {
                                          if (zombie.isAlive()) {
                                             if (zombie.level() instanceof ServerLevel serverLevel) {
                                                int alliesNow = serverLevel.getEntitiesOfClass(
                                                      Zombie.class,
                                                      zombie.getBoundingBox().inflate(16.0),
                                                      z -> z.isAlive() && z.getTags().contains("fromDeathZombie")
                                                   )
                                                   .size();
                                                if (alliesNow < 4) {
                                                   spawnReinforcement(serverLevel, zombie);
                                                }
                                             }
                                          }
                                       },
                                       delay
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void preventFriendlyFire(LivingIncomingDamageEvent event) {
      LivingEntity victim = event.getEntity();
      boolean victimIsFriendly = victim.getTags().contains("fromDeathZombie") || victim.getTags().contains("zombieDeathRider");
      if (victimIsFriendly) {
         DamageSource source = event.getSource();
         if (!(
            source.getEntity() instanceof LivingEntity livingAttacker
               && (livingAttacker.getTags().contains("fromDeathZombie") || livingAttacker.getTags().contains("zombieDeathRider"))
         )) {
            if (source.getDirectEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof LivingEntity livingOwner
               && (livingOwner.getTags().contains("fromDeathZombie") || livingOwner.getTags().contains("zombieDeathRider"))) {
               event.setCanceled(true);
            }
         } else {
            event.setCanceled(true);
         }
      }
   }

   private static void setupZombieRider(ServerLevel level, ZombieHorse horse, Zombie zombie, int day) {
      zombie.addTag("zombieRider");
      zombie.addTag("cannotBeTransmuted");
      zombie.setCustomName(Component.literal("Jinete No-Muerto").withStyle(ChatFormatting.GREEN));
      if (day >= 30) {
         zombie.addTag("zombieRiderLeader");
         Objects.requireNonNull(zombie.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(100.0);
         zombie.setHealth(zombie.getMaxHealth());
      }

      if (day >= 15) {
         equipZombieRider(level, zombie, day);
      }

      if (day >= 40) {
         zombie.addTag("zombieDeathRider");
         zombie.setCustomName(Component.literal("Jinete de la Muerte").withStyle(ChatFormatting.DARK_RED));
         createDeathRiderArmor(level, zombie);
      }

      zombie.setPos(horse.getX(), horse.getY() + 0.5, horse.getZ());
      zombie.startRiding(horse);
      ((MobSpawnTypeAccessor)zombie).setSpawnType(MobSpawnType.MOB_SUMMONED);
      horse.setOwnerUUID(zombie.getUUID());
      horse.setTamed(true);
   }

   private static void applyHorseVariant(ZombieHorse horse) {
      int chance = 1 + RANDOM.nextInt(100);
      if (chance <= 1) {
         horse.setCustomName(Component.literal("Armando el Muerto Viviente").withStyle(ChatFormatting.GREEN));
         horse.addTag("armandZombieHorse");
      } else {
         horse.setCustomName(Component.literal("Caballo No-Muerto").withStyle(ChatFormatting.GREEN));
      }
   }

   private static void equipZombieRider(ServerLevel level, Zombie zombie, int day) {
      ItemStack weapon;
      if (day >= 30) {
         weapon = day >= 40 ? new ItemStack(Items.DIAMOND_AXE) : (RANDOM.nextBoolean() ? new ItemStack(Items.DIAMOND_SWORD) : new ItemStack(Items.DIAMOND_AXE));
      } else {
         int roll = RANDOM.nextInt(3);

         weapon = switch (roll) {
            case 0 -> new ItemStack(Items.IRON_SWORD);
            case 1 -> new ItemStack(Items.IRON_AXE);
            default -> new ItemStack(Items.IRON_SHOVEL);
         };
      }

      if (RANDOM.nextFloat() < 0.05F) {
         zombie.setLeftHanded(true);
      }

      zombie.setItemSlot(EquipmentSlot.MAINHAND, weapon);
   }

   private static void equipZombieReinforcement(Zombie zombie, int day) {
      ItemStack weapon = day >= 55 ? new ItemStack(Items.NETHERITE_AXE) : new ItemStack(Items.DIAMOND_AXE);
      if (RANDOM.nextFloat() < 0.05F) {
         zombie.setLeftHanded(true);
      }

      zombie.setItemSlot(EquipmentSlot.MAINHAND, weapon);
      if (day >= 55) {
         RegistryAccess registryAccess = zombie.level().registryAccess();
         Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
         weapon.enchant(sharpness, 25);
      }
   }

   @Nullable
   private static BlockPos findReinforcementPos(ServerLevel level, BlockPos around, int radius) {
      for (int tries = 0; tries < 10; tries++) {
         int dx = RANDOM.nextInt(radius * 2 + 1) - radius;
         int dz = RANDOM.nextInt(radius * 2 + 1) - radius;
         BlockPos xz = around.offset(dx, 0, dz);
         int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, xz.getX(), xz.getZ());
         BlockPos pos = new BlockPos(xz.getX(), y, xz.getZ());
         if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
            && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
            && !level.getFluidState(pos).is(FluidTags.LAVA)) {
            return pos;
         }
      }

      return null;
   }

   private static void spawnReinforcement(ServerLevel level, Zombie captain) {
      int day = DayGlobalCount.CURRENT_DAY;
      Zombie zombie = (Zombie)EntityType.ZOMBIE.create(level);
      if (zombie != null) {
         BlockPos spawnPos = findReinforcementPos(level, captain.blockPosition(), 6);
         if (spawnPos != null) {
            double x = spawnPos.getX() + 0.5;
            double y = spawnPos.getY();
            double z = spawnPos.getZ() + 0.5;
            if (day >= 55) {
               Objects.requireNonNull(zombie.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(200.0);
               zombie.setHealth(zombie.getMaxHealth());
               zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, true));
            }

            zombie.moveTo(x, y, z, captain.getYRot(), 0.0F);
            zombie.setCustomName(Component.literal("Soldado de la Muerte").withStyle(ChatFormatting.RED));
            zombie.addTag("fromDeathZombie");
            zombie.addTag("cannotBeTransmuted");
            zombie.setCanPickUpLoot(false);
            equipZombieReinforcement(zombie, day);
            MobPreventEquipmentDrops.preventAllEquipmentDrop(zombie);
            ((MobSpawnTypeAccessor)zombie).setSpawnType(MobSpawnType.MOB_SUMMONED);
            LivingEntity captainTarget = captain.getTarget();
            if (captainTarget != null && captainTarget.isAlive()) {
               zombie.setTarget(captainTarget);
            }

            level.addFreshEntity(zombie);
            playZombieSummonVFX(level, x, y, z);
         }
      }
   }

   private static void playZombieSummonVFX(ServerLevel level, double x, double y, double z) {
      level.sendParticles(ParticleTypes.FLASH, x, y + 0.5, z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.SNEEZE, x, y + 1.0, z, 20, 0.4, 0.6, 0.4, 0.01);
      level.sendParticles(ParticleTypes.SOUL, x, y + 0.5, z, 25, 0.5, 0.4, 0.5, 0.02);
      level.sendParticles(ParticleTypes.CRIT, x, y + 0.8, z, 15, 0.4, 0.4, 0.4, 0.2);
      level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.EMERALD_BLOCK.defaultBlockState()), x, y + 0.1, z, 20, 0.6, 0.2, 0.6, 0.02);
      level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.defaultBlockState()), x, y + 1.0, z, 20, 0.4, 0.6, 0.4, 0.01);
      level.playSound(null, x, y, z, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 0.9F, 0.95F);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.HOSTILE, 0.55F, 1.15F), 6);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE, 0.7F, 1.35F), 12);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 0.55F, 0.8F), 14);
   }

   private static void createDeathRiderArmor(ServerLevel level, Zombie zombie) {
      RegistryAccess registry = level.registryAccess();
      Holder<TrimPattern> pattern = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SILENCE);
      Holder<TrimMaterial> material = registry.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.DIAMOND);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.SPAWNER);
      ItemStack chestplate = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.CHAINMAIL_LEGGINGS);
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.CHAINMAIL_BOOTS);
      boots.set(DataComponents.TRIM, trim);
      zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
      zombie.setItemSlot(EquipmentSlot.CHEST, chestplate);
      zombie.setItemSlot(EquipmentSlot.LEGS, leggings);
      zombie.setItemSlot(EquipmentSlot.FEET, boots);
      MobPreventEquipmentDrops.preventAllEquipmentDrop(zombie);
   }
}
