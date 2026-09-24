package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.items.PermadeathArmorMaterials;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
   @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
   private void permadeath$isEnchantable(CallbackInfoReturnable<Boolean> cir) {
      ItemStack stack = (ItemStack)(Object)this;
      if (!stack.isEnchanted()) {
         if (stack.getItem() instanceof ArmorItem armor) {
            ArmorMaterial armorMaterial = (ArmorMaterial)armor.getMaterial().value();
            if (armorMaterial == PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate().value()
               || armorMaterial == PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate().value()
               || armorMaterial == PermadeathArmorMaterials.PERMA_BLUE_NETHERITE.getDelegate().value()
               || armorMaterial == PermadeathArmorMaterials.PERMA_YELLOW_NETHERITE.getDelegate().value()
               || armorMaterial == PermadeathArmorMaterials.PERMA_GREEN_NETHERITE.getDelegate().value()
               || armorMaterial == PermadeathArmorMaterials.PERMA_ROSE_NETHERITE.getDelegate().value()) {
               cir.setReturnValue(true);
            }
         }
      }
   }
}
