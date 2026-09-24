package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class NetherMobRainAttachmentHelper {
   public static NetherMobRainAttachment get(Player player) {
      return (NetherMobRainAttachment)player.getData(PermadeathAttachments.NETHER_MOB_RAIN_COOLDOWN.get());
   }

   public static Optional<NetherMobRainAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.NETHER_MOB_RAIN_COOLDOWN.get());
   }
}
