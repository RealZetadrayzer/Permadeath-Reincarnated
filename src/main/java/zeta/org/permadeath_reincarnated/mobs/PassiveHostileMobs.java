package zeta.org.permadeath_reincarnated.mobs;

import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.entities.CustomZombieHorse;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.goals.ConditionalMeleeAttackGoal;
import zeta.org.permadeath_reincarnated.goals.ConditionalNearestAttackableGoal;

@EventBusSubscriber
public class PassiveHostileMobs {
   private static final double DEFAULT_DAMAGE = 4.0;
   private static final Set<EntityType<? extends LivingEntity>> PASSIVE_MELEE = Set.of(
      EntityType.ZOMBIE_HORSE,
      EntityType.ALLAY,
      EntityType.BEE,
      EntityType.RABBIT,
      EntityType.CAMEL,
      EntityType.CAT,
      EntityType.CHICKEN,
      EntityType.TROPICAL_FISH,
      EntityType.COD,
      EntityType.COW,
      EntityType.HORSE,
      EntityType.DOLPHIN,
      EntityType.FOX,
      EntityType.FROG,
      EntityType.GLOW_SQUID,
      EntityType.GOAT,
      EntityType.AXOLOTL,
      EntityType.MULE,
      EntityType.OCELOT,
      EntityType.PANDA,
      EntityType.PIG,
      EntityType.PUFFERFISH,
      EntityType.MOOSHROOM,
      EntityType.PARROT,
      EntityType.SALMON,
      EntityType.SKELETON_HORSE,
      EntityType.SQUID,
      EntityType.STRIDER,
      EntityType.TADPOLE,
      EntityType.TURTLE,
      EntityType.VILLAGER,
      EntityType.WANDERING_TRADER,
      EntityType.SHEEP,
      EntityType.WOLF,
      EntityType.DONKEY,
      EntityType.POLAR_BEAR,
      EntityType.SNIFFER,
      EntityType.ARMADILLO
   );
   private static final Set<EntityType<? extends LivingEntity>> PASSIVE_RANGED = Set.of(EntityType.LLAMA, EntityType.SNOW_GOLEM, EntityType.TRADER_LLAMA);
   private static final Set<EntityType<? extends LivingEntity>> AGGRESSIVE_NO_CHANCE = Set.of(EntityType.ZOMBIFIED_PIGLIN);

   private static boolean isCustomZombieHorse(EntityType<?> type) {
      return PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE.isBound() && type == PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE.get();
   }

   @SubscribeEvent
   public static void onEntityAttributes(EntityAttributeModificationEvent event) {
      Holder<Attribute> attackDamage = Attributes.ATTACK_DAMAGE;

      for (EntityType<? extends LivingEntity> type : PASSIVE_MELEE) {
         if (!event.has(type, attackDamage)) {
            event.add(type, attackDamage, 4.0);
         }
      }

      for (EntityType<? extends LivingEntity> type : PASSIVE_RANGED) {
         if (!event.has(type, attackDamage)) {
            event.add(type, attackDamage, 4.0);
         }
      }

      for (EntityType<? extends LivingEntity> type : AGGRESSIVE_NO_CHANCE) {
         if (!event.has(type, attackDamage)) {
            event.add(type, attackDamage, 4.0);
         }
      }

      if (PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE.isBound()) {
         EntityType<CustomZombieHorse> custom = (EntityType<CustomZombieHorse>)PermadeathEntityRegistry.CUSTOM_ZOMBIE_HORSE.get();
         if (!event.has(custom, attackDamage)) {
            event.add(custom, attackDamage, 4.0);
         }
      }
   }

   @SubscribeEvent
   public static void onEntitySpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (event.getLevel() instanceof ServerLevel) {
            if (entity instanceof LivingEntity living) {
               EntityType type = living.getType();
               if (living instanceof Mob mob) {
                  if (PASSIVE_MELEE.contains(type) || AGGRESSIVE_NO_CHANCE.contains(type) || isCustomZombieHorse(type)) {
                     makeMeleeHostile((PathfinderMob)mob);
                  }

                  if (PASSIVE_RANGED.contains(type)) {
                     makeTargetPlayer(mob);
                  }
               }
            }
         }
      }
   }

   private static void makeMeleeHostile(PathfinderMob mob) {
      if (!(mob instanceof Frog)) {
         mob.goalSelector.addGoal(1, new ConditionalMeleeAttackGoal(mob, 0.9, false, 20));
      }

      makeTargetPlayer(mob);
   }

   private static void makeTargetPlayer(Mob mob) {
      mob.targetSelector.addGoal(1, new ConditionalNearestAttackableGoal(mob, Player.class, true, 20));
   }

   public static boolean isPassiveHostileType(EntityType<?> type) {
      return PASSIVE_MELEE.contains(type) || PASSIVE_RANGED.contains(type) || AGGRESSIVE_NO_CHANCE.contains(type) || isCustomZombieHorse(type);
   }
}
