package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.PiglinVariantAccess;

@OnlyIn(Dist.CLIENT)
@Mixin(PiglinRenderer.class)
public class PiglinRendererMixin {
   @Unique
   private static final ResourceLocation COMMANDER_PIGLIN = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "textures/entity/nether_mobs/commander_piglin_brute.png"
   );
   @Unique
   private static final ResourceLocation KAMIKAZE_PIGLIN = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "textures/entity/nether_mobs/kamikaze_piglin.png"
   );
   @Unique
   private static final ResourceLocation REGULAR_PIGLIN_BRUTE = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
   @Unique
   private static final ResourceLocation REGULAR_PIGLIN = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png");

   @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
   private void permadeath$overrideTexture(Mob piglin, CallbackInfoReturnable<ResourceLocation> cir) {
      if (piglin instanceof PiglinVariantAccess access) {
         int variant = access.permadeath$getPiglinVariant();
         if (piglin.getType() == EntityType.PIGLIN_BRUTE) {
            if (variant == 1) {
               cir.setReturnValue(COMMANDER_PIGLIN);
            } else {
               cir.setReturnValue(REGULAR_PIGLIN_BRUTE);
            }
         } else {
            if (piglin.getType() == EntityType.PIGLIN) {
               if (variant == 2) {
                  cir.setReturnValue(KAMIKAZE_PIGLIN);
                  return;
               }

               cir.setReturnValue(REGULAR_PIGLIN);
            }
         }
      }
   }
}
