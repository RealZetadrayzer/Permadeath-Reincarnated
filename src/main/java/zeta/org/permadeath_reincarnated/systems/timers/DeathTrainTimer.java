package zeta.org.permadeath_reincarnated.systems.timers;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;

public class DeathTrainTimer extends SavedData {
   public int finished = 0;
   public int day;
   public int totalSeconds;
   private static final String DATA_NAME = "permadeath_deathtrain";

   public DeathTrainTimer() {
      this.day = 0;
      this.totalSeconds = 0;
      this.finished = 0;
   }

   public DeathTrainTimer(CompoundTag tag, @NotNull Provider provider) {
      this.day = tag.getInt("day");
      this.totalSeconds = tag.getInt("totalSeconds");
      this.finished = tag.getInt("finished");
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, @NotNull Provider provider) {
      tag.putInt("day", this.day);
      tag.putInt("totalSeconds", this.totalSeconds);
      tag.putInt("finished", this.finished);
      return tag;
   }

   public static DeathTrainTimer get(ServerLevel world) {
      return (DeathTrainTimer)world.getDataStorage().computeIfAbsent(new Factory<>(DeathTrainTimer::new, DeathTrainTimer::new), "permadeath_deathtrain");
   }

   public static int addTime(ServerLevel world, int day) {
      DeathTrainTimer data = get(world);
      data.finished = 1;
      int dayInWindow = day % 25;
      int secondsToAdd;
      if (day < 50) {
         secondsToAdd = (dayInWindow + 1) * 3600;
      } else {
         secondsToAdd = 3600 + dayInWindow * 1800;
      }

      data.totalSeconds += secondsToAdd;
      data.setDirty();
      return secondsToAdd;
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

   public static String formatAddedTime(int totalSeconds) {
      int days = totalSeconds / 86400;
      int hours = totalSeconds % 86400 / 3600;
      int minutes = totalSeconds % 3600 / 60;
      int seconds = totalSeconds % 60;
      List<String> parts = new ArrayList<>();
      if (days > 0) {
         parts.add(days + (days == 1 ? " día" : " días"));
      }

      if (hours > 0) {
         parts.add(hours + (hours == 1 ? " hora" : " horas"));
      }

      if (minutes > 0) {
         parts.add(minutes + (minutes == 1 ? " minuto" : " minutos"));
      }

      if (seconds > 0) {
         parts.add(seconds + (seconds == 1 ? " segundo" : " segundos"));
      }

      if (parts.isEmpty()) {
         return "0 segundos";
      }

      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < parts.size(); i++) {
         if (i > 0) {
            if (i == parts.size() - 1) {
               sb.append(" y ");
            } else {
               sb.append(" ");
            }
         }

         sb.append(parts.get(i));
      }

      return sb.toString();
   }

   public static void display(ServerLevel world) {
      DeathTrainTimer data = get(world);
      String numeric = formatFullTime(data.totalSeconds);
      String message = "§7Quedan " + numeric + " de tormenta";
      world.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (!player.getTags().contains("hide")) {
            if (data.day < 10 || !player.isSleeping()) {
               player.displayClientMessage(Component.literal(message), true);
            }
         }
      });
   }
}
