package zeta.org.permadeath_reincarnated.systems;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.systems.timers.DeathtrainGlobalState;

@EventBusSubscriber
public class BeginningKickHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (!player.isSpectator()) {
               int deathTrain = DeathtrainGlobalState.CURRENT_STATE;
               if (player.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                  if (deathTrain == 1) {
                     ServerLevel overworld = Objects.requireNonNull(player.getServer()).overworld();
                     BlockPos spawn = overworld.getSharedSpawnPos();
                     BlockPos safeSpawn = overworld.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, spawn);
                     player.setDeltaMovement(0.0, player.getDeltaMovement().y, 0.0);
                     player.displayClientMessage(
                        Component.literal("¡El ")
                           .withStyle(ChatFormatting.RED)
                           .append(Component.literal("Death Train").withStyle(ChatFormatting.DARK_RED).withStyle(Style.EMPTY.withBold(true)))
                           .append(Component.literal(" esta activo no puedes entrar!").withStyle(ChatFormatting.RED)),
                        true
                     );
                     player.hurtMarked = true;
                     player.addTag("hide");
                     PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 40);
                     ScheduleInTicks.schedule(
                        () -> player.teleportTo(overworld, safeSpawn.getX(), safeSpawn.getY() + 1, safeSpawn.getZ(), player.getYRot(), player.getXRot()), 1
                     );
                  }
               }
            }
         }
      }
   }
}
