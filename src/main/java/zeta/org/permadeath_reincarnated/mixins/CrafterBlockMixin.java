package zeta.org.permadeath_reincarnated.mixins;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
   @Inject(
      method = "dispenseFrom",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/CrafterBlock;getPotentialResults(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;"
      ),
      cancellable = true
   )
   private void permadeath$blockItemCrafting(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
      if (level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter) {
         CraftingInput input = crafter.asCraftInput();
         Optional<RecipeHolder<CraftingRecipe>> recipe = CrafterBlock.getPotentialResults(level, input);
         if (!recipe.isEmpty()) {
            int day = DayGlobalCount.CURRENT_DAY;
            ItemStack result = ((CraftingRecipe)recipe.get().value()).assemble(input, level.registryAccess());
            if (result.is((Item)PermadeathItemsRegistry.PERMA_END_RELIC.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_BEG_RELIC.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_HYPER_GOLDEN_APPLE.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_EXTRA_HYPER_GOLDEN_APPLE.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_SUPER_GOLDEN_APPLE.get())
               || result.is((Item)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get())
               || result.is((Item)PermadeathItemsRegistry.CUSTOM_INFERNAL_NETHERITE_UPGRADE_TEMPLATE.get())
               || result.is(Items.MACE)
               || result.is((Item)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get())
               || result.is((Item)PermadeathItemsRegistry.PERMA_END_ORB.get())) {
               ci.cancel();
            }

            if (result.is(Items.SHIELD)) {
               CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_beg_shield") == 1) {
                  ci.cancel();
               }
            }

            if (result.is(Items.SPLASH_POTION) || result.is(Items.LINGERING_POTION)) {
               CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_wither_rage") == 1
                  || tag.getInt("permadeath_reincarnation") == 1
                  || tag.getInt("permadeath_shock") == 1
                  || tag.getInt("permadeath_resurrection") == 1
                  || tag.getInt("permadeath_beginning") == 1) {
                  ci.cancel();
               }
            }

            if (result.is(Items.POTION)) {
               CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_resurrection") == 1 || tag.getInt("permadeath_beginning") == 1) {
                  ci.cancel();
               }
            }

            if (day >= 40 && (result.is(Items.TORCH) || result.is(Items.REDSTONE_TORCH))) {
               ci.cancel();
            }
         }
      }
   }
}
