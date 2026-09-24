package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(VexRenderer.class)
public class VexRendererMixin {
   @Unique
   private static final ResourceLocation HONEY_HEAD_VEX = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "textures/entity/beginning_mobs/honey_head_vex.png"
   );
   @Unique
   private static final ResourceLocation REGULAR_VEX = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");

   @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
   private void permadeath$overrideTexture(Vex vex, CallbackInfoReturnable<ResourceLocation> cir) {
      if (vex.hasCustomName()) {
         if (vex.getCustomName() == null) {
            return;
         }

         String name = vex.getCustomName().getString();
         if (name.contains("Vex Definitivo")) {
            cir.setReturnValue(HONEY_HEAD_VEX);
            return;
         }
      }

      cir.setReturnValue(REGULAR_VEX);
   }
}
