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
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class TadpoleChanges {
   private static final Random RANDOM = new Random();
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Tadpole tadpole) {
                     if (tadpole.getSpawnType() != MobSpawnType.BUCKET) {
                        if (!tadpole.getTags().contains("permadeathBucketed")) {
                           if (!event.loadedFromDisk()) {
                              int day = DayGlobalCount.CURRENT_DAY;
                              int minEffects = 0;
                              int maxEffects = 0;
                              if (day >= 20 && day < 25) {
                                 if (!tadpole.getTags().contains("fromBogged")) {
                                    tadpole.setCustomName(Component.literal("Renacuajo de la Muerte").withStyle(ChatFormatting.RED));
                                    tadpole.addTag("deathTadpole");
                                    minEffects = 3;
                                    maxEffects = 5;
                                 }
                              } else if (day >= 25 && !tadpole.getTags().contains("fromBogged")) {
                                 tadpole.setCustomName(Component.literal("Ultra Renacuajo de la Muerte").withStyle(ChatFormatting.RED));
                                 tadpole.addTag("deathTadpole");
                                 minEffects = 5;
                                 maxEffects = 5;
                                 if (day >= 45) {
                                    AttributeInstance tadpoleHealth = Objects.requireNonNull(tadpole.getAttribute(Attributes.MAX_HEALTH));
                                    AttributeInstance tadpoleDamage = Objects.requireNonNull(tadpole.getAttribute(Attributes.ATTACK_DAMAGE));
                                    tadpoleHealth.setBaseValue(20.0);
                                    tadpoleDamage.setBaseValue(15.0);
                                    tadpole.setHealth(tadpole.getMaxHealth());
                                 }
                              }

                              if (maxEffects > 0) {
                                 int count = minEffects + RANDOM.nextInt(maxEffects - minEffects + 1);
                                 List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                                 Collections.shuffle(shuffled);
                                 count = Math.min(count, shuffled.size());

                                 for (int i = 0; i < count; i++) {
                                    Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                                    tadpole.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
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
