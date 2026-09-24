package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class TropicalFishChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 50) {
            if (event.getEntity() instanceof TropicalFish tropicalFish) {
               if (!tropicalFish.level().isClientSide) {
                  if (tropicalFish.level() instanceof ServerLevel level) {
                     if (tropicalFish.isAlive()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        double radius = day >= 55 ? 32.0 : 16.0;

                        for (Player player : level.getEntitiesOfClass(Player.class, tropicalFish.getBoundingBox().inflate(radius))) {
                           player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600, 0, false, true));
                           if (day >= 55) {
                              player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 0, false, true));
                           }

                           if (player instanceof ServerPlayer serverPlayer) {
                              PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_DARKNESS_FISH_ID);
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
