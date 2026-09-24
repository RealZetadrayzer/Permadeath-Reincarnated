package zeta.org.permadeath_reincarnated.mixins;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.BeginningData;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.PlayerSleepingHandler;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningBlessingAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningBlessingAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.timers.DeathtrainGlobalState;

@Mixin(EndGatewayBlock.class)
public abstract class EndGatewayBlockMixin {
   @Unique
   private static final Random permadeathReincarnated$RANDOM = new Random();
   @Unique
   private static final BlockPos PORTAL_POS = new BlockPos(0, 229, 0);
   @Unique
   private static final ResourceKey<Level> BEGINNING_DIMENSION_KEY = ResourceKey.create(
      Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "the_beginning")
   );

   @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
   private void permadeath$gatewayTeleport(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
      if (!level.isClientSide && entity.canUsePortal(false)) {
         if (level.dimension() == Level.OVERWORLD) {
            if (DayGlobalCount.CURRENT_DAY < 50) {
               if (entity instanceof ServerPlayer player) {
                  ci.cancel();
                  String name = player.getName().getString();
                  long seed = level.getRandom().nextLong();
                  Random rand = permadeathReincarnated$RANDOM;
                  int dx = 15 + rand.nextInt(6);
                  int dz = 15 + rand.nextInt(6);
                  dx *= rand.nextBoolean() ? 1 : -1;
                  dz *= rand.nextBoolean() ? 1 : -1;
                  BlockPos targetPos = pos.offset(dx, 0, dz);
                  targetPos = level.getHeightmapPos(Types.MOTION_BLOCKING, targetPos);
                  player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                  player.hurtMarked = true;
                  player.addTag("hide");
                  PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 50);
                  player.displayClientMessage(
                     Component.literal("¡El ")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
                        .append(Component.literal("Permadeath Demon").withStyle(ChatFormatting.DARK_PURPLE).withStyle(Style.EMPTY.withBold(true)))
                        .append(Component.literal(" bloqueo la entrada y acabo contigo!").withStyle(ChatFormatting.LIGHT_PURPLE)),
                     true
                  );
                  Objects.requireNonNull(player.level().getServer())
                     .getPlayerList()
                     .getPlayers()
                     .forEach(
                        p -> {
                           p.displayClientMessage(
                              Component.literal("¡" + name + " intento entrar a ")
                                 .withStyle(ChatFormatting.LIGHT_PURPLE)
                                 .append(
                                    Component.literal("The Beginning")
                                       .withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})
                                       .append(Component.literal(" antes de tiempo!").withStyle(ChatFormatting.LIGHT_PURPLE))
                                 ),
                              false
                           );
                           p.connection
                              .send(
                                 new ClientboundSoundPacket(
                                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.END_PORTAL_SPAWN),
                                    SoundSource.VOICE,
                                    p.getX(),
                                    p.getY(),
                                    p.getZ(),
                                    0.5F,
                                    1.75F,
                                    seed
                                 )
                              );
                        }
                     );
                  player.hurt(player.level().damageSources().genericKill(), Float.MAX_VALUE);
               }
            } else if (entity instanceof ServerPlayer player) {
               ci.cancel();
               int deathTrain = DeathtrainGlobalState.CURRENT_STATE;
               String name = player.getName().getString();
               long seed = level.getRandom().nextLong();
               ServerLevel targetWorld = Objects.requireNonNull(player.getServer()).getLevel(BEGINNING_DIMENSION_KEY);
               if (deathTrain == 1) {
                  ServerLevel overworld = Objects.requireNonNull(player.getServer()).overworld();
                  BlockPos spawn = overworld.getSharedSpawnPos();
                  BlockPos safeSpawn = overworld.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, spawn);
                  entity.setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0);
                  player.displayClientMessage(
                     Component.literal("!El ")
                        .withStyle(ChatFormatting.RED)
                        .append(Component.literal("Death Train").withStyle(ChatFormatting.DARK_RED).withStyle(Style.EMPTY.withBold(true)))
                        .append(Component.literal(" esta activo no puedes entrar!").withStyle(ChatFormatting.RED)),
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
                  return;
               }

               if (targetWorld != null) {
                  BeginningData beginningData = BeginningData.get(targetWorld);
                  permadeathReincarnated$forceLoadPortal(targetWorld);
                  permadeathReincarnated$generatePortalOnce(targetWorld);
                  player.teleportTo(targetWorld, -8.0, 231.0, -2.0, 0.0F, player.getXRot());
                  UUID playerUUID = player.getUUID();
                  beginningData.markEntered(playerUUID);
                  if (!beginningData.blessingClaimed && !player.isSpectator() && !player.isCreative()) {
                     BeginningBlessingAttachment blessing = BeginningBlessingAttachmentHelper.get(player);
                     blessing.apply();
                     blessing.setClean();
                     beginningData.blessingClaimed = true;
                     beginningData.setDirty();
                     player.getServer()
                        .getPlayerList()
                        .getPlayers()
                        .forEach(
                           p -> p.displayClientMessage(
                              Component.literal("[PERMADEATH]")
                                 .withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD})
                                 .append(
                                    Component.literal(
                                          " "
                                             + player.getName().getString()
                                             + ", has recibido la bendición del comienzo por entrar el primero a The Beginning, suerte."
                                       )
                                       .withStyle(ChatFormatting.LIGHT_PURPLE)
                                       .withStyle(Style.EMPTY.withBold(false))
                                 ),
                              false
                           )
                        );
                  }
               }

               Objects.requireNonNull(player.level().getServer())
                  .getPlayerList()
                  .getPlayers()
                  .forEach(
                     p -> {
                        p.displayClientMessage(
                           Component.literal(name + " ha entrado a ")
                              .withStyle(ChatFormatting.LIGHT_PURPLE)
                              .append(Component.literal("The Beginning").withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})),
                           false
                        );
                        p.connection
                           .send(
                              new ClientboundSoundPacket(
                                 BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.END_PORTAL_SPAWN),
                                 SoundSource.VOICE,
                                 p.getX(),
                                 p.getY(),
                                 p.getZ(),
                                 0.5F,
                                 1.75F,
                                 seed
                              )
                           );
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_ENTER_BEGINNING_PORTAL_ID);
                     }
                  );
            } else {
               ci.cancel();
               ServerLevel targetWorld = Objects.requireNonNull(entity.getServer()).getLevel(BEGINNING_DIMENSION_KEY);
               if (targetWorld != null) {
                  entity.teleportTo(
                     targetWorld, -8.0, 231.0, -2.0, EnumSet.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z), 0.0F, entity.getXRot()
                  );
               }
            }
         } else if (level.dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
            if (entity instanceof ServerPlayer player) {
               ci.cancel();
               ServerLevel overworld = Objects.requireNonNull(player.getServer()).getLevel(Level.OVERWORLD);
               if (overworld != null) {
                  BlockPos spawn = overworld.getSharedSpawnPos();
                  player.teleportTo(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), 0.0F, player.getXRot());
               }

               String name = player.getName().getString();
               long seed = level.getRandom().nextLong();
               Objects.requireNonNull(player.level().getServer())
                  .getPlayerList()
                  .getPlayers()
                  .forEach(
                     p -> {
                        p.displayClientMessage(
                           Component.literal(name + " ha salido de ")
                              .withStyle(ChatFormatting.LIGHT_PURPLE)
                              .append(Component.literal("The Beginning").withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})),
                           false
                        );
                        p.connection
                           .send(
                              new ClientboundSoundPacket(
                                 BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.END_PORTAL_SPAWN),
                                 SoundSource.VOICE,
                                 p.getX(),
                                 p.getY(),
                                 p.getZ(),
                                 0.5F,
                                 1.75F,
                                 seed
                              )
                           );
                     }
                  );
            } else {
               ci.cancel();
               ServerLevel overworld = Objects.requireNonNull(entity.getServer()).getLevel(Level.OVERWORLD);
               if (overworld != null) {
                  BlockPos spawn = overworld.getSharedSpawnPos();
                  entity.teleportTo(
                     overworld,
                     spawn.getX(),
                     spawn.getY(),
                     spawn.getZ(),
                     EnumSet.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z),
                     0.0F,
                     entity.getXRot()
                  );
               }
            }
         }
      }
   }

   @Unique
   private static void permadeathReincarnated$generatePortalOnce(ServerLevel level) {
      BeginningData data = BeginningData.get(level);
      if (!data.portalGenerated) {
         data.portalGenerated = true;
         data.setDirty();
         StructureTemplateManager manager = level.getStructureManager();
         StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "portal"));
         StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.CLOCKWISE_180).setMirror(Mirror.NONE).setIgnoreEntities(false);
         template.placeInWorld(level, PORTAL_POS, PORTAL_POS, settings, level.getRandom(), 3);
      }
   }

   @Unique
   private static void permadeathReincarnated$forceLoadPortal(ServerLevel level) {
      int cx = PORTAL_POS.getX() >> 4;
      int cz = PORTAL_POS.getZ() >> 4;

      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            level.setChunkForced(cx + dx, cz + dz, true);
         }
      }
   }
}
