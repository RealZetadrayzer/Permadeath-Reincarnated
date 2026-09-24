package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityFluidPushMixin {
   @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("HEAD"), cancellable = true)
   private void cancelFluidPushForTaggedDragons(CallbackInfoReturnable<Boolean> cir) {
      Entity self = (Entity)(Object)this;
      if ((self instanceof EnderDragon || self instanceof EnderDragonPart) && self.getTags().contains("demonFight")) {
         cir.setReturnValue(false);
      }
   }
}
