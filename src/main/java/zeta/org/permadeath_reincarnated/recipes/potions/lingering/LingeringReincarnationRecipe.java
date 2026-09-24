package zeta.org.permadeath_reincarnated.recipes.potions.lingering;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.recipes.PermadeathRecipeSerializers;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class LingeringReincarnationRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{4, 4, 4, 4, 1, 4, 4, 4, 4};
   private static final Item[] ITEMS = new Item[]{
      Items.BLAZE_POWDER,
      Items.DRAGON_BREATH,
      Items.BLAZE_POWDER,
      Items.DRAGON_BREATH,
      Items.SPLASH_POTION,
      Items.DRAGON_BREATH,
      Items.BLAZE_POWDER,
      Items.DRAGON_BREATH,
      Items.BLAZE_POWDER
   };

   public LingeringReincarnationRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return false;
      }

      if (DayGlobalCount.CURRENT_DAY < 20) {
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

         ItemStack center = inv.getItem(4);
         return isPermadeathPotion(center);
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      ItemStack center = inv.getItem(4);
      if (!isPermadeathPotion(center)) {
         return ItemStack.EMPTY;
      }

      ItemStack result = new ItemStack(Items.LINGERING_POTION);
      result.applyComponents(center.getComponentsPatch());
      result.set(
         DataComponents.CUSTOM_NAME,
         Component.translatable("item.permadeath_reincarnated.lingering_potion_reincarnation").withStyle(style -> style.withItalic(false))
      );
      result.setCount(1);
      return result;
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack(Items.LINGERING_POTION);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.LINGERING_REINCARNATION.get();
   }

   private static boolean isPermadeathPotion(ItemStack stack) {
      if (!stack.is(Items.SPLASH_POTION)) {
         return false;
      }

      CustomData customData = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      CompoundTag tag = customData.copyTag();
      return tag.getInt("permadeath_reincarnation") == 1;
   }

   public static boolean matchesContainer(CraftingContainer inv) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
         if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            return false;
         }

         if (DayGlobalCount.CURRENT_DAY < 20) {
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

         ItemStack center = inv.getItem(4);
         return isPermadeathPotion(center);
      } else {
         return false;
      }
   }
}
