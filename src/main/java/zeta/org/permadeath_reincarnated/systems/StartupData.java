package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;

public class StartupData extends SavedData {
   private static final String DATA_NAME = "permadeath_startup_runned";
   private boolean startupRunned = false;
   private boolean startupOwnersPermission = false;
   private boolean startupBeginningPortalGenerated = false;

   public StartupData() {
   }

   public StartupData(CompoundTag tag, Provider provider) {
      this.startupRunned = tag.getBoolean("startup_runned");
      this.startupOwnersPermission = tag.getBoolean("startup_owner_permission");
      this.startupBeginningPortalGenerated = tag.getBoolean("startup_beginning_portal_generated");
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag tag, @NotNull Provider provider) {
      tag.putBoolean("startup_runned", this.startupRunned);
      tag.putBoolean("startup_owner_permission", this.startupOwnersPermission);
      tag.putBoolean("startup_beginning_portal_generated", this.startupBeginningPortalGenerated);
      return tag;
   }

   public boolean isStartupRunned() {
      return this.startupRunned;
   }

   public boolean didOwnersGavePermission() {
      return this.startupOwnersPermission;
   }

   public boolean didBegPortalGenerate() {
      return this.startupBeginningPortalGenerated;
   }

   public void setStartupRunned(boolean value) {
      this.startupRunned = value;
      this.setDirty();
   }

   public void setBegPortalGenerated(boolean value) {
      this.startupBeginningPortalGenerated = value;
      this.setDirty();
   }

   public void setStartupOwnersPermission(boolean value) {
      this.startupOwnersPermission = value;
      this.setDirty();
   }

   public static StartupData get(ServerLevel level) {
      return (StartupData)level.getDataStorage().computeIfAbsent(new Factory<>(StartupData::new, StartupData::new), "permadeath_startup_runned");
   }

   public static StartupData get(Level level) {
      return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
   }
}
