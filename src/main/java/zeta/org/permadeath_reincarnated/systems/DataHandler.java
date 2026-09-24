package zeta.org.permadeath_reincarnated.systems;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;
import org.slf4j.Logger;
import zeta.org.permadeath_reincarnated.systems.timers.DeathTrainTimerGlobalHandler;
import zeta.org.permadeath_reincarnated.systems.timers.DeathtrainGlobalState;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalHandler;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalState;
import zeta.org.permadeath_reincarnated.systems.timers.ShulkerDoubledRatesGlobalHandler;
import zeta.org.permadeath_reincarnated.systems.timers.ShulkerDoubledRatesGlobalState;

public class DataHandler {
   private static final Logger LOGGER = LogUtils.getLogger();

   @SubscribeEvent
   public void onServerTick(Pre event) {
      ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
      if (overworld != null) {
         if (!overworld.isClientSide) {
            StartupData overData = StartupData.get(overworld);
            DeathTrainTimerGlobalHandler.initialize(overworld);
            DeathTrainTimerGlobalHandler.tick(overworld);
            LifeOrbTimerGlobalHandler.initialize(overworld);
            LifeOrbTimerGlobalHandler.tick(overworld);
            ShulkerDoubledRatesGlobalHandler.initialize(overworld);
            ShulkerDoubledRatesGlobalHandler.tick(overworld);
            DayGlobalCount.CURRENT_DAY = DeathTrainTimerGlobalHandler.getDay();
            DeathtrainGlobalState.CURRENT_STATE = DeathTrainTimerGlobalHandler.getCurrentState();
            LifeOrbTimerGlobalState.CURRENT_STATE = LifeOrbTimerGlobalHandler.getState();
            ShulkerDoubledRatesGlobalState.CURRENT_STATE = ShulkerDoubledRatesGlobalHandler.getState();
            event.getServer().setPvpAllowed(DayGlobalCount.CURRENT_DAY >= 40);
            if (DayGlobalCount.CURRENT_DAY >= 40 && !overData.didBegPortalGenerate()) {
               overworld.getServer().getAllLevels().forEach(levels -> StartupData.get(levels).setBegPortalGenerated(true));
               long seed = overworld.getRandom().nextLong();
               Marker marker = (Marker)EntityType.MARKER.create(overworld);
               StructureTemplateManager manager = overworld.getStructureManager();
               StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "portal"));
               StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false);
               double minX = -3000.0;
               double maxX = 3000.0;
               double minZ = -3000.0;
               double maxZ = 3000.0;
               double x = Math.floor(Math.random() * (maxX - minX + 1.0)) + minX;
               double z = Math.floor(Math.random() * (maxZ - minZ + 1.0)) + minZ;
               BlockPos pos = new BlockPos((int)x, 229, (int)z);
               int cx = pos.getX() >> 4;
               int cz = pos.getZ() >> 4;

               for (int dx = -1; dx <= 1; dx++) {
                  for (int dz = -1; dz <= 1; dz++) {
                     overworld.setChunkForced(cx + dx, cz + dz, true);
                  }
               }

               template.placeInWorld(overworld, pos, pos, settings, overworld.getRandom(), 3);
               Objects.requireNonNull(overworld.getServer())
                  .getPlayerList()
                  .getPlayers()
                  .forEach(
                     p -> {
                        p.displayClientMessage(
                           Component.literal("El ")
                              .withStyle(ChatFormatting.YELLOW)
                              .append(Component.literal("Portal de The Beginning").withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withBold(true)))
                              .append(Component.literal(" se ha generado en un parte aleatoria del mundo.").withStyle(ChatFormatting.YELLOW)),
                           false
                        );
                        p.connection
                           .send(
                              new ClientboundSoundPacket(
                                 BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_ACTIVATE),
                                 SoundSource.VOICE,
                                 p.getX(),
                                 p.getY(),
                                 p.getZ(),
                                 0.5F,
                                 1.0F,
                                 seed
                              )
                           );
                     }
                  );
               if (marker != null) {
                  BlockPos markerPos = new BlockPos((int)x + 8, 240, (int)z - 1);
                  marker.setPos(markerPos.getX(), markerPos.getY(), markerPos.getZ());
                  marker.addTag("begPortalLocation");
                  LOGGER.info("[Permadeath Monitor] El Portal a The Beginning se genero en: {}", marker.blockPosition());
                  int mcx = markerPos.getX() >> 4;
                  int mcz = markerPos.getZ() >> 4;

                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dz = -1; dz <= 1; dz++) {
                        overworld.setChunkForced(mcx + dx, mcz + dz, true);
                     }
                  }

                  overworld.addFreshEntity(marker);
               }
            }
         }
      }
   }
}
