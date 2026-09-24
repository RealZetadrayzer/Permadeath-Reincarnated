package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class HoglinChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Hoglin hoglin) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 15) {
                           float attackKnockback = day >= 50 ? 8.0F : 2.0F + RandomUtil.RANDOM.nextFloat(1.0F);
                           Objects.requireNonNull(hoglin.getAttribute(Attributes.ATTACK_KNOCKBACK)).setBaseValue(attackKnockback);
                           hoglin.setCustomName(Component.literal("Hoglin Embestidor").withStyle(ChatFormatting.GOLD));
                           hoglin.addTag("hoglinRammer");
                           if (day >= 40) {
                              hoglin.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
                              hoglin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, true));
                           }

                           if (day >= 50) {
                              Objects.requireNonNull(hoglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(80.0);
                              hoglin.setHealth(hoglin.getMaxHealth());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (!target.level().isClientSide) {
         if (target instanceof ServerPlayer player) {
            if (attacker instanceof Hoglin hoglin) {
               if (hoglin.getTags().contains("hoglinRammer")) {
                  double startY = player.getY();
                  UUID id = player.getUUID();
                  ScheduleInTicks.schedule(() -> {
                     ServerPlayer serverPlayer = player.server.getPlayerList().getPlayer(id);
                     if (serverPlayer != null) {
                        double gained = serverPlayer.getY() - startY;
                        if (gained >= 8.0) {
                           PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.HOGLIN_WHO_NEEDS_WIND_CHARGES_ID);
                        }
                     }
                  }, 20);
               }
            }
         }
      }
   }
}
