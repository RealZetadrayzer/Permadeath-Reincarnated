package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

public class CustomZombieHorse extends ZombieHorse {
   public CustomZombieHorse(EntityType<? extends ZombieHorse> type, Level level) {
      super(type, level);
   }

   public float getWalkTargetValue(@NotNull BlockPos pos, LevelReader level) {
      return -level.getPathfindingCostFromLightLevels(pos);
   }

   public static boolean checkCustomZombieHorseSpawnRules(
      EntityType<CustomZombieHorse> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return level.getDifficulty() != Difficulty.PEACEFUL
         && (MobSpawnType.ignoresLightRequirements(reason) || Monster.isDarkEnoughToSpawn(level, pos, random))
         && Mob.checkMobSpawnRules(type, level, reason, pos, random);
   }

   public boolean removeWhenFarAway(double distSqr) {
      return true;
   }

   public static @NotNull Builder createAttributes() {
      return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.2F);
   }
}
