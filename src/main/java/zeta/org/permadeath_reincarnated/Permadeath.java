package zeta.org.permadeath_reincarnated;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.items.PermadeathArmorMaterials;
import zeta.org.permadeath_reincarnated.items.PermadeathCreativeTab;
import zeta.org.permadeath_reincarnated.items.PermadeathDataComponents;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.recipes.PermadeathRecipeSerializers;
import zeta.org.permadeath_reincarnated.systems.AdminCommands;
import zeta.org.permadeath_reincarnated.systems.DataHandler;
import zeta.org.permadeath_reincarnated.systems.PlayerDeathHandler;
import zeta.org.permadeath_reincarnated.systems.attachments.PermadeathAttachments;
import zeta.org.permadeath_reincarnated.systems.worldgen.PermadeathFeatures;

@Mod("permadeath_reincarnated")
public class Permadeath {
   public static final String MODID = "permadeath_reincarnated";

   public Permadeath(IEventBus modBus, ModContainer container) {
      container.registerConfig(Type.COMMON, PermadeathConfig.SPEC);
      PermadeathArmorMaterials.ARMOR_MATERIALS.register(modBus);
      PermadeathItemsRegistry.ITEMS.register(modBus);
      PermadeathItemsRegistry.BLOCKS.register(modBus);
      PermadeathEntityRegistry.ENTITY_TYPE.register(modBus);
      PermadeathCreativeTab.register(modBus);
      PermadeathMobEffectBuilder.register(modBus);
      PermadeathDataComponents.DATA_COMPONENTS.register(modBus);
      PermadeathRecipeSerializers.SERIALIZERS.register(modBus);
      PermadeathFeatures.FEATURES.register(modBus);
      PermadeathAttachments.ATTACHMENTS.register(modBus);
      PermadeathAttributes.ATTRIBUTES.register(modBus);
      PermadeathSounds.SOUND_EVENTS.register(modBus);
      NeoForge.EVENT_BUS.register(new PlayerDeathHandler());
      NeoForge.EVENT_BUS.register(new DataHandler());
      NeoForge.EVENT_BUS.register(new Permadeath.CommandsRegistry());
   }

   public static class CommandsRegistry {
      @SubscribeEvent
      public void onRegisterCommands(RegisterCommandsEvent event) {
         AdminCommands.register(event.getDispatcher());
      }
   }
}
