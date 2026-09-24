package zeta.org.permadeath_reincarnated.mobs;

import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class WardenChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day >= 25) {
            Entity entity = event.getEntity();
            if (!entity.level().isClientSide) {
               if (entity.level() instanceof ServerLevel level) {
                  if (entity instanceof LivingEntity) {
                     if (entity instanceof Warden warden) {
                        if (!warden.getTags().contains("fromUniversal")) {
                           if (!event.loadedFromDisk()) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (day >= 40) {
                                 setupDefinitiveWarden(warden, chance);
                              } else {
                                 setupSuperWarden(warden, chance);
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

   @SubscribeEvent
   public static void onTick(Post event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Warden warden) {
                  if (warden.getTags().contains("superWarden")) {
                     if (!warden.hasPose(Pose.DIGGING) && !warden.hasPose(Pose.EMERGING)) {
                        if (warden.tickCount % 30 == 0) {
                           if (!EventHooks.canEntityGrief(level, warden)) {
                              return;
                           }

                           double RADIUS = 0.5;
                           double FOOT_CUTOFF = 0.6;
                           AABB bb = warden.getBoundingBox();
                           AABB box = bb.inflate(0.5);
                           double minY = bb.minY + 0.6;
                           if (minY >= box.maxY) {
                              return;
                           }

                           box = new AABB(box.minX, minY, box.minZ, box.maxX, box.maxY, box.maxZ);

                           for (BlockPos pos : BlockPos.betweenClosed(
                              Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ)
                           )) {
                              BlockState state = level.getBlockState(pos);
                              if (canWardenDestroy(state) && !(state.getDestroySpeed(level, pos) < 0.0F)) {
                                 level.destroyBlock(pos, true, warden);
                              }
                           }
                        }

                        if (warden.tickCount % 20 == 0 && RANDOM.nextFloat() < 0.2F && !warden.hasPose(Pose.ROARING) && !warden.hasPose(Pose.SHOOTING)) {
                           switchWardenTarget(level, warden);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean canWardenDestroy(BlockState state) {
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
      } else if (state.is(Blocks.LAVA)) {
         return false;
      } else {
         return state.is(Blocks.FIRE) ? false : !(state.getBlock() instanceof SculkShriekerBlock) || !(Boolean)state.getValue(BlockStateProperties.CAN_SUMMON);
      }
   }

   private static void switchWardenTarget(ServerLevel level, Warden warden) {
      LivingEntity currentTarget = warden.getTarget();
      if (currentTarget != null) {
         double searchRadius = 16.0;
         AABB searchBox = warden.getBoundingBox().inflate(searchRadius);
         List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox, warden::canTargetEntity);
         candidates.remove(currentTarget);
         if (!candidates.isEmpty()) {
            LivingEntity newTarget = candidates.get(warden.getRandom().nextInt(candidates.size()));
            warden.increaseAngerAt(newTarget, 100, true);
            warden.setAttackTarget(newTarget);
            SonicBoom.setCooldown(warden, 40);
         }
      }
   }

   public static void setupSuperWarden(Warden warden, int chance) {
      if (!warden.getTags().contains("fromSniffer") && !warden.getTags().contains("fromAllay")) {
         if (chance <= 1) {
            warden.setCustomName(Component.literal("El Warden del Servidor").withStyle(ChatFormatting.GREEN));
         } else {
            warden.setCustomName(Component.literal("Súper-Warden").withStyle(ChatFormatting.LIGHT_PURPLE));
         }
      }

      warden.addTag("superWarden");
      warden.addTag("stalagImmune");
      warden.setHealth(warden.getMaxHealth());
   }

   public static void setupDefinitiveWarden(Warden warden, int chance) {
      if (!warden.getTags().contains("fromSniffer") && !warden.getTags().contains("fromAllay")) {
         if (chance <= 1) {
            warden.setCustomName(Component.literal("El Papu Silencioso").withStyle(ChatFormatting.DARK_PURPLE));
         } else {
            warden.setCustomName(Component.literal("Warden Definitivo").withStyle(ChatFormatting.GOLD));
         }
      }

      warden.addTag("superWarden");
      warden.addTag("definitiveWarden");
      warden.addTag("stalagImmune");
      warden.addTag("arrowImmune");
      warden.setHealth(warden.getMaxHealth());
      warden.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
   }
}
