package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(DispenserBlock.class)
public class BucketDispenserItemMixin {
   @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
   private void permadeath$blockDispenserBucketPickup(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         DispenserBlockEntity dispenser = (DispenserBlockEntity)level.getBlockEntity(pos);
         if (dispenser != null) {
            int slot = dispenser.getRandomSlot(level.random);
            if (slot >= 0) {
               ItemStack stack = dispenser.getItem(slot);
               if (stack.getItem() == Items.BUCKET) {
                  Direction facing = (Direction)state.getValue(DispenserBlock.FACING);
                  BlockPos targetPos = pos.relative(facing);
                  FluidState fluid = level.getFluidState(targetPos);
                  if (fluid.isSource() && (fluid.is(FluidTags.WATER) || fluid.is(FluidTags.LAVA))) {
                     ci.cancel();
                  }
               }
            }
         }
      }
   }
}
