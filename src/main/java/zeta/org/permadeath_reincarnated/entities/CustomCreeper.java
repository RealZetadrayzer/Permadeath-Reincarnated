package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import zeta.org.permadeath_reincarnated.PermadeathConfig;

public class CustomCreeper extends Creeper {
   public CustomCreeper(EntityType<? extends Creeper> type, Level level) {
      super(type, level);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader level) {
      return level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
   }

   public static boolean canSpawnAnywhere(EntityType<CustomCreeper> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return !PermadeathConfig.MIKECRACK.get() ? false : level.getMaxLocalRawBrightness(pos) > 0;
   }
}
