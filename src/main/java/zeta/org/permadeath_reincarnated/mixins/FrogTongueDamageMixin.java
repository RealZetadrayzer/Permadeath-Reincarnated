package zeta.org.permadeath_reincarnated.mixins;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.ShootTongue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@Mixin(ShootTongue.class)
public class FrogTongueDamageMixin {
   @Inject(method = "eatEntity", at = @At("HEAD"), cancellable = true)
   private void modifyPlayerDamage(ServerLevel level, Frog frog, CallbackInfo ci) {
      Optional<Entity> optional = frog.getTongueTarget();
      if (!optional.isEmpty()) {
         Entity entity = optional.get();
         float damage = 4.0F;
         if (frog.hasEffect(MobEffects.DAMAGE_BOOST)) {
            int amp = Objects.requireNonNull(frog.getEffect(MobEffects.DAMAGE_BOOST)).getAmplifier();
            damage += 3.0F * (amp + 1);
         }

         if (entity instanceof Player player) {
            if (player.level().isClientSide) {
               return;
            }

            if (player.isSpectator() || player.isCreative()) {
               return;
            }

            if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get() && frog.getTags().contains("stickyFrog")) {
               Vec3 diff = player.position().subtract(frog.position());
               double dx = diff.x;
               double dz = diff.z;
               double len = Math.sqrt(dx * dx + dz * dz);
               if (len < 1.0E-4) {
                  len = 1.0E-4;
               }

               double strength = 2.0;
               player.knockback((float)strength, dx / len, dz / len);
               player.hasImpulse = true;
               player.hurtMarked = true;
               PlayerAdvancementsHandler.award((ServerPlayer)player, PlayerAdvancementsHandler.PLAYER_STICKY_FROG_ID);
            }

            player.hurt(level.damageSources().mobAttack(frog), damage);
            player.hurtMarked = true;
            ci.cancel();
         }
      }
   }
}
