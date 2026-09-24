package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;

public class DesperateMeasuresAttachmentHelper {
   public static DesperateMeasuresAttachment get(Player player) {
      return (DesperateMeasuresAttachment)player.getData(PermadeathAttachments.DESPERATE_MEASURES_TIMER.get());
   }

   public static Optional<DesperateMeasuresAttachment> getExisting(Player player) {
      return player.getExistingData(PermadeathAttachments.DESPERATE_MEASURES_TIMER.get());
   }
}
