package zeta.org.permadeath_reincarnated.mixins;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shulker.class)
public class ShulkerTeleportPassengersMixin {
   @Unique
   private List<Entity> permadeath$passengersBeforeTeleport;

   @Inject(method = "teleportSomewhere", at = @At("HEAD"))
   private void permadeath$capturePassengers(CallbackInfoReturnable<Boolean> cir) {
      Shulker shulker = (Shulker)(Object)this;
      this.permadeath$passengersBeforeTeleport = new ArrayList<>(shulker.getPassengers());
   }

   @Inject(method = "teleportSomewhere", at = @At("RETURN"))
   private void permadeath$restorePassengers(CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue()) {
         if (this.permadeath$passengersBeforeTeleport != null && !this.permadeath$passengersBeforeTeleport.isEmpty()) {
            Shulker shulker = (Shulker)(Object)this;
            double x = shulker.getX();
            double y = shulker.getY();
            double z = shulker.getZ();

            for (Entity passenger : this.permadeath$passengersBeforeTeleport) {
               if (passenger != null && passenger.isAlive() && passenger.level() == shulker.level()) {
                  passenger.stopRiding();
                  passenger.teleportTo(x, y, z);
                  passenger.startRiding(shulker, true);
               }
            }

            this.permadeath$passengersBeforeTeleport = null;
         }
      }
   }
}
