package zeta.org.permadeath_reincarnated.demonFight;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermadeathDemonFightHandler extends SavedData {
   private static final String DATA_NAME = "permadeath_demon_fight";
   private boolean fightOn = false;
   private boolean previouslyKilled = false;
   private UUID demonDragonUUID;
   private final List<CompoundTag> foughtDemon = new ArrayList<>();

   public PermadeathDemonFightHandler() {
   }

   public PermadeathDemonFightHandler(CompoundTag tag, Provider provider) {
      this.fightOn = tag.getBoolean("fightOn");
      this.previouslyKilled = tag.getBoolean("previouslyKilled");
      if (tag.hasUUID("dragon")) {
         this.demonDragonUUID = tag.getUUID("dragon");
      }

      if (tag.contains("foughtDemon", 9)) {
         ListTag list = tag.getList("foughtDemon", 10);

         for (int i = 0; i < list.size(); i++) {
            this.foughtDemon.add(list.getCompound(i));
         }
      }
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag tag, @NotNull Provider provider) {
      tag.putBoolean("fightOn", this.fightOn);
      tag.putBoolean("previouslyKilled", this.previouslyKilled);
      if (this.demonDragonUUID != null) {
         tag.putUUID("dragon", this.demonDragonUUID);
      }

      ListTag list = new ListTag();

      for (CompoundTag entry : this.foughtDemon) {
         list.add(entry);
      }

      tag.put("foughtDemon", list);
      return tag;
   }

   public void setWasPreviouslyKilled(Boolean value) {
      this.previouslyKilled = value;
      this.setDirty();
   }

   public boolean wasPreviouslyKilled() {
      return this.previouslyKilled;
   }

   public void startFight() {
      this.fightOn = true;
      this.setDirty();
   }

   public void endFight() {
      this.fightOn = false;
      this.setDirty();
   }

   public boolean isFightOn() {
      return this.fightOn;
   }

   public void setDragon(EnderDragon dragon) {
      this.demonDragonUUID = dragon.getUUID();
      this.setDirty();
   }

   public void clearDragon() {
      this.demonDragonUUID = null;
      this.setDirty();
   }

   public void addPlayer(ServerPlayer player) {
      if (!this.hasPlayer(player.getUUID())) {
         CompoundTag entry = new CompoundTag();
         entry.putString("name", player.getGameProfile().getName());
         entry.putUUID("uuid", player.getUUID());
         this.foughtDemon.add(entry);
         this.setDirty();
      }
   }

   public boolean hasPlayer(UUID uuid) {
      for (CompoundTag tag : this.foughtDemon) {
         if (tag.getUUID("uuid").equals(uuid)) {
            return true;
         }
      }

      return false;
   }

   public List<CompoundTag> getFighters() {
      return this.foughtDemon;
   }

   public void resetFighters() {
      this.foughtDemon.clear();
      this.setDirty();
   }

   @Nullable
   public EnderDragon getDragon(ServerLevel level) {
      if (this.demonDragonUUID == null) {
         return null;
      } else {
         return level.getEntity(this.demonDragonUUID) instanceof EnderDragon dragon ? dragon : null;
      }
   }

   public static PermadeathDemonFightHandler get(ServerLevel level) {
      return (PermadeathDemonFightHandler)level.getDataStorage()
         .computeIfAbsent(new Factory<>(PermadeathDemonFightHandler::new, PermadeathDemonFightHandler::new), "permadeath_demon_fight");
   }

   public static PermadeathDemonFightHandler getIfEnd(Level level) {
      return level instanceof ServerLevel serverLevel ? get(serverLevel) : null;
   }
}
