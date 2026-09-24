package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class EnhancedTCNDCooldownAttachmentHelper {
   public static EnhancedTCNDCooldownAttachment get(Player player) {
      return (EnhancedTCNDCooldownAttachment)player.getData(PermadeathAttachments.ENHANCED_TCND_COOLDOWN.get());
   }

   public static Optional<EnhancedTCNDCooldownAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.ENHANCED_TCND_COOLDOWN.get());
   }
}
