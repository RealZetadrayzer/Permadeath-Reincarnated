package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.systems.attachments.WitherTimerAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.WitherTimerAttachmentHelper;

@EventBusSubscriber
public class PlayerWitherTimerHandler {
   @SubscribeEvent
   public static void onWitherTimerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  WitherTimerAttachment witherTimer = WitherTimerAttachmentHelper.get(player);
                  if (player.isSpectator() || player.isCreative()) {
                     player.removeTag("witherTimer");
                     player.removeEffect(PermadeathMobEffectBuilder.WITHER_TIMER_EFFECT.getDelegate());
                     witherTimer.setClean();
                  }

                  if (!player.isSpectator() && !player.isCreative()) {
                     if (day >= 60) {
                        if (!player.getTags().contains("witherTimer")) {
                           player.addTag("witherTimer");
                           if (!witherTimer.isWitherTimerOn()) {
                              witherTimer.startWitherTimer();
                           }
                        }

                        if (witherTimer.isWitherTimerOn() && player.getTags().contains("witherTimer")) {
                           witherTimer.tick();
                           player.addEffect(
                              new MobEffectInstance(
                                 PermadeathMobEffectBuilder.WITHER_TIMER_EFFECT.getDelegate(), witherTimer.getRemainingTicks() * 20, 0, true, true
                              )
                           );
                           if (witherTimer.getRemainingTicks() <= 0) {
                              WitherBoss witherBoss = (WitherBoss)EntityType.WITHER.create(level);
                              player.displayClientMessage(
                                 Component.literal("¡Un ")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE)
                                    .append(
                                       Component.literal("Wither Boss")
                                          .withStyle(ChatFormatting.DARK_PURPLE)
                                          .append(
                                             Component.literal(" aparecio en tu posicion!")
                                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                                                .withStyle(Style.EMPTY.withBold(false))
                                          )
                                    ),
                                 false
                              );
                              player.removeTag("witherTimer");
                              witherTimer.setClean();
                              if (witherBoss != null) {
                                 witherBoss.setInvulnerableTicks(220);
                                 witherBoss.setCustomName(
                                    Component.literal("Wither de " + player.getName().getString())
                                       .withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
                                 );
                                 witherBoss.addTag("fromTimer");
                                 witherBoss.setPos(player.getX(), player.getY(), player.getZ());
                                 level.playSound(null, witherBoss.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.VOICE, 0.5F, 0.5F);
                                 level.playSound(null, witherBoss.blockPosition(), SoundEvents.WITHER_HURT, SoundSource.VOICE, 1.0F, 0.5F);
                                 level.playSound(null, witherBoss.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.VOICE, 0.8F, 0.8F);
                                 level.sendParticles(
                                    ParticleTypes.END_ROD, witherBoss.getX(), witherBoss.getY() + 0.5, witherBoss.getZ(), 80, 0.0, 0.0, 0.0, 0.1
                                 );
                                 level.sendParticles(
                                    ParticleTypes.SMALL_FLAME, witherBoss.getX(), witherBoss.getY() + 0.5, witherBoss.getZ(), 80, 0.15, 0.15, 0.15, 0.1
                                 );
                                 level.addFreshEntity(witherBoss);
                              }
                           } else {
                              witherTimer.setClean();
                           }
                        }
                     } else if (player.getTags().contains("witherTimer")) {
                        witherTimer.reset();
                        witherTimer.setClean();
                        player.removeTag("witherTimer");
                        player.removeEffect(PermadeathMobEffectBuilder.WITHER_TIMER_EFFECT.getDelegate());
                     }
                  }
               }
            }
         }
      }
   }
}
