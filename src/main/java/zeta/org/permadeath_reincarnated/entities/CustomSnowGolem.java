package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class CustomSnowGolem extends SnowGolem {
   public CustomSnowGolem(EntityType<? extends SnowGolem> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader level) {
      return level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
   }

   public static boolean canDaySpawn(EntityType<CustomSnowGolem> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      if (!level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
         return false;
      } else if (level.getLevel().isRaining()) {
         return false;
      } else if (level.getLevel().isThundering()) {
         return false;
      } else {
         return level.getBiome(pos).is(BiomeTags.SNOW_GOLEM_MELTS) ? false : level.getMaxLocalRawBrightness(pos) > 8;
      }
   }
}
