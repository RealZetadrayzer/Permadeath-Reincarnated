package zeta.org.permadeath_reincarnated.systems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;
import zeta.org.permadeath_reincarnated.systems.attachments.MinutesPlayedCountAttachment;

public class PlayerPlaytimeDataHandler extends SavedData {
   private static final String DATA_NAME = "permadeath_playtime";
   private final Map<UUID, CompoundTag> playerNbt = new HashMap<>();

   public static PlayerPlaytimeDataHandler get(ServerLevel level) {
      ServerLevel overworld = level.getServer().overworld();
      return (PlayerPlaytimeDataHandler)overworld.getDataStorage()
         .computeIfAbsent(new Factory<>(PlayerPlaytimeDataHandler::new, PlayerPlaytimeDataHandler::load), "permadeath_playtime");
   }

   public void put(UUID id, CompoundTag attachmentTag) {
      this.playerNbt.put(id, attachmentTag);
      this.setDirty();
   }

   public CompoundTag get(UUID id) {
      return this.playerNbt.get(id);
   }

   public static PlayerPlaytimeDataHandler load(CompoundTag tag, @NotNull Provider provider) {
      PlayerPlaytimeDataHandler data = new PlayerPlaytimeDataHandler();
      CompoundTag players = tag.getCompound("Players");

      for (String key : players.getAllKeys()) {
         try {
            UUID id = UUID.fromString(key);
            data.playerNbt.put(id, players.getCompound(key));
         } catch (IllegalArgumentException var7) {
         }
      }

      return data;
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, @NotNull Provider provider) {
      CompoundTag players = new CompoundTag();

      for (Entry<UUID, CompoundTag> e : this.playerNbt.entrySet()) {
         players.put(e.getKey().toString(), (Tag)e.getValue());
      }

      tag.put("Players", players);
      return tag;
   }

   public void putAttachment(ServerLevel level, UUID id, MinutesPlayedCountAttachment a) {
      Provider provider = level.registryAccess();
      this.put(id, a.serializeNBT(provider));
   }
}
