package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class WitherTimerAttachmentHelper {
   public static WitherTimerAttachment get(Player player) {
      return (WitherTimerAttachment)player.getData(PermadeathAttachments.WITHER_TIMER.get());
   }

   public static Optional<WitherTimerAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.WITHER_TIMER.get());
   }
}
