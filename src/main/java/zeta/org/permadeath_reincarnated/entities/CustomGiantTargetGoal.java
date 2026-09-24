package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class CustomGiantTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   public CustomGiantTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
      super(mob, targetType, mustSee);
      this.targetConditions = TargetingConditions.forCombat().range(30.0);
   }

   @NotNull
   protected AABB getTargetSearchArea(double targetDistance) {
      return this.mob.getBoundingBox().inflate(30.0, 10.0, 30.0);
   }
}
