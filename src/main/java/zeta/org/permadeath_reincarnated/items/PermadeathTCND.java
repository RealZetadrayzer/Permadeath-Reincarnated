package zeta.org.permadeath_reincarnated.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import zeta.org.permadeath_reincarnated.systems.PlayerSleepingHandler;
import zeta.org.permadeath_reincarnated.systems.attachments.EnhancedTCNDCooldownAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.EnhancedTCNDCooldownAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.attachments.TCNDCooldownAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.TCNDCooldownAttachmentHelper;

public class PermadeathTCND extends Item {
   public PermadeathTCND(Properties properties) {
      super(properties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      TCNDCooldownAttachment cooldown = TCNDCooldownAttachmentHelper.get(player);
      EnhancedTCNDCooldownAttachment subCooldown = EnhancedTCNDCooldownAttachmentHelper.get(player);
      if (!level.isClientSide) {
         if (subCooldown.getRemainingTicks() > 0) {
            player.addTag("hide");
            PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 40);
            player.displayClientMessage(
               Component.literal("No puedes usar este item mientras tengas el poder de la ")
                  .withStyle(ChatFormatting.LIGHT_PURPLE)
                  .append(
                     Component.literal("Leyenda Oscura")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .append(Component.literal(" activo.").withStyle(ChatFormatting.LIGHT_PURPLE))
                  ),
               true
            );
            return InteractionResultHolder.fail(stack);
         }

         if (cooldown.getRemainingTicks() <= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 0, true, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2, true, true, true));
            player.sendSystemMessage(
               Component.literal("Sientes que el poder de ")
                  .withStyle(ChatFormatting.YELLOW)
                  .append(
                     Component.literal("una leyenda")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal(" te da vida...").withStyle(ChatFormatting.YELLOW))
                  )
            );
            level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
            level.playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.PLAYERS, 1.0F, 2.0F);
            level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
            level.playSound(null, player.blockPosition(), SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
            if (level instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.6, 0.8, 0.6, 0.05);
               serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 80, 0.6, 0.8, 0.6, 0.05);
               serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            }

            cooldown.startCooldown();
         } else {
            int remainingSeconds = cooldown.getRemainingTicks();
            String formatted = formatTime(remainingSeconds);
            if (!formatted.isEmpty()) {
               player.addTag("hide");
               PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 40);
               player.displayClientMessage(
                  Component.literal("No puedes usar este item por ")
                     .withStyle(ChatFormatting.YELLOW)
                     .append(Component.literal(formatted).withStyle(ChatFormatting.GOLD)),
                  true
               );
            }
         }
      }

      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
   }

   private static String formatTime(int totalSeconds) {
      if (totalSeconds <= 0) {
         return "";
      } else {
         int minutes = totalSeconds / 60;
         int seconds = totalSeconds % 60;
         String minuteText = minutes == 1 ? " minuto" : " minutos";
         String secondText = seconds == 1 ? " segundo" : " segundos";
         if (minutes > 0 && seconds > 0) {
            return minutes + minuteText + " y " + seconds + secondText;
         } else {
            return minutes > 0 ? minutes + minuteText : seconds + secondText;
         }
      }
   }
}
