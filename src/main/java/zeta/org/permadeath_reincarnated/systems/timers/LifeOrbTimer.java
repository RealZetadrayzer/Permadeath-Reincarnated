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

public class LifeOrbTimer extends SavedData {
   private static final String DATA_NAME = "permadeath_life_orb";
   public static final ResourceLocation BAR_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "life_orb_timer");
   public int totalSeconds = 0;
   public int state = 0;
   private final Set<ServerPlayer> players = new HashSet<>();
   private static final BossBarColor[] COLORS = new BossBarColor[]{
      BossBarColor.BLUE, BossBarColor.GREEN, BossBarColor.PINK, BossBarColor.PURPLE, BossBarColor.RED, BossBarColor.WHITE, BossBarColor.YELLOW
   };
   private static int colorIndex = 0;
   private static int colorDir = 1;

   public LifeOrbTimer() {
   }

   public LifeOrbTimer(CompoundTag tag, Provider provider) {
      this.totalSeconds = tag.getInt("RemainingSeconds");
      this.state = tag.getInt("State");
   }

   public CompoundTag save(CompoundTag tag, Provider provider) {
      tag.putInt("RemainingSeconds", this.totalSeconds);
      tag.putInt("State", this.state);
      return tag;
   }

   public static LifeOrbTimer get(ServerLevel world) {
      return (LifeOrbTimer)world.getDataStorage().computeIfAbsent(new Factory<>(LifeOrbTimer::new, LifeOrbTimer::new), "permadeath_life_orb");
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
            Component.literal(formatFullTime(this.totalSeconds) + " para obtener la Orbe de Vida")
               .withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
         );
      }
   }

   public static void updateAllPlayersBossBar(ServerLevel world) {
      LifeOrbTimer timer = get(world);

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
            colorIndex = COLORS.length - 2;
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
