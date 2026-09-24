package zeta.org.permadeath_reincarnated.client;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;

@EventBusSubscriber(modid = "permadeath_reincarnated", value = Dist.CLIENT)
public final class ClientSetup {
   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      ModContainer container = ModLoadingContext.get().getActiveContainer();
      container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
      event.enqueueWork(() -> {
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE.get(), ZombieHorseRenderer::new);
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_SNOWGOLEM.get(), SnowGolemRenderer::new);
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_CREEPER.get(), CreeperRenderer::new);
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_RAVAGER.get(), RavagerRenderer::new);
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_SILVERFISH.get(), SilverfishRenderer::new);
         EntityRenderers.register((EntityType)PermadeathEntityRegistry.CUSTOM_GIANT.get(), ctx -> new GiantMobRenderer(ctx, 6.0F));
      });
   }
}
