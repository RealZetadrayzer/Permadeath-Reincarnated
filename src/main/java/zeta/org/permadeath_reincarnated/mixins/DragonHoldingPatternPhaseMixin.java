package zeta.org.permadeath_reincarnated.mixins;

import java.util.List;
import java.util.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@Mixin(DragonHoldingPatternPhase.class)
public abstract class DragonHoldingPatternPhaseMixin extends AbstractDragonPhaseInstance {
   @Unique
   private static final Random permadeathReincarnated$RANDOM = new Random();
   @Unique
   private int permadeathReincarnated$scanningTime = 0;

   public DragonHoldingPatternPhaseMixin(EnderDragon dragon) {
      super(dragon);
   }

   @Inject(method = "doServerTick", at = @At("TAIL"))
   private void bombingScan(CallbackInfo ci) {
      EnderDragon dragon = this.dragon;
      if (dragon.getTags().contains("demonFight")) {
         Level level = dragon.level();
         if (!level.isClientSide) {
            List<Player> playersInRange = level.getEntitiesOfClass(
               Player.class,
               dragon.getBoundingBox().inflate(35.0, 35.0, 35.0),
               player -> dragon.getY() > player.getY() && !player.isCreative() && !player.isSpectator()
            );
            if (!playersInRange.isEmpty()) {
               this.permadeathReincarnated$scanningTime++;
            } else {
               this.permadeathReincarnated$scanningTime = 0;
            }

            if (this.permadeathReincarnated$scanningTime >= 30 && !dragon.getTags().contains("bombing")) {
               this.permadeathReincarnated$scanningTime = 0;
               float chance = dragon.getTags().contains("enraged_demon") ? 0.2F : 0.1F;
               if (permadeathReincarnated$RANDOM.nextFloat() <= chance) {
                  AreaEffectCloud areaEffectCloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(dragon.level());
                  dragon.addTag("bombing");
                  ScheduleInTicks.schedule(() -> dragon.removeTag("bombing"), 2400);
                  if (areaEffectCloud != null) {
                     areaEffectCloud.setPos(dragon.getX(), dragon.getY(), dragon.getZ());
                     areaEffectCloud.setRadius(0.01F);
                     areaEffectCloud.setDuration(20);
                     areaEffectCloud.setParticle(ParticleTypes.UNDERWATER);
                     areaEffectCloud.addTag("demonFight");
                     areaEffectCloud.addTag("spawn_tnt_trigger");
                     level.addFreshEntity(areaEffectCloud);
                  }
               }
            }
         }
      }
   }
}
