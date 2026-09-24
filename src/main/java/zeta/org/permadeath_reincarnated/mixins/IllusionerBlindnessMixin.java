package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@Mixin(targets = "net.minecraft.world.entity.monster.Illusioner$IllusionerBlindnessSpellGoal")
public abstract class IllusionerBlindnessMixin {
   @ModifyConstant(method = "performSpellCasting()V", constant = @Constant(intValue = 400))
   private int permadeath$doubleBlindnessDuration(int original) {
      return DayGlobalCount.CURRENT_DAY >= 15 && PermadeathConfig.CUSTOM_CHANGES.get() ? original * 2 : original;
   }

   @Redirect(
      method = "performSpellCasting()V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"
      )
   )
   private boolean permadeath$awardWhenBlindnessApplied(LivingEntity target, MobEffectInstance effect, Entity source) {
      boolean applied = target.addEffect(effect, source);
      if (!applied) {
         return false;
      }

      if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         return true;
      }

      if (DayGlobalCount.CURRENT_DAY < 15) {
         return true;
      }

      if (effect.getEffect() != MobEffects.BLINDNESS) {
         return true;
      }

      if (target instanceof ServerPlayer player && !player.level().isClientSide) {
         PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.ILLUSIONER_MAGICAL_BLINDNESS_ID);
      }

      return true;
   }
}
