package zeta.org.permadeath_reincarnated.systems.attachments;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

public class BurnedTotemCountAttachment implements INBTSerializable<CompoundTag> {
   public static final int TOTEMS_REQUIRED = (Integer)PermadeathConfig.SURVIVOR_MEDAL_TOTEMS_REQUIRED.get();
   private int burnedTotems = 0;
   private boolean completed = false;
   private int oldBurnedTotems = 0;
   private boolean oldCompleted = false;

   public void addBurnedTotem() {
      if (!this.completed) {
         this.burnedTotems++;
      }
   }

   public int getBurnedTotems() {
      return this.burnedTotems;
   }

   public boolean isComplete() {
      return this.completed;
   }

   public boolean isReadyToComplete() {
      return !this.completed && this.burnedTotems >= TOTEMS_REQUIRED;
   }

   public void complete() {
      this.completed = true;
   }

   public boolean isDirty() {
      return this.burnedTotems != this.oldBurnedTotems || this.completed != this.oldCompleted;
   }

   public void setClean() {
      this.oldBurnedTotems = this.burnedTotems;
      this.oldCompleted = this.completed;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("BurnedTotems", this.burnedTotems);
      tag.putBoolean("Completed", this.completed);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.burnedTotems = nbt.getInt("BurnedTotems");
      this.completed = nbt.getBoolean("Completed");
      this.oldBurnedTotems = this.burnedTotems;
      this.oldCompleted = this.completed;
   }
}
