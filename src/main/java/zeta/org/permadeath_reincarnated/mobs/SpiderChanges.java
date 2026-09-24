package zeta.org.permadeath_reincarnated.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.CaveSpider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.SkeletonRiderHelper;

@EventBusSubscriber
public class SpiderChanges {
   private static final Random RANDOM = new Random();
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;
   private static final Map<Holder<MobEffect>, Integer> EFFECTS_NO_GLOWING;

   @SubscribeEvent
   public static void onEntitySpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.level() instanceof ServerLevel level) {
               if (!event.loadedFromDisk()) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  EntityType<?> type = mob.getType();
                  if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER) {
                     if (!mob.getTags().contains("spider")) {
                        int minEffects = 0;
                        int maxEffects = 0;
                        if (day >= 10 && day < 20) {
                           minEffects = 1;
                           maxEffects = 3;
                           if (type == EntityType.CAVE_SPIDER) {
                              mob.setCustomName(Component.literal("Super Araña de Cueva").withStyle(ChatFormatting.DARK_GREEN));
                           } else {
                              mob.setCustomName(Component.literal("Super Araña").withStyle(ChatFormatting.GREEN));
                           }
                        } else if (day >= 20 && day < 25) {
                           minEffects = 3;
                           maxEffects = 5;
                           if (type == EntityType.CAVE_SPIDER) {
                              mob.setCustomName(Component.literal("Mega Araña de Cueva").withStyle(ChatFormatting.DARK_GREEN));
                           } else {
                              SkeletonRiderHelper.spawnRandomSkeleton(level, mob, day);
                           }
                        } else if (day >= 25 && day < 40) {
                           minEffects = 5;
                           maxEffects = 5;
                           if (type == EntityType.CAVE_SPIDER) {
                              mob.setCustomName(Component.literal("Ultra Araña de Cueva").withStyle(ChatFormatting.DARK_GREEN));
                              mob.addTag("ultraAraña");
                           } else {
                              SkeletonRiderHelper.spawnRandomSkeleton(level, mob, day);
                           }
                        } else if (day >= 40) {
                           minEffects = 5;
                           maxEffects = 5;
                           if (type == EntityType.CAVE_SPIDER) {
                              mob.setCustomName(Component.literal("Ultra Araña de Cueva").withStyle(ChatFormatting.DARK_GREEN));
                           }
                        }

                        if (maxEffects > 0) {
                           if (day < 50) {
                              List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                              Collections.shuffle(shuffled);
                              int count = minEffects + RANDOM.nextInt(maxEffects - minEffects + 1);
                              count = Math.min(count, shuffled.size());

                              for (int i = 0; i < count; i++) {
                                 Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                                 mob.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                              }
                           } else {
                              List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS_NO_GLOWING.entrySet());
                              Collections.shuffle(shuffled);
                              int count = minEffects + RANDOM.nextInt(maxEffects - minEffects + 1);
                              count = Math.min(count, shuffled.size());

                              for (int i = 0; i < count; i++) {
                                 Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                                 mob.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                              }
                           }
                        }

                        mob.addTag("spider");
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (!target.level().isClientSide) {
            if (target.isAffectedByPotions()) {
               if (attacker instanceof CaveSpider) {
                  if (DayGlobalCount.CURRENT_DAY >= 50) {
                     target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0, false, true));
                     target.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2, false, true));
                  }
               }
            }
         }
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
      map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 2);
      map.put(MobEffects.DAMAGE_BOOST, 3);
      map.put(MobEffects.JUMP, 4);
      map.put(MobEffects.REGENERATION, 3);
      map.put(MobEffects.INVISIBILITY, 0);
      map.put(MobEffects.SLOW_FALLING, 0);
      map.put(MobEffects.DAMAGE_RESISTANCE, 2);
      EFFECTS_NO_GLOWING = Collections.unmodifiableMap(map);
   }
}
