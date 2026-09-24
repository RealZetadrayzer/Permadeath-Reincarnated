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
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

public class NetheriteUpgradeTemplateRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{4, 1, 4, 4, 1, 4, 4, 4, 4};
   private static final Item[] ITEMS = new Item[]{
      Items.ECHO_SHARD,
      Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
      Items.ECHO_SHARD,
      Items.ECHO_SHARD,
      Items.HEAVY_CORE,
      Items.ECHO_SHARD,
      Items.ECHO_SHARD,
      Items.ECHO_SHARD,
      Items.ECHO_SHARD
   };

   public NetheriteUpgradeTemplateRecipe(CraftingBookCategory category) {
      super(CraftingBookCategory.MISC);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (inv.width() == 3 && inv.height() == 3) {
         if ((Boolean)PermadeathConfig.DISABLE_REWORK.get()) {
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
      return new ItemStack((ItemLike)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get());
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack((ItemLike)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.NETHERITE_UPGRADE.get();
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
