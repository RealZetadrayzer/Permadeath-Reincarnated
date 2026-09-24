package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.PiglinVariantAccess;

@Mixin(AbstractPiglin.class)
public abstract class PiglinVariantsMixin implements PiglinVariantAccess {
   @Unique
   private static final EntityDataAccessor<Integer> PERMADEATH_VARIANT = SynchedEntityData.defineId(AbstractPiglin.class, EntityDataSerializers.INT);

   @Inject(method = "defineSynchedData", at = @At("TAIL"))
   private void permadeath$defineData(Builder builder, CallbackInfo ci) {
      builder.define(PERMADEATH_VARIANT, 0);
   }

   @Override
   public int permadeath$getPiglinVariant() {
      return (Integer)((AbstractPiglin)(Object)this).getEntityData().get(PERMADEATH_VARIANT);
   }

   @Override
   public void permadeath$setPiglinVariant(int variant) {
      ((AbstractPiglin)(Object)this).getEntityData().set(PERMADEATH_VARIANT, variant);
   }

   @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
   private void permadeath$saveVariant(CompoundTag tag, CallbackInfo ci) {
      tag.putInt("PermadeathPiglinVariant", this.permadeath$getPiglinVariant());
   }

   @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
   private void permadeath$loadVariant(CompoundTag tag, CallbackInfo ci) {
      if (tag.contains("PermadeathPiglinVariant")) {
         this.permadeath$setPiglinVariant(tag.getInt("PermadeathPiglinVariant"));
      }
   }
}
