package zeta.org.permadeath_reincarnated.mobs;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class PandaChanges {
   private static final Set<Holder<MobEffect>> EXCLUDED = new HashSet<>();

   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 50) {
            if (event.getEntity() instanceof Panda panda) {
               if (!panda.level().isClientSide) {
                  if (panda.level() instanceof ServerLevel level) {
                     if (panda.isAlive()) {
                        double radius = 16.0;

                        for (Player player : level.getEntitiesOfClass(Player.class, panda.getBoundingBox().inflate(radius))) {
                           Set<Holder<MobEffect>> toRemove = new HashSet<>();

                           for (MobEffectInstance eff : player.getActiveEffects()) {
                              Holder<MobEffect> holder = eff.getEffect();
                              if (((MobEffect)holder.value()).isBeneficial() && !EXCLUDED.contains(holder)) {
                                 toRemove.add(holder);
                              }
                           }

                           for (Holder<MobEffect> holder : toRemove) {
                              player.removeEffect(holder);
                           }

                           if (player instanceof ServerPlayer serverPlayer) {
                              PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_EFFECTS_GONE_ID);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static {
      EXCLUDED.add(PermadeathMobEffectBuilder.POST_MORTEM_EFFECT.getDelegate());
      EXCLUDED.add(PermadeathMobEffectBuilder.INFERNAL_MORTEM_EFFECT.getDelegate());
      EXCLUDED.add(PermadeathMobEffectBuilder.WITHER_TIMER_EFFECT.getDelegate());
   }
}
