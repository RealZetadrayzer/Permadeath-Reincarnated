package zeta.org.permadeath_reincarnated.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

public class ConditionalMeleeAttackGoal extends MeleeAttackGoal {
   private final int activationDay;

   public ConditionalMeleeAttackGoal(PathfinderMob mob, double speed, boolean longMemory, int activationDay) {
      super(mob, speed, longMemory);
      this.activationDay = activationDay;
   }

   private boolean shouldBlock() {
      if (DayGlobalCount.CURRENT_DAY < this.activationDay) {
         return true;
      } else if (this.mob.getTags().contains("isGuardian")) {
         this.mob.setTarget(null);
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
}
