package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(WitherBossRenderer.class)
public class WitherRendererMixin {
   @Unique
   private static final ResourceLocation MINI_WITHER = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
   @Unique
   private static final ResourceLocation REGULAR_WITHER = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");

   @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
   private void permadeath$overrideTexture(WitherBoss witherBoss, CallbackInfoReturnable<ResourceLocation> cir) {
      if (witherBoss.hasCustomName()) {
         if (witherBoss.getCustomName() == null) {
            return;
         }

         String name = witherBoss.getCustomName().getString();
         if (name.contains("Mini-Wither")) {
            cir.setReturnValue(MINI_WITHER);
            return;
         }
      }

      cir.setReturnValue(REGULAR_WITHER);
   }
}
