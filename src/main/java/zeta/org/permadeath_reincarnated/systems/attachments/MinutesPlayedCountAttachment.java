package zeta.org.permadeath_reincarnated.systems.attachments;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class MinutesPlayedCountAttachment implements INBTSerializable<CompoundTag> {
   private static final int MINUTES_REQUIRED = 600;
   private int window = 0;
   private int minutesPlayed = 0;
   private boolean completedWindow = false;

   public int getWindow() {
      return this.window;
   }

   public boolean completedWindow() {
      return this.completedWindow;
   }

   public void tickMinute() {
      if (!this.completedWindow) {
         this.minutesPlayed++;
         if (this.minutesPlayed >= 600) {
            this.minutesPlayed = 600;
            this.completedWindow = true;
         }
      }
   }

   public void startNewWindow(int newWindow) {
      this.window = newWindow;
      this.minutesPlayed = 0;
      this.completedWindow = false;
   }

   public int getMinutesPlayed() {
      return this.minutesPlayed;
   }

   public int getMinutesRequired() {
      return 600;
   }

   public void forceCompleteWindow() {
      this.minutesPlayed = 600;
      this.completedWindow = true;
   }

   public void ensureInitialized(int currentDay) {
      if (this.window < 0) {
         this.window = 0;
      }

      if (this.minutesPlayed < 0) {
         this.minutesPlayed = 0;
      }

      if (this.minutesPlayed > 600) {
         this.minutesPlayed = 600;
      }

      if (this.minutesPlayed == 600) {
         this.completedWindow = true;
      }
   }

   public CompoundTag serializeNBT(@NotNull Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("Window", this.window);
      tag.putInt("MinutesPlayed", this.minutesPlayed);
      tag.putBoolean("CompletedWindow", this.completedWindow);
      return tag;
   }

   public void deserializeNBT(@NotNull Provider provider, CompoundTag nbt) {
      this.window = nbt.getInt("Window");
      this.minutesPlayed = nbt.getInt("MinutesPlayed");
      this.completedWindow = nbt.getBoolean("CompletedWindow");
      if (this.window < 0) {
         this.window = 0;
      }

      if (this.minutesPlayed < 0) {
         this.minutesPlayed = 0;
      }

      if (this.minutesPlayed > 600) {
         this.minutesPlayed = 600;
      }

      if (this.minutesPlayed == 600) {
         this.completedWindow = true;
      }
   }
}
