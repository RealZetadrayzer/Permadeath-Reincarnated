package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {
   @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
   private void modifyCap(CallbackInfoReturnable<Integer> cir) {
      MobCategory self = (MobCategory)(Object)this;
      if (self == MobCategory.MONSTER && (Boolean)PermadeathConfig.DOUBLED_MOBS.get() && DayGlobalCount.CURRENT_DAY >= 10) {
         cir.setReturnValue((Integer)PermadeathConfig.MOB_CAP_MONSTER.get());
      }
   }
}
