package zeta.org.permadeath_reincarnated.mobs;

import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
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
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Builder;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.Nullable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PiglinVariantAccess;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class PiglinChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Piglin piglin) {
                     if (!piglin.getTags().contains("fromUniversal")) {
                        if (piglin.getClass() == Piglin.class) {
                           if (!event.loadedFromDisk()) {
                              int day = DayGlobalCount.CURRENT_DAY;
                              if (day >= 25) {
                                 if (!piglin.getTags().contains("fromCommander")) {
                                    chooseClass(level, piglin, day);
                                 } else {
                                    chooseClassReinforcement(level, piglin);
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
   public static void onTick(Post event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.tickCount % 10 == 0) {
            if (entity instanceof Piglin piglin) {
               if (piglin.isAlive()) {
                  if (piglin.getTags().contains("piglinKamikaze")) {
                     if (!piglin.getPersistentData().getBoolean("Exploded")) {
                        boolean onFire = piglin.isOnFire();
                        if (onFire) {
                           piglin.getPersistentData().putBoolean("Exploded", true);
                           level.explode(piglin, piglin.getX(), piglin.getY(), piglin.getZ(), 5.0F, false, ExplosionInteraction.TNT);
                           piglin.discard();
                        } else {
                           LivingEntity target = piglin.getTarget();
                           if (target != null) {
                              if (target.isAlive()) {
                                 if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                                    double distance = piglin.distanceToSqr(target);
                                    if (!(distance > 9.0)) {
                                       if (target.canBeSeenAsEnemy()) {
                                          int day = DayGlobalCount.CURRENT_DAY;
                                          float explosionLevel = day >= 40 ? 7.0F : 5.0F;
                                          piglin.getPersistentData().putBoolean("Exploded", true);
                                          level.explode(piglin, piglin.getX(), piglin.getY(), piglin.getZ(), explosionLevel, false, ExplosionInteraction.TNT);
                                          piglin.discard();
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
      }
   }

   @SubscribeEvent
   public static void commanderReinforcements(Post event) {
      Entity entity = event.getEntity();
      if (entity.level() instanceof ServerLevel level) {
         if (!level.isClientSide) {
            if (entity instanceof PiglinBrute piglinBrute) {
               if (piglinBrute.getTags().contains("piglinCommander")) {
                  if (piglinBrute.isAlive()) {
                     if (piglinBrute.tickCount % 60 == 0) {
                        if (piglinBrute.getTarget() != null) {
                           int nearbyAllies = level.getEntitiesOfClass(
                                 Piglin.class, piglinBrute.getBoundingBox().inflate(16.0), p -> p.isAlive() && p.getTags().contains("classPiglin")
                              )
                              .size();
                           if (nearbyAllies < 6) {
                              int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                              int chanceThreshold = piglinBrute.getTarget() instanceof ServerPlayer ? 40 : 20;
                              int amount = piglinBrute.getTarget() instanceof ServerPlayer
                                 ? 3 + RandomUtil.RANDOM.nextInt(4)
                                 : 2 + RandomUtil.RANDOM.nextInt(3);
                              if (chance <= chanceThreshold) {
                                 for (int i = 0; i < amount; i++) {
                                    int delay = i * 10;
                                    ScheduleInTicks.schedule(
                                       () -> {
                                          if (piglinBrute.isAlive()) {
                                             if (piglinBrute.level() instanceof ServerLevel serverLevel) {
                                                int alliesNow = serverLevel.getEntitiesOfClass(
                                                      Piglin.class,
                                                      piglinBrute.getBoundingBox().inflate(16.0),
                                                      p -> p.isAlive() && p.getTags().contains("classPiglin")
                                                   )
                                                   .size();
                                                if (alliesNow < 6) {
                                                   spawnReinforcement(serverLevel, piglinBrute);
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
      boolean victimIsFriendly = victim.getTags().contains("fromCommander") || victim.getTags().contains("piglinCommander");
      if (victimIsFriendly) {
         DamageSource source = event.getSource();
         if (!(
            source.getEntity() instanceof LivingEntity livingAttacker
               && (livingAttacker.getTags().contains("fromCommander") || livingAttacker.getTags().contains("piglinCommander"))
         )) {
            if (source.getDirectEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof LivingEntity livingOwner
               && (livingOwner.getTags().contains("fromCommander") || livingOwner.getTags().contains("piglinCommander"))) {
               event.setCanceled(true);
            }
         } else {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onArrowSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof AbstractArrow arrow) {
         if (!event.loadedFromDisk()) {
            if (!arrow.level().isClientSide) {
               if (arrow.getOwner() instanceof Piglin owner) {
                  if (owner.getTags().contains("piglinMachineGun")) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     double extraDamage;
                     if (day >= 40) {
                        extraDamage = day >= 70 ? 20.0 : 10.0;
                     } else {
                        extraDamage = 2.5;
                     }

                     double finalDamage = arrow.getBaseDamage() + extraDamage;
                     arrow.setBaseDamage(finalDamage);
                  }
               }
            }
         }
      }
   }

   public static void chooseClass(ServerLevel level, Piglin piglin, int day) {
      int roll = 1 + RandomUtil.RANDOM.nextInt(100);
      int chanceThreshold = day >= 40 ? 15 : 10;
      if (roll <= chanceThreshold) {
         claseComandante(level, piglin);
      } else {
         int subRoll = 1 + RandomUtil.RANDOM.nextInt(3);
         switch (subRoll) {
            case 1:
               claseDestructor(level, piglin);
               break;
            case 2:
               claseKamikaze(level, piglin);
               break;
            case 3:
               claseAmetralladora(level, piglin);
         }
      }
   }

   public static void chooseClassReinforcement(ServerLevel level, Piglin piglin) {
      int roll = 1 + RandomUtil.RANDOM.nextInt(3);
      switch (roll) {
         case 1:
            claseDestructor(level, piglin);
            break;
         case 2:
            claseKamikaze(level, piglin);
            break;
         case 3:
            claseAmetralladora(level, piglin);
      }
   }

   @Nullable
   private static BlockPos findReinforcementPos(ServerLevel level, BlockPos around, int radius) {
      for (int tries = 0; tries < 10; tries++) {
         int dx = RandomUtil.RANDOM.nextInt(radius * 2 + 1) - radius;
         int dz = RandomUtil.RANDOM.nextInt(radius * 2 + 1) - radius;
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

   private static void spawnReinforcement(ServerLevel level, PiglinBrute commander) {
      Piglin piglin = (Piglin)EntityType.PIGLIN.create(level);
      if (piglin != null) {
         BlockPos spawnPos = findReinforcementPos(level, commander.blockPosition(), 6);
         if (spawnPos != null) {
            double x = spawnPos.getX() + 0.5;
            double y = spawnPos.getY();
            double z = spawnPos.getZ() + 0.5;
            piglin.moveTo(x, y, z, commander.getYRot(), 0.0F);
            piglin.addTag("fromCommander");
            ((MobSpawnTypeAccessor)piglin).setSpawnType(MobSpawnType.MOB_SUMMONED);
            piglin.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05F);
            LivingEntity captainTarget = commander.getTarget();
            if (captainTarget != null && captainTarget.isAlive()) {
               piglin.setTarget(captainTarget);
            }

            level.addFreshEntity(piglin);
            level.sendParticles(ParticleTypes.FLASH, x, y + 0.5, z, 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y + 1.0, z, 20, 0.4, 0.6, 0.4, 0.01);
            level.sendParticles(ParticleTypes.FLAME, x, y + 0.5, z, 25, 0.5, 0.4, 0.5, 0.02);
            level.sendParticles(ParticleTypes.CRIT, x, y + 0.8, z, 15, 0.4, 0.4, 0.4, 0.2);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.NETHERRACK.defaultBlockState()), x, y + 0.1, z, 20, 0.6, 0.2, 0.6, 0.02);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.GOLD_BLOCK.defaultBlockState()), x, y + 1.0, z, 20, 0.4, 0.6, 0.4, 0.01);
            playCommanderSummonSounds(level, x, y, z);
         }
      }
   }

   private static void playCommanderSummonSounds(ServerLevel level, double x, double y, double z) {
      level.playSound(null, x, y, z, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 0.9F, 0.95F);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.HOSTILE, 0.55F, 1.15F), 6);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE, 0.7F, 1.35F), 12);
      ScheduleInTicks.schedule(() -> level.playSound(null, x, y, z, SoundEvents.GOAT_HORN_PLAY, SoundSource.HOSTILE, 0.55F, 0.8F), 14);
   }

   private static void claseComandante(ServerLevel level, Piglin piglin) {
      int day = DayGlobalCount.CURRENT_DAY;
      PiglinBrute piglinBrute = (PiglinBrute)EntityType.PIGLIN_BRUTE.create(level);
      piglin.addTag("noAI");
      if (piglinBrute != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(piglinBrute);
         piglinBrute.moveTo(piglin.getX(), piglin.getY(), piglin.getZ(), piglin.getYRot(), piglin.getXRot());
         piglinBrute.setCustomName(Component.literal("Piglin Comandante").withStyle(ChatFormatting.GOLD));
         piglinBrute.setBaby(false);
         piglinBrute.setCanPickUpLoot(false);
         piglinBrute.setImmuneToZombification(true);
         ((MobSpawnTypeAccessor)piglinBrute).setSpawnType(MobSpawnType.MOB_SUMMONED);
         piglinBrute.addTag("classPiglin");
         piglinBrute.addTag("arrowImmune");
         piglinBrute.addTag("piglinCommander");
         RegistryAccess registryAccess = level.registryAccess();
         Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SILENCE);
         Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.NETHERITE);
         ArmorTrim trim = new ArmorTrim(material, pattern);
         if (piglinBrute instanceof PiglinVariantAccess access) {
            access.permadeath$setPiglinVariant(1);
         }

         ItemStack helmet = new ItemStack(Items.BLACK_BANNER);
         ItemStack chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
         chestplate.set(DataComponents.TRIM, trim);
         chestplate.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
         ItemStack leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
         leggings.set(DataComponents.TRIM, trim);
         leggings.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
         ItemStack boots = new ItemStack(Items.GOLDEN_BOOTS);
         boots.set(DataComponents.TRIM, trim);
         boots.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
         ItemStack netheriteAxe = new ItemStack(Items.NETHERITE_AXE);
         Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
         Holder<Enchantment> knockback = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.KNOCKBACK);
         if (day >= 40) {
            netheriteAxe.enchant(sharpness, 20);
            netheriteAxe.enchant(knockback, 5);
         } else {
            netheriteAxe.enchant(sharpness, 5);
            netheriteAxe.enchant(knockback, 2);
         }

         Registry<BannerPattern> bannerRegistry = registryAccess.registryOrThrow(Registries.BANNER_PATTERN);
         Holder<BannerPattern> stripeTop = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "stripe_top"))
         );
         Holder<BannerPattern> triangleTop = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "triangle_top"))
         );
         Holder<BannerPattern> border = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "border"))
         );
         Holder<BannerPattern> curlyBorder = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "curly_border"))
         );
         Holder<BannerPattern> piglinBanner = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "piglin"))
         );
         Holder<BannerPattern> gradientUp = bannerRegistry.getHolderOrThrow(
            ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "gradient_up"))
         );
         Builder builder = new Builder();
         builder.add(stripeTop, DyeColor.RED);
         builder.add(triangleTop, DyeColor.BLACK);
         builder.add(border, DyeColor.BLACK);
         builder.add(curlyBorder, DyeColor.BLACK);
         builder.add(piglinBanner, DyeColor.RED);
         builder.add(gradientUp, DyeColor.RED);
         helmet.set(DataComponents.BASE_COLOR, DyeColor.BLACK);
         helmet.set(DataComponents.BANNER_PATTERNS, builder.build());
         if (day >= 60) {
            piglinBrute.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
            piglinBrute.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 1, false, true));
         }

         Objects.requireNonNull(piglinBrute.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(0.75);
         Objects.requireNonNull(piglinBrute.getAttribute(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)).setBaseValue(0.75);
         Objects.requireNonNull(piglinBrute.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(200.0);
         piglinBrute.setHealth(piglinBrute.getMaxHealth());
         piglinBrute.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
         piglinBrute.setItemSlot(EquipmentSlot.MAINHAND, netheriteAxe);
         piglinBrute.setItemSlot(EquipmentSlot.HEAD, helmet);
         piglinBrute.setItemSlot(EquipmentSlot.CHEST, chestplate);
         piglinBrute.setItemSlot(EquipmentSlot.LEGS, leggings);
         piglinBrute.setItemSlot(EquipmentSlot.FEET, boots);
         piglinBrute.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05F);
         if (piglin.isPassenger()) {
            Entity vehicle = piglin.getVehicle();
            piglin.stopRiding();
            assert vehicle != null;
            piglinBrute.startRiding(vehicle, true);
         }

         if (piglin.isVehicle()) {
            List<Entity> passengers = List.copyOf(piglin.getPassengers());
            piglin.ejectPassengers();

            for (Entity passenger : passengers) {
               passenger.startRiding(piglinBrute, true);
            }
         }

         level.addFreshEntity(piglinBrute);
         level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> piglin.remove(RemovalReason.DISCARDED)));
      }
   }

   private static void claseDestructor(ServerLevel level, Piglin piglin) {
      int day = DayGlobalCount.CURRENT_DAY;
      MobPreventEquipmentDrops.preventAllEquipmentDrop(piglin);
      piglin.setCustomName(Component.literal("Piglin Destructor").withStyle(ChatFormatting.RED));
      piglin.setBaby(false);
      piglin.setCanPickUpLoot(false);
      piglin.setImmuneToZombification(true);
      piglin.addTag("classPiglin");
      piglin.addTag("piglinDestroyer");
      RegistryAccess registryAccess = level.registryAccess();
      Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SNOUT);
      Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.REDSTONE);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
      helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      helmet.set(DataComponents.TRIM, trim);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      boots.set(DataComponents.TRIM, trim);
      ItemStack mace = new ItemStack(Items.MACE);
      Holder<Enchantment> breach = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
      Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      if (day >= 40) {
         mace.enchant(sharpness, 10);
         mace.enchant(breach, 3);
      } else {
         mace.enchant(breach, 3);
      }

      Objects.requireNonNull(piglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(40.0);
      piglin.setHealth(piglin.getMaxHealth());
      piglin.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      piglin.setItemSlot(EquipmentSlot.MAINHAND, mace);
      piglin.setItemSlot(EquipmentSlot.HEAD, helmet);
      piglin.setItemSlot(EquipmentSlot.CHEST, chestplate);
      piglin.setItemSlot(EquipmentSlot.LEGS, leggings);
      piglin.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseKamikaze(ServerLevel level, Piglin piglin) {
      int day = DayGlobalCount.CURRENT_DAY;
      MobPreventEquipmentDrops.preventAllEquipmentDrop(piglin);
      piglin.setCustomName(Component.literal("Piglin Kamikaze").withStyle(ChatFormatting.DARK_RED));
      piglin.setBaby(false);
      piglin.setCanPickUpLoot(false);
      piglin.setImmuneToZombification(true);
      piglin.addTag("classPiglin");
      piglin.addTag("piglinKamikaze");
      RegistryAccess registryAccess = level.registryAccess();
      if (piglin instanceof PiglinVariantAccess access) {
         access.permadeath$setPiglinVariant(2);
      }

      ItemStack helmet = new ItemStack(Items.TNT);
      ItemStack goldenSword = new ItemStack(Items.GOLDEN_SWORD);
      Holder<Enchantment> fireAspect = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FIRE_ASPECT);
      Holder<Enchantment> breach = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
      if (day >= 40) {
         goldenSword.enchant(fireAspect, 25);
         goldenSword.enchant(breach, 2);
         Objects.requireNonNull(piglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(40.0);
      } else {
         goldenSword.enchant(fireAspect, 5);
         Objects.requireNonNull(piglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(20.0);
      }

      piglin.setHealth(piglin.getMaxHealth());
      piglin.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      piglin.setItemSlot(EquipmentSlot.MAINHAND, goldenSword);
      piglin.setItemSlot(EquipmentSlot.HEAD, helmet);
      piglin.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
      piglin.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
      piglin.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
   }

   private static void claseAmetralladora(ServerLevel level, Piglin piglin) {
      int day = DayGlobalCount.CURRENT_DAY;
      MobPreventEquipmentDrops.preventAllEquipmentDrop(piglin);
      piglin.setCustomName(Component.literal("Piglin Ametrallador").withStyle(ChatFormatting.RED));
      piglin.setBaby(false);
      piglin.setCanPickUpLoot(false);
      piglin.setImmuneToZombification(true);
      piglin.addTag("classPiglin");
      piglin.addTag("piglinMachineGun");
      RegistryAccess registryAccess = level.registryAccess();
      Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SENTRY);
      Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.NETHERITE);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
      helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
      helmet.set(DataComponents.TRIM, trim);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
      boots.set(DataComponents.TRIM, trim);
      ItemStack crossbow = new ItemStack(Items.CROSSBOW);
      Holder<Enchantment> quickCharge = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.QUICK_CHARGE);
      crossbow.enchant(quickCharge, 4);
      if (day >= 40) {
         Objects.requireNonNull(piglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(40.0);
      } else {
         Objects.requireNonNull(piglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(24.0);
      }

      piglin.setHealth(piglin.getMaxHealth());
      piglin.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      piglin.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
      piglin.setItemSlot(EquipmentSlot.HEAD, helmet);
      piglin.setItemSlot(EquipmentSlot.CHEST, chestplate);
      piglin.setItemSlot(EquipmentSlot.LEGS, leggings);
      piglin.setItemSlot(EquipmentSlot.FEET, boots);
   }
}
