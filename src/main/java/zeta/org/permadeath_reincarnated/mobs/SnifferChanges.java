package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobReplacement;

@EventBusSubscriber
public class SnifferChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 50) {
            if (event.getEntity() instanceof Sniffer sniffer) {
               if (!sniffer.level().isClientSide) {
                  if (sniffer.level() instanceof ServerLevel level) {
                     if (sniffer.isAlive()) {
                        sniffer.addTag("fromSniffer");
                        sniffer.setCustomName(Component.literal("Sniffer-Transmutado").withStyle(ChatFormatting.GOLD));
                        MobReplacement.convertToWithData(sniffer, EntityType.WARDEN, false);
                        sniffer.discard();
                     }
                  }
               }
            }
         }
      }
   }
}
