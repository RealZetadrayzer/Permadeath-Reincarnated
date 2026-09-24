package zeta.org.permadeath_reincarnated.systems.attachments;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class EnhancedTCNDCooldownAttachment implements INBTSerializable<CompoundTag> {
   private static final int MAX_SECONDS = 420;
   private int remainingTicks = 0;
   private int oldRemainingTicks = 0;

   public boolean isOnCooldown() {
      return this.remainingTicks > 0;
   }

   public void startCooldown() {
      this.remainingTicks = 420;
   }

   public void tick() {
      if (this.remainingTicks > 0) {
         this.remainingTicks--;
      }
   }

   public int getRemainingTicks() {
      return this.remainingTicks;
   }

   public boolean isDirty() {
      return this.remainingTicks != this.oldRemainingTicks;
   }

   public void setClean() {
      this.oldRemainingTicks = this.remainingTicks;
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putInt("RemainingTicks", this.remainingTicks);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.remainingTicks = nbt.getInt("RemainingTicks");
      this.oldRemainingTicks = this.remainingTicks;
   }
}
