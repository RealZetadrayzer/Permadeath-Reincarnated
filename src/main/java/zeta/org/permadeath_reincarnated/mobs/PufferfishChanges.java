package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class PufferfishChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 60) {
            if (event.getEntity() instanceof Pufferfish pufferfish) {
               if (!pufferfish.level().isClientSide) {
                  if (pufferfish.level() instanceof ServerLevel level) {
                     if (pufferfish.isAlive()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        double radius = 8.0;

                        for (Player player : level.getEntitiesOfClass(Player.class, pufferfish.getBoundingBox().inflate(radius))) {
                           player.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 24, false, true));
                           if (player instanceof ServerPlayer serverPlayer) {
                              PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_PUFFERFISH_POISON_25_ID);
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
