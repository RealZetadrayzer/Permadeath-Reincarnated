package zeta.org.permadeath_reincarnated.systems.clientsync;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PermadeathClientConsumablePredictionHandler {
   private static boolean cancelNextServerBurp = false;
   @Nullable
   private static SoundEvent soundToCancel = null;
   private static boolean predictedConsumeActive = false;

   public static void handleItemStackUsage(ItemStack stack, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player != null && mc.level != null) {
         if (player.isUsingItem()) {
            if (ItemStack.isSameItemSameComponents(player.getUseItem(), stack)) {
               if (player.getUseItemRemainingTicks() <= 0) {
                  if (PermadeathClientConsumablesUtil.isConsumableLike(stack, player)) {
                     predictedConsumeActive = true;
                     playPredictedConsumeSound(player, stack);
                     if (requiresBurp(stack)) {
                        playBurpSound(player);
                        cancelNextServerBurp = true;
                     }

                     consumeItem(player);
                     ci.cancel();
                  }
               }
            }
         }
      }
   }

   public static void handlePacket$networkThread(Packet<?> packet, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (mc.level != null && player != null && packet != null) {
         if (predictedConsumeActive) {
            if (packet instanceof ClientboundEntityEventPacket statusPacket) {
               Entity entity;
               try {
                  entity = statusPacket.getEntity(mc.level);
               } catch (Throwable t) {
                  return;
               }

               if (entity != player) {
                  return;
               }

               if (statusPacket.getEventId() == 9) {
                  predictedConsumeActive = false;
                  ci.cancel();
               }
            }
         }
      }
   }

   public static void handleEntityTrackerUpdate$clientThread(ClientboundSetEntityDataPacket packet, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (mc.level != null && player != null) {
         if (predictedConsumeActive) {
            Entity entity = mc.level.getEntity(packet.id());
            if (entity == player) {
               List<DataValue<?>> packed = packet.packedItems();
               if (!packed.isEmpty()) {
                  int consumptionId = PermadeathClientPlayerSynchedDataIds.CONSUMPTION.id();
                  List<DataValue<?>> filtered = packed.stream().filter(v -> v != null && v.id() != consumptionId).toList();
                  if (filtered.size() != packed.size()) {
                     try {
                        player.getEntityData().assignValues(filtered);
                        predictedConsumeActive = false;
                        ci.cancel();
                     } catch (Throwable var9) {
                     }
                  }
               }
            }
         }
      }
   }

   public static void handleServerSounds(SoundEvent sound, CallbackInfo ci) {
      if (sound == SoundEvents.PLAYER_BURP && cancelNextServerBurp) {
         cancelNextServerBurp = false;
         ci.cancel();
      } else {
         if (soundToCancel != null && sound == soundToCancel) {
            soundToCancel = null;
            ci.cancel();
         }
      }
   }

   private static void playPredictedConsumeSound(LocalPlayer player, ItemStack stack) {
      Minecraft mc = Minecraft.getInstance();
      UseAnim anim = stack.getUseAnimation();
      SoundEvent sound = anim == UseAnim.DRINK ? SoundEvents.GENERIC_DRINK : SoundEvents.GENERIC_EAT;
      soundToCancel = sound;
      if (mc.level != null) {
         mc.level
            .playSound(player, player.getX(), player.getY(), player.getZ(), sound, SoundSource.NEUTRAL, 1.0F, Mth.nextFloat(player.getRandom(), 0.8F, 1.2F));
      }
   }

   private static boolean requiresBurp(ItemStack stack) {
      return stack.getUseAnimation() == UseAnim.EAT || stack.is(Items.HONEY_BOTTLE);
   }

   private static void playBurpSound(LocalPlayer player) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null) {
         mc.level
            .playSound(
               player,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.PLAYER_BURP,
               SoundSource.PLAYERS,
               0.5F,
               Mth.nextFloat(player.getRandom(), 0.9F, 1.0F)
            );
      }
   }

   private static void consumeItem(LocalPlayer player) {
      if (player.isUsingItem()) {
         InteractionHand hand = player.getUsedItemHand();
         ItemStack activeStack = player.getUseItem();
         ItemStack handStack = player.getItemInHand(hand);
         if (!ItemStack.isSameItemSameComponents(activeStack, handStack)) {
            player.stopUsingItem();
         } else {
            if (!activeStack.isEmpty()) {
               Minecraft mc = Minecraft.getInstance();
               if (mc.level == null) {
                  return;
               }

               ItemStack result = activeStack.finishUsingItem(mc.level, player);
               if (!ItemStack.isSameItemSameComponents(result, activeStack)) {
                  player.setItemInHand(hand, result);
               }

               player.stopUsingItem();
            }
         }
      }
   }
}
