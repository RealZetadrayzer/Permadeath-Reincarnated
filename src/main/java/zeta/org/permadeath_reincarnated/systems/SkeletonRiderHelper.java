package zeta.org.permadeath_reincarnated.systems;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;

public class SkeletonRiderHelper {
   private static final Random RANDOM = new Random();
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

   public static void spawnRandomSkeleton(ServerLevel level, Mob mob, int day) {
      if (day < 60) {
         int chance = 1 + RANDOM.nextInt(5);
         switch (chance) {
            case 1:
               claseGuerrero(level, mob, day);
               break;
            case 2:
               claseInfernal(level, mob, day);
               break;
            case 3:
               claseAsesina(level, mob, day);
               break;
            case 4:
               claseTactico(level, mob, day);
               break;
            case 5:
               clasePesadilla(level, mob, day);
         }
      } else {
         int roll = 1 + RANDOM.nextInt(100);
         if (roll == 1) {
            claseDefinitiva(level, mob, day);
         } else {
            int subRoll = RANDOM.nextInt(7) + 1;
            switch (subRoll) {
               case 1:
                  claseGuerrero(level, mob, day);
                  break;
               case 2:
                  claseInfernal(level, mob, day);
                  break;
               case 3:
                  claseAsesina(level, mob, day);
                  break;
               case 4:
                  claseTactico(level, mob, day);
                  break;
               case 5:
                  clasePesadilla(level, mob, day);
                  break;
               case 6:
                  claseCientifico(level, mob, day);
                  break;
               case 7:
                  claseDemoniaco(level, mob, day);
            }
         }
      }
   }

   private static void claseGuerrero(ServerLevel level, Mob mob, int day) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
      if (skeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Guerrero").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Guerrero").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)skeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
         skeleton.setCustomName(skeletonName);
         skeleton.addTag("mount");
         skeleton.addTag("classSkeleton");
         skeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         skeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(skeleton);
         skeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Guerrera" : "Araña Guerrera";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Guerrero";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Guerrera").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseInfernal(ServerLevel level, Mob mob, int day) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
      if (skeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Infernal").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Infernal").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)skeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
         skeleton.setCustomName(skeletonName);
         skeleton.addTag("mount");
         skeleton.addTag("classSkeleton");
         skeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         skeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(skeleton);
         skeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Infernal" : "Araña Infernal";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Infernal";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Infernal").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseAsesina(ServerLevel level, Mob mob, int day) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
      if (skeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Asesino").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Asesino").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)skeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
         skeleton.setCustomName(skeletonName);
         skeleton.addTag("mount");
         skeleton.addTag("classSkeleton");
         skeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         skeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(skeleton);
         skeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Asesina" : "Araña Asesina";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Asesino";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Asesina").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseTactico(ServerLevel level, Mob mob, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Táctico").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Táctico").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.addTag("mount");
         witherSkeleton.addTag("punchClassSkeleton");
         witherSkeleton.addTag("classSkeleton");
         witherSkeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         witherSkeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Táctica" : "Araña Táctica";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Táctico";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Táctica").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void clasePesadilla(ServerLevel level, Mob mob, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Pesadilla").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Pesadilla").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.addTag("mount");
         witherSkeleton.addTag("classSkeleton");
         witherSkeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         witherSkeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva de Pesadilla" : "Araña de Pesadilla";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom de Pesadilla";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura de Pesadilla").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseCientifico(ServerLevel level, Mob mob, int day) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
      if (skeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Científico").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Científico").withStyle(ChatFormatting.GOLD);
         ((MobSpawnTypeAccessor)skeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
         skeleton.setCustomName(skeletonName);
         skeleton.addTag("mount");
         skeleton.addTag("classSkeleton");
         skeleton.addTag("claseCientifica");
         skeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         skeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(skeleton);
         skeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Científica" : "Araña Científica";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Científico";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Científica").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseDemoniaco(ServerLevel level, Mob mob, int day) {
      Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
      if (skeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Demoníaco").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Demoníaco").withStyle(ChatFormatting.GOLD);
         ((MobSpawnTypeAccessor)skeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
         skeleton.setCustomName(skeletonName);
         skeleton.addTag("mount");
         skeleton.addTag("claseDemoniaca");
         skeleton.addTag("classSkeleton");
         skeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
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
         skeleton.setLeftHanded(RANDOM.nextFloat() < 0.05);
         level.addFreshEntity(skeleton);
         skeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Demoníaca" : "Araña Demoníaca";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Demoníaco";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Demoníaca").withStyle(ChatFormatting.GOLD));
      }
   }

   private static void claseDefinitiva(ServerLevel level, Mob mob, int day) {
      WitherSkeleton witherSkeleton = (WitherSkeleton)EntityType.WITHER_SKELETON.create(level);
      if (witherSkeleton != null) {
         AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
         Component skeletonName = day >= 60
            ? Component.literal("Ultra Esqueleto Definitivo").withStyle(ChatFormatting.GOLD)
            : Component.literal("Esqueleto Definitivo").withStyle(ChatFormatting.GOLD);
         RegistryAccess registryAccess = level.registryAccess();
         MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true);
         ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
         witherSkeleton.setCustomName(skeletonName);
         witherSkeleton.setPersistenceRequired();
         witherSkeleton.addTag("claseDefinitiva");
         witherSkeleton.addTag("fromSkeleton");
         witherSkeleton.addTag("classSkeleton");
         witherSkeleton.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
         ItemStack bow = new ItemStack(Items.BOW);
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FLAME);
         bow.enchant(power, 1);
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
         witherSkeleton.startRiding(mob, true);
      }

      if (mob instanceof Spider) {
         boolean cave = mob instanceof CaveSpider;
         String baseName = cave ? "Araña de Cueva Definitiva" : "Araña Definitiva";
         String prefix = day >= 25 ? "Ultra " : (day >= 20 ? "Mega " : (day >= 10 ? "Súper " : ""));
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else if (mob instanceof Phantom) {
         String baseName = "Phantom Definitivo";
         String prefix = day >= 50 ? "Giga-" : (day >= 20 ? "Mega-" : "");
         mob.setCustomName(Component.literal(prefix + baseName).withStyle(ChatFormatting.GOLD));
      } else {
         mob.setCustomName(Component.literal("Montura Definitiva").withStyle(ChatFormatting.GOLD));
      }
   }
}
