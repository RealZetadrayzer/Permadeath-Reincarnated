package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(MonsterRoomFeature.class)
public class MonsterRoomFeatureMixin {
   @Inject(method = "place", at = @At("HEAD"), cancellable = true)
   private void permadeath$disableMonsterRoomsAfterDay40(FeaturePlaceContext<NoneFeatureConfiguration> ctx, CallbackInfoReturnable<Boolean> cir) {
      if (!ctx.level().isClientSide()) {
         if (DayGlobalCount.CURRENT_DAY >= 40) {
            cir.setReturnValue(false);
         }
      }
   }
}
