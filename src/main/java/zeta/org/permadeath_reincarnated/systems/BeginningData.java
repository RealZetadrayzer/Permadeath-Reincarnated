package zeta.org.permadeath_reincarnated.systems;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;

public class BeginningData extends SavedData {
   private static final String DATA_NAME = "permadeath_beginning";
   public boolean portalGenerated;
   public boolean blessingClaimed;
   public boolean curseClaimed;
   public UUID firstPlayer;
   public UUID lastPlayer;
   public final Set<UUID> alreadyEntered;

   public BeginningData() {
      this.portalGenerated = false;
      this.blessingClaimed = false;
      this.curseClaimed = false;
      this.firstPlayer = null;
      this.lastPlayer = null;
      this.alreadyEntered = new HashSet<>();
   }

   public BeginningData(CompoundTag tag, @NotNull Provider provider) {
      this.portalGenerated = tag.getBoolean("PortalGenerated");
      this.blessingClaimed = tag.getBoolean("BlessingClaimed");
      this.curseClaimed = tag.getBoolean("CurseClaimed");
      if (tag.contains("FirstPlayer")) {
         this.firstPlayer = UUID.fromString(tag.getString("FirstPlayer"));
      }

      if (tag.contains("LastPlayer")) {
         this.lastPlayer = UUID.fromString(tag.getString("LastPlayer"));
      }

      ListTag list = tag.getList("AlreadyEntered", 8);
      this.alreadyEntered = new HashSet<>();

      for (int i = 0; i < list.size(); i++) {
         this.alreadyEntered.add(UUID.fromString(list.getString(i)));
      }
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, @NotNull Provider provider) {
      tag.putBoolean("PortalGenerated", this.portalGenerated);
      tag.putBoolean("BlessingClaimed", this.blessingClaimed);
      tag.putBoolean("CurseClaimed", this.curseClaimed);
      if (this.firstPlayer != null) {
         tag.putString("FirstPlayer", this.firstPlayer.toString());
      }

      if (this.lastPlayer != null) {
         tag.putString("LastPlayer", this.lastPlayer.toString());
      }

      ListTag list = new ListTag();

      for (UUID uuid : this.alreadyEntered) {
         list.add(StringTag.valueOf(uuid.toString()));
      }

      tag.put("AlreadyEntered", list);
      return tag;
   }

   public static BeginningData get(ServerLevel level) {
      return (BeginningData)level.getDataStorage().computeIfAbsent(new Factory<>(BeginningData::new, BeginningData::new), "permadeath_beginning");
   }

   public void markEntered(UUID playerUUID) {
      this.alreadyEntered.add(playerUUID);
      this.setDirty();
   }

   public boolean hasEntered(UUID playerUUID) {
      return this.alreadyEntered.contains(playerUUID);
   }

   public UUID getNextLastPlayer(Set<UUID> allPlayers) {
      for (UUID uuid : allPlayers) {
         if (!this.alreadyEntered.contains(uuid)) {
            return uuid;
         }
      }

      return null;
   }

   public Set<UUID> getEnteredPlayers() {
      return new HashSet<>(this.alreadyEntered);
   }
}
