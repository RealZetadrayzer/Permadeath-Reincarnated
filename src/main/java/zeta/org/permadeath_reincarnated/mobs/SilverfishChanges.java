package zeta.org.permadeath_reincarnated.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.entities.CustomSilverfish;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class SilverfishChanges {
   private static final Random RANDOM = new Random();
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;

   @SubscribeEvent
   public static void onEntitySpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.level() instanceof ServerLevel level) {
               if (!event.loadedFromDisk()) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  EntityType<?> type = mob.getType();
                  if (type == EntityType.SILVERFISH || type == EntityType.ENDERMITE || type == PermadeathEntityRegistry.CUSTOM_SILVERFISH.get()) {
                     if (!mob.getTags().contains("deathSilverOrEndermite")) {
                        int minEffects = 0;
                        int maxEffects = 0;
                        if (day >= 30) {
                           if (type == EntityType.ENDERMITE) {
                              mob.setCustomName(Component.literal("Endermite de la Muerte").withStyle(ChatFormatting.LIGHT_PURPLE));
                           } else if (!mob.getTags().contains("fromChicken") && mob.getClass() != CustomSilverfish.class) {
                              mob.setCustomName(Component.literal("Silverfish de la Muerte").withStyle(ChatFormatting.GOLD));
                           } else if (mob.getClass() == CustomSilverfish.class) {
                              mob.setCustomName(Component.literal("Pollo-Transmutado").withStyle(ChatFormatting.GOLD));
                           }

                           minEffects = 5;
                           maxEffects = 5;
                        }

                        if (maxEffects > 0) {
                           List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                           Collections.shuffle(shuffled);
                           int count = minEffects + RANDOM.nextInt(maxEffects - minEffects + 1);
                           count = Math.min(count, shuffled.size());

                           for (int i = 0; i < count; i++) {
                              Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                              mob.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                           }
                        }

                        mob.addTag("deathSilverOrEndermite");
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            Entity entity = event.getEntity();
            if (!entity.level().isClientSide) {
               if (entity.level() instanceof ServerLevel level) {
                  if (entity instanceof LivingEntity) {
                     if (entity instanceof Silverfish silverfish) {
                        if (silverfish.getType() != EntityType.ENDERMITE) {
                           if (silverfish.getTags().contains("deathSilverOrEndermite")) {
                              if (silverfish.tickCount % 30 == 0) {
                                 if (!EventHooks.canEntityGrief(level, silverfish)) {
                                    return;
                                 }

                                 double RADIUS = 0.5;
                                 double FOOT_CUTOFF = 0.6;
                                 AABB bb = silverfish.getBoundingBox();
                                 AABB box = bb.inflate(0.5);
                                 double minY = bb.minY + 0.6;
                                 if (minY >= box.maxY) {
                                    return;
                                 }

                                 box = new AABB(box.minX, minY, box.minZ, box.maxX, box.maxY, box.maxZ);

                                 for (BlockPos pos : BlockPos.betweenClosed(
                                    Mth.floor(box.minX),
                                    Mth.floor(box.minY),
                                    Mth.floor(box.minZ),
                                    Mth.floor(box.maxX),
                                    Mth.floor(box.maxY),
                                    Mth.floor(box.maxZ)
                                 )) {
                                    BlockState state = level.getBlockState(pos);
                                    if (canSilverfishDestroy(state) && !(state.getDestroySpeed(level, pos) < 0.0F)) {
                                       level.destroyBlock(pos, true, silverfish);
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

   public static boolean canSilverfishDestroy(BlockState state) {
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
         return state.is(Blocks.FIRE) ? false : !(state.getBlock() instanceof InfestedBlock);
      }
   }

   static {
      Map<Holder<MobEffect>, Integer> map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 2);
      map.put(MobEffects.DAMAGE_BOOST, 3);
      map.put(MobEffects.JUMP, 4);
      map.put(MobEffects.GLOWING, 0);
      map.put(MobEffects.REGENERATION, 3);
      map.put(MobEffects.INVISIBILITY, 0);
      map.put(MobEffects.SLOW_FALLING, 0);
      map.put(MobEffects.DAMAGE_RESISTANCE, 2);
      EFFECTS = Collections.unmodifiableMap(map);
   }
}
