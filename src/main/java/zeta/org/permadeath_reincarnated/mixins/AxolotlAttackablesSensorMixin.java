package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.AxolotlAttackablesSensor;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.mobs.PassiveHostileMobs;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(AxolotlAttackablesSensor.class)
public abstract class AxolotlAttackablesSensorMixin {
   @Inject(method = "isMatchingEntity", at = @At("HEAD"), cancellable = true)
   private void allowConditionalPlayers(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         if (attacker instanceof Axolotl axolotl) {
            if (!axolotl.getTags().contains("isGuardian")) {
               if (target instanceof ServerPlayer) {
                  if (PassiveHostileMobs.isPassiveHostileType(axolotl.getType())) {
                     if (target.distanceToSqr(attacker) <= 64.0) {
                        cir.setReturnValue(true);
                     }
                  }
               }
            }
         }
      }
   }
}
