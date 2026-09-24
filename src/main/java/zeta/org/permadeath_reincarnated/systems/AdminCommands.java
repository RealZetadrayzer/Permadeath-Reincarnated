package zeta.org.permadeath_reincarnated.systems;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningCurseAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningCurseAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.attachments.MinutesPlayedCountAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.MinutesPlayedCountAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.timers.DeathTrainTimerGlobalHandler;
import zeta.org.permadeath_reincarnated.systems.timers.ShulkerDoubledRatesGlobalHandler;

public class AdminCommands {
   private static final Map<String, String> DEMON_ROLES = Map.ofEntries(
      Map.entry("zetadrayzer", "skipper"),
      Map.entry("Dev", "skipper"),
      Map.entry("AyraFC", "ghoul_controller"),
      Map.entry("iAests", "ghoul_controller"),
      Map.entry("Riethd_Rex", "ghoul_controller"),
      Map.entry("Pastelawas", "ghoul_controller"),
      Map.entry("blancolaseta", "marksman"),
      Map.entry("DarkyCraft", "marksman"),
      Map.entry("Dazx_FTW", "marksman"),
      Map.entry("Gabgo09", "marksman"),
      Map.entry("Genericont", "marksman"),
      Map.entry("Kik_va", "marksman"),
      Map.entry("MazkyBellako29", "marksman"),
      Map.entry("MisterYo484", "marksman"),
      Map.entry("SamuDog", "marksman"),
      Map.entry("anygwar", "ground_keeper"),
      Map.entry("MauLovesDaiqui", "ground_keeper"),
      Map.entry("_Neak_", "ground_keeper"),
      Map.entry("NormanliumZz", "ground_keeper"),
      Map.entry("im_grillo", "ground_keeper"),
      Map.entry("ZerO_743", "ground_keeper"),
      Map.entry("convecs_", "crystal_caretaker"),
      Map.entry("ScottGmr13", "crystal_caretaker"),
      Map.entry("Simon3OOO", "crystal_caretaker"),
      Map.entry("iSykeda", "ikari"),
      Map.entry("KreKoh", "ikari"),
      Map.entry("YaYant", "ikari")
   );

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("roles").requires(src -> src.hasPermission(4)))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("demon").then(Commands.literal("apply").executes(AdminCommands::applyDemonRoles)))
                  .then(Commands.literal("reset").executes(AdminCommands::resetDemonRoles))
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ping")
               .executes(
                  ctx -> {
                     if (((CommandSourceStack)ctx.getSource()).getEntity() instanceof ServerPlayer self) {
                        return sendPing((CommandSourceStack)ctx.getSource(), self);
                     } else {
                        ((CommandSourceStack)ctx.getSource())
                           .sendFailure(Component.literal("Este comando solo se puede ejecutar como jugador.").withStyle(ChatFormatting.RED));
                        return 0;
                     }
                  }
               ))
            .then(
               Commands.argument("target", StringArgumentType.greedyString())
                  .executes(ctx -> resolveAndSendPing((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "target")))
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playtime").executes(ctx -> {
                  if (((CommandSourceStack)ctx.getSource()).getEntity() instanceof ServerPlayer self) {
                     return sendPlaytime((CommandSourceStack)ctx.getSource(), self.getUUID(), self.getGameProfile().getName());
                  } else {
                     ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("Error de sintaxis."));
                     return 0;
                  }
               }))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("all").requires(source -> source.hasPermission(4)))
                     .then(
                        ((RequiredArgumentBuilder)Commands.argument("window", IntegerArgumentType.integer(0))
                              .then(
                                 Commands.literal("failed")
                                    .executes(
                                       ctx -> sendPlaytimeWindowFiltered(
                                          (CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "window"), false
                                       )
                                    )
                              ))
                           .then(
                              Commands.literal("completed")
                                 .executes(
                                    ctx -> sendPlaytimeWindowFiltered((CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "window"), true)
                                 )
                           )
                     )
               ))
            .then(
               Commands.argument("target", StringArgumentType.greedyString())
                  .executes(ctx -> resolveAndSendPlaytime((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "target")))
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pr")
                     .requires(source -> source.hasPermission(4)))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("event")
                           .then(
                              Commands.literal("curse")
                                 .then(
                                    Commands.literal("give")
                                       .then(
                                          Commands.argument("target", StringArgumentType.greedyString())
                                             .executes(ctx -> giveCurse((CommandSourceStack)ctx.getSource(), StringArgumentType.getString(ctx, "target")))
                                       )
                                 )
                           ))
                        .then(
                           ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("shulkers")
                                          .then(
                                             Commands.literal("add")
                                                .then(
                                                   Commands.argument("amount", IntegerArgumentType.integer())
                                                      .then(Commands.argument("unit", StringArgumentType.word()).executes(ctx -> addShulkerTime(ctx)))
                                                )
                                          ))
                                       .then(
                                          Commands.literal("set")
                                             .then(
                                                Commands.argument("amount", IntegerArgumentType.integer())
                                                   .then(Commands.argument("unit", StringArgumentType.word()).executes(ctx -> setShulkerTime(ctx)))
                                             )
                                       ))
                                    .then(Commands.literal("pause").executes(ctx -> pauseShulker(ctx))))
                                 .then(Commands.literal("resume").executes(ctx -> resumeShulker(ctx))))
                              .then(Commands.literal("reset").executes(ctx -> resetShulker(ctx)))
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathtrain")
                           .then(
                              Commands.literal("add")
                                 .then(
                                    Commands.argument("amount", IntegerArgumentType.integer())
                                       .then(Commands.argument("unit", StringArgumentType.word()).executes(ctx -> addTime(ctx, true)))
                                 )
                           ))
                        .then(
                           Commands.literal("remove")
                              .then(
                                 Commands.argument("amount", IntegerArgumentType.integer())
                                    .then(Commands.argument("unit", StringArgumentType.word()).executes(ctx -> removeTime(ctx)))
                              )
                        ))
                     .then(
                        Commands.literal("set")
                           .then(
                              Commands.argument("amount", IntegerArgumentType.integer())
                                 .then(Commands.argument("unit", StringArgumentType.word()).executes(ctx -> setTime(ctx)))
                           )
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("day")
                        .then(
                           Commands.literal("add")
                              .then(
                                 Commands.argument("amount", IntegerArgumentType.integer(1))
                                    .executes(
                                       ctx -> {
                                          int add = IntegerArgumentType.getInteger(ctx, "amount");
                                          MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
                                          ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);
                                          if (overworld != null) {
                                             DeathTrainTimerGlobalHandler.addDay(overworld, add);
                                             int day = DeathTrainTimerGlobalHandler.getDay();
                                             ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§aDía actual: §2" + day), true);
                                             Component broadcast = Component.literal("§aEl día cambió, ahora estamos en el día §2" + day);

                                             for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                                long seed = player.level().getRandom().nextLong();
                                                player.displayClientMessage(broadcast, false);
                                                player.connection
                                                   .send(
                                                      new ClientboundSoundPacket(
                                                         BuiltInRegistries.SOUND_EVENT.wrapAsHolder((SoundEvent)SoundEvents.TRIDENT_THUNDER.value()),
                                                         SoundSource.MASTER,
                                                         player.getX(),
                                                         player.getY(),
                                                         player.getZ(),
                                                         0.5F,
                                                         0.7F,
                                                         seed
                                                      )
                                                   );
                                                player.connection
                                                   .send(
                                                      new ClientboundSoundPacket(
                                                         BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_ACTIVATE),
                                                         SoundSource.MASTER,
                                                         player.getX(),
                                                         player.getY(),
                                                         player.getZ(),
                                                         0.85F,
                                                         0.8F,
                                                         seed
                                                      )
                                                   );
                                                player.connection
                                                   .send(
                                                      new ClientboundSoundPacket(
                                                         BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN),
                                                         SoundSource.MASTER,
                                                         player.getX(),
                                                         player.getY(),
                                                         player.getZ(),
                                                         0.75F,
                                                         0.9F,
                                                         seed
                                                      )
                                                   );
                                                player.connection
                                                   .send(
                                                      new ClientboundSoundPacket(
                                                         BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WITHER_DEATH),
                                                         SoundSource.MASTER,
                                                         player.getX(),
                                                         player.getY(),
                                                         player.getZ(),
                                                         0.35F,
                                                         0.6F,
                                                         seed
                                                      )
                                                   );
                                             }
                                          }

                                          return 1;
                                       }
                                    )
                              )
                        ))
                     .then(Commands.literal("remove").then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(ctx -> {
                        int remove = IntegerArgumentType.getInteger(ctx, "amount");
                        ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
                        if (overworld != null) {
                           DeathTrainTimerGlobalHandler.setDay(overworld, Math.max(0, DeathTrainTimerGlobalHandler.getDay() - remove));
                           ((CommandSourceStack)ctx.getSource())
                              .sendSuccess(() -> Component.literal("§aDía actual: §2" + DeathTrainTimerGlobalHandler.getDay()), true);
                        }

                        return 1;
                     }))))
                  .then(
                     Commands.literal("set")
                        .then(
                           Commands.argument("amount", IntegerArgumentType.integer(0))
                              .executes(
                                 ctx -> {
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
                                    if (overworld != null) {
                                       DeathTrainTimerGlobalHandler.setDay(overworld, Math.max(0, amount));
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendSuccess(() -> Component.literal("§aDía configurado a: §2" + DeathTrainTimerGlobalHandler.getDay()), true);
                                    }

                                    return 1;
                                 }
                              )
                        )
                  )
            )
      );
   }

   private static int addShulkerTime(CommandContext<CommandSourceStack> ctx) {
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      String unit = StringArgumentType.getString(ctx, "unit").toLowerCase();
      int seconds = parseTime(ctx, amount, unit);
      if (seconds <= 0) {
         return 0;
      }

      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         ShulkerDoubledRatesGlobalHandler.addTime(overworld, seconds);
         ((CommandSourceStack)ctx.getSource())
            .sendSuccess(() -> Component.literal("§dSe añadieron §5" + amount + " " + unit + " §dal evento de X2 de Shulker Shells."), true);
      }

      return 1;
   }

   private static int setShulkerTime(CommandContext<CommandSourceStack> ctx) {
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      String unit = StringArgumentType.getString(ctx, "unit").toLowerCase();
      int seconds = parseTime(ctx, amount, unit);
      if (seconds < 0) {
         return 0;
      }

      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         ShulkerDoubledRatesGlobalHandler.setTime(overworld, seconds);
         ((CommandSourceStack)ctx.getSource())
            .sendSuccess(() -> Component.literal("§dTiempo de X2 de Shulker Shells configurado a §5" + amount + " " + unit), true);
      }

      return 1;
   }

   private static int pauseShulker(CommandContext<CommandSourceStack> ctx) {
      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         ShulkerDoubledRatesGlobalHandler.pause(overworld);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§dEvento de X2 de Shulker Shells §5pausado."), true);
      }

      return 1;
   }

   private static int resumeShulker(CommandContext<CommandSourceStack> ctx) {
      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         ShulkerDoubledRatesGlobalHandler.resume(overworld);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§dEvento de X2 de Shulker Shells §5reanundado."), true);
      }

      return 1;
   }

   private static int resetShulker(CommandContext<CommandSourceStack> ctx) {
      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         ShulkerDoubledRatesGlobalHandler.reset(overworld);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§dEvento de X2 de Shulker Shells §5reiniciado."), true);
      }

      return 1;
   }

   private static int parseTime(CommandContext<CommandSourceStack> ctx, int amount, String unit) {
      return switch (unit) {
         case "s", "sec", "second", "seconds", "segundo", "segundos" -> amount;
         case "m", "min", "minute", "minutes", "minuto", "minutos" -> amount * 60;
         case "h", "hour", "hours", "hora", "horas" -> amount * 3600;
         case "d", "day", "days", "dia", "dias" -> amount * 86400;
         case "w", "week", "weeks", "semana", "semanas" -> amount * 604800;
         default -> {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("§cSintaxis inválida: usa s/m/h/d/w o variantes."));
            yield -1;
         }
      };
   }

   private static int addTime(CommandContext<CommandSourceStack> ctx, boolean sendMessage) {
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      String unit = StringArgumentType.getString(ctx, "unit").toLowerCase();

      int secondsToAdd = switch (unit) {
         case "s", "sec", "second", "seconds", "segundo", "segundos" -> amount;
         case "m", "min", "minute", "minutes", "minuto", "minutos" -> amount * 60;
         case "h", "hour", "hours", "hora", "horas" -> amount * 3600;
         case "d", "day", "days", "dia", "dias" -> amount * 86400;
         case "w", "week", "weeks", "sm", "semana", "semanas" -> amount * 604800;
         default -> {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("§cSintaxis inválida: usa s/m/h/d/w o variantes."));
            yield 0;
         }
      };
      if (secondsToAdd > 0) {
         ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
         if (overworld != null) {
            DeathTrainTimerGlobalHandler.addTime(overworld, secondsToAdd);
            if (sendMessage) {
               ((CommandSourceStack)ctx.getSource())
                  .sendSuccess(() -> Component.literal("§aSe han añadido §2" + amount + " §a" + unit + " al Death Train."), true);
            }
         }
      }

      return 1;
   }

   private static int removeTime(CommandContext<CommandSourceStack> ctx) {
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      String unit = StringArgumentType.getString(ctx, "unit").toLowerCase();

      int secondsToRemove = switch (unit) {
         case "s", "sec", "second", "seconds", "segundo", "segundos" -> amount;
         case "m", "min", "minute", "minutes", "minuto", "minutos" -> amount * 60;
         case "h", "hour", "hours", "hora", "horas" -> amount * 3600;
         case "d", "day", "days", "dia", "dias" -> amount * 86400;
         case "w", "week", "weeks", "sm", "semana", "semanas" -> amount * 604800;
         default -> {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("§cSintaxis inválida: usa s/m/h/d/w o variantes."));
            yield 0;
         }
      };
      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         int newTime = Math.max(0, DeathTrainTimerGlobalHandler.getTotalSeconds() - secondsToRemove);
         DeathTrainTimerGlobalHandler.setTime(overworld, newTime);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§aSe removió §2" + amount + " §a" + unit + " del Death Train."), true);
      }

      return 1;
   }

   private static int setTime(CommandContext<CommandSourceStack> ctx) {
      int amount = IntegerArgumentType.getInteger(ctx, "amount");
      String unit = StringArgumentType.getString(ctx, "unit").toLowerCase();

      int secondsToSet = switch (unit) {
         case "s", "sec", "second", "seconds", "segundo", "segundos" -> amount;
         case "m", "min", "minute", "minutes", "minuto", "minutos" -> amount * 60;
         case "h", "hour", "hours", "hora", "horas" -> amount * 3600;
         case "d", "day", "days", "dia", "dias" -> amount * 86400;
         case "w", "week", "weeks", "sm", "semana", "semanas" -> amount * 604800;
         default -> {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("§cSintaxis inválida: usa s/m/h/d/w o variantes."));
            yield 0;
         }
      };
      ServerLevel overworld = ((CommandSourceStack)ctx.getSource()).getServer().getLevel(ServerLevel.OVERWORLD);
      if (overworld != null) {
         DeathTrainTimerGlobalHandler.setTime(overworld, secondsToSet);
         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("§aEl Death Train se configuró a §2" + amount + " §a" + unit + "."), true);
      }

      return 1;
   }

   private static int sendPlaytime(CommandSourceStack source, UUID targetId, String targetName) {
      ServerPlayer viewer = source.getPlayer();
      if (viewer == null) {
         source.sendFailure(Component.literal("Este comando solo se puede ejecutar en jugadores.").withStyle(ChatFormatting.RED));
         return 0;
      } else {
         MinecraftServer server = source.getServer();
         int day = DayGlobalCount.CURRENT_DAY;
         Integer totalMinutesStats = readTotalMinutesFromStats(server, targetId);
         ServerPlayer online = server.getPlayerList().getPlayer(targetId);
         if (online != null) {
            MinutesPlayedCountAttachment playTime = MinutesPlayedCountAttachmentHelper.get(online);
            playTime.ensureInitialized(day);
            return sendWindowPlusTotalMessage(viewer, targetName, playTime, totalMinutesStats);
         } else {
            ServerLevel overworld = server.overworld();
            CompoundTag saved = PlayerPlaytimeDataHandler.get(overworld).get(targetId);
            if (saved != null) {
               MinutesPlayedCountAttachment playTime = new MinutesPlayedCountAttachment();
               playTime.deserializeNBT(overworld.registryAccess(), saved);
               playTime.ensureInitialized(day);
               return sendWindowPlusTotalMessage(viewer, targetName, playTime, totalMinutesStats);
            } else if (totalMinutesStats != null) {
               viewer.displayClientMessage(makeNoAttachmentButHasStatsMessage(targetName, totalMinutesStats), false);
               return 1;
            } else {
               sendNeverJoined(viewer, targetName);
               return 0;
            }
         }
      }
   }

   private static int sendWindowPlusTotalMessage(ServerPlayer viewer, String targetName, MinutesPlayedCountAttachment playTime, Integer totalMinutesStats) {
      int reqHours = playTime.getMinutesRequired() / 60;
      int window = playTime.getWindow();
      int targetDay = (window + 1) * 10;
      if (playTime.completedWindow()) {
         MutableComponent msg1 = Component.literal(targetName)
            .withStyle(ChatFormatting.GREEN)
            .append(Component.literal(" ya completó las ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(reqHours + " horas").withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(" requeridas para el día ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(String.valueOf(targetDay)).withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(".").withStyle(ChatFormatting.GREEN));
         viewer.displayClientMessage(msg1, false);
      } else {
         int minutes = playTime.getMinutesPlayed();
         MutableComponent msg1 = Component.literal(targetName)
            .withStyle(ChatFormatting.GREEN)
            .append(Component.literal(" jugó ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(formatHoursMinsCon(minutes)).withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(" de ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(reqHours + " horas").withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(" requeridas para el día ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(String.valueOf(targetDay)).withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(".").withStyle(ChatFormatting.GREEN));
         viewer.displayClientMessage(msg1, false);
      }

      if (totalMinutesStats != null) {
         viewer.displayClientMessage(makeTotalSentence(targetName, totalMinutesStats), false);
      }

      return 1;
   }

   private static int sendPlaytimeWindowFiltered(CommandSourceStack source, int targetWindow, boolean completed) {
      ServerPlayer viewer = source.getPlayer();
      if (viewer == null) {
         source.sendFailure(Component.literal("Este comando solo se puede ejecutar en jugadores.").withStyle(ChatFormatting.RED));
         return 0;
      }

      MinecraftServer server = source.getServer();
      ServerLevel overworld = server.overworld();
      Path statsDir = server.getWorldPath(LevelResource.PLAYER_STATS_DIR);
      AtomicBoolean foundAny = new AtomicBoolean(false);

      try {
         Files.list(statsDir)
            .filter(p -> p.toString().endsWith(".json"))
            .forEach(
               path -> {
                  try {
                     UUID uuid = UUID.fromString(path.getFileName().toString().replace(".json", ""));
                     String name = server.getProfileCache().get(uuid).<String>map(GameProfile::getName).orElse(uuid.toString());
                     MinutesPlayedCountAttachment playTime = null;
                     ServerPlayer online = server.getPlayerList().getPlayer(uuid);
                     if (online != null) {
                        playTime = MinutesPlayedCountAttachmentHelper.get(online);
                     } else {
                        CompoundTag saved = PlayerPlaytimeDataHandler.get(overworld).get(uuid);
                        if (saved != null) {
                           playTime = new MinutesPlayedCountAttachment();
                           playTime.deserializeNBT(overworld.registryAccess(), saved);
                        }
                     }

                     if (playTime == null) {
                        viewer.displayClientMessage(
                           Component.literal(name)
                              .withStyle(ChatFormatting.RED)
                              .append(Component.literal(" ESTE JUGADOR AUN NO SE CONECTA AL SERVIDOR").withStyle(ChatFormatting.DARK_RED)),
                           false
                        );
                        return;
                     }

                     if (playTime.getWindow() != targetWindow) {
                        return;
                     }

                     boolean isCompleted = playTime.completedWindow();
                     if (completed == isCompleted) {
                        sendWindowPlusTotalMessage(viewer, name, playTime, readTotalMinutesFromStats(server, uuid));
                        foundAny.set(true);
                     }
                  } catch (Exception var12) {
                  }
               }
            );
      } catch (Exception e) {
         source.sendFailure(Component.literal("Error leyendo datos de jugadores.").withStyle(ChatFormatting.RED));
         return 0;
      }

      if (completed && !foundAny.get()) {
         viewer.displayClientMessage(Component.literal("NINGUN JUGADOR COMPLETA LAS 10 HORAS AUN").withStyle(ChatFormatting.RED), false);
      }

      return 1;
   }

   private static Integer readTotalMinutesFromStats(MinecraftServer server, UUID uuid) {
      try {
         Path statsDir = server.getWorldPath(LevelResource.PLAYER_STATS_DIR);
         Path file = statsDir.resolve(uuid.toString() + ".json");
         if (!Files.exists(file)) {
            return null;
         }

         JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
         JsonObject stats = root.getAsJsonObject("stats");
         if (stats == null) {
            return null;
         }

         JsonObject custom = stats.getAsJsonObject("minecraft:custom");
         if (custom == null) {
            return null;
         }

         if (!custom.has("minecraft:play_time")) {
            return null;
         }

         int ticks = custom.get("minecraft:play_time").getAsInt();
         return ticks / 1200;
      } catch (Exception e) {
         return null;
      }
   }

   private static int sendPing(CommandSourceStack source, ServerPlayer target) {
      ServerPlayer viewer = source.getPlayer();
      if (viewer == null) {
         source.sendFailure(Component.literal("Este comando solo se puede ejecutar en jugadores.").withStyle(ChatFormatting.RED));
         return 0;
      } else {
         int ping = target.connection.latency();
         MutableComponent msg = Component.literal(target.getGameProfile().getName())
            .withStyle(ChatFormatting.GREEN)
            .append(Component.literal(" tiene un ping de ").withStyle(ChatFormatting.GREEN))
            .append(Component.literal(String.valueOf(ping)).withStyle(ChatFormatting.DARK_GREEN))
            .append(Component.literal(" ms").withStyle(ChatFormatting.GREEN));
         viewer.displayClientMessage(msg, false);
         return 1;
      }
   }

   private static int resolveAndSendPing(CommandSourceStack source, String raw) {
      raw = raw.trim();
      MinecraftServer server = source.getServer();
      if (raw.startsWith("@")) {
         try {
            EntitySelector sel = new EntitySelectorParser(new StringReader(raw), true).parse();
            ServerPlayer target = sel.findSinglePlayer(source);
            return sendPing(source, target);
         } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("No se encontraron jugadores.").withStyle(ChatFormatting.RED));
            return 0;
         }
      } else {
         ServerPlayer onlineByName = server.getPlayerList().getPlayerByName(raw);
         if (onlineByName != null) {
            return sendPing(source, onlineByName);
         }

         try {
            UUID id = UUID.fromString(raw);
            ServerPlayer onlineById = server.getPlayerList().getPlayer(id);
            if (onlineById != null) {
               return sendPing(source, onlineById);
            }
         } catch (IllegalArgumentException var7) {
         }

         source.sendFailure(Component.literal("Jugador no conectado (no se puede ver el ping offline).").withStyle(ChatFormatting.RED));
         return 0;
      }
   }

   private static int resolveAndSendPlaytime(CommandSourceStack source, String raw) {
      raw = raw.trim();
      MinecraftServer server = source.getServer();
      if (raw.startsWith("@")) {
         try {
            EntitySelector sel = new EntitySelectorParser(new StringReader(raw), true).parse();
            ServerPlayer target = sel.findSinglePlayer(source);
            return sendPlaytime(source, target.getUUID(), target.getGameProfile().getName());
         } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("No se encontraron jugadores").withStyle(ChatFormatting.RED));
            return 0;
         }
      } else {
         GameProfileCache cache = server.getProfileCache();
         if (cache != null) {
            Optional<GameProfile> prof = cache.get(raw);
            if (prof.isPresent() && prof.get().getId() != null) {
               return sendPlaytime(source, prof.get().getId(), prof.get().getName());
            }
         }

         try {
            UUID id = UUID.fromString(raw);
            return sendPlaytime(source, id, raw);
         } catch (IllegalArgumentException var7) {
            if (source.getEntity() instanceof ServerPlayer viewer) {
               sendNeverJoined(viewer, raw);
            } else {
               source.sendFailure(Component.literal("Jugador no encontrado.").withStyle(ChatFormatting.RED));
            }

            return 0;
         }
      }
   }

   private static MutableComponent makeTotalSentence(String targetName, int totalMinutesStats) {
      return Component.literal(targetName)
         .withStyle(ChatFormatting.GREEN)
         .append(Component.literal(" lleva dentro ").withStyle(ChatFormatting.GREEN))
         .append(Component.literal("del servidor").withStyle(ChatFormatting.GREEN))
         .append(Component.literal(" por ").withStyle(ChatFormatting.GREEN))
         .append(Component.literal(formatDaysHoursMinsConY(totalMinutesStats)).withStyle(ChatFormatting.DARK_GREEN))
         .append(Component.literal(".").withStyle(ChatFormatting.GREEN));
   }

   private static MutableComponent makeNoAttachmentButHasStatsMessage(String targetName, int totalMinutesStats) {
      return Component.literal(targetName)
         .withStyle(ChatFormatting.GREEN)
         .append(Component.literal(" aún no tiene registros de tiempo jugado en ").withStyle(ChatFormatting.GREEN))
         .append(Component.literal("el servidor").withStyle(ChatFormatting.GREEN))
         .append(Component.literal(", pero su tiempo total dentro es de ").withStyle(ChatFormatting.GREEN))
         .append(Component.literal(formatDaysHoursMinsConY(totalMinutesStats)).withStyle(ChatFormatting.DARK_GREEN))
         .append(Component.literal(".").withStyle(ChatFormatting.GREEN));
   }

   private static void sendNeverJoined(ServerPlayer viewer, String targetName) {
      viewer.displayClientMessage(
         Component.literal(targetName)
            .withStyle(ChatFormatting.DARK_RED)
            .append(Component.literal(" nunca se conectó a Permadeath: Reincarnated.").withStyle(ChatFormatting.RED)),
         false
      );
   }

   private static String formatHoursMinsCon(int totalMinutes) {
      int hours = totalMinutes / 60;
      int mins = totalMinutes % 60;
      if (hours <= 0) {
         return mins + (mins == 1 ? " minuto" : " minutos");
      } else {
         return mins <= 0
            ? hours + (hours == 1 ? " hora" : " horas")
            : hours + (hours == 1 ? " hora" : " horas") + " con " + mins + (mins == 1 ? " minuto" : " minutos");
      }
   }

   private static String formatDaysHoursMinsConY(int totalMinutes) {
      int days = totalMinutes / 1440;
      int rem = totalMinutes % 1440;
      int hours = rem / 60;
      int mins = rem % 60;
      String d = days > 0 ? days + (days == 1 ? " día" : " días") : null;
      String h = hours > 0 ? hours + (hours == 1 ? " hora" : " horas") : null;
      String m = mins > 0 ? mins + (mins == 1 ? " minuto" : " minutos") : null;
      if (d != null && h != null && m != null) {
         return d + " y " + h + " con " + m;
      } else if (d != null && h != null) {
         return d + " con " + h;
      } else if (d != null && m != null) {
         return d + " con " + m;
      } else if (h != null && m != null) {
         return h + " con " + m;
      } else if (d != null) {
         return d;
      } else if (h != null) {
         return h;
      } else {
         return m != null ? m : "0 minutos";
      }
   }

   private static void ensureRoleTeamsExist(Scoreboard scoreboard) {
      Startup.createTeam(scoreboard, "skipper", "[SKIPPER] ", ChatFormatting.DARK_RED, true);
      Startup.createTeam(scoreboard, "ikari", "[IKARI] ", ChatFormatting.GOLD, true);
      Startup.createTeam(scoreboard, "ghoul_controller", "[GHOUL CONTROLLER] ", ChatFormatting.RED, true);
      Startup.createTeam(scoreboard, "crystal_caretaker", "[CRYSTAL CARETAKER] ", ChatFormatting.LIGHT_PURPLE, true);
      Startup.createTeam(scoreboard, "marksman", "[MARKSMAN] ", ChatFormatting.BLUE, true);
      Startup.createTeam(scoreboard, "ground_keeper", "[GROUND KEEPER] ", ChatFormatting.AQUA, true);
   }

   private static int applyDemonRoles(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      Scoreboard scoreboard = server.overworld().getScoreboard();
      ensureRoleTeamsExist(scoreboard);

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         String name = player.getScoreboardName();
         CompoundTag tag = player.getPersistentData();
         if (!tag.contains("permadeath_old_team")) {
            PlayerTeam currentTeam = scoreboard.getPlayersTeam(name);
            if (currentTeam != null) {
               tag.putString("permadeath_old_team", currentTeam.getName());
            }

            String role = DEMON_ROLES.getOrDefault(name, "marksman");
            PlayerTeam roleTeam = scoreboard.getPlayerTeam(role);
            if (roleTeam != null) {
               scoreboard.addPlayerToTeam(name, roleTeam);
            }
         }
      }

      ((CommandSourceStack)ctx.getSource())
         .sendSuccess(() -> Component.literal("§dLos §5Roles §dpara la pelea contra el §5Permadeath Demon§d fueron aplicados."), true);

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         long seed = player.level().getRandom().nextLong();
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder((SoundEvent)SoundEvents.TRIDENT_THUNDER.value()),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.5F,
                  0.7F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_ACTIVATE),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.85F,
                  0.8F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.75F,
                  0.9F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WITHER_DEATH),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.35F,
                  0.6F,
                  seed
               )
            );
      }

      return 1;
   }

   private static int resetDemonRoles(CommandContext<CommandSourceStack> ctx) {
      MinecraftServer server = ((CommandSourceStack)ctx.getSource()).getServer();
      Scoreboard scoreboard = server.overworld().getScoreboard();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         CompoundTag tag = player.getPersistentData();
         if (tag.contains("permadeath_old_team")) {
            String name = player.getScoreboardName();
            String oldTeamName = tag.getString("permadeath_old_team");
            PlayerTeam currentTeam = scoreboard.getPlayersTeam(name);
            PlayerTeam oldTeam = scoreboard.getPlayerTeam(oldTeamName);
            if (oldTeam == null) {
               tag.remove("permadeath_old_team");
            } else if (currentTeam == oldTeam) {
               tag.remove("permadeath_old_team");
            } else {
               scoreboard.addPlayerToTeam(name, oldTeam);
               tag.remove("permadeath_old_team");
            }
         }
      }

      ((CommandSourceStack)ctx.getSource())
         .sendSuccess(() -> Component.literal("§dLos §5Roles §dpara la pelea contra el §5Permadeath Demon§d fueron reiniciados."), true);

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         long seed = player.level().getRandom().nextLong();
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder((SoundEvent)SoundEvents.TRIDENT_THUNDER.value()),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.5F,
                  0.7F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_DEACTIVATE),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.85F,
                  0.8F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RESPAWN_ANCHOR_SET_SPAWN),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.75F,
                  0.9F,
                  seed
               )
            );
         player.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WITHER_DEATH),
                  SoundSource.MASTER,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  0.35F,
                  0.6F,
                  seed
               )
            );
      }

      return 1;
   }

   private static int giveCurse(CommandSourceStack source, String raw) {
      raw = raw.trim();
      MinecraftServer server = source.getServer();
      ServerPlayer target = null;
      if (raw.startsWith("@")) {
         try {
            EntitySelector sel = new EntitySelectorParser(new StringReader(raw), true).parse();
            target = sel.findSinglePlayer(source);
         } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("No se encontraron jugadores.").withStyle(ChatFormatting.RED));
            return 0;
         }
      } else {
         target = server.getPlayerList().getPlayerByName(raw);
      }

      if (target == null) {
         source.sendFailure(Component.literal("Jugador no encontrado.").withStyle(ChatFormatting.RED));
         return 0;
      } else if (!target.isCreative() && !target.isSpectator()) {
         BeginningCurseAttachment curse = BeginningCurseAttachmentHelper.get(target);
         curse.apply();
         curse.setClean();
         ServerPlayer finalTarget = target;
         server.getPlayerList()
            .getPlayers()
            .forEach(
               p -> p.displayClientMessage(
                  Component.literal("[PERMADEATH] ")
                     .withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD})
                     .append(
                        Component.literal(
                              finalTarget.getName().getString()
                                 + " ¡Desgracia! has regibido la maldición de The Beginning por entrar el último. ¡Sufre y muere por lento! No puedes usar cubos de leche en 12 horas dentro de Permadeath o serás permabaneado.."
                           )
                           .withStyle(ChatFormatting.LIGHT_PURPLE)
                           .withStyle(Style.EMPTY.withBold(false))
                     ),
                  false
               )
            );
         long seed = target.level().getRandom().nextLong();
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WITHER_SPAWN),
                  SoundSource.MASTER,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  1.0F,
                  0.8F,
                  seed
               )
            );
         ServerPlayer finalTarget1 = target;
         source.sendSuccess(() -> Component.literal("§5Curse aplicada a §d" + finalTarget1.getName().getString()), true);
         return 1;
      } else {
         source.sendFailure(Component.literal("No puedes maldecir a un jugador en creativo/espectador.").withStyle(ChatFormatting.RED));
         return 0;
      }
   }
}
