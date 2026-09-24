package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnderDragon.class)
public class DragonRegenAmountMixin {
   @ModifyArg(
      method = "checkCrystals",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;setHealth(F)V"),
      index = 0
   )
   private float permadeath$modifyCrystalRegen(float originalHealth) {
      EnderDragon dragon = (EnderDragon)(Object)this;
      if (dragon.getTags().contains("demonFight") && !dragon.getTags().contains("enraged_demon")) {
         float current = dragon.getHealth();
         float max = dragon.getMaxHealth();
         return Math.min(current + 2.5F, max);
      } else if (dragon.getTags().contains("demonFight") && dragon.getTags().contains("enraged_demon")) {
         float current = dragon.getHealth();
         float max = dragon.getMaxHealth();
         return Math.min(current + 1.5F, max);
      } else {
         return originalHealth;
      }
   }
}
