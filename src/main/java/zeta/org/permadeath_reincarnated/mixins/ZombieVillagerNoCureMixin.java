package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.ZombieVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerNoCureMixin {
   @Inject(method = "finishConversion(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("HEAD"), cancellable = true)
   private void permadeath$blockCure(ServerLevel level, CallbackInfo ci) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 10) {
            ci.cancel();
         }
      }
   }
}
