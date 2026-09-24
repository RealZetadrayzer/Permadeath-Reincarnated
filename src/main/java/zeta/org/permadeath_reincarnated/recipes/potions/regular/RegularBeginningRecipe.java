package zeta.org.permadeath_reincarnated.recipes.potions.regular;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.recipes.PermadeathRecipeSerializers;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class RegularBeginningRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4};
   private static final Item[] ITEMS = new Item[]{
      Items.BLAZE_POWDER,
      Items.DIAMOND,
      Items.BLAZE_POWDER,
      Items.DIAMOND,
      Items.DRAGON_BREATH,
      Items.DIAMOND,
      Items.BLAZE_POWDER,
      Items.DIAMOND,
      Items.BLAZE_POWDER
   };

   public RegularBeginningRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return false;
      }

      if (DayGlobalCount.CURRENT_DAY < 45) {
         return false;
      }

      if (inv.width() == 3 && inv.height() == 3) {
         for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
               return false;
            }

            if (stack.getItem() != ITEMS[i]) {
               return false;
            }

            if (stack.getCount() < COUNTS[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      ItemStack result = new ItemStack(Items.POTION);
      result.set(
         DataComponents.CUSTOM_NAME, Component.translatable("item.permadeath_reincarnated.potion_beginning").withStyle(style -> style.withItalic(false))
      );
      PotionContents contents = new PotionContents(
         Optional.empty(),
         Optional.of(16777215),
         List.of(
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 18000, 0),
            new MobEffectInstance(MobEffects.SLOW_FALLING, 18000, 0),
            new MobEffectInstance(MobEffects.REGENERATION, 200, 1),
            new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1)
         )
      );
      result.set(DataComponents.POTION_CONTENTS, contents);
      CompoundTag tag = new CompoundTag();
      tag.putInt("permadeath_beginning", 1);
      result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return result;
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack(Items.POTION);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.REGULAR_BEGINNING.get();
   }

   public static boolean matchesContainer(CraftingContainer inv) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
         if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            return false;
         }

         if (DayGlobalCount.CURRENT_DAY < 45) {
            return false;
         }

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
               return false;
            }

            if (stack.getItem() != ITEMS[i]) {
               return false;
            }

            if (stack.getCount() < COUNTS[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
