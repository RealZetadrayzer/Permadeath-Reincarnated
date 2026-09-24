package zeta.org.permadeath_reincarnated.mixins;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.clientsync.PermadeathClientConsumablePredictionHandler;

@Mixin(Connection.class)
public class ConnectionConsumeMixin {
   @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$channelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
      if (!Minecraft.getInstance().hasSingleplayerServer()) {
         PermadeathClientConsumablePredictionHandler.handlePacket$networkThread(packet, ci);
      }
   }
}
