package zeta.org.permadeath_reincarnated.items;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import zeta.org.permadeath_reincarnated.PermadeathAttributes;

@EventBusSubscriber
public class PermadeathItemAttributes {
   @SubscribeEvent
   public static void onItemAttributes(ItemAttributeModifierEvent event) {
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "netherite_helmet_echo_guard", 0.1);
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "netherite_chestplate_echo_guard", 0.1);
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "netherite_leggings_echo_guard", 0.1);
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "netherite_boots_echo_guard", 0.1);
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "infernal_netherite_helmet_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "infernal_netherite_chestplate_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "infernal_netherite_leggings_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "infernal_netherite_boots_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "blue_netherite_helmet_echo_guard", 0.1);
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "blue_netherite_chestplate_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "blue_netherite_leggings_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "blue_netherite_boots_echo_guard", 0.1);
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "rose_netherite_helmet_echo_guard", 0.1);
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "rose_netherite_chestplate_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "rose_netherite_leggings_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "rose_netherite_boots_echo_guard", 0.1);
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "green_netherite_helmet_echo_guard", 0.1);
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "green_netherite_chestplate_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "green_netherite_leggings_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "green_netherite_boots_echo_guard", 0.1);
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_HELMET, EquipmentSlotGroup.HEAD, "yellow_netherite_helmet_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, "yellow_netherite_chestplate_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(
         event, PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, "yellow_netherite_leggings_echo_guard", 0.1
      );
      addArmorEchoGuardModifier(event, PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_BOOTS, EquipmentSlotGroup.FEET, "yellow_netherite_boots_echo_guard", 0.1);
   }

   private static void addArmorEchoGuardModifier(
      ItemAttributeModifierEvent event, DeferredItem<? extends ArmorItem> armorItem, EquipmentSlotGroup slot, String nameSuffix, double value
   ) {
      if (event.getItemStack().is((Item)armorItem.get())) {
         event.addModifier(
            Holder.direct((Attribute)PermadeathAttributes.ECHO_GUARD.value()),
            new AttributeModifier(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", nameSuffix), value, Operation.ADD_VALUE),
            slot
         );
      }
   }
}
