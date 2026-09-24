package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PlayerBlindnessUnderRainHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  long seed = player.level().getRandom().nextLong();
                  if (day >= 40) {
                     int chance = day >= 50 ? 1 + RandomUtil.RANDOM.nextInt(2500) : 1 + RandomUtil.RANDOM.nextInt(10000);
                     if (chance <= 1 && player.isInWaterOrRain() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS)) {
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_GET_BLINDNESS_UNDER_RAIN_ID);
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1200, 0, false, true));
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
                     }
                  }
               }
            }
         }
      }
   }
}
