package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Slime.class)
public abstract class SlimeMagmaSizeMixin {
   @Inject(method = "finalizeSpawn", at = @At("RETURN"))
   private void permadeathReincarnated$forceMaxSize(
      ServerLevelAccessor p_33601_, DifficultyInstance p_33602_, MobSpawnType p_33603_, SpawnGroupData p_33604_, CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      Slime self = (Slime)(Object)this;
      if (!self.level().isClientSide) {
         if (DayGlobalCount.CURRENT_DAY >= 25) {
            if (self.getClass() == MagmaCube.class && !self.getTags().contains("fromZPiglin")) {
               self.setCustomName(Component.literal("Giga-Magmacube").withStyle(ChatFormatting.GOLD));
               self.addTag("gigaMagma");
               self.setSize(16, true);
            } else if (self.getClass() != MagmaCube.class) {
               self.setCustomName(Component.literal("Giga-Slime").withStyle(ChatFormatting.GREEN));
               self.addTag("gigaSlime");
               self.setSize(15, true);
            }
         }
      }
   }

   @Inject(method = "setSize", at = @At("TAIL"))
   private void permadeathReincarnated$scaleGigaHealth(int size, boolean resetHealth, CallbackInfo ci) {
      Slime self = (Slime)(Object)this;
      if (!self.level().isClientSide) {
         AttributeInstance maxHealth = self.getAttribute(Attributes.MAX_HEALTH);
         if (maxHealth != null) {
            double vanillaBaseHealth = size * size;
            double newHealth = permadeathReincarnated$getNewHealth(vanillaBaseHealth, self);
            maxHealth.setBaseValue(newHealth);
            if (resetHealth) {
               self.setHealth(self.getMaxHealth());
            } else if (self.getHealth() > self.getMaxHealth()) {
               self.setHealth(self.getMaxHealth());
            }
         }
      }
   }

   @Unique
   private static double permadeathReincarnated$getNewHealth(double vanillaBaseHealth, Slime self) {
      double newHealth = vanillaBaseHealth;
      if (self.getClass() == MagmaCube.class) {
         if (self.getTags().contains("gigaMagma") && DayGlobalCount.CURRENT_DAY >= 50) {
            newHealth = vanillaBaseHealth * 2.0;
         }
      } else if (self.getTags().contains("gigaSlime")) {
         if (DayGlobalCount.CURRENT_DAY >= 50) {
            newHealth = vanillaBaseHealth * 4.0;
         } else if (DayGlobalCount.CURRENT_DAY >= 25) {
            newHealth = vanillaBaseHealth * 2.0;
         }
      }

      return newHealth;
   }
}
