package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class CustomSilverfish extends Silverfish {
   public CustomSilverfish(EntityType<? extends Silverfish> type, Level level) {
      super(type, level);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader level) {
      return level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
   }

   public static boolean canDaySpawn(EntityType<CustomSilverfish> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return !level.getBlockState(pos.below()).isSolidRender(level, pos.below())
         ? false
         : level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && level.getRawBrightness(pos, 0) > 8;
   }
}
