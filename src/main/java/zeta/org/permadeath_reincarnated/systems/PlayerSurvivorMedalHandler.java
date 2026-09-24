package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import zeta.org.permadeath_reincarnated.systems.attachments.BurnedTotemCountAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.BurnedTotemCountAttachmentHelper;

@EventBusSubscriber
public class PlayerSurvivorMedalHandler {
   @SubscribeEvent
   public static void onTotemBurned(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof ItemEntity item) {
         if (item.isOnFire() || item.isInLava()) {
            ItemStack stack = item.getItem();
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
               if (item.getOwner() instanceof ServerPlayer player) {
                  if (player.level() instanceof ServerLevel level) {
                     if (DayGlobalCount.CURRENT_DAY == 55) {
                        BurnedTotemCountAttachment attachment = BurnedTotemCountAttachmentHelper.get(player);
                        if (!attachment.isComplete()) {
                           attachment.addBurnedTotem();
                           if (attachment.isReadyToComplete()) {
                              attachment.complete();
                              player.displayClientMessage(
                                 Component.literal("¡Enhorabuena ")
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.GOLD))
                                    .append(Component.literal(" obtuviste la "))
                                    .withStyle(ChatFormatting.YELLOW)
                                    .append(
                                       Component.literal("Medalla de Superviviente")
                                          .withStyle(ChatFormatting.GOLD)
                                          .append(Component.literal("!").withStyle(ChatFormatting.YELLOW))
                                    ),
                                 false
                              );
                              level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
                              level.playSound(null, player.blockPosition(), (SoundEvent)SoundEvents.TRIDENT_THUNDER.value(), SoundSource.PLAYERS, 0.8F, 0.5F);
                              level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 0.8F);
                              level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 0.5, player.getZ(), 100, 0.15, 0.15, 0.15, 0.5);
                              level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 200, 0.15, 0.15, 0.15, 0.05);
                              level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                              ItemStack medalla = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
                              CompoundTag tag = new CompoundTag();
                              tag.putBoolean("medalla", true);
                              medalla.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                              medalla.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                              Component name = Component.literal("")
                                 .append(Component.literal("[").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.DARK_RED)))
                                 .append(Component.literal("☠").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.RED)))
                                 .append(Component.literal("]").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.DARK_RED)))
                                 .append(
                                    Component.literal(" ℑ ").withStyle(style -> style.withObfuscated(true).withColor(ChatFormatting.WHITE).withItalic(false))
                                 )
                                 .append(
                                    Component.literal("Medalla de Superviviente")
                                       .withStyle(style -> style.withBold(true).withItalic(false).withColor(ChatFormatting.GOLD))
                                 )
                                 .append(
                                    Component.literal(" ℑ ").withStyle(style -> style.withObfuscated(true).withColor(ChatFormatting.WHITE).withItalic(false))
                                 )
                                 .append(Component.literal("[").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.DARK_RED)))
                                 .append(Component.literal("☠").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.RED)))
                                 .append(Component.literal("]").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.DARK_RED)));
                              medalla.set(DataComponents.CUSTOM_NAME, name);
                              if (!player.getInventory().add(medalla)) {
                                 player.drop(medalla, false);
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
