package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.demonFight.CrystalRespawnData;
import zeta.org.permadeath_reincarnated.demonFight.PermadeathDemonFightHandler;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(EndCrystal.class)
public class EndCrystalMixin {
   @Unique
   private boolean permadeathReincarnated$hasExploded = false;

   @Inject(method = "onDestroyedBy", at = @At("HEAD"))
   private void onCrystalDestroyed(DamageSource source, CallbackInfo ci) {
      EndCrystal self = (EndCrystal)(Object)this;
      if (self.level() instanceof ServerLevel level) {
         if (self.getTags().contains("demonFight")) {
            if (!this.permadeathReincarnated$hasExploded) {
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               if (handler.isFightOn()) {
                  this.permadeathReincarnated$hasExploded = true;
                  double x = self.getX();
                  double y = self.getY();
                  double z = self.getZ();
                  Ghast ghast = (Ghast)EntityType.GHAST.create(level);
                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("demonFight");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("demonFight");
                     team.setColor(ChatFormatting.LIGHT_PURPLE);
                  }

                  if (source.getEntity() instanceof ServerPlayer attacker && !attacker.isCreative() && !attacker.isSpectator()) {
                     double RANGE = 80.0;
                     level.getEntitiesOfClass(Ghast.class, attacker.getBoundingBox().inflate(80.0), g -> g.isAlive() && g.getTags().contains("demonFight"))
                        .forEach(g -> {
                           g.setTarget(attacker);
                           g.setAggressive(true);
                           g.setLastHurtByMob(attacker);
                        });
                  }

                  if (ghast != null) {
                     ghast.moveTo(x, y + 3.0, z, ghast.getYRot(), ghast.getXRot());
                     ghast.addTag("demonFight");
                     ghast.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0, false, true, false));
                     ghast.setPersistenceRequired();
                     ghast.setGlowingTag(true);
                     scoreboard.addPlayerToTeam(ghast.getStringUUID(), team);
                     level.addFreshEntity(ghast);
                  }

                  permadeathReincarnated$playGlobalSound(level, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 4.0F, 0.6F);
                  permadeathReincarnated$playGlobalSound(level, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 2.0F, 0.5F);
                  permadeathReincarnated$playGlobalSoundHolder(level, SoundEvents.TRIDENT_THUNDER, SoundSource.HOSTILE, 2.0F, 0.5F);
                  permadeathReincarnated$crystalImplosion(level, x, y, z);
                  permadeathReincarnated$crystalShatter(level, x, y, z);
                  permadeathReincarnated$crystalShockwave(level, x, y, z);
                  permadeathReincarnated$crystalResidue(level, x, y, z);
                  CrystalRespawnData data = CrystalRespawnData.get(level);
                  data.add(x, y, z, 12000, 18000);
               }
            }
         }
      }
   }

   @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
   private void crystalDamageImmunities(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      EndCrystal crystal = (EndCrystal)(Object)this;
      if (crystal.getTags().contains("demonFight")) {
         if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
            return;
         }

         if (source.is(DamageTypeTags.IS_LIGHTNING)) {
            cir.setReturnValue(false);
         }
      } else if ((crystal.getTags().contains("yticCrystal") || crystal.getTags().contains("fromShulkerCaotico")) && source.is(DamageTypeTags.IS_EXPLOSION)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
   private void perma$overrideExplosion(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      EndCrystal crystal = (EndCrystal)(Object)this;
      if (!crystal.level().isClientSide) {
         if (crystal.getTags().contains("fromShulkerCaotico") || crystal.getTags().contains("yticCrystal")) {
            if (!crystal.isRemoved()) {
               int day = DayGlobalCount.CURRENT_DAY;
               float explosionLevel = crystal.getTags().contains("yticCrystal") ? 20.0F : (day >= 50 ? 20.0F : 12.0F);
               cir.setReturnValue(true);
               crystal.remove(RemovalReason.KILLED);
               crystal.level()
                  .explode(
                     crystal,
                     crystal.level().damageSources().explosion(crystal, crystal),
                     null,
                     crystal.getX(),
                     crystal.getY(),
                     crystal.getZ(),
                     explosionLevel,
                     false,
                     ExplosionInteraction.BLOCK
                  );
            }
         }
      }
   }

   @Redirect(
      method = "tick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
      )
   )
   private boolean permadeath$skipFireForShulkerCrystal(Level level, BlockPos pos, BlockState state) {
      EndCrystal crystal = (EndCrystal)(Object)this;
      return crystal.getTags().contains("fromShulkerCaotico")
            && crystal.getVehicle() instanceof Shulker shulker
            && shulker.getTags().contains("shulkerCaotico")
         ? false
         : level.setBlockAndUpdate(pos, state);
   }

   @Unique
   private static <T extends ParticleOptions> void permadeathReincarnated$sendLongRangeParticles(
      ServerLevel level, T type, double x, double y, double z, int count, double dx, double dy, double dz, double speed
   ) {
      for (ServerPlayer player : level.getPlayers(playerx -> true)) {
         Packet<?> packet = new ClientboundLevelParticlesPacket(type, true, x, y, z, (float)dx, (float)dy, (float)dz, (float)speed, count);
         permadeathReincarnated$sendParticles(player, true, x, y, z, packet, level);
      }
   }

   @Unique
   private static void permadeathReincarnated$sendParticles(
      ServerPlayer player, boolean longDistance, double posX, double posY, double posZ, Packet<?> packet, ServerLevel level
   ) {
      if (player.level() == level) {
         BlockPos blockpos = player.blockPosition();
         if (blockpos.closerToCenterThan(new Vec3(posX, posY, posZ), longDistance ? 512.0 : 32.0)) {
            player.connection.send(packet);
         }
      }
   }

   @Unique
   private static void permadeathReincarnated$crystalImplosion(ServerLevel level, double x, double y, double z) {
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.REVERSE_PORTAL, x, y + 1.0, z, 80, 0.8, 0.8, 0.8, -0.2);
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.PORTAL, x, y + 1.0, z, 60, 0.5, 0.5, 0.5, -0.1);
   }

   @Unique
   private static void permadeathReincarnated$crystalShatter(ServerLevel level, double x, double y, double z) {
      permadeathReincarnated$sendLongRangeParticles(
         level, new ItemParticleOption(ParticleTypes.ITEM, Items.ENDER_EYE.getDefaultInstance()), x, y + 1.0, z, 40, 0.6, 0.6, 0.6, 0.2
      );
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.CRIT, x, y + 1.0, z, 60, 0.7, 0.7, 0.7, 0.3);
   }

   @Unique
   private static void permadeathReincarnated$crystalShockwave(ServerLevel level, double x, double y, double z) {
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.EXPLOSION, x, y + 1.0, z, 2, 0.0, 0.0, 0.0, 0.0);
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.FLASH, x, y + 1.0, z, 1, 0.0, 0.0, 0.0, 0.0);
      permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.SMOKE, x, y + 0.5, z, 30, 0.8, 0.3, 0.8, 0.05);
   }

   @Unique
   private static void permadeathReincarnated$crystalResidue(ServerLevel level, double x, double y, double z) {
      for (int i = 0; i < 20; i++) {
         double dx = (level.random.nextDouble() - 0.5) * 1.2;
         double dz = (level.random.nextDouble() - 0.5) * 1.2;
         permadeathReincarnated$sendLongRangeParticles(level, ParticleTypes.END_ROD, x + dx, y + 0.2, z + dz, 1, 0.0, 0.03, 0.0, 0.0);
      }
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

   @Unique
   private static void permadeathReincarnated$playGlobalSoundHolder(
      ServerLevel level, Holder<SoundEvent> soundHolder, SoundSource source, float volume, float pitch
   ) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         double px = player.getX();
         double py = player.getY();
         double pz = player.getZ();
         if (player.level() == level) {
            player.connection.send(new ClientboundSoundPacket(soundHolder, source, px, py, pz, volume, pitch, seed));
         }
      });
   }
}
