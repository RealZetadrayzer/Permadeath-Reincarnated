package zeta.org.permadeath_reincarnated.recipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class DragonBreathRecipe extends CustomRecipe {
   public DragonBreathRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (!level.isClientSide && DayGlobalCount.CURRENT_DAY < 40) {
         return false;
      }

      if (inv.ingredientCount() != 1) {
         return false;
      }

      ItemStack only = ItemStack.EMPTY;

      for (ItemStack chorus : inv.items()) {
         if (!chorus.isEmpty()) {
            only = chorus;
            break;
         }
      }

      return only.isEmpty() ? false : only.is(Items.CHORUS_FLOWER);
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      return new ItemStack(Items.DRAGON_BREATH);
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w * h >= 4;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack(Items.DRAGON_BREATH);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.SHULKER_SHELL.get();
   }
}
