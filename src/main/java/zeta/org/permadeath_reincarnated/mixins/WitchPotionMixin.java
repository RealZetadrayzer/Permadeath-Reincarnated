package zeta.org.permadeath_reincarnated.mixins;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Witch.class)
public class WitchPotionMixin {
   @Unique
   private static final Random permadeathReincarnated$RANDOM = new Random();

   @Inject(method = "performRangedAttack", at = @At("HEAD"), cancellable = true)
   private void impossibleWitchPotions(LivingEntity target, float distanceFactor, CallbackInfo ci) {
      Witch witch = (Witch)(Object)this;
      if (!witch.level().isClientSide) {
         if (DayGlobalCount.CURRENT_DAY >= 40) {
            if (!witch.isDrinkingPotion()) {
               if (witch.getTags().contains("impossibleWitch")) {
                  ci.cancel();
                  Vec3 targetMotion = target.getDeltaMovement();
                  double dx = target.getX() + targetMotion.x - witch.getX();
                  double dy = target.getEyeY() - 1.1 - witch.getY();
                  double dz = target.getZ() + targetMotion.z - witch.getZ();
                  double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                  MobEffectInstance potionEffect;
                  if (target instanceof Raider) {
                     if (target.getHealth() <= 4.0F) {
                        potionEffect = new MobEffectInstance(MobEffects.HEAL, 1, 2);
                     } else {
                        potionEffect = new MobEffectInstance(MobEffects.REGENERATION, 400, 2);
                     }

                     witch.setTarget(null);
                  } else if (horizontalDistance >= 8.0 && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                     potionEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 4);
                  } else if (target.getHealth() >= 8.0 && !target.hasEffect(MobEffects.POISON)) {
                     potionEffect = new MobEffectInstance(MobEffects.POISON, 6000, 2);
                  } else if (horizontalDistance <= 3.0 && !target.hasEffect(MobEffects.WEAKNESS) && permadeathReincarnated$RANDOM.nextFloat() < 0.25F) {
                     potionEffect = new MobEffectInstance(MobEffects.WEAKNESS, 400, 2);
                  } else {
                     potionEffect = new MobEffectInstance(MobEffects.HARM, 1, 3);
                  }

                  PotionContents customPotion = new PotionContents(Optional.empty(), Optional.empty(), List.of(potionEffect));
                  ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
                  potionStack.set(DataComponents.POTION_CONTENTS, customPotion);
                  ThrownPotion thrownPotion = new ThrownPotion(witch.level(), witch);
                  thrownPotion.setItem(potionStack);
                  thrownPotion.shoot(dx, dy + horizontalDistance * 0.2, dz, 0.75F, 8.0F);
                  witch.level().addFreshEntity(thrownPotion);
                  if (!witch.isSilent()) {
                     witch.level()
                        .playSound(
                           null,
                           witch.getX(),
                           witch.getY(),
                           witch.getZ(),
                           SoundEvents.WITCH_THROW,
                           witch.getSoundSource(),
                           1.0F,
                           0.8F + permadeathReincarnated$RANDOM.nextFloat() * 0.4F
                        );
                  }
               }
            }
         }
      }
   }
}
