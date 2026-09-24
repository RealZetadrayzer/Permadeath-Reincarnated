package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.systems.attachments.EnhancedTCNDCooldownAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.EnhancedTCNDCooldownAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.attachments.TCNDCooldownAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.TCNDCooldownAttachmentHelper;

@EventBusSubscriber
public class PlayerTCNDHandler {
   @SubscribeEvent
   public static void onCooldownTickTCND(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  if (player.level() instanceof ServerLevel level) {
                     TCNDCooldownAttachment cooldown = TCNDCooldownAttachmentHelper.get(player);
                     if (cooldown.isOnCooldown()) {
                        cooldown.tick();
                        if (cooldown.getRemainingTicks() <= 0) {
                           player.displayClientMessage(
                              Component.literal("¡Ya puedes usar el item ")
                                 .withStyle(ChatFormatting.YELLOW)
                                 .append(Component.literal("T.C.N.D").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                                 .append(Component.literal(" otra vez!").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false))),
                              false
                           );
                           level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
                           level.playSound(null, player.blockPosition(), (SoundEvent)SoundEvents.TRIDENT_THUNDER.value(), SoundSource.PLAYERS, 0.8F, 0.5F);
                           level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 0.8F);
                           level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.25);
                           level.sendParticles(ParticleTypes.SMALL_FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.5);
                           level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.15, 0.15, 0.15, 0.05);
                           cooldown.setClean();
                        } else {
                           cooldown.setClean();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCooldownTickEnhancedTCND(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  if (player.level() instanceof ServerLevel level) {
                     EnhancedTCNDCooldownAttachment cooldown = EnhancedTCNDCooldownAttachmentHelper.get(player);
                     if (cooldown.isOnCooldown()) {
                        cooldown.tick();
                        if (cooldown.getRemainingTicks() <= 0) {
                           player.displayClientMessage(
                              Component.literal("¡Ya puedes usar el item ")
                                 .withStyle(ChatFormatting.LIGHT_PURPLE)
                                 .append(Component.literal("T.C.N.D").withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}))
                                 .append(Component.literal(" otra vez!").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(Style.EMPTY.withBold(false))),
                              false
                           );
                           level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
                           level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.8F, 0.5F);
                           level.playSound(null, player.blockPosition(), (SoundEvent)SoundEvents.TRIDENT_THUNDER.value(), SoundSource.PLAYERS, 0.8F, 0.5F);
                           level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 0.8F);
                           level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.25);
                           level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.5);
                           level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.15, 0.15, 0.15, 0.05);
                           cooldown.setClean();
                        } else {
                           cooldown.setClean();
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
