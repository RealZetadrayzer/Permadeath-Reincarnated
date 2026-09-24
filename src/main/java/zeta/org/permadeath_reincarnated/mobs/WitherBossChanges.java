package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class WitherBossChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day >= 15) {
            if (event.getEntity() instanceof WitherBoss witherBoss) {
               if (!witherBoss.getTags().contains("fromUniversal")) {
                  if (!event.loadedFromDisk()) {
                     if (!witherBoss.level().isClientSide) {
                        if (witherBoss.getTags().contains("fromPlayer")) {
                           Objects.requireNonNull(witherBoss.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(witherBoss.getMaxHealth() * 2.0F);
                           witherBoss.setHealth(witherBoss.getMaxHealth());
                           witherBoss.setCustomName(Component.literal("Wither Reencarnado").withStyle(ChatFormatting.LIGHT_PURPLE));
                           if (day >= 40) {
                              witherBoss.setCustomName(Component.literal("Ultra Wither Reencarnado").withStyle(ChatFormatting.LIGHT_PURPLE));
                              Objects.requireNonNull(witherBoss.getAttribute(Attributes.ARMOR)).setBaseValue(20.0);
                              witherBoss.addTag("ultraWither");
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
   public static void onSpawnBeginning(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof WitherBoss witherBoss) {
         if (!event.loadedFromDisk()) {
            if (!witherBoss.level().isClientSide) {
               if (!witherBoss.getTags().contains("fromTimer")) {
                  if (!witherBoss.getTags().contains("fromPlayer")) {
                     if (witherBoss.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                        if (witherBoss.getTags().contains("miniWither")) {
                           witherBoss.setInvulnerableTicks(0);
                           Objects.requireNonNull(witherBoss.getAttribute(Attributes.SCALE)).setBaseValue(0.5);
                           witherBoss.setCustomName(Component.literal("Mini-Wither").withStyle(ChatFormatting.DARK_PURPLE));
                           witherBoss.addTag("beginningWitherCountdown");
                           witherBoss.addTag("beginningWither");
                        } else {
                           witherBoss.setInvulnerableTicks(0);
                           witherBoss.setCustomName(Component.literal("Wither del Comienzo").withStyle(ChatFormatting.DARK_PURPLE));
                           witherBoss.addTag("beginningWitherCountdown");
                           witherBoss.addTag("beginningWither");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWitherTick(Post event) {
      if (event.getEntity() instanceof WitherBoss wither) {
         if (!wither.level().isClientSide) {
            if (wither.getTags().contains("beginningWitherCountdown")) {
               CompoundTag data = wither.getPersistentData();
               if (!data.contains("beginningCountdown")) {
                  int countdown = 50 + wither.getRandom().nextInt(151);
                  data.putInt("beginningCountdown", countdown);
                  wither.setHealth(wither.getMaxHealth());
               }

               int countdown = data.getInt("beginningCountdown");
               wither.setNoAi(true);
               wither.setInvulnerable(true);
               data.putInt("beginningCountdown", --countdown);
               if (countdown <= 0) {
                  wither.setNoAi(false);
                  wither.setInvulnerable(false);
                  wither.level().explode(wither, wither.getX(), wither.getEyeY(), wither.getZ(), 7.0F, false, ExplosionInteraction.MOB);
                  if (!wither.isSilent()) {
                     wither.level().globalLevelEvent(1023, wither.blockPosition(), 0);
                  }

                  wither.getTags().remove("beginningWitherCountdown");
                  data.remove("beginningCountdown");
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWitherExplosionTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day >= 15) {
            if (event.getEntity() instanceof WitherBoss wither) {
               if (wither.getTags().contains("fromPlayer")) {
                  if (wither.level() instanceof ServerLevel level) {
                     if (!level.isClientSide()) {
                        if (wither.tickCount % 10 == 0) {
                           if (wither.getHealth() <= wither.getMaxHealth() * 0.5 && wither.isPowered() && !wither.getPersistentData().getBoolean("Exploded")) {
                              wither.getPersistentData().putBoolean("Exploded", true);
                              triggerExplosion(level, wither);
                              spawnWitherGuards(level, wither, day);
                           }

                           if (wither.getHealth() <= wither.getMaxHealth() * 0.1
                              && wither.isPowered()
                              && !wither.getPersistentData().getBoolean("ExplodedAgain")
                              && wither.getTags().contains("ultraWither")) {
                              wither.getPersistentData().putBoolean("ExplodedAgain", true);
                              triggerExplosion(level, wither);
                              spawnWitherGuards(level, wither, day);
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
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      HitResult hit = event.getRayTraceResult();
      if (!projectile.level().isClientSide()) {
         Entity owner = projectile.getOwner();
         if (owner != null) {
            if (owner.getType().equals(EntityType.WITHER)) {
               if (owner.getTags().contains("fromTimer")) {
                  if (hit.getType() == Type.ENTITY) {
                     EntityHitResult entityHit = (EntityHitResult)hit;
                     if (entityHit.getEntity() instanceof ServerPlayer player) {
                        if (player.isAlive()) {
                           if (player.isBlocking()) {
                              ItemStack using = player.getUseItem();
                              if (using.is(Items.SHIELD)) {
                                 player.getCooldowns().addCooldown(Items.SHIELD, 300);
                                 player.stopUsingItem();
                                 player.level().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
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
   public static void onProjectileImpactWitherSkeleton(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult) {
         Entity hit = hitResult.getEntity();
         if (hit instanceof LivingEntity target) {
            if (!hit.level().isClientSide) {
               if (!target.isDeadOrDying()) {
                  if (target.isAffectedByPotions()) {
                     if (event.getProjectile().getOwner() instanceof WitherSkeleton witherSkeleton) {
                        if (DayGlobalCount.CURRENT_DAY >= 40) {
                           if (witherSkeleton.getTags().contains("fromWither")) {
                              target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 0, false, true));
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
   public static void onLivingDamageWitherSkeleton(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (!target.level().isClientSide) {
            if (!target.isDeadOrDying()) {
               if (target.isAffectedByPotions()) {
                  if (attacker instanceof WitherSkeleton witherSkeleton) {
                     if (DayGlobalCount.CURRENT_DAY >= 40) {
                        if (witherSkeleton.getTags().contains("fromWither")) {
                           if (!witherSkeleton.getTags().contains("isRanged")) {
                              target.addEffect(new MobEffectInstance(MobEffects.WITHER, 800, 1, false, true));
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
   private static void onSuffocationDamage(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (mob.level() instanceof ServerLevel level) {
            if (!mob.level().isClientSide) {
               if (mob.getTags().contains("ultraWither")) {
                  DamageSource source = event.getSource();
                  if (source.is(DamageTypes.IN_WALL)) {
                     mob.heal(40.0F);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void despawnBeginningWithers(Post event) {
      if (event.getEntity() instanceof WitherBoss witherBoss) {
         if (!witherBoss.level().isClientSide) {
            if (witherBoss.level() instanceof ServerLevel level) {
               if (witherBoss.getTags().contains("beginningWither")) {
                  if (witherBoss.tickCount % 100 == 0) {
                     if (!witherBoss.hasControllingPassenger()) {
                        Player nearest = level.getNearestPlayer(witherBoss, 100.0);
                        if (nearest == null) {
                           witherBoss.remove(RemovalReason.DISCARDED);
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
      boolean victimIsFriendly = victim.getTags().contains("fromWither") || victim.getTags().contains("fromPlayer");
      if (victimIsFriendly) {
         DamageSource source = event.getSource();
         if (!(
            source.getEntity() instanceof LivingEntity livingAttacker
               && (livingAttacker.getTags().contains("fromWither") || livingAttacker.getTags().contains("fromPlayer"))
         )) {
            if (source.getDirectEntity() instanceof Projectile projectile
               && projectile.getOwner() instanceof LivingEntity livingOwner
               && (livingOwner.getTags().contains("fromWither") || livingOwner.getTags().contains("fromPlayer"))) {
               event.setCanceled(true);
            }
         } else {
            event.setCanceled(true);
         }
      }
   }

   private static void spawnWitherGuards(ServerLevel level, WitherBoss wither, int day) {
      for (int i = 0; i < 2; i++) {
         spawnSwordSkeleton(level, wither, day);
      }

      for (int i = 0; i < 2; i++) {
         spawnArcherSkeleton(level, wither, day);
      }
   }

   private static void triggerExplosion(ServerLevel level, WitherBoss wither) {
      Explosion explosion = level.explode(wither, wither.getX(), wither.getY(), wither.getZ(), 7.0F, ExplosionInteraction.MOB);
      wither.addDeltaMovement(new Vec3(0.0, -2.0, 0.0));
      wither.hurtMarked = true;
      explosion.explode();
      explosion.finalizeExplosion(true);
      level.playSound(null, wither.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.VOICE, 0.5F, 0.5F);
      level.playSound(null, wither.blockPosition(), SoundEvents.WITHER_HURT, SoundSource.VOICE, 1.0F, 0.5F);
      level.playSound(null, wither.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.VOICE, 0.8F, 0.8F);
      level.sendParticles(ParticleTypes.END_ROD, wither.getX(), wither.getY() + 0.5, wither.getZ(), 80, 0.0, 0.0, 0.0, 0.1);
      level.sendParticles(ParticleTypes.SMALL_FLAME, wither.getX(), wither.getY() + 0.5, wither.getZ(), 80, 0.15, 0.15, 0.15, 0.1);
   }

   private static ItemStack createTrimmedArmor(ServerLevel level, Item item) {
      RegistryAccess registry = level.registryAccess();
      Holder<TrimPattern> pattern = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.WARD);
      Holder<TrimMaterial> material = registry.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.AMETHYST);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack stack = new ItemStack(item);
      stack.set(DataComponents.TRIM, trim);
      return stack;
   }

   private static void createVocaloidArmor(ServerLevel level, WitherSkeleton skeleton) {
      RegistryAccess registry = level.registryAccess();
      Holder<TrimPattern> patternHelmetAndLeggings = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SNOUT);
      Holder<TrimPattern> patternChestplate = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.VEX);
      Holder<TrimPattern> patternBoots = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.WILD);
      Holder<TrimMaterial> materialNetherite = registry.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.NETHERITE);
      Holder<TrimMaterial> materialDiamond = registry.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.DIAMOND);
      ArmorTrim trimHelmet = new ArmorTrim(materialNetherite, patternHelmetAndLeggings);
      ArmorTrim trimChestplate = new ArmorTrim(materialDiamond, patternChestplate);
      ArmorTrim trimLeggings = new ArmorTrim(materialDiamond, patternHelmetAndLeggings);
      ArmorTrim trimBoots = new ArmorTrim(materialDiamond, patternBoots);
      ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
      helmet.set(DataComponents.TRIM, trimHelmet);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.TRIM, trimChestplate);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(10329495, false));
      ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
      leggings.set(DataComponents.TRIM, trimLeggings);
      ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
      boots.set(DataComponents.TRIM, trimBoots);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void spawnSwordSkeleton(ServerLevel level, WitherBoss wither, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
         int chance = 1 + RandomUtil.RANDOM.nextInt(100);
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         RegistryAccess registry = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.moveTo(wither.position());
         witherSkeleton.addTag("fromWither");
         witherSkeleton.invulnerableTime = 60;
         witherSkeleton.setPersistenceRequired();
         ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
         int speedLevel = day >= 80 ? 2 : (day >= 40 ? 1 : -1);
         MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel, false, true);
         Holder<Enchantment> breach = registry.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
         Holder<Enchantment> fireAspect = registry.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FIRE_ASPECT);
         int breachLevel = day >= 80 ? 4 : (day >= 40 ? 2 : 0);
         int fireAspectLevel = day >= 80 ? 25 : (day >= 40 ? 10 : 0);
         float healthValue = day >= 80 ? 120.0F : (day >= 40 ? 80.0F : 40.0F);
         skeletonHealth.setBaseValue(healthValue);
         netheriteSword.enchant(breach, breachLevel);
         netheriteSword.enchant(fireAspect, fireAspectLevel);
         if (speedLevel >= 0) {
            witherSkeleton.addEffect(speed);
         }

         if (chance <= 5 && day >= 40) {
            createVocaloidArmor(level, witherSkeleton);
            witherSkeleton.setCustomName(Component.literal("Ultra Espadachín Vocaloider Guardián del Wither").withStyle(ChatFormatting.AQUA));
            witherSkeleton.addTag("witherVocaloider");
         } else {
            String baseName = "Espadachín Guardián del Wither";
            String prefix = "Ultra ";
            Component name = day >= 40
               ? Component.literal(prefix + baseName).withStyle(ChatFormatting.LIGHT_PURPLE)
               : Component.literal(baseName).withStyle(ChatFormatting.LIGHT_PURPLE);
            witherSkeleton.setItemSlot(EquipmentSlot.HEAD, createTrimmedArmor(level, Items.NETHERITE_HELMET));
            witherSkeleton.setItemSlot(EquipmentSlot.CHEST, createTrimmedArmor(level, Items.NETHERITE_CHESTPLATE));
            witherSkeleton.setItemSlot(EquipmentSlot.LEGS, createTrimmedArmor(level, Items.NETHERITE_LEGGINGS));
            witherSkeleton.setItemSlot(EquipmentSlot.FEET, createTrimmedArmor(level, Items.NETHERITE_BOOTS));
            witherSkeleton.setCustomName(name);
         }

         witherSkeleton.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05F);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         level.addFreshEntity(witherSkeleton);
      }
   }

   private static void spawnArcherSkeleton(ServerLevel level, WitherBoss wither, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
         int chance = 1 + RandomUtil.RANDOM.nextInt(100);
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         RegistryAccess registry = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.moveTo(wither.position());
         witherSkeleton.addTag("fromWither");
         witherSkeleton.addTag("isRanged");
         witherSkeleton.invulnerableTime = 60;
         witherSkeleton.setPersistenceRequired();
         ItemStack bow = new ItemStack(Items.BOW);
         int speedLevel = day >= 80 ? 2 : (day >= 40 ? 1 : -1);
         MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel, false, true);
         Holder<Enchantment> power = registry.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         int powerLevel = day >= 80 ? 50 : (day >= 40 ? 25 : 5);
         float healthValue = day >= 80 ? 120.0F : (day >= 40 ? 80.0F : 40.0F);
         skeletonHealth.setBaseValue(healthValue);
         bow.enchant(power, powerLevel);
         if (speedLevel >= 0) {
            witherSkeleton.addEffect(speed);
         }

         if (chance <= 5 && day >= 40) {
            createVocaloidArmor(level, witherSkeleton);
            witherSkeleton.setCustomName(Component.literal("Ultra Arquero Vocaloider Guardián del Wither").withStyle(ChatFormatting.AQUA));
            witherSkeleton.addTag("witherVocaloider");
         } else {
            String baseName = "Arquero Guardián del Wither";
            String prefix = "Ultra ";
            Component name = day >= 40
               ? Component.literal(prefix + baseName).withStyle(ChatFormatting.LIGHT_PURPLE)
               : Component.literal(baseName).withStyle(ChatFormatting.LIGHT_PURPLE);
            witherSkeleton.setItemSlot(EquipmentSlot.HEAD, createTrimmedArmor(level, Items.NETHERITE_HELMET));
            witherSkeleton.setItemSlot(EquipmentSlot.CHEST, createTrimmedArmor(level, Items.NETHERITE_CHESTPLATE));
            witherSkeleton.setItemSlot(EquipmentSlot.LEGS, createTrimmedArmor(level, Items.NETHERITE_LEGGINGS));
            witherSkeleton.setItemSlot(EquipmentSlot.FEET, createTrimmedArmor(level, Items.NETHERITE_BOOTS));
            witherSkeleton.setCustomName(name);
         }

         witherSkeleton.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05F);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         level.addFreshEntity(witherSkeleton);
      }
   }
}
