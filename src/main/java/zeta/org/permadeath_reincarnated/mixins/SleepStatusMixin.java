package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(SleepStatus.class)
public class SleepStatusMixin {
   @ModifyReturnValue(method = "sleepersNeeded", at = @At("RETURN"))
   private int modifySleepersNeeded(int original) {
      int day = DayGlobalCount.CURRENT_DAY;
      return day >= 10 ? 4 : 1;
   }
}
