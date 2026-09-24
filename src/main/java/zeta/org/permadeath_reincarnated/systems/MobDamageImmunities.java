package zeta.org.permadeath_reincarnated.systems;

import java.util.Set;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class MobDamageImmunities {
   @SubscribeEvent
   public static void onStalagDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide()) {
         if (entity.isAlive()) {
            if (entity.getTags().contains("stalagImmune")) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypes.STALAGMITE) || source.is(DamageTypes.FALLING_STALACTITE)) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSuffocationDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide()) {
         if (entity.isAlive()) {
            if (entity.getTags().contains("suffocationImmune") || entity instanceof Slime slime && slime.getSize() >= 6) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypes.IN_WALL)) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   private static void onExplosionDamage(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.getTags().contains("explosionImmune")) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_EXPLOSION)) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.getTags().contains("projectileImmune")) {
            DamageSource source = event.getSource();
            Entity immediate = source.getDirectEntity();
            Set<EntityType<?>> blockedProjectiles = Set.of(
               EntityType.ARROW,
               EntityType.SPECTRAL_ARROW,
               EntityType.TRIDENT,
               EntityType.SNOWBALL,
               EntityType.EGG,
               EntityType.FIREBALL,
               EntityType.SMALL_FIREBALL,
               EntityType.DRAGON_FIREBALL,
               EntityType.WITHER_SKULL,
               EntityType.SHULKER_BULLET,
               EntityType.LLAMA_SPIT
            );
            if (immediate != null && blockedProjectiles.contains(immediate.getType())) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onArrowDamage(LivingIncomingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.getTags().contains("arrowImmune")) {
            DamageSource source = event.getSource();
            Entity immediate = source.getDirectEntity();
            Set<EntityType<?>> blockedProjectiles = Set.of(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
            if (immediate != null && blockedProjectiles.contains(immediate.getType())) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   private static void onMagicAndExplosionDamageReceived(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.getTags().contains("fromModule")) {
               DamageSource source = event.getSource();
               if (source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC) || source.is(DamageTypeTags.IS_EXPLOSION)) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
