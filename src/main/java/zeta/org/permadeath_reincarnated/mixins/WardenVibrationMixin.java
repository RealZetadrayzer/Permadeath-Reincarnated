package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.Warden$VibrationUser")
public class WardenVibrationMixin {
   @Shadow
   @Final
   Warden this$0;

   @Inject(method = "canReceiveVibration", at = @At("HEAD"), cancellable = true)
   private void ignoreSelfBlockDrops(ServerLevel level, BlockPos pos, Holder<GameEvent> event, Context context, CallbackInfoReturnable<Boolean> cir) {
      Entity source = context.sourceEntity();
      if (source != this.this$0 && !(source instanceof Warden)) {
         if (source instanceof ItemEntity && this.this$0.getTags().contains("superWarden")) {
            cir.setReturnValue(false);
            cir.cancel();
         }
      } else {
         cir.setReturnValue(false);
         cir.cancel();
      }
   }
}
