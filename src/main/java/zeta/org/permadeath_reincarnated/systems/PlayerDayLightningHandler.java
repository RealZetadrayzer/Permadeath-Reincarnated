package zeta.org.permadeath_reincarnated.systems;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PlayerDayLightningHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (player.level().dimension().equals(Level.OVERWORLD)) {
                  if (!player.isSpectator()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     long seed = player.level().getRandom().nextLong();
                     if (day >= 60) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100000);
                        if (chance <= 1 && player.level().isDay() && player.level().dimension() == Level.OVERWORLD) {
                           LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(player.level());
                           if (lightningBolt == null) {
                              return;
                           }

                           lightningBolt.setPos(player.getX(), player.getY(), player.getZ());
                           player.level().addFreshEntity(lightningBolt);
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_LIGHTNING_PROBABILITY_ID);
                           player.connection
                              .send(
                                 new ClientboundSoundPacket(
                                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.TRIDENT_RETURN),
                                    SoundSource.VOICE,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    100.0F,
                                    0.5F,
                                    seed
                                 )
                              );
                           Objects.requireNonNull(player.level().getServer())
                              .getPlayerList()
                              .getPlayers()
                              .forEach(
                                 p -> p.displayClientMessage(
                                    Component.literal("A " + player.getName().getString() + " le cayó un rayo.").withStyle(ChatFormatting.RED), false
                                 )
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
