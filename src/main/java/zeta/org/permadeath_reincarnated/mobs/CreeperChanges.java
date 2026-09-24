package zeta.org.permadeath_reincarnated.mobs;

import java.lang.reflect.Field;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import zeta.org.permadeath_reincarnated.mixins.CreeperAccessor;
import zeta.org.permadeath_reincarnated.mixins.CreeperMixin;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class CreeperChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Creeper creeper) {
         if (!event.loadedFromDisk()) {
            if (!creeper.level().isClientSide) {
               if (day >= 30 && day < 40) {
                  if (!creeper.level().dimension().equals(Level.END) && !creeper.level().dimension().equals(Level.NETHER)) {
                     creeper.setCustomName(Component.literal("Creeper Energético").withStyle(ChatFormatting.BLUE));
                     creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                     creeper.addTag("energeticCreeper");
                  } else {
                     creeper.setCustomName(Component.literal("Ender Creeper").withStyle(ChatFormatting.LIGHT_PURPLE));
                     creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                     creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                     creeper.addTag("ender");

                     try {
                        Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                        explosionRadiusField.setAccessible(true);
                        explosionRadiusField.setInt(creeper, 4);
                     } catch (Exception var12) {
                     }
                  }
               }

               if (day >= 40 && day < 50) {
                  creeper.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                  creeper.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                  if (!creeper.level().dimension().equals(Level.END) && !creeper.level().dimension().equals(Level.NETHER)) {
                     creeper.setCustomName(Component.literal("Creeper Energético").withStyle(ChatFormatting.BLUE));
                     creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                     creeper.addTag("energeticCreeper");
                  } else {
                     if (!creeper.getTags().contains("fromEnderman")) {
                        creeper.setCustomName(Component.literal("Ender Creeper").withStyle(ChatFormatting.LIGHT_PURPLE));
                     }

                     creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                     creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                     creeper.addTag("ender");

                     try {
                        Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                        explosionRadiusField.setAccessible(true);
                        explosionRadiusField.setInt(creeper, 4);
                     } catch (Exception var11) {
                     }
                  }
               }

               if (day >= 50 && day < 60) {
                  int chance = 1 + RANDOM.nextInt(100);
                  creeper.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                  creeper.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                  if (!creeper.level().dimension().equals(Level.END) && !creeper.level().dimension().equals(Level.NETHER)) {
                     if (chance <= 80) {
                        creeper.setCustomName(Component.literal("Quantum Creeper").withStyle(ChatFormatting.GOLD));
                        creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);

                        try {
                           Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                           explosionRadiusField.setAccessible(true);
                           explosionRadiusField.setInt(creeper, 12);
                        } catch (Exception var9) {
                        }
                     } else {
                        if (!creeper.getTags().contains("fromEnderman")) {
                           creeper.setCustomName(Component.literal("Ender Creeper").withStyle(ChatFormatting.LIGHT_PURPLE));
                        }

                        creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                        creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                        creeper.addTag("ender");

                        try {
                           Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                           explosionRadiusField.setAccessible(true);
                           explosionRadiusField.setInt(creeper, 4);
                        } catch (Exception var8) {
                        }
                     }
                  } else {
                     if (!creeper.getTags().contains("fromEnderman")) {
                        creeper.setCustomName(Component.literal("Ender Creeper").withStyle(ChatFormatting.LIGHT_PURPLE));
                     }

                     creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                     creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                     creeper.addTag("ender");

                     try {
                        Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                        explosionRadiusField.setAccessible(true);
                        explosionRadiusField.setInt(creeper, 4);
                     } catch (Exception var10) {
                     }
                  }
               }

               if (day >= 60) {
                  creeper.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                  creeper.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                  creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                  if (!creeper.getTags().contains("fromEnderman")) {
                     creeper.setCustomName(Component.literal("Ender Quantum Creeper").withStyle(ChatFormatting.GOLD));
                  }

                  creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                  ((CreeperAccessor)creeper).setMaxSwell(15);
                  creeper.addTag("ender_quantum");

                  try {
                     Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                     explosionRadiusField.setAccessible(true);
                     explosionRadiusField.setInt(creeper, 12);
                  } catch (Exception var7) {
                  }
               }

               if (creeper.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                  creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), true);
                  creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                  creeper.setCustomName(Component.literal("Ender Quantum Creeper").withStyle(ChatFormatting.GOLD));
                  creeper.addTag("ender_quantum");

                  try {
                     Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                     explosionRadiusField.setAccessible(true);
                     explosionRadiusField.setInt(creeper, 12);
                  } catch (Exception var6) {
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onExplosion(Detonate event) {
      if (event.getExplosion().getDirectSourceEntity() instanceof Creeper creeper) {
         if (!creeper.level().isClientSide) {
            if (creeper.getTags().contains("ender") || creeper.getTags().contains("ender_quantum")) {
               creeper.getEntityData().set(CreeperMixin.accessor$DATA_IS_POWERED(), false);
            }

            creeper.removeAllEffects();
         }
      }
   }
}
