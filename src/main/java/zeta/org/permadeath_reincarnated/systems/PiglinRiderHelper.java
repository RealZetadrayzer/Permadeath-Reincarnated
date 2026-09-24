package zeta.org.permadeath_reincarnated.systems;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;

public class PiglinRiderHelper {
   private static final Random RANDOM = new Random();
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;

   public static void spawnRandomZPiglin(ServerLevel level, Mob mob) {
      int chance = 1 + RANDOM.nextInt(5);
      switch (chance) {
         case 1:
            onPig(level, mob);
            break;
         case 2:
            onBee(level, mob);
            break;
         case 3:
            onGhast(level, mob);
            break;
         case 4:
            onMagma(level, mob);
            break;
         case 5:
            onTrio(level, mob);
      }
   }

   private static void onPig(ServerLevel level, Mob mob) {
      Pig pig = (Pig)EntityType.PIG.create(level);
      if (pig != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(pig);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(mob);
         mob.setCustomName(Component.literal("Benjamín el Asqueroso").withStyle(ChatFormatting.LIGHT_PURPLE));
         pig.setCustomName(Component.literal("Scott el Sucio").withStyle(ChatFormatting.LIGHT_PURPLE));
         pig.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
         ((MobSpawnTypeAccessor)pig).setSpawnType(MobSpawnType.MOB_SUMMONED);
         mob.addTag("fromZPiglin");
         pig.addTag("fromZPiglin");
         pig.addTag("shouldNaturallyDespawn");
         List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
         Collections.shuffle(shuffled);
         int count = 5;
         count = Math.min(count, shuffled.size());

         for (int i = 0; i < count; i++) {
            Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
            pig.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
         }

         ItemStack helmet = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_HELMET.get());
         ItemStack chestplate = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_CHESTPLATE.get());
         ItemStack leggings = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_LEGGINGS.get());
         ItemStack boots = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_BOOTS.get());
         if (RANDOM.nextFloat() < 0.05F) {
            pig.setBaby(true);
         }

         Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(20.0);
         mob.setItemSlot(EquipmentSlot.HEAD, helmet);
         mob.setItemSlot(EquipmentSlot.CHEST, chestplate);
         mob.setItemSlot(EquipmentSlot.LEGS, leggings);
         mob.setItemSlot(EquipmentSlot.FEET, boots);
         level.addFreshEntity(pig);
         mob.startRiding(pig, true);
      }
   }

   private static void onBee(ServerLevel level, Mob mob) {
      Bee bee = (Bee)EntityType.BEE.create(level);
      if (bee != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(bee);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(mob);
         mob.setCustomName(Component.literal("Cristian el Apícola").withStyle(ChatFormatting.GOLD));
         bee.setCustomName(Component.literal("Miel la Ludópata").withStyle(ChatFormatting.GOLD));
         bee.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
         ((MobSpawnTypeAccessor)bee).setSpawnType(MobSpawnType.MOB_SUMMONED);
         mob.addTag("fromZPiglin");
         bee.addTag("fromZPiglin");
         bee.addTag("shouldNaturallyDespawn");
         ItemStack helmet = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_HELMET.get());
         ItemStack chestplate = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_CHESTPLATE.get());
         ItemStack leggings = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_LEGGINGS.get());
         ItemStack boots = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_BOOTS.get());
         Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(6.0);
         Objects.requireNonNull(bee.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(100.0);
         bee.setHealth(bee.getMaxHealth());
         mob.setItemSlot(EquipmentSlot.HEAD, helmet);
         mob.setItemSlot(EquipmentSlot.CHEST, chestplate);
         mob.setItemSlot(EquipmentSlot.LEGS, leggings);
         mob.setItemSlot(EquipmentSlot.FEET, boots);
         level.addFreshEntity(bee);
         mob.startRiding(bee, true);
      }
   }

   private static void onGhast(ServerLevel level, Mob mob) {
      Ghast ghast = (Ghast)EntityType.GHAST.create(level);
      if (ghast != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(ghast);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(mob);
         mob.setCustomName(Component.literal("Diego el Putrefacto").withStyle(ChatFormatting.RED));
         ghast.setCustomName(Component.literal("Bryan el Llorica").withStyle(ChatFormatting.RED));
         ghast.moveTo(mob.getX(), mob.getY() + 0.5, mob.getZ(), mob.getYRot(), mob.getXRot());
         ((MobSpawnTypeAccessor)ghast).setSpawnType(MobSpawnType.MOB_SUMMONED);
         mob.addTag("fromZPiglin");
         ghast.addTag("fromZPiglin");
         int powerExplosion = 0;

         try {
            Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
            explosionPowerField.setAccessible(true);
            explosionPowerField.setInt(ghast, powerExplosion);
         } catch (Exception var5) {
         }

         Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(4.0);
         Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(10.0);
         ghast.setHealth(ghast.getMaxHealth());
         mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
         mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, true));
         level.addFreshEntity(ghast);
         mob.startRiding(ghast, true);
      }
   }

   private static void onMagma(ServerLevel level, Mob mob) {
      MagmaCube magma = (MagmaCube)EntityType.MAGMA_CUBE.create(level);
      if (magma != null) {
         MobPreventEquipmentDrops.preventAllEquipmentDrop(magma);
         MobPreventEquipmentDrops.preventAllEquipmentDrop(mob);
         mob.setCustomName(Component.literal("Óscar el Olvidado").withStyle(ChatFormatting.GREEN));
         magma.setCustomName(Component.literal("Willy el Cubo").withStyle(ChatFormatting.GREEN));
         magma.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
         ((MobSpawnTypeAccessor)magma).setSpawnType(MobSpawnType.MOB_SUMMONED);
         mob.addTag("fromZPiglin");
         magma.addTag("fromZPiglin");
         mob.addTag("projectileImmune");
         magma.addTag("projectileImmune");
         ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
         PotionContents contents = new PotionContents(Optional.of(Potions.WATER), Optional.of(6553600), List.of(new MobEffectInstance(MobEffects.HARM, 1, 1)));
         arrow.set(DataComponents.POTION_CONTENTS, contents);
         mob.setItemSlot(EquipmentSlot.MAINHAND, arrow);
         Objects.requireNonNull(mob.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(1.0);
         Objects.requireNonNull(magma.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(1.0);
         magma.setHealth(magma.getMaxHealth());
         magma.setSize(1, false);
         mob.setHealth(mob.getMaxHealth());
         mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
         mob.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
         level.addFreshEntity(magma);
         mob.startRiding(magma, true);
      }
   }

   private static void onTrio(ServerLevel level, Mob mob) {
      CustomRavager ravager = (CustomRavager)((EntityType)PermadeathEntityRegistry.CUSTOM_RAVAGER.get()).create(level);
      Villager villager = (Villager)EntityType.VILLAGER.create(level);
      if (villager != null) {
         if (ravager != null) {
            MobPreventEquipmentDrops.preventAllEquipmentDrop(ravager);
            MobPreventEquipmentDrops.preventAllEquipmentDrop(villager);
            MobPreventEquipmentDrops.preventAllEquipmentDrop(mob);
            mob.setCustomName(Component.literal("Paste la Esclava").withStyle(ChatFormatting.GOLD));
            ravager.setCustomName(Component.literal("Mauricio el Lacayo").withStyle(ChatFormatting.GOLD));
            villager.setCustomName(Component.literal("Luna la Emperatriz").withStyle(ChatFormatting.GOLD));
            ravager.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
            villager.moveTo(mob.getX(), mob.getY() + 1.0, mob.getZ(), mob.getYRot(), mob.getXRot());
            ((MobSpawnTypeAccessor)ravager).setSpawnType(MobSpawnType.MOB_SUMMONED);
            ((MobSpawnTypeAccessor)villager).setSpawnType(MobSpawnType.MOB_SUMMONED);
            mob.addTag("fromZPiglin");
            mob.addTag("slavePiglin");
            ravager.addTag("fromZPiglin");
            villager.addTag("fromZPiglin");
            villager.addTag("shouldNaturallyDespawn");
            ItemStack gold = new ItemStack(Items.GOLD_INGOT);
            ItemStack goldenApple = new ItemStack(Items.GOLDEN_APPLE);
            Objects.requireNonNull(villager.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(240.0);
            Objects.requireNonNull(mob.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(150.0);
            Objects.requireNonNull(ravager.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(500.0);
            villager.setHealth(villager.getMaxHealth());
            mob.setHealth(mob.getMaxHealth());
            ravager.setHealth(ravager.getMaxHealth());
            Objects.requireNonNull(villager.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(12.0);
            Objects.requireNonNull(mob.getAttribute(Attributes.ARMOR)).setBaseValue(20.0);
            Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(8.0);
            Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_KNOCKBACK)).setBaseValue(10.0);
            ravager.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
            ravager.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
            villager.setItemSlot(EquipmentSlot.MAINHAND, goldenApple);
            mob.setItemSlot(EquipmentSlot.MAINHAND, gold);
            mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            mob.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
            mob.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
            level.addFreshEntity(ravager);
            level.addFreshEntity(villager);
            villager.startRiding(mob, true);
            mob.startRiding(ravager, true);
         }
      }
   }

   static {
      Map<Holder<MobEffect>, Integer> map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 2);
      map.put(MobEffects.DAMAGE_BOOST, 3);
      map.put(MobEffects.JUMP, 4);
      map.put(MobEffects.GLOWING, 0);
      map.put(MobEffects.REGENERATION, 3);
      map.put(MobEffects.INVISIBILITY, 0);
      map.put(MobEffects.SLOW_FALLING, 0);
      map.put(MobEffects.DAMAGE_RESISTANCE, 2);
      EFFECTS = Collections.unmodifiableMap(map);
   }
}
