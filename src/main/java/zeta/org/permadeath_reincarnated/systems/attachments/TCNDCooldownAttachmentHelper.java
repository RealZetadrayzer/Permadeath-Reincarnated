package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class TCNDCooldownAttachmentHelper {
   public static TCNDCooldownAttachment get(Player player) {
      return (TCNDCooldownAttachment)player.getData(PermadeathAttachments.TCND_COOLDOWN.get());
   }

   public static Optional<TCNDCooldownAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.TCND_COOLDOWN.get());
   }
}
