package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnEggItem.class)
public class SpawnEggMixin {
   @WrapOperation(
      method = "spawnOffspringFromSpawnEgg",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setCustomName(Lnet/minecraft/network/chat/Component;)V")
   )
   private void permadeath$preserveExistingName(Mob mob, Component name, Operation<Void> original) {
      if (!mob.hasCustomName()) {
         if (name != null) {
            original.call(new Object[]{mob, name});
         }
      }
   }
}
