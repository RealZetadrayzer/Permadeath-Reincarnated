package zeta.org.permadeath_reincarnated.demonFight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class CrystalRespawnData extends SavedData {
   private static final String DATA_NAME = "permadeath_crystal_respawn";
   final Map<UUID, CrystalRespawnData.RespawnEntry> respawns = new HashMap<>();

   public CrystalRespawnData() {
   }

   public CrystalRespawnData(CompoundTag tag, Provider provider) {
      ListTag list = tag.getList("crystals", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag c = list.getCompound(i);
         UUID id = UUID.fromString(c.getString("id"));
         double x = c.getDouble("x");
         double y = c.getDouble("y");
         double z = c.getDouble("z");
         int ticks = c.getInt("ticks");
         this.respawns.put(id, new CrystalRespawnData.RespawnEntry(x, y, z, ticks));
      }
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag tag, @NotNull Provider provider) {
      ListTag list = new ListTag();

      for (Entry<UUID, CrystalRespawnData.RespawnEntry> e : this.respawns.entrySet()) {
         CompoundTag c = new CompoundTag();
         c.putString("id", e.getKey().toString());
         c.putDouble("x", e.getValue().x);
         c.putDouble("y", e.getValue().y);
         c.putDouble("z", e.getValue().z);
         c.putInt("ticks", e.getValue().ticksLeft);
         list.add(c);
      }

      tag.put("crystals", list);
      return tag;
   }

   public static CrystalRespawnData get(ServerLevel level) {
      return (CrystalRespawnData)level.getDataStorage()
         .computeIfAbsent(new Factory<>(CrystalRespawnData::new, CrystalRespawnData::new), "permadeath_crystal_respawn");
   }

   public void add(double x, double y, double z, int minTicks, int maxTicks) {
      int ticks = minTicks + (int)(Math.random() * (maxTicks - minTicks + 1));
      this.respawns.put(UUID.randomUUID(), new CrystalRespawnData.RespawnEntry(x, y, z, ticks));
      this.setDirty();
   }

   public void tick(ServerLevel level) {
      Iterator<Entry<UUID, CrystalRespawnData.RespawnEntry>> it = this.respawns.entrySet().iterator();

      while (it.hasNext()) {
         Entry<UUID, CrystalRespawnData.RespawnEntry> e = it.next();
         e.getValue().ticksLeft--;
         if (e.getValue().ticksLeft <= 0) {
            PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
            if (handler.isFightOn()) {
               EndCrystal crystal = (EndCrystal)EntityType.END_CRYSTAL.create(level);
               Scoreboard scoreboard = level.getScoreboard();
               PlayerTeam team = scoreboard.getPlayerTeam("demonFight");
               if (team == null) {
                  team = scoreboard.addPlayerTeam("demonFight");
                  team.setColor(ChatFormatting.LIGHT_PURPLE);
               }

               if (crystal != null) {
                  CrystalRespawnData.RespawnEntry entry = e.getValue();
                  crystal.moveTo(entry.x, entry.y, entry.z, 0.0F, 0.0F);
                  crystal.setShowBottom(true);
                  crystal.setGlowingTag(true);
                  crystal.addTag("demonFight");
                  scoreboard.addPlayerToTeam(crystal.getStringUUID(), team);
                  level.addFreshEntity(crystal);
                  spawnSphere(level, entry.x, entry.y, entry.z);
                  spawnSpiral(level, entry.x, entry.y, entry.z);
                  spawnChaos(level, entry.x, entry.y, entry.z);
                  spawnBurst(level, entry.x, entry.y, entry.z);
                  long seed = level.getRandom().nextLong();
                  level.getServer()
                     .getPlayerList()
                     .getPlayers()
                     .forEach(
                        player -> {
                           if (player.level() == level) {
                              player.connection
                                 .send(
                                    new ClientboundSoundPacket(
                                       BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.END_PORTAL_SPAWN),
                                       SoundSource.HOSTILE,
                                       entry.x,
                                       entry.y,
                                       entry.z,
                                       0.75F,
                                       0.7F,
                                       seed
                                    )
                                 );
                           }
                        }
                     );
               }

               it.remove();
               this.setDirty();
            }
         }
      }
   }

   private static <T extends ParticleOptions> void sendLongRangeParticles(
      ServerLevel level, T type, double x, double y, double z, int count, double dx, double dy, double dz, double speed
   ) {
      for (ServerPlayer player : level.getPlayers(p -> true)) {
         Packet<?> packet = new ClientboundLevelParticlesPacket(type, true, x, y, z, (float)dx, (float)dy, (float)dz, (float)speed, count);
         sendParticles(player, true, x, y, z, packet, level);
      }
   }

   private static void sendParticles(ServerPlayer player, boolean longDistance, double posX, double posY, double posZ, Packet<?> packet, ServerLevel level) {
      if (player.level() == level) {
         BlockPos blockPos = player.blockPosition();
         if (blockPos.closerToCenterThan(new Vec3(posX, posY, posZ), longDistance ? 512.0 : 32.0)) {
            player.connection.send(packet);
         }
      }
   }

   private static void spawnSphere(ServerLevel level, double x, double y, double z) {
      int points = 80;
      double radius = 1.5;

      for (int i = 0; i < points; i++) {
         double theta = Math.acos(2.0 * level.random.nextDouble() - 1.0);
         double phi = (Math.PI * 2) * level.random.nextDouble();
         double px = x + radius * Math.sin(theta) * Math.cos(phi);
         double py = y + radius * Math.cos(theta);
         double pz = z + radius * Math.sin(theta) * Math.sin(phi);
         sendLongRangeParticles(level, ParticleTypes.PORTAL, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private static void spawnSpiral(ServerLevel level, double x, double y, double z) {
      for (int i = 0; i < 40; i++) {
         double angle = i * 0.3;
         double radius = 0.7;
         double py = y + i * 0.05;
         double px = x + Math.cos(angle) * radius;
         double pz = z + Math.sin(angle) * radius;
         sendLongRangeParticles(level, ParticleTypes.END_ROD, px, py, pz, 1, 0.0, 0.02, 0.0, 0.0);
      }
   }

   private static void spawnChaos(ServerLevel level, double x, double y, double z) {
      sendLongRangeParticles(level, ParticleTypes.REVERSE_PORTAL, x, y + 1.0, z, 50, 0.8, 0.8, 0.8, 0.1);
      sendLongRangeParticles(level, ParticleTypes.SMOKE, x, y + 0.5, z, 20, 0.3, 0.1, 0.3, 0.01);
   }

   private static void spawnBurst(ServerLevel level, double x, double y, double z) {
      sendLongRangeParticles(level, ParticleTypes.EXPLOSION, x, y + 0.5, z, 1, 0.0, 0.0, 0.0, 0.0);
      sendLongRangeParticles(level, ParticleTypes.FLASH, x, y + 1.0, z, 1, 0.0, 0.0, 0.0, 0.0);
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      for (ServerLevel level : event.getServer().getAllLevels()) {
         CrystalRespawnData data = get(level);
         data.tick(level);
      }
   }

   static class RespawnEntry {
      double x;
      double y;
      double z;
      int ticksLeft;

      RespawnEntry(double x, double y, double z, int ticksLeft) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.ticksLeft = ticksLeft;
      }
   }
}
