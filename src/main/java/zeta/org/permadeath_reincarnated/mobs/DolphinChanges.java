package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;

@EventBusSubscriber
public class DolphinChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Dolphin dolphin) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 40) {
                           AttributeInstance dolphinHealth = Objects.requireNonNull(dolphin.getAttribute(Attributes.MAX_HEALTH));
                           MobEffectInstance resistance = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 3, false, true);
                           MobEffectInstance strength = new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, true);
                           Drowned drowned = (Drowned)EntityType.DROWNED.create(level);
                           double healthValue = day >= 60 ? 250.0 : 40.0;
                           MobPreventEquipmentDrops.preventAllEquipmentDrop(dolphin);
                           if (!dolphin.getTags().contains("fromAxolotl")) {
                              dolphin.setCustomName(Component.literal("Delfín de las Profundidades").withStyle(ChatFormatting.AQUA));
                           }

                           dolphinHealth.setBaseValue(healthValue);
                           dolphin.setHealth(dolphin.getMaxHealth());
                           dolphin.addEffect(resistance);
                           dolphin.addEffect(strength);
                           dolphin.addTag("dolphinDepths");
                           if (drowned != null) {
                              setupDrownedRider(level, dolphin, drowned, day);
                              if (day >= 60) {
                                 drowned.addEffect(resistance);
                              }

                              level.addFreshEntity(drowned);
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
   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (!target.level().isClientSide) {
            if (attacker instanceof Dolphin dolphin) {
               if (dolphin.level() instanceof ServerLevel level) {
                  if (DayGlobalCount.CURRENT_DAY >= 40) {
                     if (dolphin.getTags().contains("dolphinDepths")) {
                        if (target.isUnderWater()) {
                           if (target.getAirSupply() > 0) {
                              target.setAirSupply(target.getAirSupply() - 40);
                           } else {
                              target.hurt(level.damageSources().drown(), 4.0F);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void setupDrownedRider(ServerLevel level, Dolphin dolphin, Drowned drowned, int day) {
      AttributeInstance drownedSize = Objects.requireNonNull(drowned.getAttribute(Attributes.SCALE));
      AttributeInstance drownedHealth = Objects.requireNonNull(drowned.getAttribute(Attributes.MAX_HEALTH));
      ((MobSpawnTypeAccessor)drowned).setSpawnType(MobSpawnType.MOB_SUMMONED);
      setupDrownedRiderEquipment(level, drowned);
      drownedSize.setBaseValue(0.5);
      drownedHealth.setBaseValue(40.0);
      drowned.setHealth(drowned.getMaxHealth());
      drowned.addTag("fromDolphin");
      drowned.setCustomName(Component.literal("Drowned de las Profundidades").withStyle(ChatFormatting.AQUA));
      drowned.setPos(dolphin.getX(), dolphin.getY() + 0.5, dolphin.getZ());
      UUID pairId = dolphin.getUUID();
      drowned.getPersistentData().putUUID("depthsPair", pairId);
      dolphin.getPersistentData().putUUID("depthsPair", pairId);
      drowned.startRiding(dolphin);
   }

   private static void setupDrownedRiderEquipment(ServerLevel level, Drowned drowned) {
      RegistryAccess registry = level.registryAccess();
      Holder<TrimPattern> pattern = registry.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.FLOW);
      Holder<TrimMaterial> material = registry.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.DIAMOND);
      ArmorTrim trim = new ArmorTrim(material, pattern);
      ItemStack helmet = new ItemStack(Items.SEA_LANTERN);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(4947824, false));
      chestplate.set(DataComponents.TRIM, trim);
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(4947824, false));
      leggings.set(DataComponents.TRIM, trim);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(4947824, false));
      boots.set(DataComponents.TRIM, trim);
      ItemStack trident = new ItemStack(Items.TRIDENT);
      Holder<Enchantment> sharpness = registry.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      trident.enchant(sharpness, 20);
      drowned.setItemSlot(EquipmentSlot.MAINHAND, trident);
      drowned.setItemSlot(EquipmentSlot.HEAD, helmet);
      drowned.setItemSlot(EquipmentSlot.CHEST, chestplate);
      drowned.setItemSlot(EquipmentSlot.LEGS, leggings);
      drowned.setItemSlot(EquipmentSlot.FEET, boots);
      MobPreventEquipmentDrops.preventAllEquipmentDrop(drowned);
   }
}
