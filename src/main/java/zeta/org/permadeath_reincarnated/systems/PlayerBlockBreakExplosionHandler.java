package zeta.org.permadeath_reincarnated.systems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerBlockBreakExplosionHandler {
   private static final Map<String, String> DIMENSION_NAMES = new HashMap<>();

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Player player = event.getPlayer();
         if (!player.level().isClientSide) {
            if (player.level() instanceof ServerLevel level) {
               if (!player.isSpectator() && !player.isCreative()) {
                  if (!(event.getState().getBlock() instanceof FireBlock)) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 55) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                        if (chance <= 1) {
                           String playerName = player.getName().getString();
                           List<ServerPlayer> serverPlayerList = Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers();
                           String dimId = level.dimension().location().toString();
                           String dimensionName = DIMENSION_NAMES.getOrDefault(dimId, dimId);
                           BlockPos pos = event.getPos();
                           int x = Mth.floor(pos.getX());
                           int y = Mth.floor(pos.getY());
                           int z = Mth.floor(pos.getZ());
                           serverPlayerList.forEach(
                              players -> players.displayClientMessage(
                                 Component.literal("¡Un bloque minado por " + playerName + " exploto en " + dimensionName + ": " + x + " " + y + " " + z + "! ")
                                    .withStyle(ChatFormatting.RED),
                                 false
                              )
                           );
                           level.explode(null, player.getX(), player.getY(), player.getZ(), 50.0F, true, ExplosionInteraction.MOB).finalizeExplosion(true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static {
      DIMENSION_NAMES.put("minecraft:overworld", "Overworld");
      DIMENSION_NAMES.put("minecraft:the_nether", "Nether");
      DIMENSION_NAMES.put("minecraft:the_end", "End");
      DIMENSION_NAMES.put("permadeath_reincarnated:the_beginning", "The Beginning");
   }
}
