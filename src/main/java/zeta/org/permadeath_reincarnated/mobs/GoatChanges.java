package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.Vec3;
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
public class GoatChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Goat goat) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 45) {
                           goat.setCustomName(Component.literal("Cabras Lanzadoras").withStyle(ChatFormatting.GOLD));
                           goat.addTag("superGoat");
                           goat.setInvulnerable(true);
                           goat.setScreamingGoat(true);
                           goat.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 9, false, true));
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
      if (attacker != null) {
         if (!target.level().isClientSide) {
            if (attacker instanceof Goat goat) {
               if (goat.getTags().contains("superGoat")) {
                  ScheduleInTicks.schedule(() -> throwTarget(goat, target), 3);
               }
            }
         }
      }
   }

   static void throwTarget(LivingEntity goat, LivingEntity target) {
      double d3 = target.getX() - goat.getX();
      double d4 = target.getZ() - goat.getZ();
      float f = RandomUtil.RANDOM.nextInt(21) - 10;
      double d5 = RandomUtil.RANDOM.nextFloat() * 0.5F + 0.2F;
      Vec3 vec3 = new Vec3(d3, 0.0, d4).normalize().scale(d5).yRot(f);
      double d6 = RandomUtil.RANDOM.nextFloat() * 0.5F;
      target.push(vec3.x * 20.0, d6 * 20.0, vec3.z * 20.0);
      target.hurtMarked = true;
      if (target instanceof ServerPlayer player) {
         PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_GOAT_DAMAGE_RECEIVED_ID);
      }
   }
}
