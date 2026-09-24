package zeta.org.permadeath_reincarnated.recipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class BegShieldRecipe extends CustomRecipe {
   public static final int[] COUNTS = new int[]{16, 4, 16, 1, 1, 1, 16, 4, 16};
   private static final Item[] ITEMS = new Item[]{
      Items.DIAMOND_BLOCK,
      (Item)PermadeathItemsRegistry.PERMA_WITHER_ESSENCE.get(),
      Items.DIAMOND_BLOCK,
      (Item)PermadeathItemsRegistry.PERMA_VOID_ESSENCE.get(),
      Items.SHIELD,
      (Item)PermadeathItemsRegistry.PERMA_VOID_ESSENCE.get(),
      Items.DIAMOND_BLOCK,
      (Item)PermadeathItemsRegistry.PERMA_WITHER_ESSENCE.get(),
      Items.DIAMOND_BLOCK
   };

   public BegShieldRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level level) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return false;
      }

      if (DayGlobalCount.CURRENT_DAY < 55) {
         return false;
      }

      if (inv.width() == 3 && inv.height() == 3) {
         if (inv.getItem(4).has(DataComponents.CUSTOM_DATA)) {
            CustomData data = (CustomData)inv.getItem(4).get(DataComponents.CUSTOM_DATA);
            if (data != null) {
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_beg_shield") == 1) {
                  return false;
               }
            }
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
      ItemStack result = new ItemStack(Items.SHIELD);
      result.set(DataComponents.CUSTOM_NAME, Component.translatable("item.permadeath_reincarnated.beg_shield").withStyle(style -> style.withItalic(false)));
      result.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
      CompoundTag tag = new CompoundTag();
      tag.putInt("permadeath_beg_shield", 1);
      result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      return result;
   }

   public boolean canCraftInDimensions(int w, int h) {
      return w == 3 && h == 3;
   }

   public ItemStack getResultItem(Provider provider) {
      return new ItemStack(Items.SHIELD);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)PermadeathRecipeSerializers.BEG_SHIELD.get();
   }

   public static boolean matchesContainer(CraftingContainer inv) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
         if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            return false;
         }

         if (DayGlobalCount.CURRENT_DAY < 55) {
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
