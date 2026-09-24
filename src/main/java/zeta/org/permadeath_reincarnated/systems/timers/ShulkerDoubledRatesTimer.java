package zeta.org.permadeath_reincarnated.systems.timers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class ShulkerDoubledRatesTimer extends SavedData {
   private static final String DATA_NAME = "permadeath_shulker_doubled_rates";
   public static final ResourceLocation BAR_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "double_shulker_shells_timer");
   public int totalSeconds = 0;
   public int state = 0;
   private final Set<ServerPlayer> players = new HashSet<>();
   private static final BossBarColor[] COLORS = new BossBarColor[]{BossBarColor.PURPLE, BossBarColor.PINK};
   private static int colorIndex = 0;
   private static int colorDir = 1;

   public ShulkerDoubledRatesTimer() {
   }

   public ShulkerDoubledRatesTimer(CompoundTag tag, Provider provider) {
      this.totalSeconds = tag.getInt("RemainingSeconds");
      this.state = tag.getInt("State");
   }

   public CompoundTag save(CompoundTag tag, Provider provider) {
      tag.putInt("RemainingSeconds", this.totalSeconds);
      tag.putInt("State", this.state);
      return tag;
   }

   public static ShulkerDoubledRatesTimer get(ServerLevel world) {
      return (ShulkerDoubledRatesTimer)world.getDataStorage()
         .computeIfAbsent(new Factory<>(ShulkerDoubledRatesTimer::new, ShulkerDoubledRatesTimer::new), "permadeath_shulker_doubled_rates");
   }

   public void addPlayer(ServerPlayer player) {
      if (this.players.add(player)) {
         CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(BAR_ID);
         if (bar != null) {
            bar.addPlayer(player);
         }

         this.updateBossBarFor(player, bar);
      }
   }

   public void removePlayer(ServerPlayer player) {
      this.players.remove(player);
      CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(BAR_ID);
      if (bar != null) {
         bar.removePlayer(player);
      }
   }

   public void clearPlayers() {
      for (ServerPlayer p : this.players) {
         CustomBossEvent bar = Objects.requireNonNull(p.getServer()).getCustomBossEvents().get(BAR_ID);
         if (bar != null) {
            bar.removePlayer(p);
         }
      }

      this.players.clear();
   }

   public void updateBossBarFor(ServerPlayer player, CustomBossEvent bar) {
      if (bar != null) {
         bar.setValue(100);
         bar.setName(
            Component.literal("X2 de ")
               .withStyle(new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD})
               .append(
                  Component.literal("Shulker Shells: ")
                     .withStyle(ChatFormatting.DARK_PURPLE)
                     .append(Component.literal(formatFullTime(this.totalSeconds)).withStyle(ChatFormatting.LIGHT_PURPLE))
               )
         );
      }
   }

   public static void updateAllPlayersBossBar(ServerLevel world) {
      ShulkerDoubledRatesTimer timer = get(world);

      for (ServerPlayer player : world.getServer().getPlayerList().getPlayers()) {
         CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(BAR_ID);
         if (bar != null) {
            timer.addPlayer(player);
            timer.updateBossBarFor(player, bar);
         }
      }
   }

   public static void cycleColor(ServerPlayer player) {
      CustomBossEvent bar = Objects.requireNonNull(player.getServer()).getCustomBossEvents().get(BAR_ID);
      if (bar != null) {
         bar.setColor(COLORS[colorIndex]);
         colorIndex = colorIndex + colorDir;
         if (colorIndex >= COLORS.length) {
            colorIndex = 0;
            colorDir = -1;
         } else if (colorIndex < 0) {
            colorIndex = 1;
            colorDir = 1;
         }
      }
   }

   public static String formatFullTime(int totalSeconds) {
      int days = totalSeconds / 86400;
      int hours = totalSeconds % 86400 / 3600;
      int minutes = totalSeconds % 3600 / 60;
      int seconds = totalSeconds % 60;
      if (days > 0) {
         return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
      } else {
         return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
      }
   }
}
