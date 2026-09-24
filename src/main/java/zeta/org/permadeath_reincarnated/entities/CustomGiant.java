package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CustomGiant extends Giant {
   public CustomGiant(EntityType<? extends Giant> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
      this.targetSelector.addGoal(2, new CustomGiantTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new CustomGiantTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new CustomGiantTargetGoal(this, IronGolem.class, true));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
   }

   public float getWalkTargetValue(@NotNull BlockPos pos, LevelReader level) {
      return -level.getPathfindingCostFromLightLevels(pos);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   @NotNull
   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return SoundEvents.ZOMBIE_HURT;
   }

   @NotNull
   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState block) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected void populateDefaultEquipmentSlots(@NotNull RandomSource random, @NotNull DifficultyInstance difficulty) {
      super.populateDefaultEquipmentSlots(random, difficulty);
      float weaponChance = this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F;
      if (random.nextFloat() < weaponChance) {
         if (random.nextInt(2) == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }
   }

   public static @NotNull Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 600.0)
         .add(Attributes.MOVEMENT_SPEED, 0.5)
         .add(Attributes.ATTACK_DAMAGE, 50.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
   }
}
