package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.items.PermadeathInfernalElytra;

@OnlyIn(Dist.CLIENT)
@Mixin(ElytraLayer.class)
public abstract class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>> {
   @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true)
   private void allowCustomElytra(ItemStack stack, T entity, CallbackInfoReturnable<Boolean> cir) {
      if (stack.getItem() instanceof PermadeathInfernalElytra) {
         cir.setReturnValue(true);
      }
   }
}
