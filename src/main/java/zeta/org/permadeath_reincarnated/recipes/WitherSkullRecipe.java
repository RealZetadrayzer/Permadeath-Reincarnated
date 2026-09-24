package zeta.org.permadeath_reincarnated.recipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class WitherSkullRecipe extends CustomRecipe {
   public WitherSkullRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return false;
      }

      if (!level.isClientSide && DayGlobalCount.CURRENT_DAY < 40) {
         return false;
      }

      if (inv.ingredientCount() != 1) {
         return false;
      }

      ItemStack only = ItemStack.EMPTY;

      for (ItemStack itemStack : inv.items()) {
         if (!itemStack.isEmpty()) {
            only = itemStack;
            break;
         }
      }

      return only.isEmpty() ? false : only.is(Items.BEACON);
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      return new ItemStack(Items.WITHER_SKELETON_SKULL, 2);
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w * h >= 4;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack(Items.WITHER_SKELETON_SKULL, 2);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.WITHER_SKULL.get();
   }
}
