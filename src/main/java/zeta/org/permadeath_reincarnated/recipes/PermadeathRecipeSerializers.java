package zeta.org.permadeath_reincarnated.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import zeta.org.permadeath_reincarnated.recipes.infernal_armor.InfernalNetheriteBoots;
import zeta.org.permadeath_reincarnated.recipes.infernal_armor.InfernalNetheriteChestplate;
import zeta.org.permadeath_reincarnated.recipes.infernal_armor.InfernalNetheriteElytra;
import zeta.org.permadeath_reincarnated.recipes.infernal_armor.InfernalNetheriteHelmet;
import zeta.org.permadeath_reincarnated.recipes.infernal_armor.InfernalNetheriteLeggings;
import zeta.org.permadeath_reincarnated.recipes.original_recipes.BegRelicOriginalRecipe;
import zeta.org.permadeath_reincarnated.recipes.original_recipes.EndRelicOriginalRecipe;
import zeta.org.permadeath_reincarnated.recipes.original_recipes.LifeOrbOriginalRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringReincarnationRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringShockRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringWitherRageRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.regular.RegularBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.regular.RegularResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashReincarnationRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashShockRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashWitherRageRecipe;

public class PermadeathRecipeSerializers {
   public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "permadeath_reincarnated");
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalElytraRecipe>> INFERNAL_ELYTRA = SERIALIZERS.register(
      "crafting_infernal_elytra", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalElytraRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EndOrbRecipe>> END_ORB = SERIALIZERS.register(
      "crafting_end_orb", () -> new SimpleCraftingRecipeSerializer(id -> new EndOrbRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BegShieldRecipe>> BEG_SHIELD = SERIALIZERS.register(
      "crafting_beg_shield", () -> new SimpleCraftingRecipeSerializer(id -> new BegShieldRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnhancedTCNDRecipe>> ENHANCED_TCND = SERIALIZERS.register(
      "crafting_enhanced_tcnd", () -> new SimpleCraftingRecipeSerializer(id -> new EnhancedTCNDRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LingeringBeginningRecipe>> LINGERING_BEGINNING = SERIALIZERS.register(
      "crafting_lingering_beginning", () -> new SimpleCraftingRecipeSerializer(id -> new LingeringBeginningRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LingeringResurrectionRecipe>> LINGERING_RESURRECTION = SERIALIZERS.register(
      "crafting_lingering_resurrection", () -> new SimpleCraftingRecipeSerializer(id -> new LingeringResurrectionRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SplashBeginningRecipe>> SPLASH_BEGINNING = SERIALIZERS.register(
      "crafting_splash_beginning", () -> new SimpleCraftingRecipeSerializer(id -> new SplashBeginningRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SplashResurrectionRecipe>> SPLASH_RESURRECTION = SERIALIZERS.register(
      "crafting_splash_resurrection", () -> new SimpleCraftingRecipeSerializer(id -> new SplashResurrectionRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RegularBeginningRecipe>> REGULAR_BEGINNING = SERIALIZERS.register(
      "crafting_regular_beginning", () -> new SimpleCraftingRecipeSerializer(id -> new RegularBeginningRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RegularResurrectionRecipe>> REGULAR_RESURRECTION = SERIALIZERS.register(
      "crafting_regular_resurrection", () -> new SimpleCraftingRecipeSerializer(id -> new RegularResurrectionRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WitherSkullRecipe>> WITHER_SKULL = SERIALIZERS.register(
      "crafting_wither_skull", () -> new SimpleCraftingRecipeSerializer(id -> new WitherSkullRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LingeringShockRecipe>> LINGERING_SHOCK = SERIALIZERS.register(
      "crafting_lingering_shock", () -> new SimpleCraftingRecipeSerializer(id -> new LingeringShockRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LingeringReincarnationRecipe>> LINGERING_REINCARNATION = SERIALIZERS.register(
      "crafting_lingering_reincarnation", () -> new SimpleCraftingRecipeSerializer(id -> new LingeringReincarnationRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LingeringWitherRageRecipe>> LINGERING_WITHER_RAGE = SERIALIZERS.register(
      "crafting_lingering_wither_rage", () -> new SimpleCraftingRecipeSerializer(id -> new LingeringWitherRageRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SplashShockRecipe>> SPLASH_SHOCK = SERIALIZERS.register(
      "crafting_splash_shock", () -> new SimpleCraftingRecipeSerializer(id -> new SplashShockRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SplashReincarnationRecipe>> SPLASH_REINCARNATION = SERIALIZERS.register(
      "crafting_splash_reincarnation", () -> new SimpleCraftingRecipeSerializer(id -> new SplashReincarnationRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SplashWitherRageRecipe>> SPLASH_WITHER_RAGE = SERIALIZERS.register(
      "crafting_splash_wither_rage", () -> new SimpleCraftingRecipeSerializer(id -> new SplashWitherRageRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceRecipe>> MACE = SERIALIZERS.register(
      "crafting_mace", () -> new SimpleCraftingRecipeSerializer(id -> new MaceRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DragonBreathRecipe>> DRAGON_BREATH = SERIALIZERS.register(
      "crafting_dragon_breath", () -> new SimpleCraftingRecipeSerializer(id -> new DragonBreathRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShulkerShellRecipe>> SHULKER_SHELL = SERIALIZERS.register(
      "crafting_shulker_shell", () -> new SimpleCraftingRecipeSerializer(id -> new ShulkerShellRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HyperGoldenAppleRecipe>> HYPER_GOLDEN_APPLE = SERIALIZERS.register(
      "crafting_hyper_golden_apple", () -> new SimpleCraftingRecipeSerializer(id -> new HyperGoldenAppleRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtraHyperGoldenAppleRecipe>> EXTRA_HYPER_GOLDEN_APPLE = SERIALIZERS.register(
      "crafting_extra_hyper_golden_apple", () -> new SimpleCraftingRecipeSerializer(id -> new ExtraHyperGoldenAppleRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SuperGoldenAppleRecipe>> SUPER_GOLDEN_APPLE = SERIALIZERS.register(
      "crafting_super_golden_apple", () -> new SimpleCraftingRecipeSerializer(id -> new SuperGoldenAppleRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EndRelicRecipe>> END_RELIC = SERIALIZERS.register(
      "crafting_end_relic", () -> new SimpleCraftingRecipeSerializer(id -> new EndRelicRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EndRelicOriginalRecipe>> END_RELIC_ORIGINAL = SERIALIZERS.register(
      "crafting_end_relic_original", () -> new SimpleCraftingRecipeSerializer(id -> new EndRelicOriginalRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BegRelicRecipe>> BEG_RELIC = SERIALIZERS.register(
      "crafting_beg_relic", () -> new SimpleCraftingRecipeSerializer(id -> new BegRelicRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BegRelicOriginalRecipe>> BEG_RELIC_ORIGINAL = SERIALIZERS.register(
      "crafting_beg_relic_original", () -> new SimpleCraftingRecipeSerializer(id -> new BegRelicOriginalRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LifeOrbRecipe>> LIFE_ORB = SERIALIZERS.register(
      "crafting_life_orb", () -> new SimpleCraftingRecipeSerializer(id -> new LifeOrbRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LifeOrbOriginalRecipe>> LIFE_ORB_ORIGINAL = SERIALIZERS.register(
      "crafting_life_orb_original", () -> new SimpleCraftingRecipeSerializer(id -> new LifeOrbOriginalRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<NetheriteUpgradeTemplateRecipe>> NETHERITE_UPGRADE = SERIALIZERS.register(
      "crafting_netherite_upgrade", () -> new SimpleCraftingRecipeSerializer(id -> new NetheriteUpgradeTemplateRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<NetheriteUpgradeTemplateDupeRecipe>> NETHERITE_UPGRADE_DUPE = SERIALIZERS.register(
      "crafting_netherite_upgrade_dupe", () -> new SimpleCraftingRecipeSerializer(id -> new NetheriteUpgradeTemplateDupeRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteUpgradeTemplateRecipe>> INFERNAL_NETHERITE_UPGRADE = SERIALIZERS.register(
      "crafting_infernal_netherite_upgrade",
      () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteUpgradeTemplateRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteUpgradeTemplateDupeRecipe>> INFERNAL_NETHERITE_UPGRADE_DUPE = SERIALIZERS.register(
      "crafting_infernal_netherite_upgrade_dupe",
      () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteUpgradeTemplateDupeRecipe(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteHelmet>> INFERNAL_NETHERITE_HELMET = SERIALIZERS.register(
      "crafting_infernal_netherite_helmet", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteHelmet(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteChestplate>> INFERNAL_NETHERITE_CHESTPLATE = SERIALIZERS.register(
      "crafting_infernal_netherite_chestplate", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteChestplate(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteLeggings>> INFERNAL_NETHERITE_LEGGINGS = SERIALIZERS.register(
      "crafting_infernal_netherite_leggings", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteLeggings(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteBoots>> INFERNAL_NETHERITE_BOOTS = SERIALIZERS.register(
      "crafting_infernal_netherite_boots", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteBoots(CraftingBookCategory.MISC))
   );
   public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfernalNetheriteElytra>> INFERNAL_NETHERITE_ELYTRA = SERIALIZERS.register(
      "crafting_infernal_netherite_elytra", () -> new SimpleCraftingRecipeSerializer(id -> new InfernalNetheriteElytra(CraftingBookCategory.MISC))
   );
}
