package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(AbstractFurnaceBlockEntity.class)
public class IronGoldNuggetMixin {
   private static boolean isIronGoldOre(ItemStack input) {
      return input.is(Blocks.IRON_ORE.asItem())
         || input.is(Blocks.DEEPSLATE_IRON_ORE.asItem())
         || input.is(Blocks.GOLD_ORE.asItem())
         || input.is(Blocks.DEEPSLATE_GOLD_ORE.asItem())
         || input.is(Items.RAW_IRON)
         || input.is(Items.RAW_GOLD);
   }

   @Unique
   private static ItemStack permadeathReincarnated$convert(ItemStack input, ItemStack result) {
      if (DayGlobalCount.CURRENT_DAY < 50) {
         return result;
      } else if (!isIronGoldOre(input)) {
         return result;
      } else if (result.is(Items.IRON_INGOT)) {
         return new ItemStack(Items.IRON_NUGGET, result.getCount());
      } else {
         return result.is(Items.GOLD_INGOT) ? new ItemStack(Items.GOLD_NUGGET, result.getCount()) : result;
      }
   }

   @ModifyVariable(method = "burn", at = @At("STORE"), ordinal = 1)
   private static ItemStack permadeath$burnConvert(
      ItemStack result, RegistryAccess access, RecipeHolder<?> recipe, NonNullList<ItemStack> items, int maxStack, AbstractFurnaceBlockEntity furnace
   ) {
      ItemStack input = (ItemStack)items.get(0);
      return permadeathReincarnated$convert(input, result);
   }

   @ModifyVariable(method = "canBurn", at = @At("STORE"), ordinal = 0)
   private static ItemStack permadeath$canBurnConvert(
      ItemStack result, RegistryAccess access, RecipeHolder<?> recipe, NonNullList<ItemStack> items, int maxStack, AbstractFurnaceBlockEntity furnace
   ) {
      ItemStack input = (ItemStack)items.get(0);
      return permadeathReincarnated$convert(input, result);
   }
}
