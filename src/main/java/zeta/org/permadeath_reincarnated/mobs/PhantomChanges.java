package zeta.org.permadeath_reincarnated.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.SkeletonRiderHelper;

@EventBusSubscriber
public class PhantomChanges {
   private static final Random RANDOM = new Random();
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Phantom phantom) {
                  if (!phantom.getTags().contains("fromUniversal")) {
                     if (!event.loadedFromDisk()) {
                        if (!phantom.getTags().contains("phantom")) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           phantom.addTag("phantom");
                           if (day >= 20 && day < 40) {
                              phantom.setPhantomSize(9);
                              phantom.setCustomName(Component.literal("Mega-Phantom").withStyle(ChatFormatting.LIGHT_PURPLE));
                              phantom.addTag("megaPhantom");
                              double baseHealth = Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                              Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                              phantom.setHealth(phantom.getMaxHealth());
                           } else if (day >= 40 && day < 50) {
                              phantom.setPhantomSize(9);
                              double baseHealth = Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                              Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                              phantom.setHealth(phantom.getMaxHealth());
                              SkeletonRiderHelper.spawnRandomSkeleton(level, phantom, day);
                           } else if (day >= 50 && day < 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 99) {
                                 phantom.setPhantomSize(18);
                                 double baseHealth = Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                                 Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                                 phantom.setHealth(phantom.getMaxHealth());
                                 SkeletonRiderHelper.spawnRandomSkeleton(level, phantom, day);
                                 List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                                 Collections.shuffle(shuffled);
                                 int count = 1;
                                 count = Math.min(count, shuffled.size());

                                 for (int i = 0; i < count; i++) {
                                    Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                                    phantom.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                                 }
                              } else {
                                 for (int i = 0; i < 4; i++) {
                                    Ghast ghast = (Ghast)EntityType.GHAST.create(level);
                                    if (ghast != null) {
                                       ghast.moveTo(phantom.getX(), phantom.getY(), phantom.getZ(), phantom.getYRot(), phantom.getXRot());
                                       ((MobSpawnTypeAccessor)ghast).setSpawnType(MobSpawnType.MOB_SUMMONED);
                                       ghast.setCustomName(Component.literal("Giga-Phantom-Transmutado").withStyle(ChatFormatting.LIGHT_PURPLE));
                                       ghast.addTag("fromPhantom");
                                       level.addFreshEntity(ghast);
                                    }
                                 }

                                 level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> phantom.remove(RemovalReason.DISCARDED)));
                              }
                           } else if (day >= 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 75) {
                                 phantom.setPhantomSize(18);
                                 double baseHealth = Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                                 Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                                 phantom.setHealth(phantom.getMaxHealth());
                                 SkeletonRiderHelper.spawnRandomSkeleton(level, phantom, day);
                                 List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                                 Collections.shuffle(shuffled);
                                 int count = 1;
                                 count = Math.min(count, shuffled.size());

                                 for (int i = 0; i < count; i++) {
                                    Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                                    phantom.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                                 }
                              } else {
                                 for (int i = 0; i < 4; i++) {
                                    Ghast ghast = (Ghast)EntityType.GHAST.create(level);
                                    if (ghast != null) {
                                       ((MobSpawnTypeAccessor)ghast).setSpawnType(MobSpawnType.MOB_SUMMONED);
                                       ghast.moveTo(phantom.getX(), phantom.getY(), phantom.getZ(), phantom.getYRot(), phantom.getXRot());
                                       ghast.setCustomName(Component.literal("Giga-Phantom-Transmutado").withStyle(ChatFormatting.LIGHT_PURPLE));
                                       ghast.addTag("fromPhantom");
                                       level.addFreshEntity(ghast);
                                    }
                                 }

                                 level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> phantom.remove(RemovalReason.DISCARDED)));
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
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 60) {
            Entity entity = event.getEntity();
            if (!entity.level().isClientSide) {
               if (entity.level() instanceof ServerLevel level) {
                  if (entity instanceof LivingEntity) {
                     if (entity instanceof Phantom phantom) {
                        if (phantom.tickCount % 30 == 0) {
                           if (!EventHooks.canEntityGrief(level, phantom)) {
                              return;
                           }

                           double RADIUS = 0.5;
                           double FOOT_CUTOFF = 0.6;
                           AABB bb = phantom.getBoundingBox();
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
                              if (canPhantomDestroy(state) && !(state.getDestroySpeed(level, pos) < 0.0F)) {
                                 level.destroyBlock(pos, true, phantom);
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

   public static boolean canPhantomDestroy(BlockState state) {
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

   static {
      Map<Holder<MobEffect>, Integer> map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 0);
      map.put(MobEffects.DAMAGE_BOOST, 0);
      map.put(MobEffects.REGENERATION, 0);
      map.put(MobEffects.INVISIBILITY, 0);
      EFFECTS = Collections.unmodifiableMap(map);
   }
}
