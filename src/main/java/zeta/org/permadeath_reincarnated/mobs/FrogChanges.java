package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class FrogChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Frog frog) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 20) {
                           if (!frog.getTags().contains("fromBogged")) {
                              frog.setCustomName(Component.literal("Rana Pegajosa").withStyle(ChatFormatting.GREEN));
                           }

                           frog.addTag("stickyFrog");
                           frog.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                           if (day >= 45) {
                              AttributeInstance frogHealth = Objects.requireNonNull(frog.getAttribute(Attributes.MAX_HEALTH));
                              frogHealth.setBaseValue(50.0);
                              frog.setHealth(frog.getMaxHealth());
                              frog.setCustomName(Component.literal("Rana Ultra-Pegajosa").withStyle(ChatFormatting.GREEN));
                              frog.addTag("ultraFrog");
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
      if (attacker != null) {
         if (target.level() instanceof ServerLevel level) {
            if (attacker instanceof Frog frog) {
               if (frog.getTags().contains("ultraFrog")) {
                  if (!target.level().isClientSide) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     Tadpole tadpole = (Tadpole)EntityType.TADPOLE.create(level);
                     int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                     int chanceThreshold = day >= 60 ? 15 : 5;
                     if (chance <= chanceThreshold && tadpole != null) {
                        ((MobSpawnTypeAccessor)tadpole).setSpawnType(MobSpawnType.MOB_SUMMONED);
                        if (day >= 30) {
                           tadpole.addTag("onDeathCloud");
                        }

                        tadpole.addTag("fromBogged");
                        tadpole.addTag("shouldNaturallyDespawn");
                        tadpole.setCustomName(Component.literal("Renacuajo Pegajoso").withStyle(ChatFormatting.GREEN));
                        tadpole.moveTo(target.getX(), target.getY() + 0.5, target.getZ());
                        tadpole.removeAllEffects();
                        tadpole.addEffect(new MobEffectInstance(MobEffects.OOZING, -1, 0, false, true));
                        level.addFreshEntity(tadpole);
                     }
                  }
               }
            }
         }
      }
   }
}
