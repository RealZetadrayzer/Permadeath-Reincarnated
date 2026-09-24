package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconDisabledMixin {
   @Shadow
   int levels;
   @Shadow
   Holder<MobEffect> primaryPower;
   @Shadow
   Holder<MobEffect> secondaryPower;

   @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
   private static void disableBeacon(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         BeaconDisabledMixin self = (BeaconDisabledMixin)(Object)blockEntity;
         assert self != null;
         self.levels = 0;
         self.primaryPower = null;
         self.secondaryPower = null;
         ci.cancel();
      }
   }

   @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
   private void disableMenu(int id, Inventory inv, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         cir.setReturnValue(null);
      }
   }
}
