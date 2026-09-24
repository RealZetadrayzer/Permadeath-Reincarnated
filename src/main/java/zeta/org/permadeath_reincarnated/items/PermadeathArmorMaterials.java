package zeta.org.permadeath_reincarnated.items;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "permadeath_reincarnated");
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_netherite"))),
         3.5F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_INFERNAL_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_infernal_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         25,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_infernal_netherite"))),
         4.0F,
         0.2F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_BLUE_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_blue_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_blue_netherite"))),
         3.5F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_YELLOW_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_yellow_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_yellow_netherite"))),
         3.5F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_GREEN_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_green_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_green_netherite"))),
         3.5F,
         0.15F
      )
   );
   public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PERMA_ROSE_NETHERITE = ARMOR_MATERIALS.register(
      "permadeath_rose_netherite",
      () -> new ArmorMaterial(
         Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3),
         15,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.EMPTY,
         List.of(new Layer(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "permadeath_rose_netherite"))),
         3.5F,
         0.15F
      )
   );
}
