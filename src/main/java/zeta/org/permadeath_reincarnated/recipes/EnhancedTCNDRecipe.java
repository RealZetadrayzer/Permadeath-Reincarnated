package zeta.org.permadeath_reincarnated.recipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

public class EnhancedTCNDRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{16, 1, 16, 1, 1, 1, 16, 1, 16};
   private static final Item[] ITEMS = new Item[]{
      Items.ECHO_SHARD,
      Items.NETHERITE_INGOT,
      Items.ECHO_SHARD,
      Items.NETHERITE_INGOT,
      (Item)PermadeathItemsRegistry.PERMA_TCND.get(),
      Items.NETHERITE_INGOT,
      Items.ECHO_SHARD,
      Items.NETHERITE_INGOT,
      Items.ECHO_SHARD
   };

   public EnhancedTCNDRecipe(CraftingBookCategory category) {
      super(CraftingBookCategory.MISC);
   }

   public boolean matches(CraftingInput inv, Level level) {
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
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get());
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.ENHANCED_TCND.get();
   }

   public static boolean matchesContainer(CraftingContainer inv) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
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
