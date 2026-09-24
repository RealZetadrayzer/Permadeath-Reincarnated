package zeta.org.permadeath_reincarnated.systems.timers;

import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerSleepingHandler;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

public class DeathTrainTimerGlobalHandler {
   private static int globalDay = 0;
   private static int globalTotalSeconds = 0;
   private static int globalFinished = 0;

   private static void syncAll(ServerLevel reference) {
      for (ServerLevel world : reference.getServer().getAllLevels()) {
         DeathTrainTimer timer = DeathTrainTimer.get(world);
         timer.day = globalDay;
         timer.totalSeconds = globalTotalSeconds;
         timer.finished = globalFinished;
         timer.setDirty();
      }
   }

   public static int getDay() {
      return globalDay;
   }

   public static void setDay(ServerLevel reference, int day) {
      globalDay = day;
      syncAll(reference);
   }

   public static void addDay(ServerLevel reference, int add) {
      globalDay += add;
      syncAll(reference);
   }

   public static int getTotalSeconds() {
      return globalTotalSeconds;
   }

   public static int getCurrentState() {
      return globalFinished;
   }

   public static void setTime(ServerLevel reference, int seconds) {
      globalTotalSeconds = seconds;
      globalFinished = seconds > 0 ? 1 : 2;
      syncAll(reference);
   }

   public static void addTime(ServerLevel reference, int seconds) {
      globalTotalSeconds += seconds;
      if (globalTotalSeconds > 0) {
         globalFinished = 1;
      }

      syncAll(reference);
   }

   public static void tick(ServerLevel reference) {
      if (reference.getServer().getTickCount() % 20 == 0) {
         ResourceKey<Level> BEGINNING = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "the_beginning")
         );
         ServerLevel beginning = reference.getServer().getLevel(BEGINNING);
         boolean justFinished = false;
         if (globalTotalSeconds > 0) {
            globalTotalSeconds--;
         }

         if (globalTotalSeconds <= 0 && globalFinished == 1) {
            globalFinished = 2;
            justFinished = true;
         }

         for (ServerLevel world : reference.getServer().getAllLevels()) {
            DeathTrainTimer timer = DeathTrainTimer.get(world);
            timer.day = globalDay;
            timer.totalSeconds = globalTotalSeconds;
            timer.finished = globalFinished;
            timer.setDirty();
            if (globalFinished == 1 && globalTotalSeconds > 0) {
               ((BooleanValue)world.getServer().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, world.getServer());
               world.setWeatherParameters(0, 40, true, true);
               if (globalDay >= 25) {
                  int amplifier = Math.min((globalDay - 25) / 25, 3);
                  world.getAllEntities().forEach(e -> {
                     if (e instanceof LivingEntity entity && !(entity instanceof Player)) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, amplifier, false, true));
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, amplifier, false, true));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, amplifier, false, true));
                     }
                  });
               }

               if (globalDay >= 50) {
                  ((BooleanValue)world.getServer().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION)).set(false, world.getServer());
               }

               if (beginning != null && !beginning.players().isEmpty()) {
                  long seed = world.getRandom().nextLong();
                  ServerLevel overworld = reference.getServer().overworld();
                  BlockPos spawn = overworld.getSharedSpawnPos();
                  BlockPos safeSpawn = overworld.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, spawn);

                  for (ServerPlayer player : new ArrayList<>(beginning.players())) {
                     if (!player.isSpectator() && !player.isCreative()) {
                        player.setDeltaMovement(0.0, player.getDeltaMovement().y, 0.0);
                        player.displayClientMessage(
                           Component.literal("!El ")
                              .withStyle(ChatFormatting.RED)
                              .append(Component.literal("Death Train").withStyle(ChatFormatting.DARK_RED).withStyle(Style.EMPTY.withBold(true)))
                              .append(Component.literal(" esta activo has sido expulsado!").withStyle(ChatFormatting.RED)),
                           true
                        );
                        player.hurtMarked = true;
                        player.addTag("hide");
                        PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 40);
                        ScheduleInTicks.schedule(
                           () -> {
                              player.teleportTo(overworld, safeSpawn.getX(), safeSpawn.getY() + 1, safeSpawn.getZ(), player.getYRot(), player.getXRot());
                              player.connection
                                 .send(
                                    new ClientboundSoundPacket(
                                       BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_DEACTIVATE),
                                       SoundSource.MASTER,
                                       player.getX(),
                                       player.getY(),
                                       player.getZ(),
                                       1.0F,
                                       0.8F,
                                       seed
                                    )
                                 );
                           },
                           1
                        );
                     }
                  }
               }

               DeathTrainTimer.display(world);
            }
         }

         if (justFinished) {
            for (Player p : reference.getServer().getPlayerList().getPlayers()) {
               p.sendSystemMessage(Component.literal("§a¡El Death Train ha terminado!"));
               if (!p.getTags().contains("hide")) {
                  p.displayClientMessage(Component.literal("§7Quedan 00:00 de tormenta"), true);
               }

               if (DayGlobalCount.CURRENT_DAY >= 10 && !p.isSleeping()) {
                  p.displayClientMessage(Component.literal("§7Quedan 00:00 de tormenta"), true);
               }
            }

            for (ServerLevel world : reference.getServer().getAllLevels()) {
               ((BooleanValue)world.getServer().getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE)).set(true, world.getServer());
               ((BooleanValue)world.getServer().getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION)).set(true, world.getServer());
               world.setWeatherParameters(0, 0, false, false);
               world.resetWeatherCycle();
            }
         }
      }
   }

   public static void initialize(ServerLevel reference) {
      DeathTrainTimer timer = DeathTrainTimer.get(reference);
      globalDay = timer.day;
      globalTotalSeconds = timer.totalSeconds;
      globalFinished = timer.finished;
      syncAll(reference);
   }
}
