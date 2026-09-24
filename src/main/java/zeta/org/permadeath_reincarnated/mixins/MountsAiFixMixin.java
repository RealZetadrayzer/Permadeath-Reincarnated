package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public class MountsAiFixMixin {
   @Redirect(
      method = "updateControlFlags",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getControllingPassenger()Lnet/minecraft/world/entity/LivingEntity;")
   )
   private LivingEntity permadeath$allowMobRidersButNotMounts(Mob self) {
      if (self.getFirstPassenger() instanceof VehicleEntity) {
         return null;
      } else {
         LivingEntity passenger = self.getControllingPassenger();
         if (passenger == null) {
            return null;
         } else if (!(passenger instanceof Mob)) {
            return passenger;
         } else if (passenger instanceof Shulker) {
            return null;
         } else {
            return this.permadeathReincarnated$isVanillaMount(self) ? passenger : null;
         }
      }
   }

   @Unique
   private boolean permadeathReincarnated$isVanillaMount(Mob mob) {
      return mob instanceof AbstractHorse
         || mob instanceof Camel
         || mob instanceof Pig
         || mob instanceof Strider
         || mob instanceof Llama
         || mob instanceof Mule
         || mob instanceof Donkey
         || mob instanceof Spider;
   }
}
