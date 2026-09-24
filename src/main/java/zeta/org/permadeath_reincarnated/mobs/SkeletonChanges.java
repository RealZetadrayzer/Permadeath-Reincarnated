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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;

@EventBusSubscriber
public class SkeletonChanges {
   private static final Random RANDOM = new Random();
   private static final int[] EXPLOSION_POWERS = new int[]{7, 8, 9, 10};
   private static final PotionContents HARM_ARROW = new PotionContents(
      Optional.of(Potions.WATER), Optional.of(6553600), List.of(new MobEffectInstance(MobEffects.HARM, 1, 1))
   );
   private static final PotionContents CIENTIFIC_ARROW = new PotionContents(
      Optional.of(Potions.WATER),
      Optional.of(7638184),
      List.of(
         new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3600, 2),
         new MobEffectInstance(MobEffects.WEAKNESS, 3600, 0),
         new MobEffectInstance(MobEffects.GLOWING, 3600, 0),
         new MobEffectInstance(MobEffects.POISON, 3600, 2)
      )
   );
   private static final PotionContents DEMONIC_ARROW = new PotionContents(
      Optional.of(Potions.WATER), Optional.of(4443904), List.of(new MobEffectInstance(MobEffects.HARM, 1, 1))
   );

   private static int getRandomExplosionPower() {
      return EXPLOSION_POWERS[RANDOM.nextInt(EXPLOSION_POWERS.length)];
   }

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof Skeleton skeleton) {
         if (!skeleton.level().isClientSide) {
            if (entity.getClass() == Skeleton.class) {
               if (skeleton.level() instanceof ServerLevel level) {
                  if (!event.loadedFromDisk()) {
                     if (!skeleton.getTags().contains("mount")) {
                        if (!skeleton.getTags().contains("classSkeleton")) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           if (day >= 30) {
                              chooseClass(level, skeleton, day);
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
   public static void onArrowSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof AbstractArrow arrow) {
         Entity var4 = arrow.getOwner();
         if (!arrow.level().isClientSide) {
            if (var4 instanceof Skeleton skeleton) {
               if (skeleton.getTags().contains("claseDefinitiva")) {
                  arrow.setBaseDamage(arrow.getBaseDamage() + 16382.5);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      Entity owner = projectile.getOwner();
      if (owner instanceof Skeleton skeleton) {
         if (skeleton.getTags().contains("claseDemoniaca")) {
            HitResult ray = event.getRayTraceResult();
            if (ray instanceof EntityHitResult hitResult) {
               Entity target = hitResult.getEntity();
               if (!target.isAlive()) {
                  return;
               }

               double strength = 0.25;
               target.setDeltaMovement(projectile.getDeltaMovement().x * strength, 0.1, projectile.getDeltaMovement().z * strength);
               target.hurtMarked = true;
               BlockPos pos = target.blockPosition();
               projectile.level().explode(owner, pos.getX(), pos.getY(), pos.getZ(), getRandomExplosionPower(), true, ExplosionInteraction.MOB);
               projectile.remove(RemovalReason.KILLED);
            } else if (ray instanceof BlockHitResult blockHit) {
               BlockPos pos = blockHit.getBlockPos();
               projectile.level().explode(owner, pos.getX(), pos.getY(), pos.getZ(), getRandomExplosionPower(), true, ExplosionInteraction.MOB);
               projectile.remove(RemovalReason.KILLED);
            }
         }
      }
   }

   public static void chooseClass(ServerLevel level, Mob skeleton, int day) {
      if (day < 60) {
         int chance = 1 + RANDOM.nextInt(5);
         switch (chance) {
            case 1:
               claseGuerrero(level, skeleton, day);
               break;
            case 2:
               claseInfernal(level, skeleton, day);
               break;
            case 3:
               claseAsesina(level, skeleton, day);
               break;
            case 4:
               claseTactico(level, skeleton, day);
               break;
            case 5:
               clasePesadilla(level, skeleton, day);
         }
      } else {
         int roll = 1 + RANDOM.nextInt(100);
         if (roll == 1) {
            claseDefinitiva(level, skeleton, day);
         } else {
            int subRoll = RANDOM.nextInt(7) + 1;
            switch (subRoll) {
               case 1:
                  claseGuerrero(level, skeleton, day);
                  break;
               case 2:
                  claseInfernal(level, skeleton, day);
                  break;
               case 3:
                  claseAsesina(level, skeleton, day);
                  break;
               case 4:
                  claseTactico(level, skeleton, day);
                  break;
               case 5:
                  clasePesadilla(level, skeleton, day);
                  break;
               case 6:
                  claseCientifico(level, skeleton, day);
                  break;
               case 7:
                  claseDemoniaco(level, skeleton, day);
            }
         }
      }
   }

   private static void claseGuerrero(ServerLevel level, Mob skeleton, int day) {
      AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      Component skeletonName = day >= 60
         ? Component.literal("Ultra Esqueleto Guerrero").withStyle(ChatFormatting.GOLD)
         : Component.literal("Esqueleto Guerrero").withStyle(ChatFormatting.GOLD);
      RegistryAccess registryAccess = level.registryAccess();
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(skeletonName);
      skeleton.addTag("classSkeleton");
      ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
      ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
      ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
      ItemStack bow = new ItemStack(Items.BOW);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      arrow.set(DataComponents.POTION_CONTENTS, HARM_ARROW);
      Holder<Enchantment> protection = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PROTECTION);
      Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
      int protectionLevel = day >= 60 ? 5 : (day >= 30 ? 4 : 0);
      int powerLevel = day >= 60 ? 50 : 0;
      float healthValue = day >= 50 ? 100.0F : (day >= 30 ? 40.0F : 20.0F);
      if (day >= 20) {
         helmet.enchant(protection, protectionLevel);
         chestplate.enchant(protection, protectionLevel);
         leggings.enchant(protection, protectionLevel);
         boots.enchant(protection, protectionLevel);
         bow.enchant(power, powerLevel);
         skeletonHealth.setBaseValue(healthValue);
      }

      if (day >= 30) {
         skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      }

      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseInfernal(ServerLevel level, Mob skeleton, int day) {
      AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      Component skeletonName = day >= 60
         ? Component.literal("Ultra Esqueleto Infernal").withStyle(ChatFormatting.GOLD)
         : Component.literal("Esqueleto Infernal").withStyle(ChatFormatting.GOLD);
      RegistryAccess registryAccess = level.registryAccess();
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(skeletonName);
      skeleton.addTag("classSkeleton");
      ItemStack helmet = new ItemStack(Items.IRON_HELMET);
      ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.IRON_LEGGINGS);
      ItemStack boots = new ItemStack(Items.IRON_BOOTS);
      ItemStack axe = day >= 30 ? new ItemStack(Items.DIAMOND_AXE) : new ItemStack(Items.IRON_AXE);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      arrow.set(DataComponents.POTION_CONTENTS, HARM_ARROW);
      Holder<Enchantment> fireAspect = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FIRE_ASPECT);
      Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      int fireAspectLevel = day >= 50 ? 20 : (day >= 30 ? 10 : 2);
      int sharpnessLevel = day >= 60 ? 100 : (day >= 50 ? 25 : 0);
      float healthValue = day >= 60 ? 100.0F : (day >= 30 ? 40.0F : 20.0F);
      if (day >= 20) {
         axe.enchant(fireAspect, fireAspectLevel);
         axe.enchant(sharpness, sharpnessLevel);
         skeletonHealth.setBaseValue(healthValue);
      }

      if (day >= 30) {
         skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      }

      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, axe);
   }

   private static void claseAsesina(ServerLevel level, Mob skeleton, int day) {
      AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      Component skeletonName = day >= 60
         ? Component.literal("Ultra Esqueleto Asesino").withStyle(ChatFormatting.GOLD)
         : Component.literal("Esqueleto Asesino").withStyle(ChatFormatting.GOLD);
      RegistryAccess registryAccess = level.registryAccess();
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(skeletonName);
      skeleton.addTag("classSkeleton");
      ItemStack helmet = new ItemStack(Items.GOLDEN_HELMET);
      ItemStack chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
      ItemStack boots = new ItemStack(Items.GOLDEN_BOOTS);
      ItemStack crossbow = new ItemStack(Items.CROSSBOW);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      arrow.set(DataComponents.POTION_CONTENTS, HARM_ARROW);
      int speedLevel = day >= 60 ? 3 : (day >= 30 ? 1 : -1);
      MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel, false, true);
      Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      int sharpnessLevel = day >= 60 ? 100 : (day >= 50 ? 50 : (day >= 30 ? 25 : 20));
      float healthValue = day >= 60 ? 60.0F : 40.0F;
      if (day >= 20) {
         crossbow.enchant(sharpness, sharpnessLevel);
         skeletonHealth.setBaseValue(healthValue);
         if (speedLevel >= 0) {
            skeleton.addEffect(speed);
         }
      }

      if (day >= 30) {
         skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      }

      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
   }

   private static void claseTactico(ServerLevel level, Mob skeleton, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      skeleton.addTag("noAI");
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Táctico").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Táctico").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.addTag("fromSkeleton");
         witherSkeleton.addTag("punchClassSkeleton");
         witherSkeleton.addTag("classSkeleton");
         ItemStack helmet = new ItemStack(Items.CHAINMAIL_HELMET);
         ItemStack chestplate = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
         ItemStack leggings = new ItemStack(Items.CHAINMAIL_LEGGINGS);
         ItemStack boots = new ItemStack(Items.CHAINMAIL_BOOTS);
         ItemStack bow = new ItemStack(Items.BOW);
         ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
         arrow.set(DataComponents.POTION_CONTENTS, HARM_ARROW);
         Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         int punchLevel = day >= 50 ? 50 : (day >= 30 ? 30 : 20);
         int powerLevel = day >= 60 ? 110 : (day >= 50 ? 40 : (day >= 30 ? 25 : 0));
         float healthValue = day >= 60 ? 60.0F : 40.0F;
         if (skeleton.isPassenger()) {
            Entity vehicle = skeleton.getVehicle();
            skeleton.stopRiding();
            assert vehicle != null;
            witherSkeleton.startRiding(vehicle, true);
         }

         if (skeleton.isVehicle()) {
            List<Entity> passengers = List.copyOf(skeleton.getPassengers());
            skeleton.ejectPassengers();

            for (Entity passenger : passengers) {
               passenger.startRiding(witherSkeleton, true);
            }
         }

         if (day >= 20) {
            bow.enchant(punch, punchLevel);
            bow.enchant(power, powerLevel);
            skeletonHealth.setBaseValue(healthValue);
         }

         if (day >= 30) {
            witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
         }

         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         witherSkeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
         witherSkeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
         witherSkeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
         witherSkeleton.setItemSlot(EquipmentSlot.FEET, boots);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
         witherSkeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(witherSkeleton);
         level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> skeleton.remove(RemovalReason.DISCARDED)));
      }
   }

   private static void clasePesadilla(ServerLevel level, Mob skeleton, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      skeleton.addTag("noAI");
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Pesadilla").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Pesadilla").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.addTag("fromSkeleton");
         witherSkeleton.addTag("classSkeleton");
         ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
         helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
         ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
         chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
         ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
         leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
         ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
         boots.set(DataComponents.DYED_COLOR, new DyedItemColor(11546150, false));
         ItemStack bow = new ItemStack(Items.BOW);
         ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
         arrow.set(DataComponents.POTION_CONTENTS, HARM_ARROW);
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         int powerLevel = day >= 60 ? 150 : (day >= 50 ? 60 : (day >= 30 ? 50 : 10));
         float healthValue = day >= 60 ? 60.0F : 40.0F;
         if (skeleton.isPassenger()) {
            Entity vehicle = skeleton.getVehicle();
            skeleton.stopRiding();
            assert vehicle != null;
            witherSkeleton.startRiding(vehicle, true);
         }

         if (skeleton.isVehicle()) {
            List<Entity> passengers = List.copyOf(skeleton.getPassengers());
            skeleton.ejectPassengers();

            for (Entity passenger : passengers) {
               passenger.startRiding(witherSkeleton, true);
            }
         }

         if (day >= 20) {
            bow.enchant(power, powerLevel);
            skeletonHealth.setBaseValue(healthValue);
         }

         if (day >= 30) {
            witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
         }

         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         witherSkeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
         witherSkeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
         witherSkeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
         witherSkeleton.setItemSlot(EquipmentSlot.FEET, boots);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
         witherSkeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(witherSkeleton);
         level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> skeleton.remove(RemovalReason.DISCARDED)));
      }
   }

   private static void claseCientifico(ServerLevel level, Mob skeleton, int day) {
      AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      Component skeletonName = day >= 60
         ? Component.literal("Ultra Esqueleto Científico").withStyle(ChatFormatting.GOLD)
         : Component.literal("Esqueleto Científico").withStyle(ChatFormatting.GOLD);
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(skeletonName);
      skeleton.addTag("classSkeleton");
      skeleton.addTag("claseCientifica");
      ItemStack helmet = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_HELMET.get());
      ItemStack chestplate = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_CHESTPLATE.get());
      ItemStack leggings = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_LEGGINGS.get());
      ItemStack boots = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_BOOTS.get());
      ItemStack bow = new ItemStack(Items.BOW);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      arrow.set(DataComponents.POTION_CONTENTS, CIENTIFIC_ARROW);
      if (day >= 30) {
         skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      }

      skeletonHealth.setBaseValue(100.0);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseDemoniaco(ServerLevel level, Mob skeleton, int day) {
      AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
      Component skeletonName = day >= 60
         ? Component.literal("Ultra Esqueleto Demoníaco").withStyle(ChatFormatting.GOLD)
         : Component.literal("Esqueleto Demoníaco").withStyle(ChatFormatting.GOLD);
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(skeletonName);
      skeleton.addTag("claseDemoniaca");
      skeleton.addTag("classSkeleton");
      ItemStack helmet = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_HELMET.get());
      ItemStack chestplate = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_CHESTPLATE.get());
      ItemStack leggings = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_LEGGINGS.get());
      ItemStack boots = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_BOOTS.get());
      ItemStack bow = new ItemStack(Items.BOW);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      arrow.set(DataComponents.POTION_CONTENTS, DEMONIC_ARROW);
      if (day >= 30) {
         skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      }

      skeletonHealth.setBaseValue(100.0);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseDefinitiva(ServerLevel level, Mob skeleton, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      skeleton.addTag("noAI");
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Definitivo").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Definitivo").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true);
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.setPersistenceRequired();
         witherSkeleton.addTag("claseDefinitiva");
         witherSkeleton.addTag("fromSkeleton");
         witherSkeleton.addTag("classSkeleton");
         ItemStack bow = new ItemStack(Items.BOW);
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FLAME);
         bow.enchant(power, 1);
         if (skeleton.isPassenger()) {
            Entity vehicle = skeleton.getVehicle();
            skeleton.stopRiding();
            assert vehicle != null;
            witherSkeleton.startRiding(vehicle, true);
         }

         if (skeleton.isVehicle()) {
            List<Entity> passengers = List.copyOf(skeleton.getPassengers());
            skeleton.ejectPassengers();

            for (Entity passenger : passengers) {
               passenger.startRiding(witherSkeleton, true);
            }
         }

         witherSkeleton.addEffect(speed);
         skeletonHealth.setBaseValue(400.0);
         witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
         witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
         witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
         witherSkeleton.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
         witherSkeleton.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
         witherSkeleton.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
         witherSkeleton.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
         witherSkeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(witherSkeleton);
         level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> skeleton.remove(RemovalReason.DISCARDED)));
      }
   }
}
