package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
   @Redirect(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;updateBase(Lnet/minecraft/world/level/Level;III)I")
   )
   private static int permadeath$blockBeaconBaseDuringDemonFight(Level level, int x, int y, int z) {
      int levels = 0;
      if (level.dimension() == Level.END) {
         AABB area = new AABB(x - 500, y - 500, z - 500, x + 500, y + 500, z + 500);
         boolean demonDragonNearby = !level.getEntitiesOfClass(EnderDragon.class, area, dragon -> dragon.getTags().contains("demonFight")).isEmpty();
         if (demonDragonNearby) {
            return 0;
         }
      }

      for (int j = 1; j <= 4; levels = j++) {
         int yCheck = y - j;
         if (yCheck < level.getMinBuildHeight()) {
            break;
         }

         boolean valid = true;

         for (int dx = x - j; dx <= x + j && valid; dx++) {
            for (int dz = z - j; dz <= z + j; dz++) {
               if (!level.getBlockState(new BlockPos(dx, yCheck, dz)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                  valid = false;
                  break;
               }
            }
         }

         if (!valid) {
            break;
         }
      }

      return levels;
   }
}
