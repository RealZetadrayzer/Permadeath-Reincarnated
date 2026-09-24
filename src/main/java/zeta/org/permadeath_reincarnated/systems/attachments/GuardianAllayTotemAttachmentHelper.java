package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class GuardianAllayTotemAttachmentHelper {
   public static GuardianAllayTotemAttachment get(Player player) {
      return (GuardianAllayTotemAttachment)player.getData(PermadeathAttachments.GUARDIAN_COOLDOWN.get());
   }

   public static Optional<GuardianAllayTotemAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.GUARDIAN_COOLDOWN.get());
   }
}
