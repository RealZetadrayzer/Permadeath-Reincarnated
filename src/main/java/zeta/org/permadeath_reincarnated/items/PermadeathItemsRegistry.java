package zeta.org.permadeath_reincarnated.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister.Items;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathGhastEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathImmortalityEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathMagmaEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathSlimeEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathSpiderEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathVoidEssence;
import zeta.org.permadeath_reincarnated.items.essences.PermadeathWitherEssence;

public class PermadeathItemsRegistry {
   public static final Items ITEMS = DeferredRegister.createItems("permadeath_reincarnated");
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("permadeath_reincarnated");
   public static final DeferredItem<PermadeathTemplates> CUSTOM_NETHERITE_UPGRADE_TEMPLATE = ITEMS.register(
      "netherite_upgrade", PermadeathTemplates::createNetheriteUpgradeTemplate
   );
   public static final DeferredItem<PermadeathTemplates> CUSTOM_INFERNAL_NETHERITE_UPGRADE_TEMPLATE = ITEMS.register(
      "infernal_netherite_upgrade", PermadeathTemplates::createInfernalNetheriteUpgradeTemplate
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_INFERNAL_ELYTRA = ITEMS.register(
      "infernal_netherite_elytra", () -> new PermadeathInfernalElytra(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant().durability(540))
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_ENHANCED_TCND = ITEMS.register(
      "enhanced_tcnd", () -> new PermadeathEnhancedTCND(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_TCND = ITEMS.register(
      "tcnd", () -> new PermadeathTCND(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_IMMORTALITY_ESSENCE = ITEMS.register(
      "immortality_essence", () -> new PermadeathImmortalityEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_VOID_ESSENCE = ITEMS.register(
      "void_essence", () -> new PermadeathVoidEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_WITHER_ESSENCE = ITEMS.register(
      "wither_essence", () -> new PermadeathWitherEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_GHAST_ESSENCE = ITEMS.register(
      "ghast_essence", () -> new PermadeathGhastEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_SLIME_ESSENCE = ITEMS.register(
      "slime_essence", () -> new PermadeathSlimeEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_SPIDER_ESSENCE = ITEMS.register(
      "spider_essence", () -> new PermadeathSpiderEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_MAGMA_ESSENCE = ITEMS.register(
      "magma_cube_essence", () -> new PermadeathMagmaEssence(new Properties().rarity(Rarity.EPIC).stacksTo(64).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_END_ORB = ITEMS.register(
      "end_orb", () -> new PermadeathEndOrb(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_LIFE_ORB = ITEMS.register(
      "life_orb", () -> new PermadeathLifeOrb(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_BEG_RELIC = ITEMS.register(
      "beg_relic", () -> new PermadeathBegRelic(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_END_RELIC = ITEMS.register(
      "end_relic", () -> new PermadeathEndRelic(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_STRUCTURE_VOID = ITEMS.register(
      "structure_void", () -> new PermadeathCustomVoid(new Properties().rarity(Rarity.COMMON).stacksTo(1).fireResistant())
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_HYPER_GOLDEN_APPLE = ITEMS.register(
      "hyper_golden_apple",
      () -> new PermadeathHyperGoldenApple(
         new Properties().rarity(Rarity.EPIC).stacksTo(64), new Builder().nutrition(40).saturationModifier(10.0F).alwaysEdible().build()
      )
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_EXTRA_HYPER_GOLDEN_APPLE = ITEMS.register(
      "extra_hyper_golden_apple",
      () -> new PermadeathExtraHyperGoldenApple(
         new Properties().rarity(Rarity.EPIC).stacksTo(64), new Builder().nutrition(40).saturationModifier(10.0F).alwaysEdible().build()
      )
   );
   public static final DeferredHolder<Item, ? extends Item> PERMA_SUPER_GOLDEN_APPLE = ITEMS.register(
      "super_golden_apple",
      () -> new PermadeathSuperGoldenApple(
         new Properties().rarity(Rarity.EPIC).stacksTo(64),
         new Builder()
            .nutrition(20)
            .saturationModifier(10.0F)
            .alwaysEdible()
            .effect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 0, false, true), 1.0F)
            .build()
      )
   );
   public static final DeferredBlock<Block> INFERNAL_NETHERTITE_BLOCK = BLOCKS.register(
      "infernal_netherite_block",
      () -> new Block(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .requiresCorrectToolForDrops()
            .strength(10.0F, 1200.0F)
            .sound(SoundType.NETHERITE_BLOCK)
      )
   );
   public static final DeferredItem<BlockItem> INFERNAL_NETHERTITE_BLOCK_ITEM = ITEMS.register(
      "infernal_netherite_block",
      () -> new BlockItem((Block)INFERNAL_NETHERTITE_BLOCK.get(), new Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(64))
   );
   public static final DeferredItem<ArmorItem> PERMA_NETHERITE_HELMET = ITEMS.register(
      "netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_NETHERITE_CHESTPLATE = ITEMS.register(
      "netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_NETHERITE_LEGGINGS = ITEMS.register(
      "netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_NETHERITE_BOOTS = ITEMS.register(
      "netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_INFERNAL_NETHERITE_HELMET = ITEMS.register(
      "infernal_netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_INFERNAL_NETHERITE_CHESTPLATE = ITEMS.register(
      "infernal_netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_INFERNAL_NETHERITE_LEGGINGS = ITEMS.register(
      "infernal_netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_INFERNAL_NETHERITE_BOOTS = ITEMS.register(
      "infernal_netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().component(DataComponents.UNBREAKABLE, new Unbreakable(true))
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_BLUE_NETHERITE_HELMET = ITEMS.register(
      "blue_netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_BLUE_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_BLUE_NETHERITE_CHESTPLATE = ITEMS.register(
      "blue_netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_BLUE_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_BLUE_NETHERITE_LEGGINGS = ITEMS.register(
      "blue_netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_BLUE_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_BLUE_NETHERITE_BOOTS = ITEMS.register(
      "blue_netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_BLUE_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_GREEN_NETHERITE_HELMET = ITEMS.register(
      "green_netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_GREEN_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_GREEN_NETHERITE_CHESTPLATE = ITEMS.register(
      "green_netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_GREEN_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_GREEN_NETHERITE_LEGGINGS = ITEMS.register(
      "green_netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_GREEN_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_GREEN_NETHERITE_BOOTS = ITEMS.register(
      "green_netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_GREEN_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_ROSE_NETHERITE_HELMET = ITEMS.register(
      "rose_netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_ROSE_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_ROSE_NETHERITE_CHESTPLATE = ITEMS.register(
      "rose_netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_ROSE_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_ROSE_NETHERITE_LEGGINGS = ITEMS.register(
      "rose_netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_ROSE_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_ROSE_NETHERITE_BOOTS = ITEMS.register(
      "rose_netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_ROSE_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_YELLOW_NETHERITE_HELMET = ITEMS.register(
      "yellow_netherite_helmet",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_YELLOW_NETHERITE.getDelegate(),
         Type.HELMET,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_YELLOW_NETHERITE_CHESTPLATE = ITEMS.register(
      "yellow_netherite_chestplate",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_YELLOW_NETHERITE.getDelegate(),
         Type.CHESTPLATE,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_YELLOW_NETHERITE_LEGGINGS = ITEMS.register(
      "yellow_netherite_leggings",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_YELLOW_NETHERITE.getDelegate(),
         Type.LEGGINGS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<ArmorItem> PERMA_YELLOW_NETHERITE_BOOTS = ITEMS.register(
      "yellow_netherite_boots",
      () -> new ArmorItem(
         PermadeathArmorMaterials.PERMA_YELLOW_NETHERITE.getDelegate(),
         Type.BOOTS,
         new Properties().component(DataComponents.UNBREAKABLE, new Unbreakable(true)).stacksTo(1).rarity(Rarity.EPIC).fireResistant()
      )
   );
   public static final DeferredItem<SwordItem> PERMA_NETHERITE_SWORD = ITEMS.register(
      "netherite_sword",
      () -> new SwordItem(
         PermadeathTiers.NETHERITE,
         new Properties()
            .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
            .fireResistant()
            .attributes(SwordItem.createAttributes(PermadeathTiers.NETHERITE, 3, -2.4F))
      )
   );
   public static final DeferredItem<ShovelItem> PERMA_NETHERITE_SHOVEL = ITEMS.register(
      "netherite_shovel",
      () -> new ShovelItem(
         PermadeathTiers.NETHERITE,
         new Properties()
            .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
            .fireResistant()
            .attributes(ShovelItem.createAttributes(PermadeathTiers.NETHERITE, 1.5F, -3.0F))
      )
   );
   public static final DeferredItem<PickaxeItem> PERMA_NETHERITE_PICKAXE = ITEMS.register(
      "netherite_pickaxe",
      () -> new PickaxeItem(
         PermadeathTiers.NETHERITE,
         new Properties()
            .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
            .fireResistant()
            .attributes(PickaxeItem.createAttributes(PermadeathTiers.NETHERITE, 1.0F, -2.8F))
      )
   );
   public static final DeferredItem<AxeItem> PERMA_NETHERITE_AXE = ITEMS.register(
      "netherite_axe",
      () -> new AxeItem(
         PermadeathTiers.NETHERITE,
         new Properties()
            .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
            .fireResistant()
            .attributes(AxeItem.createAttributes(PermadeathTiers.NETHERITE, 5.0F, -3.0F))
      )
   );
   public static final DeferredItem<HoeItem> PERMA_NETHERITE_HOE = ITEMS.register(
      "netherite_hoe",
      () -> new HoeItem(
         PermadeathTiers.NETHERITE,
         new Properties()
            .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
            .fireResistant()
            .attributes(HoeItem.createAttributes(PermadeathTiers.NETHERITE, -4.0F, 0.0F))
      )
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_GIANT_SPAWN_EGG = ITEMS.register(
      "wild_giant_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_GIANT, 44975, 7969893, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_RAVAGER_SPAWN_EGG = ITEMS.register(
      "wild_ravager_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_RAVAGER, 7697781, 3815994, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_SILVERFISH_SPAWN_EGG = ITEMS.register(
      "wild_silverfish_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_SILVERFISH, 7237230, 13619151, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_SNOWGOLEM_SPAWN_EGG = ITEMS.register(
      "wild_snow_golem_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_SNOWGOLEM, 14283506, 8496292, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_ZOMBIE_HORSE_SPAWN_EGG = ITEMS.register(
      "wild_zombie_horse_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE, 3232308, 9945732, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredItem<DeferredSpawnEggItem> PERMA_CREEPER_SPAWN_EGG = ITEMS.register(
      "creeper_spawn_egg",
      () -> new DeferredSpawnEggItem(PermadeathEntityRegistry.CUSTOM_CREEPER, 894731, 0, new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
}
