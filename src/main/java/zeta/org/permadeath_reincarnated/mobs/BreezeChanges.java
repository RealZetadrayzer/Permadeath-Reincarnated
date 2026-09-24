package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class BreezeChanges {
   private static final double BURST_RADIUS = 6.0;

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 25) {
            Entity entity = event.getEntity();
            if (!entity.level().isClientSide) {
               if (entity.level() instanceof ServerLevel) {
                  if (entity instanceof Breeze breeze) {
                     if (!event.loadedFromDisk()) {
                        breeze.setCustomName(Component.literal("Breeze-Support").withStyle(ChatFormatting.AQUA));
                        breeze.addTag("breezeSupport");
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWindChargeImpact(ProjectileImpactEvent event) {
      if (event.getProjectile() instanceof BreezeWindCharge windCharge) {
         if (windCharge.getOwner() instanceof Breeze breeze) {
            if (breeze.getTags().contains("breezeSupport")) {
               if (breeze.level() instanceof ServerLevel level) {
                  if (!level.isClientSide) {
                     HitResult hitResult = event.getRayTraceResult();
                     Vec3 hitPosition = hitResult.getLocation();
                     AABB burstArea = new AABB(
                        hitPosition.x - 6.0, hitPosition.y - 6.0, hitPosition.z - 6.0, hitPosition.x + 6.0, hitPosition.y + 6.0, hitPosition.z + 6.0
                     );
                     Entity directHitEntity = null;
                     if (hitResult instanceof EntityHitResult entityHitResult) {
                        directHitEntity = entityHitResult.getEntity();
                     }

                     if (directHitEntity instanceof Player player) {
                        Vec3 playerCenter = player.position().add(0.0, 1.0, 0.0);
                        ScheduleInTicks.schedule(() -> spawnExtraWindCharge(level, playerCenter), 5 + RandomUtil.RANDOM.nextInt(16));
                        ScheduleInTicks.schedule(() -> spawnExtraWindCharge(level, playerCenter), 10 + RandomUtil.RANDOM.nextInt(21));
                     } else {
                        if (directHitEntity instanceof LivingEntity livingEntity && livingEntity.isAffectedByPotions()) {
                           applyBreezeSupport(livingEntity);
                           applyBreezeSupport(breeze);
                        }

                        for (LivingEntity targetEntity : level.getEntitiesOfClass(LivingEntity.class, burstArea, LivingEntity::isAlive)) {
                           if (!(targetEntity instanceof Player)
                              && targetEntity != breeze
                              && targetEntity != directHitEntity
                              && targetEntity.isAffectedByPotions()
                              && targetEntity.isAlive()
                              && !targetEntity.isDeadOrDying()) {
                              applyBreezeSupport(targetEntity);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void applyBreezeSupport(LivingEntity target) {
      int roll = 1 + RandomUtil.RANDOM.nextInt(6);
      switch (roll) {
         case 1:
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, false, true));
            break;
         case 2:
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1, false, true));
            break;
         case 3:
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, true));
            break;
         case 4:
            target.addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 1, false, true));
            break;
         case 5:
            target.addEffect(new MobEffectInstance(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate(), 100, 0, false, true));
            break;
         case 6:
            target.heal(2.0F);
      }
   }

   private static void spawnExtraWindCharge(ServerLevel level, Vec3 centerPosition) {
      double offsetX = level.random.nextDouble() * 10.0 - 5.0;
      double offsetZ = level.random.nextDouble() * 10.0 - 5.0;
      double offsetY = 3.0 + (level.random.nextDouble() * 2.0 - 1.0);
      Vec3 spawnPosition = centerPosition.add(offsetX, offsetY, offsetZ);
      OminousItemSpawner ominousSpawner = OminousItemSpawner.create(level, new ItemStack(Items.WIND_CHARGE));
      ominousSpawner.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
      level.addFreshEntity(ominousSpawner);
   }
}
