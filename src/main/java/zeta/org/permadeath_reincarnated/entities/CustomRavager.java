package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class CustomRavager extends Ravager {
   public CustomRavager(EntityType<? extends Ravager> type, Level level) {
      super(type, level);
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader level) {
      return level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
   }

   public void aiStep() {
      super.aiStep();
      if (this.getTags().contains("fromZPiglin")) {
         if (this.horizontalCollision) {
            boolean brokeSomething = false;
            AABB aabb = this.getBoundingBox().inflate(0.2);

            for (BlockPos pos : BlockPos.betweenClosed(
               (int)Math.floor(aabb.minX),
               (int)Math.floor(aabb.minY),
               (int)Math.floor(aabb.minZ),
               (int)Math.floor(aabb.maxX),
               (int)Math.floor(aabb.maxY),
               (int)Math.floor(aabb.maxZ)
            )) {
               BlockState state = this.level().getBlockState(pos);
               Block block = state.getBlock();
               if (this.isCustomBreakable(block.defaultBlockState())) {
                  brokeSomething = this.level().destroyBlock(pos, true, this) || brokeSomething;
               }
            }

            if (!brokeSomething && this.onGround()) {
               this.jumpFromGround();
            }
         }
      }
   }

   private boolean isCustomBreakable(BlockState state) {
      if (state.is(BlockTags.BASE_STONE_OVERWORLD) && state.getBlock() != Blocks.OBSIDIAN) {
         return true;
      } else {
         return state.is(Blocks.NETHERRACK)
            ? true
            : state.is(Blocks.NETHER_BRICKS) || state.is(Blocks.CRACKED_NETHER_BRICKS) || state.is(Blocks.CHISELED_NETHER_BRICKS);
      }
   }

   public boolean canAttack(@NotNull LivingEntity target) {
      if (target instanceof AbstractVillager villager && villager.isBaby()) {
         return false;
      } else {
         return target.getTags().contains("fromZPiglin") ? false : super.canAttack(target);
      }
   }

   public static boolean canDaySpawn(EntityType<CustomRavager> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return !level.getBlockState(pos.below()).isSolidRender(level, pos.below())
         ? false
         : level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && level.getRawBrightness(pos, 0) > 8;
   }
}
