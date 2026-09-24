package zeta.org.permadeath_reincarnated.recipes.original_recipes;

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
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.recipes.PermadeathRecipeSerializers;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalState;

public class LifeOrbOriginalRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{64, 64, 64, 64, 1, 64, 64, 64, 64};
   private static final Item[] ITEMS = new Item[]{
      Items.DIAMOND,
      Items.GOLD_INGOT,
      Items.BONE_BLOCK,
      Items.BLAZE_ROD,
      Items.HEART_OF_THE_SEA,
      Items.END_STONE,
      Items.NETHER_BRICKS,
      Items.OBSIDIAN,
      Items.LAPIS_BLOCK
   };

   public LifeOrbOriginalRecipe(CraftingBookCategory category) {
      super(CraftingBookCategory.MISC);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (inv.width() == 3 && inv.height() == 3) {
         if (LifeOrbTimerGlobalState.CURRENT_STATE == 2) {
            return false;
         }

         if (!(Boolean)PermadeathConfig.DISABLE_REWORK.get()) {
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

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_LIFE_ORB.get());
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_LIFE_ORB.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.LIFE_ORB_ORIGINAL.get();
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
