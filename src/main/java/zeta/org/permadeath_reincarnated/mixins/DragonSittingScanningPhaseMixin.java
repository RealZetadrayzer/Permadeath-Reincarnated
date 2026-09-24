package zeta.org.permadeath_reincarnated.mixins;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@Mixin(DragonSittingScanningPhase.class)
public abstract class DragonSittingScanningPhaseMixin extends AbstractDragonPhaseInstance {
   @Shadow
   @Final
   private TargetingConditions scanTargeting;
   @Unique
   private static final Random permadeathReincarnated$RANDOM = new Random();
   @Unique
   private static final int SPIN_TICKS = 600;
   @Unique
   private static final float YAW_STEP = 5.0F;
   @Unique
   private int permadeathReincarnated$scanningTime = 0;

   public DragonSittingScanningPhaseMixin(EnderDragon dragon) {
      super(dragon);
   }

   @Inject(method = "doServerTick", at = @At("TAIL"))
   private void attack360(CallbackInfo ci) {
      EnderDragon dragon = this.dragon;
      if (dragon.getTags().contains("demonFight")) {
         Level level = dragon.level();
         if (level.isClientSide) {
            return;
         }

         Player target = dragon.level().getNearestPlayer(this.scanTargeting, dragon);
         if (target != null) {
            this.permadeathReincarnated$scanningTime++;
         } else {
            this.permadeathReincarnated$scanningTime = 0;
         }

         if (this.permadeathReincarnated$scanningTime >= 20 && !dragon.getTags().contains("360")) {
            this.permadeathReincarnated$scanningTime = 0;
            float chance = dragon.getTags().contains("enraged_demon") ? 0.25F : 0.15F;
            if (permadeathReincarnated$RANDOM.nextFloat() <= chance) {
               dragon.addTag("360");
               permadeathReincarnated$playGlobalSound((ServerLevel)level, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 4.0F, 0.85F);
               this.permadeathReincarnated$cloudAttack(level, dragon);
               this.permadeathReincarnated$scheduleSpin(dragon);
            }
         }
      }
   }

   @Unique
   private void permadeathReincarnated$scheduleSpin(EnderDragon dragon) {
      for (int i = 0; i < 600; i++) {
         int tickIndex = i;
         ScheduleInTicks.schedule(() -> {
            if (dragon.isAlive()) {
               if (!dragon.getPhaseManager().getCurrentPhase().isSitting()) {
                  dragon.removeTag("360");
               } else {
                  float newYaw = dragon.getYRot() + 5.0F;
                  dragon.absRotateTo(newYaw, dragon.getXRot());
                  dragon.yRotO = dragon.getYRot();
                  dragon.yBodyRot = dragon.getYRot();
                  dragon.yBodyRotO = dragon.getYRot();
                  if (tickIndex == 599) {
                     dragon.removeTag("360");
                  }
               }
            }
         }, tickIndex);
      }
   }

   @Unique
   private void permadeathReincarnated$cloudAttack(Level level, EnderDragon dragon) {
      BlockPos center = dragon.blockPosition();
      float radius = 16.0F;
      List<String> tags = List.of("demonFight", "white_cloud");
      ScheduleInTicks.schedule(() -> permadeathReincarnated$spawnCloud(level, center, radius, 0.0F, 600, ParticleTypes.CLOUD, tags), 40);
   }

   @Unique
   private static void permadeathReincarnated$spawnCloud(
      Level level, BlockPos pos, float radius, float radiusPerTick, int duration, ParticleOptions particle, List<String> tags
   ) {
      AreaEffectCloud cloud = new AreaEffectCloud(level, pos.getX(), pos.getY() - 1.75F, pos.getZ());
      cloud.setRadius(radius);
      cloud.setRadiusOnUse(radiusPerTick);
      cloud.setDuration(duration);
      cloud.setParticle(particle);
      if (tags != null) {
         cloud.getTags().addAll(tags);
      }

      level.addFreshEntity(cloud);
   }

   @Unique
   private static void permadeathReincarnated$playGlobalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
         }
      });
   }
}
