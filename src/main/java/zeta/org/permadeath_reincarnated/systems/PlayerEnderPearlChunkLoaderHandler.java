package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

@EventBusSubscriber
public class PlayerEnderPearlChunkLoaderHandler {
   public static final TicketType<Integer> ENDER_PEARL_TICKET = TicketType.create("permadeath_ender_pearl", Integer::compareTo, 40);

   @SubscribeEvent
   public static void onPearlTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 30) {
            if (event.getEntity() instanceof ThrownEnderpearl pearl) {
               if (pearl.level() instanceof ServerLevel level) {
                  if (pearl.getOwner() instanceof ServerPlayer) {
                     CompoundTag data = pearl.getPersistentData();
                     if (!data.contains("permadeath_reincarnated:ender_pearl_age_ticks")) {
                        data.putLong("permadeath_reincarnated:ender_pearl_age_ticks", level.getGameTime());
                     }

                     long spawnTick = data.getLong("permadeath_reincarnated:ender_pearl_age_ticks");
                     long age = level.getGameTime() - spawnTick;
                     if (age >= 6000L) {
                        pearl.discard();
                     } else if (level.getGameTime() % 20L == 0L) {
                        ChunkPos chunkPos = new ChunkPos(pearl.blockPosition());
                        int radius = 1;
                        int ticketLevel = 2;

                        for (int dx = -radius; dx <= radius; dx++) {
                           for (int dz = -radius; dz <= radius; dz++) {
                              ChunkPos pos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                              level.getChunkSource().addRegionTicket(ENDER_PEARL_TICKET, pos, ticketLevel, pearl.getId());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPearlRemove(EntityLeaveLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 30) {
            if (event.getEntity() instanceof ThrownEnderpearl pearl) {
               if (event.getLevel() instanceof ServerLevel level) {
                  if (pearl.getOwner() instanceof ServerPlayer) {
                     ChunkPos chunkPos = new ChunkPos(pearl.blockPosition());
                     int radius = 1;
                     int ticketLevel = 2;

                     for (int dx = -radius; dx <= radius; dx++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                           ChunkPos pos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                           level.getChunkSource().removeRegionTicket(ENDER_PEARL_TICKET, pos, ticketLevel, pearl.getId());
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
