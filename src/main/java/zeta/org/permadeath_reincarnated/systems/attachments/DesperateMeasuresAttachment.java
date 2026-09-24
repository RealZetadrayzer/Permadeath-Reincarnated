package zeta.org.permadeath_reincarnated.systems.attachments;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class DesperateMeasuresAttachment implements INBTSerializable<CompoundTag> {
   private static final int REQUIRED_TICKS = 300;
   private int progressTicks = 0;
   private boolean completed = false;
   private int oldProgressTicks = 0;
   private boolean oldCompleted = false;

   public void tickProgress() {
      if (!this.completed) {
         this.progressTicks++;
      }
   }

   public void reset() {
      if (!this.completed) {
         this.progressTicks = 0;
      }
   }

   public int getProgressTicks() {
      return this.progressTicks;
   }

   public boolean isComplete() {
      return this.completed;
   }

   public boolean isReadyToComplete() {
      return !this.completed && this.progressTicks >= 300;
   }

   public void complete() {
      this.completed = true;
   }

   public boolean isDirty() {
      return this.progressTicks != this.oldProgressTicks || this.completed != this.oldCompleted;
   }

   public void setClean() {
      this.oldProgressTicks = this.progressTicks;
      this.oldCompleted = this.completed;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("ProgressTicks", this.progressTicks);
      tag.putBoolean("Completed", this.completed);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.progressTicks = nbt.getInt("ProgressTicks");
      this.completed = nbt.getBoolean("Completed");
      this.oldProgressTicks = this.progressTicks;
      this.oldCompleted = this.completed;
   }
}
