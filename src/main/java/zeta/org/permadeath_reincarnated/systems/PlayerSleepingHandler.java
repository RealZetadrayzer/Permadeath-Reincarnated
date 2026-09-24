package zeta.org.permadeath_reincarnated.systems;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.timers.DeathTrainTimer;

@EventBusSubscriber
public class PlayerSleepingHandler {
   private static final Random RANDOM = new Random();
   public static final Map<UUID, Integer> HIDE_TIMER = new HashMap<>();

   private static int requiredSleepers(ServerLevel level) {
      int day = DeathTrainTimer.get(level).day;
      return day >= 10 ? 4 : 1;
   }

   private static boolean isNight(ServerLevel level) {
      long time = level.getDayTime() % 24000L;
      return time >= 12541L && time <= 23458L;
   }

   private static String formatSleepingNames(ServerLevel level) {
      List<String> names = level.players().stream().filter(Player::isSleepingLongEnough).map(player -> player.getGameProfile().getName()).toList();
      int size = names.size();
      if (size == 0) {
         return "";
      }

      if (size == 1) {
         return names.get(0);
      }

      if (size == 2) {
         return names.get(0) + " y " + names.get(1);
      }

      StringBuilder builder = new StringBuilder();

      for (int index = 0; index < size; index++) {
         if (index > 0) {
            if (index == size - 1) {
               builder.append(" y ");
            } else {
               builder.append(", ");
            }
         }

         builder.append(names.get(index));
      }

      return builder.toString();
   }

   private static String pickSkipperName(ServerLevel level) {
      return level.players()
         .stream()
         .filter(player -> !player.isSpectator())
         .filter(Player::isSleepingLongEnough)
         .map(player -> player.getGameProfile().getName())
         .findFirst()
         .orElse("");
   }

   @SubscribeEvent
   public static void onSleepAttempt(CanPlayerSleepEvent event) {
      Player player = event.getEntity();
      if (!player.level().isClientSide) {
         if (player.level() instanceof ServerLevel level) {
            if (level.dimension().equals(level.getServer().overworld().dimension())) {
               DeathTrainTimer timer = DeathTrainTimer.get(level);
               boolean deathTrainActive = timer.totalSeconds > 0 && timer.finished == 1;
               int day = DayGlobalCount.CURRENT_DAY;
               if (day >= 0 && day < 20) {
                  if (deathTrainActive && !isNight(level)) {
                     event.setProblem(BedSleepingProblem.NOT_POSSIBLE_HERE);
                     player.displayClientMessage(Component.literal("§7No puedes dormir durante el Death Train hasta que sea de noche."), true);
                     player.addTag("hide");
                     HIDE_TIMER.put(player.getUUID(), 40);
                  }
               } else {
                  if (day >= 20) {
                     BlockPos bed = event.getPos();
                     long seed = player.level().getRandom().nextLong();
                     int chance = 1 + RANDOM.nextInt(100);
                     int chanceThreshold = day >= 55 && PermadeathConfig.CUSTOM_CHANGES.get() ? 1 : 10;
                     event.setProblem(BedSleepingProblem.NOT_POSSIBLE_HERE);
                     player.addTag("hide");
                     level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, bed.getX(), bed.getY(), bed.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                     player.level().playSound(null, bed.getX(), bed.getY(), bed.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
                     PlayerAdvancementsHandler.award((ServerPlayer)player, PlayerAdvancementsHandler.PLAYER_INSOMNIA_ID);
                     if (day < 50 || chance <= chanceThreshold) {
                        player.displayClientMessage(Component.literal("§7¡Tu contador de §dPhantoms §7fue reseteado!"), true);
                        HIDE_TIMER.put(player.getUUID(), 40);
                        ((ServerPlayer)player)
                           .connection
                           .send(
                              new ClientboundSoundPacket(
                                 BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PHANTOM_BITE),
                                 SoundSource.VOICE,
                                 player.getX(),
                                 player.getY(),
                                 player.getZ(),
                                 1.0F,
                                 0.8F,
                                 seed
                              )
                           );
                        if (player instanceof ServerPlayer serverPlayer) {
                           serverPlayer.getStats().setValue(serverPlayer, Stats.CUSTOM.get(Stats.TIME_SINCE_REST), 0);
                           if (day >= 55) {
                              PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_PHANTOM_ROULETTE_ID);

                              for (int i = 0; i < 4; i++) {
                                 Phantom phantom = (Phantom)EntityType.PHANTOM.create(level);
                                 if (phantom != null) {
                                    ((MobSpawnTypeAccessor)phantom).setSpawnType(MobSpawnType.MOB_SUMMONED);
                                    phantom.moveTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
                                    level.addFreshEntity(phantom);
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
   public static void onSleepFinished(SleepFinishedTimeEvent event) {
      ServerLevel level = (ServerLevel)event.getLevel();
      if (!level.isClientSide) {
         if (level.dimension().equals(level.getServer().overworld().dimension())) {
            DeathTrainTimer timer = DeathTrainTimer.get(level);
            int required = requiredSleepers(level);
            long sleepingCount = level.players().stream().filter(LivingEntity::isSleeping).count();
            if (sleepingCount >= required) {
               String message;
               if (timer.day >= 10 && timer.day < 20) {
                  String names = formatSleepingNames(level);
                  if (names.isEmpty()) {
                     return;
                  }

                  message = "§6" + names + " §efueron a dormir.";
               } else {
                  String skipperName = pickSkipperName(level);
                  if (skipperName.isEmpty()) {
                     return;
                  }

                  message = "§6" + skipperName + " §efue a dormir.";
               }

               for (ServerPlayer player : level.players()) {
                  player.sendSystemMessage(Component.literal(message));
               }

               long dayTime = level.getDayTime();
               long nextMorning = (dayTime / 24000L + 1L) * 24000L;
               event.setTimeAddition(nextMorning);
               if (timer.totalSeconds <= 0 || timer.finished == 2) {
                  level.setWeatherParameters(0, 0, false, false);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerTickBedMessage(Post event) {
      Iterator<Entry<UUID, Integer>> iterator = HIDE_TIMER.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<UUID, Integer> entry = iterator.next();
         int ticksLeft = entry.getValue() - 1;
         ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
         if (ticksLeft <= 0) {
            if (player != null) {
               player.removeTag("hide");
            }

            iterator.remove();
         } else {
            entry.setValue(ticksLeft);
         }
      }
   }

   @SubscribeEvent
   public static void onServerTickSleepProgress(Post event) {
      if (event.getServer().getTickCount() % 10 == 0) {
         ServerLevel level = event.getServer().overworld();
         DeathTrainTimer timer = DeathTrainTimer.get(level);
         if (timer.day >= 10 && timer.day < 20) {
            if (!isNight(level)) {
               return;
            }

            int required = requiredSleepers(level);
            long sleepingCount = level.players().stream().filter(LivingEntity::isSleeping).count();
            if (sleepingCount <= 0L) {
               return;
            }

            int remaining = required - (int)sleepingCount;
            Component bar;
            if (remaining <= 0) {
               bar = Component.literal("§eSaltando la noche...");
            } else if (remaining == 1) {
               bar = Component.literal("§eSe necesita §6un jugador §emás durmiendo para saltar la noche");
            } else {
               bar = Component.literal("§eSe necesitan §6" + remaining + " §ejugadores más durmiendo para saltar la noche");
            }

            for (ServerPlayer player : level.players()) {
               if (player.isSleeping()) {
                  player.displayClientMessage(bar, true);
               }
            }
         }
      }
   }
}
