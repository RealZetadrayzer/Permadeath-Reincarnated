package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
   @Final
   @Shadow
   public Fluid content;

   @Inject(method = "use", at = @At("HEAD"), cancellable = true)
   private void permadeathreincarnated$blockWaterLavaPickup(
      Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
   ) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (this.content == Fluids.EMPTY) {
            ItemStack stack = player.getItemInHand(hand);
            BlockHitResult hit = BucketItem.getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == Type.BLOCK) {
               BlockPos pos = hit.getBlockPos();
               FluidState fluid = level.getFluidState(pos);
               if (fluid.isSource() && (fluid.is(FluidTags.WATER) || fluid.is(FluidTags.LAVA))) {
                  cir.setReturnValue(InteractionResultHolder.fail(stack));
               }
            }
         }
      }
   }
}
