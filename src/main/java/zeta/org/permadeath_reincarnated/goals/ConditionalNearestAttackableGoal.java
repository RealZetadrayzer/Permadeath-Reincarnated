package zeta.org.permadeath_reincarnated.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class ConditionalNearestAttackableGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private final int activationDay;

   public ConditionalNearestAttackableGoal(Mob mob, Class<T> targetClass, boolean mustSee, int activationDay) {
      super(mob, targetClass, mustSee);
      this.activationDay = activationDay;
   }

   private boolean shouldBlock() {
      if (DayGlobalCount.CURRENT_DAY < this.activationDay) {
         return true;
      } else if (this.mob.getTags().contains("isGuardian")) {
         this.mob.setTarget(null);
         this.mob.setLastHurtByMob(null);
         return true;
      } else {
         return false;
      }
   }

   public boolean canUse() {
      return this.shouldBlock() ? false : super.canUse();
   }

   public boolean canContinueToUse() {
      return this.shouldBlock() ? false : super.canContinueToUse();
   }

   public void stop() {
      this.mob.setTarget(null);
      super.stop();
   }
}
