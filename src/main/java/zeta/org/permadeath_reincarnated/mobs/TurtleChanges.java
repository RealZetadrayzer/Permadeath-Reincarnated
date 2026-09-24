package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class TurtleChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof Turtle turtle) {
                  if (turtle.tickCount % 15 == 0) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 40) {
                        AABB area = turtle.getBoundingBox().inflate(1.4);

                        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != turtle)) {
                           float damage = day >= 60 ? 20.0F : (day >= 50 ? 10.0F : 2.0F);
                           if (!(target instanceof Turtle) && !target.hasEffect(MobEffects.CONDUIT_POWER) && !target.hasEffect(MobEffects.WATER_BREATHING)) {
                              target.hurt(level.damageSources().drown(), damage);
                              level.playSound(target, target.blockPosition(), SoundEvents.PLAYER_HURT_DROWN, SoundSource.PLAYERS, 1.0F, 1.0F);
                              if (target instanceof ServerPlayer player) {
                                 PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_TURTLE_NO_OXYGEN_ID);
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
}
