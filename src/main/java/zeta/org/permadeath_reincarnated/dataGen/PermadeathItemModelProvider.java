package zeta.org.permadeath_reincarnated.dataGen;

import java.util.LinkedHashMap;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

public class PermadeathItemModelProvider extends ItemModelProvider {
   private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();

   public PermadeathItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "permadeath_reincarnated", existingFileHelper);
   }

   protected void registerModels() {
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_BOOTS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_BLUE_NETHERITE_BOOTS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_GREEN_NETHERITE_BOOTS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_BOOTS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_HELMET);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_CHESTPLATE);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_LEGGINGS);
      this.trimmedArmorItem(PermadeathItemsRegistry.PERMA_YELLOW_NETHERITE_BOOTS);
   }

   private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
      String MOD_ID = "permadeath_reincarnated";
      if (itemDeferredItem.get() instanceof ArmorItem armorItem) {
         trimMaterials.forEach(
            (trimMaterial, value) -> {
               float trimValue = value;

               String armorType = switch (armorItem.getEquipmentSlot()) {
                  case HEAD -> "helmet";
                  case CHEST -> "chestplate";
                  case LEGS -> "leggings";
                  case FEET -> "boots";
                  default -> "";
               };
               String armorItemPath = armorItem.toString();
               String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
               String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
               ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
               ResourceLocation trimResLoc = ResourceLocation.parse(trimPath);
               ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);
               this.existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");
               ((ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(currentTrimName)).parent(new UncheckedModelFile("item/generated")))
                     .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath()))
                  .texture("layer1", trimResLoc);
               ((ItemModelBuilder)this.withExistingParent(itemDeferredItem.getId().getPath(), this.mcLoc("item/generated")))
                  .override()
                  .model(new UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                  .predicate(this.mcLoc("trim_type"), trimValue)
                  .end()
                  .texture("layer0", ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "item/" + itemDeferredItem.getId().getPath()));
            }
         );
      }
   }

   static {
      trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
      trimMaterials.put(TrimMaterials.IRON, 0.2F);
      trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
      trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
      trimMaterials.put(TrimMaterials.COPPER, 0.5F);
      trimMaterials.put(TrimMaterials.GOLD, 0.6F);
      trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
      trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
      trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
      trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
   }
}
