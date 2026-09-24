package zeta.org.permadeath_reincarnated.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PermadeathNetworkingDemon(int crystalCount, double dragonHealthPercent, boolean isEnraged) implements CustomPacketPayload {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "demon_fight_sync");
   public static final Type<PermadeathNetworkingDemon> TYPE = new Type(ID);
   public static final StreamCodec<FriendlyByteBuf, PermadeathNetworkingDemon> STREAM_CODEC = StreamCodec.of(
      PermadeathNetworkingDemon::encode, PermadeathNetworkingDemon::decode
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   private static void encode(FriendlyByteBuf buf, PermadeathNetworkingDemon payload) {
      buf.writeInt(payload.crystalCount);
      buf.writeDouble(payload.dragonHealthPercent);
      buf.writeBoolean(payload.isEnraged);
   }

   private static PermadeathNetworkingDemon decode(FriendlyByteBuf buf) {
      return new PermadeathNetworkingDemon(buf.readInt(), buf.readDouble(), buf.readBoolean());
   }
}
