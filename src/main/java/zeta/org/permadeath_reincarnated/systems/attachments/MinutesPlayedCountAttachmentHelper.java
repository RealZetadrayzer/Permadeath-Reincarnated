package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class MinutesPlayedCountAttachmentHelper {
   public static MinutesPlayedCountAttachment get(Player player) {
      return (MinutesPlayedCountAttachment)player.getData(PermadeathAttachments.MINUTES_PLAYED.get());
   }

   public static Optional<MinutesPlayedCountAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.MINUTES_PLAYED.get());
   }
}
