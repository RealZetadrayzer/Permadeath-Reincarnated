package zeta.org.permadeath_reincarnated.mixins;

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
public abstract class AbstractPiglinMixin implements PiglinVariantAccess {
   @Unique
   private static final EntityDataAccessor<Integer> PERMADEATH$VARIANT = SynchedEntityData.defineId(AbstractPiglin.class, EntityDataSerializers.INT);

   @Inject(method = "defineSynchedData", at = @At("TAIL"))
   private void permadeath$defineSynchedData(Builder builder, CallbackInfo ci) {
      builder.define(PERMADEATH$VARIANT, 0);
   }

   @Override
   public int permadeath$getPiglinVariant() {
      return (Integer)((AbstractPiglin)(Object)this).getEntityData().get(PERMADEATH$VARIANT);
   }

   @Override
   public void permadeath$setPiglinVariant(int variant) {
      ((AbstractPiglin)(Object)this).getEntityData().set(PERMADEATH$VARIANT, variant);
   }
}
