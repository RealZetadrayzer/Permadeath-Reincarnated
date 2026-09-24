package zeta.org.permadeath_reincarnated.systems.worldgen;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PurpurChorusFeature extends Feature<NoneFeatureConfiguration> {
   public PurpurChorusFeature(Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
      WorldGenLevel level = ctx.level();
      BlockPos origin = ctx.origin();
      RandomSource random = ctx.random();
      if (!level.isEmptyBlock(origin)) {
         return false;
      }

      if (!level.getBlockState(origin.below()).is(Blocks.PURPUR_BLOCK)) {
         return false;
      }

      placePlant(level, origin);
      growRecursive(level, origin, random, origin, 8, 0);
      return true;
   }

   private static void placePlant(LevelAccessor level, BlockPos pos) {
      BlockState state = Blocks.END_STONE_BRICK_WALL.defaultBlockState();

      for (Direction dir : Plane.HORIZONTAL) {
         BlockPos neighborPos = pos.relative(dir);
         BlockState neighbor = level.getBlockState(neighborPos);
         state = state.updateShape(dir, neighbor, level, pos, neighborPos);
      }

      level.setBlock(pos, state, 2);

      for (Direction dir : Plane.HORIZONTAL) {
         BlockPos neighborPos = pos.relative(dir);
         BlockState neighbor = level.getBlockState(neighborPos);
         if (neighbor.getBlock() instanceof WallBlock) {
            level.setBlock(neighborPos, neighbor.updateShape(dir.getOpposite(), state, level, neighborPos, pos), 2);
         }
      }
   }

   private static void growRecursive(LevelAccessor level, BlockPos branchPos, RandomSource random, BlockPos origin, int maxHorizontalDistance, int iterations) {
      int height = random.nextInt(4) + 1;
      if (iterations == 0) {
         height++;
      }

      for (int j = 0; j < height; j++) {
         BlockPos up = branchPos.above(j + 1);
         if (!allNeighborsEmpty(level, up, null)) {
            return;
         }

         placePlant(level, up);
         placePlant(level, up.below());
      }

      boolean branched = false;
      if (iterations < 4) {
         int attempts = random.nextInt(4);
         if (iterations == 0) {
            attempts++;
         }

         for (int k = 0; k < attempts; k++) {
            Direction dir = Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos side = branchPos.above(height).relative(dir);
            if (Math.abs(side.getX() - origin.getX()) < maxHorizontalDistance
               && Math.abs(side.getZ() - origin.getZ()) < maxHorizontalDistance
               && level.isEmptyBlock(side)
               && level.isEmptyBlock(side.below())
               && allNeighborsEmpty(level, side, dir.getOpposite())) {
               branched = true;
               placePlant(level, side);
               placePlant(level, side.relative(dir.getOpposite()));
               growRecursive(level, side, random, origin, maxHorizontalDistance, iterations + 1);
            }
         }
      }

      if (!branched) {
         level.setBlock(branchPos.above(height), Blocks.SEA_LANTERN.defaultBlockState(), 2);
      }
   }

   private static boolean allNeighborsEmpty(LevelReader level, BlockPos pos, @Nullable Direction excluding) {
      for (Direction dir : Plane.HORIZONTAL) {
         if (dir != excluding && !level.isEmptyBlock(pos.relative(dir))) {
            return false;
         }
      }

      return true;
   }
}
