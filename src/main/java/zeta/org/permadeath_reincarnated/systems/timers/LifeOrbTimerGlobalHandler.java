package zeta.org.permadeath_reincarnated.systems.timers;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class LifeOrbTimerGlobalHandler {
   private static final int orbTimerDuration = 28800;
   private static int globalTotalSeconds = 0;
   private static int globalState = 0;

   private static void syncAll(ServerLevel reference) {
      for (ServerLevel world : reference.getServer().getAllLevels()) {
         LifeOrbTimer timer = LifeOrbTimer.get(world);
         timer.totalSeconds = globalTotalSeconds;
         timer.state = globalState;
         timer.setDirty();
      }
   }

   public static int getState() {
      return globalState;
   }

   public static void initialize(ServerLevel reference) {
      LifeOrbTimer timer = LifeOrbTimer.get(reference);
      globalTotalSeconds = timer.totalSeconds;
      globalState = timer.state;
      syncAll(reference);
      if (globalState == 1) {
         LifeOrbTimer.updateAllPlayersBossBar(reference);

         for (ServerPlayer player : reference.getServer().getPlayerList().getPlayers()) {
            CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(LifeOrbTimer.BAR_ID);
            if (bar != null) {
               bar.addPlayer(player);
            }
         }
      }
   }

   public static void tick(ServerLevel reference) {
      if (reference.getServer().getTickCount() % 20 == 0) {
         boolean justFinished = false;
         if (DayGlobalCount.CURRENT_DAY >= 60 && globalState == 0) {
            globalTotalSeconds = 28800;
            globalState = 1;
            showTimer(reference);

            for (ServerPlayer player : reference.getServer().getPlayerList().getPlayers()) {
               long seed = player.level().getRandom().nextLong();
               player.sendSystemMessage(
                  Component.literal("Todos los jugadores tienen ")
                     .withStyle(ChatFormatting.YELLOW)
                     .append(
                        Component.literal("8 horas")
                           .withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
                           .append(Component.literal(" para obtener la ").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false)))
                           .append(Component.literal("Orbe de Vida").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                           .append(Component.literal(".").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false)))
                     )
               );
               player.connection
                  .send(
                     new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.TRIDENT_RETURN),
                        SoundSource.VOICE,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        1.0F,
                        0.25F,
                        seed
                     )
                  );
            }
         }

         if (DayGlobalCount.CURRENT_DAY >= 60 || globalState != 1 && globalState != 3) {
            if (DayGlobalCount.CURRENT_DAY >= 60 && globalState == 4) {
               globalState = 3;
               showTimer(reference);
            }
         } else {
            reset(reference);
            hideTimer(reference);
         }

         if ((globalState == 1 || globalState == 3) && globalTotalSeconds > 0) {
            globalTotalSeconds--;
            if (globalTotalSeconds <= 0) {
               globalState = 2;
               justFinished = true;
            }
         }

         syncAll(reference);
         if (globalState == 1 || globalState == 3 || globalState == 4) {
            for (ServerPlayer player : reference.getServer().getPlayerList().getPlayers()) {
               LifeOrbTimer timer = LifeOrbTimer.get((ServerLevel)player.level());
               CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(LifeOrbTimer.BAR_ID);
               if (bar != null) {
                  timer.addPlayer(player);
                  timer.updateBossBarFor(player, bar);
                  LifeOrbTimer.cycleColor(player);
               }
            }
         }

         if (justFinished) {
            for (ServerPlayer player : reference.getServer().getPlayerList().getPlayers()) {
               player.sendSystemMessage(
                  Component.literal("El tiempo para obtener la ")
                     .withStyle(ChatFormatting.YELLOW)
                     .append(
                        Component.literal("Orbe de Vida")
                           .withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
                           .append(Component.literal(" ha finalizado.").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false)))
                     )
               );
            }

            hideTimer(reference);
         }
      }
   }

   public static void setTime(ServerLevel ref, int seconds) {
      globalTotalSeconds = seconds;
      globalState = seconds > 0 ? 1 : 2;
      syncAll(ref);
      if (seconds > 0 && DayGlobalCount.CURRENT_DAY >= 60) {
         showTimer(ref);
      }
   }

   public static void addTime(ServerLevel ref, int seconds) {
      globalTotalSeconds += seconds;
      if (globalTotalSeconds > 0) {
         globalState = 1;
      }

      syncAll(ref);
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         showTimer(ref);
      }
   }

   public static void pause(ServerLevel ref) {
      globalState = 4;
      syncAll(ref);
   }

   public static void resume(ServerLevel ref) {
      globalState = 3;
      syncAll(ref);
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         showTimer(ref);
      }
   }

   public static void reset(ServerLevel ref) {
      globalTotalSeconds = 28800;
      globalState = 0;
      syncAll(ref);
      hideTimer(ref);
   }

   private static void showTimer(ServerLevel reference) {
      CustomBossEvent bar = reference.getServer().getCustomBossEvents().get(LifeOrbTimer.BAR_ID);
      if (bar != null) {
         bar.setVisible(true);

         for (ServerPlayer p : reference.getServer().getPlayerList().getPlayers()) {
            bar.addPlayer(p);
         }
      }
   }

   private static void hideTimer(ServerLevel reference) {
      CustomBossEvent bar = reference.getServer().getCustomBossEvents().get(LifeOrbTimer.BAR_ID);
      if (bar != null) {
         for (ServerPlayer p : reference.getServer().getPlayerList().getPlayers()) {
            bar.removePlayer(p);
         }

         if (bar.isVisible()) {
            bar.setVisible(false);
         }
      }
   }

   public static boolean orbTimerFinished() {
      return globalState == 2 && globalTotalSeconds <= 0;
   }
}
