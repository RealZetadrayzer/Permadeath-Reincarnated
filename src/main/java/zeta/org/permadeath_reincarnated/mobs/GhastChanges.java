package zeta.org.permadeath_reincarnated.mobs;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class GhastChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Ghast ghast) {
         if (!ghast.getTags().contains("fromUniversal")) {
            if (!event.loadedFromDisk()) {
               if (!ghast.level().isClientSide) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  if (ghast.level().dimension().equals(Level.NETHER)) {
                     if (day >= 25 && day < 40) {
                        int powerExplosion = 3 + RANDOM.nextInt(3);
                        int health = PermadeathConfig.CUSTOM_CHANGES.get() ? 80 + RANDOM.nextInt(41) : 40 + RANDOM.nextInt(21);
                        ghast.setCustomName(Component.literal("Ghast Demoníaco").withStyle(ChatFormatting.GOLD));
                        ghast.addTag("ghastDemoniaco");
                        Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                        ghast.setHealth(ghast.getMaxHealth());

                        try {
                           Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                           explosionPowerField.setAccessible(true);
                           explosionPowerField.setInt(ghast, powerExplosion);
                        } catch (Exception var13) {
                        }
                     }

                     if (day >= 40 && !ghast.getTags().contains("fromZPiglin")) {
                        int chance = 1 + RANDOM.nextInt(100);
                        if (chance <= 75) {
                           int powerExplosion = 0;
                           int health = PermadeathConfig.CUSTOM_CHANGES.get() ? 80 + RANDOM.nextInt(41) : 40 + RANDOM.nextInt(21);
                           ghast.setCustomName(Component.literal("Demonio Flotante").withStyle(ChatFormatting.GOLD));
                           ghast.addTag("floatingDemon");
                           Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                           ghast.setHealth(ghast.getMaxHealth());

                           try {
                              Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                              explosionPowerField.setAccessible(true);
                              explosionPowerField.setInt(ghast, powerExplosion);
                           } catch (Exception var12) {
                           }
                        } else {
                           int powerExplosion = 3 + RANDOM.nextInt(3);
                           int health = PermadeathConfig.CUSTOM_CHANGES.get() ? 80 + RANDOM.nextInt(41) : 40 + RANDOM.nextInt(21);
                           ghast.setCustomName(Component.literal("Ghast Demoníaco").withStyle(ChatFormatting.GOLD));
                           ghast.addTag("ghastDemoniaco");
                           Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                           ghast.setHealth(ghast.getMaxHealth());

                           try {
                              Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                              explosionPowerField.setAccessible(true);
                              explosionPowerField.setInt(ghast, powerExplosion);
                           } catch (Exception var11) {
                           }
                        }
                     }
                  }

                  if (ghast.level().dimension().equals(Level.END) && day >= 30) {
                     int powerExplosion = 7;
                     int health = 200;
                     ghast.setCustomName(Component.literal("Ender Ghast").withStyle(ChatFormatting.LIGHT_PURPLE));
                     Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                     ghast.setHealth(ghast.getMaxHealth());
                     ghast.addTag("ender");

                     try {
                        Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                        explosionPowerField.setAccessible(true);
                        explosionPowerField.setInt(ghast, powerExplosion);
                     } catch (Exception var10) {
                     }
                  }

                  if (ghast.level().dimension().equals(Level.OVERWORLD) && day >= 50) {
                     int powerExplosion = 7;
                     int health = 200;
                     if (!ghast.getTags().contains("fromPhantom")) {
                        ghast.setCustomName(Component.literal("Ender Ghast").withStyle(ChatFormatting.LIGHT_PURPLE));
                     }

                     Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                     ghast.setHealth(ghast.getMaxHealth());
                     ghast.addTag("ender");

                     try {
                        Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                        explosionPowerField.setAccessible(true);
                        explosionPowerField.setInt(ghast, powerExplosion);
                     } catch (Exception var9) {
                     }
                  }

                  if (ghast.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                     int powerExplosion = 12;
                     int health = 400;
                     ghast.setCustomName(Component.literal("Ghast Definitivo").withStyle(ChatFormatting.GOLD));
                     Objects.requireNonNull(ghast.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                     ghast.setHealth(ghast.getMaxHealth());
                     ghast.addTag("definitivo");

                     try {
                        Field explosionPowerField = Ghast.class.getDeclaredField("explosionPower");
                        explosionPowerField.setAccessible(true);
                        explosionPowerField.setInt(ghast, powerExplosion);
                     } catch (Exception var8) {
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult) {
         Entity hit = hitResult.getEntity();
         if (hit instanceof LivingEntity living) {
            if (!hit.level().isClientSide) {
               if (!living.isDeadOrDying()) {
                  if (living.isAffectedByPotions()) {
                     if (event.getProjectile().getOwner() != null) {
                        if (event.getProjectile().getOwner().getTags().contains("floatingDemon")) {
                           living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 400, 49, false, true));
                           living.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 4, false, true));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
