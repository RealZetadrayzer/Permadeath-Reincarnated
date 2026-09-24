package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class EndermiteChanges {
   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof Endermite endermite) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  if (day >= 55) {
                     AABB area = endermite.getBoundingBox().inflate(1.4);

                     for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != endermite)) {
                        if (target instanceof ServerPlayer player && !player.isSpectator()) {
                           Vec3 pos = player.position();
                           player.teleportTo(player.serverLevel(), pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
                           player.setDeltaMovement(0.0, 0.0, 0.0);
                           player.hurtMarked = true;
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_PERSONAL_SPACE_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
