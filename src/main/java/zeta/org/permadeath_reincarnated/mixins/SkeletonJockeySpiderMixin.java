package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Spider.class)
public class SkeletonJockeySpiderMixin {
   @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
   private int permadeath$blockSkeletonJockey(RandomSource random, int bound) {
      return DayGlobalCount.CURRENT_DAY >= 20 ? 1 : random.nextInt(bound);
   }
}
