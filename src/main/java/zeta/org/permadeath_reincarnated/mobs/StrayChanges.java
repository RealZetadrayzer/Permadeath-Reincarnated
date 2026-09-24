package zeta.org.permadeath_reincarnated.mobs;

import java.util.List;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class StrayChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Stray stray) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 20) {
                           chooseClass(level, stray);
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
      int day = DayGlobalCount.CURRENT_DAY;
      Projectile projectile = event.getProjectile();
      Level level = projectile.level();
      if (!level.isClientSide) {
         Entity owner = projectile.getOwner();
         if (owner != null) {
            if (owner.getTags().contains("frostStray")) {
               HitResult hitResult = event.getRayTraceResult();
               Vec3 center;
               if (hitResult.getType() == Type.ENTITY) {
                  if (!(((EntityHitResult)hitResult).getEntity() instanceof LivingEntity living)) {
                     return;
                  }

                  if (!living.isAlive() || living.isDeadOrDying()) {
                     return;
                  }

                  center = new Vec3(living.getX(), living.getY() + 0.5, living.getZ());
               } else {
                  if (hitResult.getType() != Type.BLOCK) {
                     return;
                  }

                  center = hitResult.getLocation().add(0.0, 0.15, 0.0);
               }

               ServerLevel serverLevel = (ServerLevel)level;
               float radius = day >= 30 ? 6.5F : 5.0F;
               AABB box = new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius, center.y + radius, center.z + radius);
               serverLevel.sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0);
               serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, center.x, center.y, center.z, 80, 1.2, 0.8, 1.2, 0.15);
               serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, center.x, center.y, center.z, 45, 0.9, 0.4, 0.9, 0.25);
               serverLevel.sendParticles(ParticleTypes.CLOUD, center.x, center.y, center.z, 25, 0.8, 0.2, 0.8, 0.08);
               BlockPos soundPos = BlockPos.containing(center);
               serverLevel.playSound(null, soundPos, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.VOICE, 1.0F, 0.8F);
               serverLevel.playSound(null, soundPos, SoundEvents.BREEZE_INHALE, SoundSource.VOICE, 0.8F, 0.5F);
               serverLevel.playSound(null, soundPos, SoundEvents.BREEZE_SHOOT, SoundSource.VOICE, 2.0F, 0.8F);
               serverLevel.playSound(null, soundPos, SoundEvents.GLASS_BREAK, SoundSource.VOICE, 0.8F, 1.2F);

               for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
                  if (entity.isAlive()
                     && !(entity instanceof PolarBear)
                     && !(entity instanceof Stray)
                     && !(entity instanceof SnowGolem)
                     && !(entity.position().distanceToSqr(center) > radius * radius)) {
                     entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + day >= 30 ? 150 : 100));
                     entity.hurt(serverLevel.damageSources().freeze(), day >= 30 ? 10.0F : 5.0F);
                     if (entity instanceof ServerPlayer player) {
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_FROST_EXPLOSION_RECEIVED_ID);
                     }
                  }
               }

               projectile.discard();
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingReceivingDamage(Post event) {
      int day = DayGlobalCount.CURRENT_DAY;
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (target.level() instanceof ServerLevel level) {
            if (!target.level().isClientSide) {
               if (attacker instanceof LivingEntity entity) {
                  if (target.getTags().contains("gelidStray")) {
                     int chance = 1 + RANDOM.nextInt(100);
                     int chanceThreshold = day >= 50 ? 50 : (day >= 30 ? 25 : 10);
                     if (chance <= chanceThreshold) {
                        entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + day >= 30 ? 300 : 200));
                        entity.hurt(level.damageSources().freeze(), day >= 30 ? 10.0F : 5.0F);
                        level.sendParticles(ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY(), entity.getZ(), 20, 1.2, 0.8, 1.2, 0.15);
                        level.sendParticles(ParticleTypes.ITEM_SNOWBALL, entity.getX(), entity.getY(), entity.getZ(), 15, 0.9, 0.4, 0.9, 0.25);
                        level.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY(), entity.getZ(), 10, 0.8, 0.2, 0.8, 0.08);
                        level.playSound(null, entity.blockPosition(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.VOICE, 1.0F, 0.8F);
                        level.playSound(null, entity.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.VOICE, 0.8F, 1.2F);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      int day = DayGlobalCount.CURRENT_DAY;
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (target.level() instanceof ServerLevel level) {
            if (!target.level().isClientSide) {
               if (attacker instanceof PolarBear polarBear) {
                  if (polarBear.getTags().contains("fromStray")) {
                     target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + day >= 30 ? 80 : 40));
                     target.hurt(level.damageSources().freeze(), day >= 50 ? 15.0F : (day >= 30 ? 5.0F : 2.5F));
                     level.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 10, 1.2, 0.8, 1.2, 0.15);
                     level.sendParticles(ParticleTypes.ITEM_SNOWBALL, target.getX(), target.getY(), target.getZ(), 5, 0.9, 0.4, 0.9, 0.25);
                     level.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY(), target.getZ(), 5, 0.8, 0.2, 0.8, 0.08);
                     level.playSound(null, target.blockPosition(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.VOICE, 1.0F, 0.8F);
                     level.playSound(null, target.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.VOICE, 0.8F, 1.2F);
                  }
               }
            }
         }
      }
   }

   public static void chooseClass(ServerLevel level, Mob skeleton) {
      int chance = 1 + RANDOM.nextInt(3);
      switch (chance) {
         case 1:
            clasePermafrost(level, skeleton);
            break;
         case 2:
            clasePolarJockey(level, skeleton);
            break;
         case 3:
            claseGelida(level, skeleton);
      }
   }

   private static void clasePermafrost(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      AttributeInstance strayHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Stray Perma-Frost").withStyle(ChatFormatting.AQUA));
      skeleton.addTag("permaFrostStray");
      skeleton.addTag("classStray");
      RegistryAccess registryAccess = level.registryAccess();
      Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SILENCE);
      Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.LAPIS);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.IRON_HELMET);
      helmet.set(DataComponents.TRIM, trim);
      ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.IRON_LEGGINGS);
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.IRON_BOOTS);
      boots.set(DataComponents.TRIM, trim);
      Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
      ItemStack bow = new ItemStack(Items.BOW);
      int powerLevel = day >= 50 ? 40 : 20;
      int speedLevel = day >= 50 ? 1 : -1;
      if (day >= 30) {
         strayHealth.setBaseValue(40.0);
         bow.enchant(power, powerLevel);
         if (speedLevel >= 0) {
            skeleton.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel, false, true));
         }
      } else {
         strayHealth.setBaseValue(30.0);
         bow.enchant(power, 5);
      }

      List<MobEffectInstance> effects = List.of(
         new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, day >= 50 ? 4 : 2, false, true),
         new MobEffectInstance(MobEffects.WEAKNESS, 600, day >= 50 ? 2 : (day >= 30 ? 1 : 0), false, true),
         new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, day >= 30 ? 2 : 0, false, true)
      );
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      PotionContents contents = new PotionContents(Optional.of(Potions.WATER), Optional.of(3949738), effects);
      arrow.set(DataComponents.POTION_CONTENTS, contents);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void clasePolarJockey(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      PolarBear polarBear = (PolarBear)EntityType.POLAR_BEAR.create(level);
      if (polarBear != null) {
         AttributeInstance polarBearHealth = Objects.requireNonNull(polarBear.getAttribute(Attributes.MAX_HEALTH));
         AttributeInstance polarBearDamage = Objects.requireNonNull(polarBear.getAttribute(Attributes.ATTACK_DAMAGE));
         MobPreventEquipmentDrops.preventAllEquipmentDrop(polarBear);
         polarBear.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ());
         polarBear.setCustomName(Component.literal("Oso Polar de la Escarcha").withStyle(ChatFormatting.DARK_AQUA));
         polarBear.addTag("fromStray");
         polarBear.addTag("shouldNaturallyDespawn");
         float healthValue = day >= 50 ? 200.0F : 100.0F;
         float damageValue = day >= 50 ? 20.0F : 12.0F;
         if (day >= 30) {
            polarBearHealth.setBaseValue(healthValue);
            polarBearDamage.setBaseValue(damageValue);
         } else {
            polarBearHealth.setBaseValue(40.0);
            polarBearDamage.setBaseValue(8.0);
         }

         polarBear.setHealth(polarBear.getMaxHealth());
         skeleton.startRiding(polarBear);
         level.addFreshEntity(polarBear);
      }

      AttributeInstance strayHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Stray Polar de la Escarcha").withStyle(ChatFormatting.DARK_AQUA));
      skeleton.addTag("frostStray");
      skeleton.addTag("classStray");
      RegistryAccess registryAccess = level.registryAccess();
      Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.FLOW);
      Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.IRON);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.ICE);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(3847130, false));
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(3847130, false));
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(3847130, false));
      boots.set(DataComponents.TRIM, trim);
      int frostLevel = day >= 50 ? 5 : 2;
      Holder<Enchantment> frostWalker = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FROST_WALKER);
      boots.enchant(frostWalker, frostLevel);
      Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
      Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
      ItemStack bow = new ItemStack(Items.BOW);
      int powerLevel = day >= 50 ? 25 : 10;
      if (day >= 30) {
         strayHealth.setBaseValue(40.0);
         bow.enchant(power, powerLevel);
         bow.enchant(punch, 5);
      } else {
         strayHealth.setBaseValue(30.0);
         bow.enchant(punch, 5);
      }

      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseGelida(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      AttributeInstance strayHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Stray Gélido").withStyle(ChatFormatting.GOLD));
      skeleton.addTag("gelidStray");
      skeleton.addTag("classStray");
      RegistryAccess registryAccess = level.registryAccess();
      Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.DUNE);
      Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.DIAMOND);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.SNOW_BLOCK);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.TRIM, trim);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(16383998, false));
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(16383998, false));
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(16383998, false));
      boots.set(DataComponents.TRIM, trim);
      ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
      Holder<Enchantment> breach = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
      Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      float healthValue = day >= 50 ? 100.0F : 60.0F;
      int breachLevel = day >= 50 ? 5 : 2;
      int sharpnessLevel = day >= 50 ? 5 : 0;
      if (day >= 30) {
         strayHealth.setBaseValue(healthValue);
         axe.enchant(breach, breachLevel);
         axe.enchant(sharpness, sharpnessLevel);
      } else {
         strayHealth.setBaseValue(40.0);
      }

      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, axe);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }
}
