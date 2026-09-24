package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
   @Inject(method = "addEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
   private void permadeathreincarnated$suppressRemovedEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity.isRemoved()) {
         cir.setReturnValue(false);
      }
   }
}
