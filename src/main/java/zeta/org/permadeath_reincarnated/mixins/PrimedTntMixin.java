package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin {
   @Shadow
   private boolean usedPortal;
   @Shadow
   @Final
   @Mutable
   private static ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR;

   protected PrimedTntMixin(boolean usedPortal) {
      this.usedPortal = usedPortal;
   }

   @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
   private void onExplode(CallbackInfo ci) {
      PrimedTnt self = (PrimedTnt)(Object)this;
      Level level = self.level();
      if (!level.isClientSide()) {
         if (self.getTags().contains("demonFight")) {
            float explosionPower = self.getTags().contains("demonFight") ? 8.0F : 4.0F;
            ExplosionInteraction explosionInteraction = self.getTags().contains("demonFight") ? ExplosionInteraction.NONE : ExplosionInteraction.TNT;
            level.explode(
               self,
               Explosion.getDefaultDamageSource(level, self),
               this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
               self.getX(),
               self.getY(0.0625),
               self.getZ(),
               explosionPower,
               false,
               explosionInteraction
            );
            ci.cancel();
            BlockPos center = self.blockPosition();
            double radiusXZ = 4.0;
            double radiusY = 1.5;

            for (int dx = (int)(-radiusXZ); dx <= radiusXZ; dx++) {
               for (int dy = (int)(-radiusY); dy <= radiusY; dy++) {
                  for (int dz = (int)(-radiusXZ); dz <= radiusXZ; dz++) {
                     if (!(dx * dx + dz * dz > radiusXZ * radiusXZ)) {
                        BlockPos pos = center.offset(dx, dy, dz);
                        BlockState state = level.getBlockState(pos);
                        if (state.getBlock() == Blocks.WATER) {
                           level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        } else if (permadeathReincarnated$canMoveBlock(state)) {
                           level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                           double rx = level.random.nextDouble();
                           double ry = 1.5 + level.random.nextDouble() * 1.5;
                           double rz = level.random.nextDouble();
                           FallingBlockEntity falling = FallingBlockEntityAccessor.invokeInit(level, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, state);
                           falling.setDeltaMovement(
                              0.05 + level.random.nextGaussian() * 0.1, 2.0 + level.random.nextDouble() * 0.5, -0.03 + level.random.nextGaussian() * 0.1
                           );
                           falling.dropItem = false;
                           falling.setInvulnerable(true);
                           falling.setHurtsEntities(0.5F, 10);
                           ScheduleInTicks.schedule(() -> level.addFreshEntity(falling), 2);
                        }
                     }
                  }
               }
            }
         } else if (self.getTags().contains("fromShulker")) {
            int day = DayGlobalCount.CURRENT_DAY;
            float explosionPower = day >= 50 ? 10.0F : 7.0F;
            ExplosionInteraction explosionInteraction = ExplosionInteraction.TNT;
            level.explode(
               self,
               Explosion.getDefaultDamageSource(level, self),
               this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
               self.getX(),
               self.getY(0.0625),
               self.getZ(),
               explosionPower,
               false,
               explosionInteraction
            );
            ci.cancel();
         } else if (self.getTags().contains("fromParrot")) {
            float explosionPower = 50.0F;
            ExplosionInteraction explosionInteraction = ExplosionInteraction.TNT;
            level.explode(
               self,
               Explosion.getDefaultDamageSource(level, self),
               this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
               self.getX(),
               self.getY(0.0625),
               self.getZ(),
               explosionPower,
               false,
               explosionInteraction
            );
            ci.cancel();
         }
      }
   }

   @Unique
   private static boolean permadeathReincarnated$canMoveBlock(BlockState state) {
      if (state.isAir()) {
         return false;
      } else if (state.is(Blocks.BEDROCK)) {
         return false;
      } else if (state.is(Blocks.REINFORCED_DEEPSLATE)) {
         return false;
      } else if (state.is(Blocks.END_PORTAL)) {
         return false;
      } else if (state.is(Blocks.END_GATEWAY)) {
         return false;
      } else if (state.is(Blocks.NETHER_PORTAL)) {
         return false;
      } else if (state.is(Blocks.BARRIER)) {
         return false;
      } else if (state.is(Blocks.STRUCTURE_VOID)) {
         return false;
      } else if (state.is(Blocks.COMMAND_BLOCK)) {
         return false;
      } else if (state.is(Blocks.CHAIN_COMMAND_BLOCK)) {
         return false;
      } else if (state.is(Blocks.REPEATING_COMMAND_BLOCK)) {
         return false;
      } else if (state.is(Blocks.JIGSAW)) {
         return false;
      } else if (state.is(Blocks.STRUCTURE_BLOCK)) {
         return false;
      } else if (state.is(Blocks.SPAWNER)) {
         return false;
      } else if (state.is(Blocks.MOVING_PISTON)) {
         return false;
      } else if (state.is(Blocks.LIGHT)) {
         return false;
      } else if (state.is(Blocks.TRIAL_SPAWNER)) {
         return false;
      } else if (state.is(Blocks.WATER)) {
         return false;
      } else {
         return state.is(Blocks.LAVA) ? false : !state.is(Blocks.FIRE);
      }
   }
}
