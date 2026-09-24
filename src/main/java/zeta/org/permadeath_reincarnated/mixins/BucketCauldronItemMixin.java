package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(AbstractCauldronBlock.class)
public class BucketCauldronItemMixin {
   @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
   private void blockWaterLavaBucketAfterDay50(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit,
      CallbackInfoReturnable<ItemInteractionResult> cir
   ) {
      if (stack.getItem() == Items.BUCKET && DayGlobalCount.CURRENT_DAY >= 50 && (state.is(Blocks.WATER_CAULDRON) || state.is(Blocks.LAVA_CAULDRON))) {
         cir.setReturnValue(ItemInteractionResult.FAIL);
         cir.cancel();
      }
   }
}
