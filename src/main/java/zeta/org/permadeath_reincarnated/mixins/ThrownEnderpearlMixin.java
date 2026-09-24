package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {
   @Unique
   private static final Integer permadeathReincarnated$DAY = DayGlobalCount.CURRENT_DAY;

   @ModifyConstant(method = "onHit", constant = @Constant(floatValue = 0.05F, ordinal = 0))
   private float permadeathreincarnated$doubleEndermiteChance(float original) {
      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return original;
      } else {
         return permadeathReincarnated$DAY >= 55
            ? Math.min(original * 4.0F, 1.0F)
            : (permadeathReincarnated$DAY >= 45 ? Math.min(original * 2.0F, 1.0F) : original);
      }
   }

   @Inject(
      method = "onHit",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0)
   )
   private void permadeathreincarnated$endermiteLogic(HitResult result, CallbackInfo ci) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         ThrownEnderpearl pearl = (ThrownEnderpearl)(Object)this;
         if (pearl.level() instanceof ServerLevel level) {
            if (pearl.getOwner() instanceof ServerPlayer player) {
               if (permadeathReincarnated$DAY >= 30) {
                  PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_SUMMON_DEATH_ENDERMITE_ID);
               }

               if (permadeathReincarnated$DAY >= 40) {
                  int amount = switch (RandomUtil.RANDOM.nextInt(3)) {
                     case 0 -> permadeathReincarnated$DAY >= 55 ? 2 : 1;
                     case 1 -> permadeathReincarnated$DAY >= 55 ? 4 : 2;
                     default -> permadeathReincarnated$DAY >= 55 ? 6 : 4;
                  };

                  for (int i = 1; i < amount; i++) {
                     Endermite endermite = (Endermite)EntityType.ENDERMITE.create(level);
                     if (endermite != null) {
                        endermite.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        level.addFreshEntity(endermite);
                        if (amount > 1) {
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_TOW_OR_MORE_ENDERMITE_SUMMON_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
