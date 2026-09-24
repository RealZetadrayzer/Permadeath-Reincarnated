package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSkeleton.class)
public abstract class WitherSkeletonWeaponMixin {
   @ModifyVariable(method = "reassessWeaponGoal", at = @At(value = "STORE", ordinal = 0))
   private ItemStack emperadorOnlyMainHand(ItemStack original) {
      AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
      return skeleton instanceof WitherSkeleton && skeleton.getTags().contains("witherEmperador") ? skeleton.getMainHandItem() : original;
   }
}
