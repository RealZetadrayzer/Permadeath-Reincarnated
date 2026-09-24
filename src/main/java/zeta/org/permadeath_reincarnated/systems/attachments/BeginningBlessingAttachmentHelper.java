package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class BeginningBlessingAttachmentHelper {
   public static BeginningBlessingAttachment get(Player player) {
      return (BeginningBlessingAttachment)player.getData(PermadeathAttachments.BLESSING.get());
   }

   public static Optional<BeginningBlessingAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.BLESSING.get());
   }
}
