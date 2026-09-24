package zeta.org.permadeath_reincarnated.mixins;

import java.util.Random;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class DragonStrafePlayerPhaseMixin extends AbstractDragonPhaseInstance {
   @Shadow
   private LivingEntity attackTarget;
   @Unique
   private final Random permadeathReincarnated$rand = new Random();

   protected DragonStrafePlayerPhaseMixin(EnderDragon dragon) {
      super(dragon);
   }

   @Inject(
      method = "doServerTick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = Shift.AFTER)
   )
   private void startExtraFireballs(CallbackInfo ci) {
      if (this.dragon.getTags().contains("demonFight")) {
         if (this.attackTarget == null) {
            return;
         }

         double healthPct = this.dragon.getHealth() / this.dragon.getMaxHealth();
         int burstCount;
         if (healthPct >= 0.7) {
            burstCount = 0;
         } else if (healthPct >= 0.5) {
            burstCount = 1;
         } else if (healthPct >= 0.25) {
            burstCount = 2;
         } else if (healthPct >= 0.1) {
            burstCount = 3;
         } else {
            burstCount = 4;
         }

         if (burstCount > 0 && !this.dragon.getTags().contains("demonShot")) {
            this.dragon.addTag("demonShot");

            for (int i = 0; i < burstCount; i++) {
               int delay = (i + 1) * 50;
               ScheduleInTicks.schedule(
                  () -> {
                     if (this.attackTarget != null) {
                        Vec3 head = this.dragon.getEyePosition();
                        double dx = this.attackTarget.getX() - head.x;
                        double dy = this.attackTarget.getY(0.5) - head.y;
                        double dz = this.attackTarget.getZ() - head.z;
                        Vec3 direction = new Vec3(dx, dy, dz).normalize();
                        double spread = 0.25;
                        direction = direction.add(
                              (this.permadeathReincarnated$rand.nextDouble() - 0.5) * spread,
                              (this.permadeathReincarnated$rand.nextDouble() - 0.5) * spread * 0.4,
                              (this.permadeathReincarnated$rand.nextDouble() - 0.5) * spread
                           )
                           .normalize();
                        DragonFireball fireball = new DragonFireball(this.dragon.level(), this.dragon, direction);
                        fireball.setOwner(this.dragon);
                        fireball.moveTo(head.x, head.y, head.z, 0.0F, 0.0F);
                        permadeathReincarnated$playGlobalSound(
                           (ServerLevel)this.dragon.level(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F
                        );
                        this.dragon.level().addFreshEntity(fireball);
                     }
                  },
                  delay
               );
            }

            ScheduleInTicks.schedule(() -> this.dragon.removeTag("demonShot"), burstCount * 50);
         }
      }
   }

   @Unique
   private static void permadeathReincarnated$playGlobalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         double px = player.getX();
         double py = player.getY();
         double pz = player.getZ();
         if (player.level() == level) {
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
         }
      });
   }
}
