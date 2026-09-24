package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class BeginningCurseAttachmentHelper {
   public static BeginningCurseAttachment get(Player player) {
      return (BeginningCurseAttachment)player.getData(PermadeathAttachments.CURSE.get());
   }

   public static Optional<BeginningCurseAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.CURSE.get());
   }
}
