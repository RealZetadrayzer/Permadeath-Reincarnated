package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class BurnedTotemCountAttachmentHelper {
   public static BurnedTotemCountAttachment get(Player player) {
      return (BurnedTotemCountAttachment)player.getData(PermadeathAttachments.BURNED_TOTEM_COUNT.get());
   }

   public static Optional<BurnedTotemCountAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.BURNED_TOTEM_COUNT.get());
   }
}
