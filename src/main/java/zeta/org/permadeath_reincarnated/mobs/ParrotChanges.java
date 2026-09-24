package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.PermadeathSounds;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class ParrotChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Parrot parrot) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 45) {
                           if (!parrot.getTags().contains("fromAllay")) {
                              parrot.setCustomName(Component.literal("Loro Bomba").withStyle(ChatFormatting.RED));
                           }

                           parrot.addTag("bombParrot");
                           if (day >= 55) {
                              parrot.addTag("arrowImmune");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      if (event.getEntity() instanceof Parrot parrot) {
         if (!parrot.level().isClientSide) {
            if (parrot.level() instanceof ServerLevel level) {
               if (parrot.tickCount % 10 == 0) {
                  if (parrot.isAlive()) {
                     if (parrot.getTags().contains("bombParrot")) {
                        if (!parrot.getPersistentData().getBoolean("BombPlanted")) {
                           LivingEntity target = parrot.getTarget();
                           if (target != null) {
                              if (target.isAlive()) {
                                 if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                                    double distance = parrot.distanceToSqr(target);
                                    if (!(distance > 9.0)) {
                                       if (target.canBeSeenAsEnemy()) {
                                          parrot.getPersistentData().putBoolean("BombPlanted", true);
                                          spawnTnt(level, parrot.blockPosition(), parrot);
                                          level.playSound(
                                             null,
                                             parrot.blockPosition(),
                                             (SoundEvent)PermadeathSounds.PARROT_BOMB_PLANTED.get(),
                                             SoundSource.VOICE,
                                             1.0F,
                                             1.0F
                                          );
                                          level.playSound(null, parrot.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.VOICE, 1.0F, 1.0F);
                                          level.playSound(null, parrot.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.VOICE, 1.0F, 0.5F);
                                          level.sendParticles(ParticleTypes.POOF, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 20, 0.0, 0.0, 0.0, 0.2);
                                          level.sendParticles(
                                             ParticleTypes.SMALL_FLAME, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 20, 0.0, 0.0, 0.0, 0.2
                                          );
                                          level.sendParticles(ParticleTypes.FLASH, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                                          if (target instanceof ServerPlayer serverPlayer) {
                                             PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_BOMB_PARROT_TRIGGERED_ID);
                                          }

                                          parrot.discard();
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void spawnTnt(ServerLevel level, BlockPos pos, Mob parrot) {
      PrimedTnt tnt = new PrimedTnt(level, pos.getX(), pos.getY(), pos.getZ(), parrot);
      tnt.addTag("fromParrot");
      tnt.setFuse(200);
      level.addFreshEntity(tnt);
   }
}
