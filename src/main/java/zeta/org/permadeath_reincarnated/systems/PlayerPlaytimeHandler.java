package zeta.org.permadeath_reincarnated.systems;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.systems.attachments.MinutesPlayedCountAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.MinutesPlayedCountAttachmentHelper;

@EventBusSubscriber
public class PlayerPlaytimeHandler {
   private static final int ACTIVE_GRACE_SECONDS = 60;
   private static final int STASIS_SECONDS = 60;
   private static final double WATER_RESET_DIST_SQR = 1.0;
   private static final double STASIS_RADIUS_SQR = 0.25;
   private static final int MIN_CHANGES_PER_MINUTE = 3;
   private static final int MIN_UNIQUE_SIGS_PER_MINUTE = 3;
   private static final int MIN_YAW_STEPS_PER_MINUTE = 3;
   private static final Map<UUID, Long> LAST_ACTIVE_SEC = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> WATER_ANCHOR_SEC = new ConcurrentHashMap<>();
   private static final Map<UUID, Vec3> WATER_ANCHOR_POS = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> RIDE_START_SEC = new ConcurrentHashMap<>();
   private static final Map<UUID, PlayerPlaytimeHandler.InputHistory> INPUT_HISTORY = new ConcurrentHashMap<>();

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            if (!level.isClientSide) {
               if (!player.getTags().contains("adminSpectator")) {
                  if (!player.isSpectator() && !player.isCreative()) {
                     if (player.tickCount % 20 == 0) {
                        long nowSec = level.getGameTime() / 20L;
                        UUID id = player.getUUID();
                        boolean rotated = player.yRotO != player.getYRot() || player.xRotO != player.getXRot();
                        boolean crouchSprint = player.isSprinting() || player.isCrouching();
                        boolean usingItem = player.isUsingItem();
                        boolean swinging = player.swingTime > 0;
                        double dx = player.getX() - player.xo;
                        double dy = player.getY() - player.yo;
                        double dz = player.getZ() - player.zo;
                        boolean moved = dx * dx + dy * dy + dz * dz > 1.0E-4;
                        boolean touchingWater = player.isInWaterOrBubble() || player.isInWater() || player.isEyeInFluid(FluidTags.WATER);
                        boolean ridingBoat = player.getVehicle() instanceof Boat;
                        boolean ridingMinecart = player.getVehicle() instanceof AbstractMinecart;
                        boolean ridingStasis = ridingBoat || ridingMinecart;
                        boolean movementCountsAsActivity = !touchingWater && !ridingStasis;
                        boolean activeThisSecond = rotated || crouchSprint || usingItem || swinging || movementCountsAsActivity && moved;
                        if (activeThisSecond) {
                           LAST_ACTIVE_SEC.put(id, nowSec);
                        }

                        int signature = 0;
                        if (rotated) {
                           signature |= 1;
                        }

                        if (player.isSprinting()) {
                           signature |= 2;
                        }

                        if (player.isCrouching()) {
                           signature |= 4;
                        }

                        if (usingItem) {
                           signature |= 8;
                        }

                        if (swinging) {
                           signature |= 16;
                        }

                        if (moved) {
                           signature |= 32;
                        }

                        if (touchingWater) {
                           signature |= 64;
                        }

                        if (ridingStasis) {
                           signature |= 128;
                        }

                        int yawQuant = (int)Math.floor((player.getYRot() % 360.0F + 360.0F) % 360.0F / 5.0F);
                        INPUT_HISTORY.computeIfAbsent(id, k -> new PlayerPlaytimeHandler.InputHistory()).push(signature, yawQuant);
                        boolean blockedByWaterStasis = false;
                        if (touchingWater) {
                           Vec3 anchor = WATER_ANCHOR_POS.get(id);
                           Long anchorSec = WATER_ANCHOR_SEC.get(id);
                           if (anchor != null && anchorSec != null) {
                              double distSqr = player.position().distanceToSqr(anchor);
                              if (distSqr > 1.0) {
                                 WATER_ANCHOR_POS.put(id, player.position());
                                 WATER_ANCHOR_SEC.put(id, nowSec);
                              } else if (nowSec - anchorSec >= 60L && distSqr <= 0.25) {
                                 blockedByWaterStasis = true;
                              }
                           } else {
                              WATER_ANCHOR_POS.put(id, player.position());
                              WATER_ANCHOR_SEC.put(id, nowSec);
                           }
                        } else {
                           WATER_ANCHOR_POS.remove(id);
                           WATER_ANCHOR_SEC.remove(id);
                        }

                        boolean blockedByRideStasis = false;
                        if (ridingStasis) {
                           RIDE_START_SEC.putIfAbsent(id, nowSec);
                           long rideStart = RIDE_START_SEC.get(id);
                           boolean humanInput = rotated || crouchSprint || usingItem || swinging;
                           if (humanInput) {
                              RIDE_START_SEC.put(id, nowSec);
                           } else if (nowSec - rideStart >= 60L) {
                              blockedByRideStasis = true;
                           }
                        } else {
                           RIDE_START_SEC.remove(id);
                        }

                        if (nowSec % 60L == 0L) {
                           Long lastActive = LAST_ACTIVE_SEC.get(id);
                           boolean activeRecently = lastActive != null && nowSec - lastActive <= 60L;
                           if (activeRecently) {
                              if (!blockedByWaterStasis) {
                                 if (!blockedByRideStasis) {
                                    PlayerPlaytimeHandler.InputHistory hist = INPUT_HISTORY.get(id);
                                    if (hist == null || !hist.looksMacroLike()) {
                                       int day = DayGlobalCount.CURRENT_DAY;
                                       MinutesPlayedCountAttachment playTime = MinutesPlayedCountAttachmentHelper.get(player);
                                       playTime.ensureInitialized(day);
                                       if (!playTime.completedWindow()) {
                                          playTime.tickMinute();
                                          PlayerPlaytimeDataHandler.get(level).putAttachment(level, id, playTime);
                                          if (playTime.completedWindow()) {
                                             player.displayClientMessage(
                                                Component.literal("¡Enhorabuena completaste las ")
                                                   .withStyle(ChatFormatting.YELLOW)
                                                   .append(Component.literal("10 horas").withStyle(ChatFormatting.GOLD))
                                                   .append(
                                                      Component.literal(" requeridas!").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false))
                                                   ),
                                                false
                                             );
                                             level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                                             level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_CELEBRATE, SoundSource.PLAYERS, 1.0F, 0.7F);
                                             level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.0, 0.0, 0.0, 0.5);
                                             level.sendParticles(
                                                ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.15, 0.15, 0.15, 0.05
                                             );
                                          }
                                       }
                                    }
                                 }
                              }
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
   public static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
      MinecraftServer server = event.getServer();
      if (server.getTickCount() % 20 == 0) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day >= 0) {
            int currentWindow = day / 10;

            for (ServerPlayer player : List.copyOf(server.getPlayerList().getPlayers())) {
               MinecraftServer world = player.server;
               if (world.isSingleplayer()) {
                  return;
               }

               if (!player.getTags().contains("adminSpectator") && !player.isSpectator() && !player.isCreative()) {
                  MinutesPlayedCountAttachment playTime = MinutesPlayedCountAttachmentHelper.get(player);
                  playTime.ensureInitialized(day);

                  while (playTime.getWindow() < currentWindow) {
                     boolean pardoned = player.getTags().contains("playtimePardoned");
                     int nextWindow = playTime.getWindow() + 1;
                     if (!playTime.completedWindow() && !pardoned) {
                        String reason = "§cHas sido PERMABANEADO por AFK";
                        player.connection.disconnect(Component.literal(reason));
                        server.getPlayerList()
                           .getBans()
                           .add(new UserBanListEntry(player.getGameProfile(), new Date(), "Permadeath: Reincarnated", null, reason));
                        break;
                     }

                     if (pardoned && !playTime.completedWindow()) {
                        playTime.forceCompleteWindow();
                     }

                     playTime.startNewWindow(nextWindow);
                     PlayerPlaytimeDataHandler.get(server.overworld()).putAttachment(server.overworld(), player.getUUID(), playTime);
                     player.displayClientMessage(Component.literal("Tu tiempo de juego se ha reseteado.").withStyle(ChatFormatting.YELLOW), false);
                     if (pardoned) {
                        player.removeTag("playtimePardoned");
                     }

                     awardForWindow(player, nextWindow);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLogout(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            int day = DayGlobalCount.CURRENT_DAY;
            MinutesPlayedCountAttachment playTime = MinutesPlayedCountAttachmentHelper.get(player);
            playTime.ensureInitialized(day);
            PlayerPlaytimeDataHandler.get(level).putAttachment(level, player.getUUID(), playTime);
         }

         UUID id = player.getUUID();
         LAST_ACTIVE_SEC.remove(id);
         WATER_ANCHOR_SEC.remove(id);
         WATER_ANCHOR_POS.remove(id);
         RIDE_START_SEC.remove(id);
         INPUT_HISTORY.remove(id);
      }
   }

   private static void awardForWindow(ServerPlayer player, int window) {
      switch (window) {
         case 1:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_10_ID);
            break;
         case 2:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_20_ID);
            break;
         case 3:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_30_ID);
            break;
         case 4:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_40_ID);
            break;
         case 5:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_50_ID);
            break;
         case 6:
            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ROOT_DAY_60_ID);
      }
   }

   private static final class InputHistory {
      private final int[] sig = new int[60];
      private final int[] yawStep = new int[60];
      private int idx = 0;
      private int filled = 0;

      void push(int signature, int yawQuant) {
         this.sig[this.idx] = signature;
         this.yawStep[this.idx] = yawQuant;
         this.idx = (this.idx + 1) % 60;
         if (this.filled < 60) {
            this.filled++;
         }
      }

      boolean ready() {
         return this.filled >= 60;
      }

      int changesLastMinute() {
         if (!this.ready()) {
            return 60;
         }

         int changes = 0;
         int prev = this.sig[(this.idx + 59) % 60];

         for (int i = 58; i >= 0; i--) {
            int cur = this.sig[(this.idx + i) % 60];
            if (cur != prev) {
               changes++;
            }

            prev = cur;
         }

         return changes;
      }

      int uniqueSigsLastMinute() {
         if (!this.ready()) {
            return 60;
         }

         int uniq = 0;

         label28:
         for (int i = 0; i < 60; i++) {
            int a = this.sig[(this.idx + i) % 60];

            for (int j = 0; j < i; j++) {
               int b = this.sig[(this.idx + j) % 60];
               if (a == b) {
                  continue label28;
               }
            }

            uniq++;
         }

         return uniq;
      }

      int yawStepsLastMinute() {
         if (!this.ready()) {
            return 60;
         }

         int uniq = 0;

         label28:
         for (int i = 0; i < 60; i++) {
            int a = this.yawStep[(this.idx + i) % 60];

            for (int j = 0; j < i; j++) {
               int b = this.yawStep[(this.idx + j) % 60];
               if (a == b) {
                  continue label28;
               }
            }

            uniq++;
         }

         return uniq;
      }

      boolean looksMacroLike() {
         if (!this.ready()) {
            return false;
         }

         int changes = this.changesLastMinute();
         int uniq = this.uniqueSigsLastMinute();
         int yawUniq = this.yawStepsLastMinute();
         return changes < 3 || uniq < 3 || yawUniq < 3;
      }
   }
}
