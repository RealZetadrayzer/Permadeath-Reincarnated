package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends Projectile {
   protected DragonFireballMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
      super(p_37248_, p_37249_);
   }

   public boolean canHitEntity(@NotNull Entity target) {
      Entity owner = this.getOwner();
      if (owner instanceof EnderDragon dragon && (target == dragon || target.getRootVehicle() == dragon)) {
         return false;
      } else {
         return !(owner instanceof EnderDragonPart dragonPart && (target == dragonPart || target.getRootVehicle() == dragonPart.parentMob))
            ? super.canHitEntity(target)
            : false;
      }
   }
}
