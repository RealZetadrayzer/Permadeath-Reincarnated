package zeta.org.permadeath_reincarnated.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import zeta.org.permadeath_reincarnated.demonFight.ClientCrystalData;

@EventBusSubscriber(modid = "permadeath_reincarnated")
public class PermadeathPayloads {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      event.registrar("permadeath_reincarnated")
         .playToClient(PermadeathNetworkingDemon.TYPE, PermadeathNetworkingDemon.STREAM_CODEC, PermadeathPayloads::handleClient);
   }

   private static void handleClient(PermadeathNetworkingDemon payload, IPayloadContext context) {
      context.enqueueWork(() -> {
         ClientCrystalData.crystalCount = payload.crystalCount();
         ClientCrystalData.dragonHealthPercent = payload.dragonHealthPercent();
         ClientCrystalData.isEnraged = payload.isEnraged();
      });
   }
}
