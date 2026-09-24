package zeta.org.permadeath_reincarnated.systems;

import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PreventModPiracy {
   private static final Set<String> PERMADEATH_REINCARNATED_OWNERS = Set.of("zetadrayzer", "Krekoh", "KreKoh", "krekoh", "OGImKrekoh", "TheMinator", "Dev");

   @SubscribeEvent
   public static void onPiracy(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            if (!player.level().isClientSide) {
               if (!StartupData.get(level).didOwnersGavePermission()) {
                  if (player.tickCount % 10 == 0) {
                     MinecraftServer server = player.server;
                     String playerName = player.getName().getString();
                     if (server.isSingleplayer()) {
                        if (!PERMADEATH_REINCARNATED_OWNERS.contains(playerName)) {
                           player.connection.disconnect(Component.literal("¡NO PUEDES JUGAR EN SINGLEPLAYER!").withStyle(ChatFormatting.RED));
                        }
                     } else if (!server.isSingleplayer()) {
                        if (PERMADEATH_REINCARNATED_OWNERS.contains(playerName)) {
                           player.server.getAllLevels().forEach(levels -> StartupData.get(levels).setStartupOwnersPermission(true));
                        } else if (!PERMADEATH_REINCARNATED_OWNERS.contains(playerName)) {
                           player.connection.disconnect(Component.literal("¡NO PUEDES JUGAR SIN PERMISO DE LOS CREADORES!").withStyle(ChatFormatting.RED));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
