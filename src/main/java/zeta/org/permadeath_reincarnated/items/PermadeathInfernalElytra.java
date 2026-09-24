package zeta.org.permadeath_reincarnated.items;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import zeta.org.permadeath_reincarnated.PermadeathAttributes;

public class PermadeathInfernalElytra extends ElytraItem implements Equipable {
   public PermadeathInfernalElytra(Properties properties) {
      super(
         properties.attributes(
            ItemAttributeModifiers.builder()
               .add(
                  Holder.direct((Attribute)Attributes.ARMOR.value()),
                  new AttributeModifier(
                     ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_netherite_elytra_armor"), 8.0, Operation.ADD_VALUE
                  ),
                  EquipmentSlotGroup.CHEST
               )
               .add(
                  Holder.direct((Attribute)Attributes.ARMOR_TOUGHNESS.value()),
                  new AttributeModifier(
                     ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_netherite_elytra_armor_toughness"), 4.0, Operation.ADD_VALUE
                  ),
                  EquipmentSlotGroup.CHEST
               )
               .add(
                  Holder.direct((Attribute)Attributes.KNOCKBACK_RESISTANCE.value()),
                  new AttributeModifier(
                     ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_netherite_elytra_knockback_resistance"),
                     0.2,
                     Operation.ADD_VALUE
                  ),
                  EquipmentSlotGroup.CHEST
               )
               .add(
                  Holder.direct((Attribute)PermadeathAttributes.ECHO_GUARD.value()),
                  new AttributeModifier(
                     ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_netherite_elytra_echo_guard"), 0.1, Operation.ADD_VALUE
                  ),
                  EquipmentSlotGroup.CHEST
               )
               .build()
         )
      );
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
      return repair.is(Items.PHANTOM_MEMBRANE);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return this.swapWithEquipmentSlot(this, level, player, hand);
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      return isFlyEnabled(stack);
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      if (!entity.level().isClientSide) {
         int next = flightTicks + 1;
         if (next % 10 == 0) {
            if (next % 20 == 0) {
               stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
            }

            entity.gameEvent(GameEvent.ELYTRA_GLIDE);
         }
      }

      return true;
   }

   public Holder<SoundEvent> getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_ELYTRA;
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.CHEST;
   }
}
