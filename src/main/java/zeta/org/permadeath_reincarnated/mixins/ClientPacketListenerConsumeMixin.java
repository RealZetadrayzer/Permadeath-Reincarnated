package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.clientsync.PermadeathClientConsumablePredictionHandler;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerConsumeMixin {
   @Inject(method = "handleSoundEvent(Lnet/minecraft/network/protocol/game/ClientboundSoundPacket;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$onSound(ClientboundSoundPacket packet, CallbackInfo ci) {
      if (!Minecraft.getInstance().hasSingleplayerServer()) {
         PermadeathClientConsumablePredictionHandler.handleServerSounds((SoundEvent)packet.getSound().value(), ci);
      }
   }

   @Inject(method = "handleSoundEntityEvent(Lnet/minecraft/network/protocol/game/ClientboundSoundEntityPacket;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$onSoundEntity(ClientboundSoundEntityPacket packet, CallbackInfo ci) {
      if (!Minecraft.getInstance().hasSingleplayerServer()) {
         PermadeathClientConsumablePredictionHandler.handleServerSounds((SoundEvent)packet.getSound().value(), ci);
      }
   }

   @Inject(method = "handleSetEntityData(Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$onSetEntityData(ClientboundSetEntityDataPacket packet, CallbackInfo ci) {
      if (!Minecraft.getInstance().hasSingleplayerServer()) {
         PermadeathClientConsumablePredictionHandler.handleEntityTrackerUpdate$clientThread(packet, ci);
      }
   }
}
