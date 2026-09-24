package zeta.org.permadeath_reincarnated.mixins;

import java.util.Set;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import zeta.org.permadeath_reincarnated.systems.MobEffectInstanceAccess;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceAccess {
   @Shadow
   private int duration;
   @Shadow
   private int amplifier;
   @Shadow
   private boolean ambient;
   @Shadow
   private boolean visible;
   @Shadow
   private boolean showIcon;
   @Shadow
   private MobEffectInstance hiddenEffect;
   @Mutable
   @Final
   @Shadow
   private Set<EffectCure> cures;

   @Unique
   @Override
   public void permadeathReincarnated$setDuration(int duration) {
      this.duration = duration;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setAmplifier(int amplifier) {
      this.amplifier = amplifier;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setAmbient(boolean ambient) {
      this.ambient = ambient;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setVisible(boolean visible) {
      this.visible = visible;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setShowIcon(boolean showIcon) {
      this.showIcon = showIcon;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setHiddenEffect(MobEffectInstance hiddenEffect) {
      this.hiddenEffect = hiddenEffect;
   }

   @Unique
   @Override
   public void permadeathReincarnated$setCures(Set<EffectCure> cures) {
      this.cures = cures;
   }

   @Unique
   @Override
   public int permadeathReincarnated$getDuration() {
      return this.duration;
   }

   @Unique
   @Override
   public int permadeathReincarnated$getAmplifier() {
      return this.amplifier;
   }

   @Unique
   @Override
   public boolean permadeathReincarnated$isAmbient() {
      return this.ambient;
   }

   @Unique
   @Override
   public boolean permadeathReincarnated$isVisible() {
      return this.visible;
   }

   @Unique
   @Override
   public boolean permadeathReincarnated$showIcon() {
      return this.showIcon;
   }

   @Unique
   @Override
   public MobEffectInstance permadeathReincarnated$getHiddenEffect() {
      return this.hiddenEffect;
   }

   @Unique
   @Override
   public Set<EffectCure> permadeathReincarnated$getCures() {
      return this.cures;
   }
}
