package zeta.org.permadeath_reincarnated.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ZombieHorseRenderer extends UndeadHorseRenderer {
   private static final ResourceLocation HORSE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_zombie.png");

   public ZombieHorseRenderer(Context ctx) {
      super(ctx, ModelLayers.ZOMBIE_HORSE);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull AbstractHorse horse) {
      return HORSE_TEXTURE;
   }
}
