package zeta.org.permadeath_reincarnated.demonFight;

import com.mojang.brigadier.ParseResults;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.network.PermadeathNetworkingDemon;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class PermadeathDemon {
   private static final Random RANDOM = new Random();
   private static final double ARENA_RADIUS = 300.0;
   private static final Map<UUID, Float> ULTIMATE_PLUS_CHANCE = new HashMap<>();
   private static final BlockPos[] OFFSETS = new BlockPos[]{
      new BlockPos(5, 0, 0),
      new BlockPos(-5, 0, 0),
      new BlockPos(0, 0, 5),
      new BlockPos(0, 0, -5),
      new BlockPos(7, 0, 7),
      new BlockPos(7, 0, -7),
      new BlockPos(-7, 0, 7),
      new BlockPos(-7, 0, -7)
   };
   private static final EntityType<?>[] MOB_POOL_ULTIMATE = new EntityType[]{
      EntityType.SPIDER,
      EntityType.SKELETON,
      EntityType.SILVERFISH,
      EntityType.ENDERMITE,
      EntityType.CREEPER,
      EntityType.DRAGON_FIREBALL,
      EntityType.TNT,
      EntityType.LIGHTNING_BOLT
   };
   private static final EntityType<?>[] MOB_POOL_ULTIMATE_PLUS = new EntityType[]{
      EntityType.FIREBALL, EntityType.DRAGON_FIREBALL, EntityType.TNT, EntityType.LIGHTNING_BOLT
   };

   @SubscribeEvent
   public static void onDemonSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof EnderDragon dragon) {
         Level level = dragon.level();
         if (!level.isClientSide()) {
            int day = DayGlobalCount.CURRENT_DAY;
            ServerLevel serverLevel = (ServerLevel)level;
            MinecraftServer server = serverLevel.getServer();
            PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
            if (day >= 30 && !handler.wasPreviouslyKilled()) {
               if (!dragon.getTags().contains("demonFight")) {
                  List<EndCrystal> crystals = serverLevel.getEntitiesOfClass(EndCrystal.class, dragon.getBoundingBox().inflate(400.0), EndCrystal::showsBottom);
                  handler.startFight();
                  handler.setDragon(dragon);
                  dragon.setCustomName(Component.literal("PERMADEATH DEMON").withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)));
                  dragon.addTag("demonFight");
                  Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.MAX_HEALTH)).setBaseValue(1000.0);
                  Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1000.0);
                  Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)).setBaseValue(1000.0);
                  dragon.setHealth(dragon.getMaxHealth());
                  Scoreboard scoreboard = serverLevel.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("demonFight");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("demonFight");
                     team.setColor(ChatFormatting.LIGHT_PURPLE);
                  }

                  PlayerTeam finalTeam = team;
                  ScheduleInTicks.schedule(
                     () -> {
                        placeAllHealingStations(serverLevel);

                        for (EndCrystal crystal : crystals) {
                           crystal.setGlowingTag(true);
                           crystal.addTag("demonFight");
                           scoreboard.addPlayerToTeam(crystal.getStringUUID(), finalTeam);
                           String fillCommand = String.format(
                              "execute at %s run fill ~5 ~ ~5 ~-5 0 ~-5 minecraft:bedrock replace minecraft:obsidian", crystal.getStringUUID()
                           );
                           CommandSourceStack source = server.createCommandSourceStack();
                           ParseResults<CommandSourceStack> parse = server.getCommands().getDispatcher().parse(fillCommand, source);
                           server.getCommands().performCommand(parse, fillCommand);
                        }
                     },
                     10
                  );
               }
            } else if (day >= 40 && !dragon.getTags().contains("demonFight")) {
               List<EndCrystal> crystals = serverLevel.getEntitiesOfClass(EndCrystal.class, dragon.getBoundingBox().inflate(400.0), EndCrystal::showsBottom);
               handler.startFight();
               handler.setDragon(dragon);
               dragon.setCustomName(Component.literal("PERMADEATH DEMON").withStyle(style -> style.withColor(ChatFormatting.GOLD).withBold(true)));
               dragon.addTag("demonFight");
               Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.MAX_HEALTH)).setBaseValue(1000.0);
               Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1000.0);
               Objects.requireNonNull(dragon.getAttributes().getInstance(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)).setBaseValue(1000.0);
               dragon.setHealth(dragon.getMaxHealth());
               Scoreboard scoreboard = serverLevel.getScoreboard();
               PlayerTeam team = scoreboard.getPlayerTeam("demonFight");
               if (team == null) {
                  team = scoreboard.addPlayerTeam("demonFight");
                  team.setColor(ChatFormatting.LIGHT_PURPLE);
               }

               PlayerTeam finalTeam = team;
               ScheduleInTicks.schedule(
                  () -> {
                     placeAllHealingStations(serverLevel);

                     for (EndCrystal crystal : crystals) {
                        crystal.setGlowingTag(true);
                        crystal.addTag("demonFight");
                        scoreboard.addPlayerToTeam(crystal.getStringUUID(), finalTeam);
                        String fillCommand = String.format(
                           "execute at %s run fill ~5 ~ ~5 ~-5 0 ~-5 minecraft:bedrock replace minecraft:obsidian", crystal.getStringUUID()
                        );
                        CommandSourceStack source = server.createCommandSourceStack();
                        ParseResults<CommandSourceStack> parse = server.getCommands().getDispatcher().parse(fillCommand, source);
                        server.getCommands().performCommand(parse, fillCommand);
                     }
                  },
                  10
               );
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDemonTick(Post event) {
      if (event.getEntity() instanceof EnderDragon dragon) {
         if (dragon.getTags().contains("demonFight")) {
            Level level = dragon.level();
            if (!level.isClientSide()) {
               ServerLevel serverLevel = (ServerLevel)level;
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               List<EndCrystal> crystals = serverLevel.getEntitiesOfClass(EndCrystal.class, dragon.getBoundingBox().inflate(400.0), EndCrystal::showsBottom);

               for (Ghast ghast : serverLevel.getEntitiesOfClass(Ghast.class, dragon.getBoundingBox().inflate(400.0), e -> true)) {
                  if (!ghast.getTags().contains("demonFight")) {
                     ghast.remove(RemovalReason.DISCARDED);
                  }
               }

               PermadeathNetworkingDemon payload = new PermadeathNetworkingDemon(
                  crystals.size(), dragon.getHealth() / dragon.getMaxHealth(), dragon.getTags().contains("enraged_demon")
               );
               level.getServer()
                  .getPlayerList()
                  .getPlayers()
                  .forEach(
                     player -> {
                        if (player.level() == level) {
                           player.connection.send(payload);
                           if (!player.isCreative()
                              && !player.isSpectator()
                              && player.isAlive()
                              && !player.getTags().contains("foughtDemon")
                              && handler.isFightOn()) {
                              handler.addPlayer(player);
                              player.addTag("foughtDemon");
                              PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_MEET_DEMON_ID);
                           }
                        }
                     }
                  );
               if (dragon.getY() <= 54.0 && handler.isFightOn()) {
                  dragon.teleportTo(dragon.getX(), 120.0, dragon.getZ());
                  dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
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
                                       BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ILLUSIONER_MIRROR_MOVE),
                                       SoundSource.HOSTILE,
                                       player.getX(),
                                       player.getY(),
                                       player.getZ(),
                                       1.0F,
                                       1.0F,
                                       seed
                                    )
                                 );
                           }
                        }
                     );
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEndTick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
      if (event.getLevel() instanceof ServerLevel level) {
         if (level.dimension().equals(Level.END)) {
            if (level.getGameTime() % 10L == 0L) {
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               if (handler.isFightOn()) {
                  for (ServerPlayer player : level.players()) {
                     if (!player.isSpectator() && !player.isCreative()) {
                        double x = player.getX();
                        double z = player.getZ();
                        boolean out = x > 300.0 || x < -300.0 || z > 300.0 || z < -300.0;
                        if (out) {
                           player.teleportTo(0.0, 120.0, 0.0);
                           level.playSound(
                              null, player.getX(), player.getY() + 0.5, player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.VOICE, 0.85F, 0.8F
                           );
                           level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 30, 0.4, 0.4, 0.4, 0.15);
                           player.displayClientMessage(
                              Component.literal("¡No puedes escapar del ")
                                 .withStyle(ChatFormatting.LIGHT_PURPLE)
                                 .append(
                                    Component.literal("Permadeath Demon")
                                       .withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})
                                       .append("!")
                                       .withStyle(ChatFormatting.LIGHT_PURPLE)
                                       .withStyle(Style.EMPTY.withBold(false))
                                 ),
                              true
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDemonDamageReceived(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof EnderDragon dragon) {
         if (dragon.getTags().contains("demonFight")) {
            Level level = dragon.level();
            if (!level.isClientSide()) {
               ServerLevel serverLevel = (ServerLevel)level;
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               List<ServerPlayer> playerCount = serverLevel.getEntitiesOfClass(
                  ServerPlayer.class, dragon.getBoundingBox().inflate(400.0), player -> !player.isSpectator() && !player.isCreative()
               );
               float damageReduction = Math.min(playerCount.size() * 0.05F + 0.45F, 0.9F);
               float originalDamage = event.getOriginalAmount();
               float scaledDamage = originalDamage * (1.0F - damageReduction);
               if (scaledDamage > 40.0F && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                  scaledDamage = 40.0F;
               }

               if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                  event.setAmount(Math.max(1.0F, scaledDamage));
               }

               if (dragon.getHealth() <= dragon.getMaxHealth() * 0.5 && !dragon.getTags().contains("enraged_demon")) {
                  AreaEffectCloud areaEffectCloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(level);
                  if (areaEffectCloud != null) {
                     areaEffectCloud.setPos(dragon.getX(), dragon.getY(), dragon.getZ());
                     areaEffectCloud.setDuration(1200);
                     areaEffectCloud.setRadius(0.01F);
                     areaEffectCloud.setParticle(ParticleTypes.UNDERWATER);
                     areaEffectCloud.addTag("demonFight");
                     areaEffectCloud.addTag("thunder_cloud_variant_ultimate");
                     level.addFreshEntity(areaEffectCloud);
                  }

                  dragon.addTag("enraged_demon");
                  dragon.setCustomName(
                     Component.empty()
                        .append(Component.literal("☠️ ").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.OBFUSCATED}))
                        .append(Component.literal("PERMADEATH DEMON").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}))
                        .append(Component.literal(" ☠️").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.OBFUSCATED}))
                  );
                  serverLevel.sendParticles(ParticleTypes.FLAME, dragon.getX(), dragon.getY() + 1.0, dragon.getZ(), 3000, 0.0, 0.0, 0.0, 0.5);
                  serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, dragon.getX(), dragon.getY() + 1.0, dragon.getZ(), 1, 0.0, 0.0, 0.0, 1.0);
                  playGlobalSound((ServerLevel)level, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 4.0F, 1.0F);
                  playGlobalSound((ServerLevel)level, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 4.0F, 1.0F);

                  for (ServerPlayer player : serverLevel.getPlayers(playerx -> playerx.distanceToSqr(dragon) <= 640000.0)) {
                     if (!player.isCreative() && !player.isSpectator() && player.isAlive()) {
                        LightningBolt bolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                        if (bolt != null && !player.isSpectator() && !player.isCreative()) {
                           bolt.moveTo(player.getX(), player.getY(), player.getZ());
                           serverLevel.addFreshEntity(bolt);
                        }
                     }
                  }
               }

               if (dragon.getTags().contains("360")) {
                  DamageSource source = event.getSource();
                  Entity direct = source.getDirectEntity();
                  Entity attackerEntity = source.getEntity();
                  if (!(direct instanceof LivingEntity attacker)) {
                     return;
                  }

                  if (direct != attackerEntity) {
                     return;
                  }

                  event.setCanceled(true);
                  float healthPc = attacker.getHealth() / attacker.getMaxHealth();
                  Vec3 knockbackVec = attacker.position().subtract(dragon.position()).normalize().multiply(4.0, 2.0, 4.0).add(0.5, 0.5, 0.5);
                  attacker.push(knockbackVec.x, knockbackVec.y, knockbackVec.z);
                  if (healthPc >= 0.5) {
                     attacker.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 3, false, false));
                     attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2, false, true));
                     attacker.addEffect(new MobEffectInstance(MobEffects.INFESTED, 1200, 0, false, true));
                  } else {
                     attacker.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 2, false, true));
                     attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2, false, true));
                     attacker.addEffect(new MobEffectInstance(MobEffects.INFESTED, 1200, 0, false, true));
                  }

                  serverLevel.sendParticles(ParticleTypes.FLASH, attacker.getX(), attacker.getY() + 1.0, attacker.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                  serverLevel.sendParticles(ParticleTypes.FLAME, attacker.getX(), attacker.getY() + 1.0, attacker.getZ(), 500, 0.0, 0.0, 0.0, 0.05);
                  serverLevel.playSound(null, attacker.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.MASTER, 1.0F, 1.0F);
                  serverLevel.playSound(null, attacker.blockPosition(), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.MASTER, 1.0F, 1.0F);
               }

               if (event.getSource().getEntity() instanceof ServerPlayer
                  && dragon.getTags().contains("enraged_demon")
                  && !dragon.getTags().contains("ultimate")
                  && RANDOM.nextFloat() <= 0.05F) {
                  dragon.addTag("ultimate");
                  UUID dragonId = dragon.getUUID();
                  float plusChance = ULTIMATE_PLUS_CHANCE.getOrDefault(dragonId, 0.1F);
                  serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, dragon.getX(), dragon.getY(), dragon.getZ(), 3000, 0.0, 0.0, 0.0, 0.5);
                  playGlobalSound((ServerLevel)level, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 4.0F, 1.0F);
                  playGlobalSound((ServerLevel)level, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 4.0F, 1.0F);
                  playGlobalSound((ServerLevel)level, SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.MASTER, 10.0F, 1.0F);
                  playGlobalSound((ServerLevel)level, SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.MASTER, 10.0F, 0.5F);

                  for (int t = 100; t <= 1500; t += 100) {
                     if (dragon.isAlive() && handler.isFightOn()) {
                        ScheduleInTicks.schedule(() -> spawnUltimate(dragon, serverLevel), t);
                     }
                  }

                  boolean plusTriggered = RANDOM.nextFloat() <= plusChance;
                  if (!plusTriggered) {
                     ULTIMATE_PLUS_CHANCE.put(dragonId, Math.min(plusChance + 0.1F, 1.0F));
                  } else {
                     for (int t = 100; t <= 1500; t += 100) {
                        if (dragon.isAlive() && handler.isFightOn()) {
                           ScheduleInTicks.schedule(() -> spawnUltimateExtra(dragon, serverLevel), t);
                        }
                     }

                     ULTIMATE_PLUS_CHANCE.put(dragonId, 0.1F);
                  }

                  ScheduleInTicks.schedule(() -> dragon.removeTag("ultimate"), 3600);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDemonDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof EnderDragon dragon) {
         if (dragon.getTags().contains("demonFight")) {
            Level level = dragon.level();
            if (!level.isClientSide()) {
               ServerLevel serverLevel = (ServerLevel)level;
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               AABB awardBox = dragon.getBoundingBox().inflate(400.0);
               List<EndCrystal> crystals = serverLevel.getEntitiesOfClass(EndCrystal.class, dragon.getBoundingBox().inflate(400.0), EndCrystal::showsBottom);
               List<Ghast> ghasts = serverLevel.getEntitiesOfClass(Ghast.class, dragon.getBoundingBox().inflate(400.0), e -> true);
               List<AreaEffectCloud> clouds = serverLevel.getEntitiesOfClass(AreaEffectCloud.class, dragon.getBoundingBox().inflate(400.0), e -> true);

               for (Ghast ghast : ghasts) {
                  if (ghast.getTags().contains("demonFight")) {
                     ghast.remove(RemovalReason.DISCARDED);
                  }
               }

               for (AreaEffectCloud cloud : clouds) {
                  if (cloud.getTags().contains("demonFight")) {
                     cloud.remove(RemovalReason.DISCARDED);
                  }
               }

               for (ServerPlayer players : level.getEntitiesOfClass(ServerPlayer.class, awardBox, p -> p.getTags().contains("foughtDemon"))) {
                  PlayerAdvancementsHandler.award(players, PlayerAdvancementsHandler.PLAYER_SURVIVE_DEMON_ID);
               }

               if (!handler.getFighters().isEmpty()) {
                  handler.resetFighters();
               }

               handler.setWasPreviouslyKilled(true);
               handler.endFight();
               handler.clearDragon();
               crystals.forEach(crystal -> crystal.remove(RemovalReason.DISCARDED));
               CrystalRespawnData data = CrystalRespawnData.get(serverLevel);
               data.respawns.clear();
               data.setDirty();
            }
         }
      }
   }

   private static void playGlobalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
         }
      });
   }

   private static void spawnUltimate(EnderDragon dragon, ServerLevel level) {
      List<ServerPlayer> players = level.getPlayers(p -> !p.isSpectator() && !p.isCreative() && p.distanceToSqr(dragon) <= 640000.0);
      if (!players.isEmpty()) {
         boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
         int chance = 1 + RANDOM.nextInt(100);
         long seed = level.getRandom().nextLong();
         ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
         EntityType<?> type = MOB_POOL_ULTIMATE[RANDOM.nextInt(MOB_POOL_ULTIMATE.length)];
         if (type == EntityType.SKELETON && chance <= 20 && flag) {
            type = RANDOM.nextBoolean() ? EntityType.STRAY : EntityType.BOGGED;
         }

         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EVOKER_CAST_SPELL),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  1.0F,
                  1.0F,
                  seed
               )
            );
         spawnFromPool(dragon, level, target.blockPosition(), type, target);
      }
   }

   private static void spawnUltimateExtra(EnderDragon dragon, ServerLevel level) {
      List<ServerPlayer> players = level.getPlayers(p -> !p.isSpectator() && !p.isCreative() && p.distanceToSqr(dragon) <= 640000.0);
      if (!players.isEmpty()) {
         long seed = level.getRandom().nextLong();
         ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
         BlockPos offset = OFFSETS[RANDOM.nextInt(OFFSETS.length)];
         EntityType<?> type = MOB_POOL_ULTIMATE_PLUS[RANDOM.nextInt(MOB_POOL_ULTIMATE_PLUS.length)];
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.VEX_CHARGE),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  1.5F,
                  0.85F,
                  seed
               )
            );
         spawnFromPool(dragon, level, target.blockPosition().offset(offset), type, target);
      }
   }

   private static void spawnFromPool(EnderDragon dragon, ServerLevel level, BlockPos pos, EntityType<?> type, ServerPlayer target) {
      Entity entity = null;
      long seed = level.getRandom().nextLong();
      if (type == EntityType.TNT) {
         PrimedTnt tnt = new PrimedTnt(level, pos.getX(), pos.getY() + 15, pos.getZ(), dragon);
         tnt.setFuse(60);
         tnt.addTag("demonFight");
         entity = tnt;
      } else if (type == EntityType.LIGHTNING_BOLT) {
         LightningBolt bolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
         if (bolt != null) {
            bolt.moveTo(Vec3.atCenterOf(pos));
            bolt.setDamage(30.0F);
            bolt.addTag("demonFight");
            entity = bolt;
         }
      } else if (type == EntityType.DRAGON_FIREBALL) {
         Vec3 motion = new Vec3(0.0, -0.1, 0.0);
         DragonFireball fireball = new DragonFireball(level, dragon, motion);
         fireball.setPos(pos.getX(), pos.getY() + 25, pos.getZ());
         fireball.addTag("demonFight");
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GHAST_SHOOT),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  0.6F,
                  1.0F,
                  seed
               )
            );
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.SHULKER_SHOOT),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  2.0F,
                  0.8F,
                  seed
               )
            );
         entity = fireball;
      } else if (type == EntityType.FIREBALL) {
         Vec3 motion = new Vec3(0.0, -0.1, 0.0);
         LargeFireball fireball = new LargeFireball(level, dragon, motion, 6);
         fireball.setPos(pos.getX(), pos.getY() + 25, pos.getZ());
         fireball.addTag("demonFight");
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GHAST_SHOOT),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  1.0F,
                  1.0F,
                  seed
               )
            );
         target.connection
            .send(
               new ClientboundSoundPacket(
                  BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WITHER_SHOOT),
                  SoundSource.HOSTILE,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  0.5F,
                  0.6F,
                  seed
               )
            );
         entity = fireball;
      } else {
         Entity generic = type.create(level);
         if (generic != null) {
            generic.moveTo(pos.getX(), pos.getY() + 15, pos.getZ(), RANDOM.nextFloat() * 360.0F, 0.0F);
            entity = generic;
         }
      }

      if (entity != null) {
         entity.addTag("demonFight");
         level.addFreshEntity(entity);
      }
   }

   private static BlockPos pickStationPos(ServerLevel level, int x, int z) {
      int surfaceY = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
      if (surfaceY <= level.getMinBuildHeight() || surfaceY >= level.getMaxBuildHeight()) {
         surfaceY = 55;
      }

      int bury = 2;
      return new BlockPos(x, surfaceY - bury, z);
   }

   private static void placeHealingStation(ServerLevel level, BlockPos pos) {
      StructureTemplateManager manager = level.getStructureManager();
      StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "healing_station"));
      StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false);
      template.placeInWorld(level, pos, pos, settings, level.getRandom(), 3);
      supportUnderTemplate(level, template, settings, pos);
   }

   private static void supportUnderTemplate(ServerLevel level, StructureTemplate template, StructurePlaceSettings settings, BlockPos origin) {
      BoundingBox box = template.getBoundingBox(settings, origin);

      for (int x = box.minX(); x <= box.maxX(); x++) {
         for (int z = box.minZ(); z <= box.maxZ(); z++) {
            int topY = box.maxY();
            int firstSolidY = -1;

            for (int y = topY; y >= box.minY(); y--) {
               if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                  firstSolidY = y;
                  break;
               }
            }

            if (firstSolidY != -1) {
               BlockPos below = new BlockPos(x, firstSolidY - 1, z);

               for (int limit = level.getMinBuildHeight() + 1; below.getY() > limit && level.getBlockState(below).isAir(); below = below.below()) {
                  level.setBlockAndUpdate(below, Blocks.END_STONE.defaultBlockState());
               }
            }
         }
      }
   }

   private static void placeAllHealingStations(ServerLevel level) {
      BlockPos[] points = new BlockPos[]{
         pickStationPos(level, 70, 0), pickStationPos(level, 0, 70), pickStationPos(level, -70, 0), pickStationPos(level, 0, -70)
      };

      for (BlockPos p : points) {
         placeHealingStation(level, p);
      }
   }
}
