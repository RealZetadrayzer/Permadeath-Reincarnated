package zeta.org.permadeath_reincarnated.recipes.infernal_armor;

import net.minecraft.core.HolderLookup.Provider;
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

public class InfernalNetheriteChestplate extends CustomRecipe {
   private static final Item[] ITEMS = new Item[]{
      Items.AIR,
      (Item)PermadeathItemsRegistry.INFERNAL_NETHERTITE_BLOCK_ITEM.get(),
      Items.AIR,
      (Item)PermadeathItemsRegistry.INFERNAL_NETHERTITE_BLOCK_ITEM.get(),
      (Item)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get(),
      (Item)PermadeathItemsRegistry.INFERNAL_NETHERTITE_BLOCK_ITEM.get(),
      Items.AIR,
      (Item)PermadeathItemsRegistry.INFERNAL_NETHERTITE_BLOCK_ITEM.get(),
      Items.AIR
   };

   public InfernalNetheriteChestplate(CraftingBookCategory category) {
      super(CraftingBookCategory.MISC);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (inv.width() == 3 && inv.height() == 3) {
         if (!(Boolean)PermadeathConfig.DISABLE_REWORK.get()) {
            return false;
         }

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            Item expected = ITEMS[i];
            if (expected == Items.AIR) {
               if (!stack.isEmpty()) {
                  return false;
               }
            } else {
               if (stack.isEmpty()) {
                  return false;
               }

               if (stack.getItem() != expected) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput inv, Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE.get());
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.INFERNAL_NETHERITE_CHESTPLATE.get();
   }
}
