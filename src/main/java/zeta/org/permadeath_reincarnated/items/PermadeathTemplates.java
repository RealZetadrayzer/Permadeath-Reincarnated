package zeta.org.permadeath_reincarnated.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class PermadeathTemplates extends Item {
   private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
   private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
   private static final Component INGREDIENTS_TITLE = Component.translatable(
         Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients"))
      )
      .withStyle(style -> style.withColor(ChatFormatting.GRAY));
   private static final Component APPLIES_TO_TITLE = Component.translatable(
         Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to"))
      )
      .withStyle(style -> style.withColor(ChatFormatting.GRAY));
   private static final Component UPGRADE_TITLE = Component.translatable("item.permadeath_reincarnated.smithing_template.netherite_upgrade.upgrade")
      .withStyle(TITLE_FORMAT);
   private static final Component APPLIES_TO = Component.translatable("item.permadeath_reincarnated.smithing_template.netherite_upgrade.applies_to")
      .withStyle(DESCRIPTION_FORMAT);
   private static final Component INGREDIENTS = Component.translatable("item.permadeath_reincarnated.smithing_template.netherite_upgrade.ingredients")
      .withStyle(DESCRIPTION_FORMAT);
   private static final Component BASE_SLOT_DESC = Component.translatable(
      "item.permadeath_reincarnated.smithing_template.netherite_upgrade.base_slot_description"
   );
   private static final Component ADDITION_SLOT_DESC = Component.translatable(
      "item.permadeath_reincarnated.smithing_template.netherite_upgrade.additions_slot_description"
   );
   private static final Component INFERNAL_UPGRADE_TITLE = Component.translatable(
         "item.permadeath_reincarnated.smithing_template.infernal_netherite_upgrade.upgrade"
      )
      .withStyle(TITLE_FORMAT);
   private static final Component INFERNAL_APPLIES_TO = Component.translatable(
         "item.permadeath_reincarnated.smithing_template.infernal_netherite_upgrade.applies_to"
      )
      .withStyle(DESCRIPTION_FORMAT);
   private static final Component INFERNAL_INGREDIENTS = Component.translatable(
         "item.permadeath_reincarnated.smithing_template.infernal_netherite_upgrade.ingredients"
      )
      .withStyle(DESCRIPTION_FORMAT);
   private static final Component INFERNAL_BASE_SLOT_DESC = Component.translatable(
      "item.permadeath_reincarnated.smithing_template.infernal_netherite_upgrade.base_slot_description"
   );
   private static final Component INFERNAL_ADDITION_SLOT_DESC = Component.translatable(
      "item.permadeath_reincarnated.smithing_template.infernal_netherite_upgrade.additions_slot_description"
   );
   private static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
   private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
   private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
   private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
   private static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("item/empty_slot_ingot");
   private final Component appliesTo;
   private final Component ingredients;
   private final Component upgradeDescription;
   private final Component baseSlotDescription;
   private final Component additionsSlotDescription;
   private final List<ResourceLocation> baseSlotEmptyIcons;
   private final List<ResourceLocation> additionalSlotEmptyIcons;

   public PermadeathTemplates(
      Component appliesTo,
      Component ingredients,
      Component upgradeDescription,
      Component baseSlotDescription,
      Component additionsSlotDescription,
      List<ResourceLocation> baseSlotEmptyIcons,
      List<ResourceLocation> additionalSlotEmptyIcons
   ) {
      super(new Properties().fireResistant());
      this.appliesTo = appliesTo;
      this.ingredients = ingredients;
      this.upgradeDescription = upgradeDescription;
      this.baseSlotDescription = baseSlotDescription;
      this.additionsSlotDescription = additionsSlotDescription;
      this.baseSlotEmptyIcons = baseSlotEmptyIcons;
      this.additionalSlotEmptyIcons = additionalSlotEmptyIcons;
   }

   public static PermadeathTemplates createNetheriteUpgradeTemplate() {
      return new PermadeathTemplates(
         APPLIES_TO,
         INGREDIENTS,
         UPGRADE_TITLE,
         BASE_SLOT_DESC,
         ADDITION_SLOT_DESC,
         List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS),
         List.of(EMPTY_SLOT_INGOT)
      );
   }

   public static PermadeathTemplates createInfernalNetheriteUpgradeTemplate() {
      return new PermadeathTemplates(
         INFERNAL_APPLIES_TO,
         INFERNAL_INGREDIENTS,
         INFERNAL_UPGRADE_TITLE,
         INFERNAL_BASE_SLOT_DESC,
         INFERNAL_ADDITION_SLOT_DESC,
         List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS),
         List.of(EMPTY_SLOT_INGOT)
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(this.upgradeDescription);
      tooltip.add(CommonComponents.EMPTY);
      tooltip.add(APPLIES_TO_TITLE);
      tooltip.add(CommonComponents.space().append(this.appliesTo));
      tooltip.add(INGREDIENTS_TITLE);
      tooltip.add(CommonComponents.space().append(this.ingredients));
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }

   public Component getBaseSlotDescription() {
      return this.baseSlotDescription;
   }

   public Component getAdditionSlotDescription() {
      return this.additionsSlotDescription;
   }

   public List<ResourceLocation> getBaseSlotEmptyIcons() {
      return this.baseSlotEmptyIcons;
   }

   public List<ResourceLocation> getAdditionalSlotEmptyIcons() {
      return this.additionalSlotEmptyIcons;
   }
}
