package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.clientsync.PermadeathClientConsumablePredictionHandler;

@Mixin(LivingEntity.class)
public class LivingEntityConsumeMixin {
   @Inject(method = "updateUsingItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$updateUsingItem(ItemStack usingItem, CallbackInfo ci) {
      if (!Minecraft.getInstance().hasSingleplayerServer()) {
         LivingEntity self = (LivingEntity)(Object)this;
         if (self == Minecraft.getInstance().player) {
            PermadeathClientConsumablePredictionHandler.handleItemStackUsage(usingItem, ci);
         }
      }
   }
}
