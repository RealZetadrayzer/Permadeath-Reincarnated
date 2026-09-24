package zeta.org.permadeath_reincarnated.systems;

import java.util.Random;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber
public class PlayerLevitationOnNightHandler {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (player.level().dimension().equals(Level.OVERWORLD)) {
                  if (!player.isSpectator()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     long seed = player.level().getRandom().nextLong();
                     if (day >= 50) {
                        int chance = 1 + RANDOM.nextInt(2500);
                        int duration = 60 + RANDOM.nextInt(341);
                        if (chance <= 1 && player.level().isNight() && !player.hasEffect(MobEffects.LEVITATION)) {
                           player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, duration, 0, false, true));
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
}
