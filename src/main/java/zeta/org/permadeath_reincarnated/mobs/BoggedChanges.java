package zeta.org.permadeath_reincarnated.mobs;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import org.jetbrains.annotations.NotNull;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class BoggedChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Bogged bogged) {
                     if (!event.loadedFromDisk()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 20) {
                           chooseClass(level, bogged);
                        }
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
         Entity var8 = hitResult.getEntity();
         int day = DayGlobalCount.CURRENT_DAY;
         if (var8 instanceof LivingEntity living) {
            if (!var8.level().isClientSide) {
               if (!living.isDeadOrDying()) {
                  if (living.isAffectedByPotions()) {
                     if (event.getProjectile().getOwner() != null) {
                        if (event.getProjectile().getOwner().getTags().contains("darkBogged")) {
                           if (day < 30) {
                              living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1, false, true));
                           } else {
                              int duration = day >= 50 ? 2400 : 800;
                              living.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, day >= 50 ? 4 : 2, false, true));
                              living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, day >= 50 ? 4 : 1, false, true));
                           }
                        }

                        if (event.getProjectile().getOwner().getTags().contains("alucBogged")) {
                           List<MobEffectInstance> effects = getBoggedEffects(day);
                           MobEffectInstance chosen = effects.get(RandomSource.create().nextInt(effects.size()));
                           living.addEffect(chosen);
                           if (living instanceof ServerPlayer player) {
                              PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_BAD_TRIP_ID);
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
   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (target.level() instanceof ServerLevel level) {
            if (!target.level().isClientSide) {
               int day = DayGlobalCount.CURRENT_DAY;
               if (attacker instanceof Bogged bogged) {
                  if (!bogged.getTags().contains("stickyBogged")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  int chanceThreshold = day >= 50 ? 50 : (day >= 30 ? 25 : 10);
                  if (chance <= chanceThreshold) {
                     if (day < 30) {
                        Tadpole tadpole = (Tadpole)EntityType.TADPOLE.create(level);
                        if (tadpole != null) {
                           ((MobSpawnTypeAccessor)tadpole).setSpawnType(MobSpawnType.MOB_SUMMONED);
                           tadpole.addTag("fromBogged");
                           tadpole.addTag("shouldNaturallyDespawn");
                           tadpole.setCustomName(Component.literal("Renacuajo Pegajoso").withStyle(ChatFormatting.GREEN));
                           tadpole.moveTo(target.getX(), target.getY() + 0.5, target.getZ());
                           tadpole.removeAllEffects();
                           tadpole.addEffect(new MobEffectInstance(MobEffects.OOZING, -1, 0, false, true));
                           level.addFreshEntity(tadpole);
                        }
                     } else {
                        for (int i = 0; i < 2; i++) {
                           Tadpole tadpole = (Tadpole)EntityType.TADPOLE.create(level);
                           if (tadpole != null) {
                              ((MobSpawnTypeAccessor)tadpole).setSpawnType(MobSpawnType.MOB_SUMMONED);
                              tadpole.addTag("onDeathCloud");
                              tadpole.addTag("fromBogged");
                              tadpole.addTag("shouldNaturallyDespawn");
                              tadpole.setCustomName(Component.literal("Renacuajo Pegajoso").withStyle(ChatFormatting.GREEN));
                              tadpole.moveTo(target.getX(), target.getY() + 0.5, target.getZ());
                              tadpole.removeAllEffects();
                              tadpole.addEffect(new MobEffectInstance(MobEffects.OOZING, -1, 0, false, true));
                              level.addFreshEntity(tadpole);
                           }
                        }
                     }
                  }
               }

               if (attacker instanceof Frog frog) {
                  if (!frog.getTags().contains("fromBogged") && !frog.getTags().contains("ultraFrog")) {
                     return;
                  }

                  List<MobEffectInstance> effects = getFrogEffects(day);
                  MobEffectInstance chosen = effects.get(RandomSource.create().nextInt(effects.size()));
                  target.addEffect(chosen);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof Tadpole tadpole) {
               if (tadpole.getTags().contains("fromBogged")) {
                  if (tadpole.getTags().contains("onDeathCloud")) {
                     AreaEffectCloud cloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(level);
                     BlockPos pos = tadpole.blockPosition();
                     if (cloud != null) {
                        cloud.addTag("fromStickyTadpole");
                        cloud.addEffect(new MobEffectInstance(MobEffects.OOZING, 200, 0, false, true));
                        cloud.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                        cloud.setParticle(ParticleTypes.SNEEZE);
                        cloud.setRadius(2.0F);
                        cloud.setDuration(200);
                        cloud.setWaitTime(0);
                        cloud.setRadiusPerTick(0.0F);
                        cloud.setDurationOnUse(0);
                        cloud.setRadiusOnUse(0.0F);
                        cloud.setOwner(tadpole);
                        level.addFreshEntity(cloud);
                     }
                  }
               }
            }
         }
      }
   }

   public static void chooseClass(ServerLevel level, Mob skeleton) {
      int chance = 1 + RandomUtil.RANDOM.nextInt(3);
      switch (chance) {
         case 1:
            claseAlucinogena(level, skeleton);
            break;
         case 2:
            claseOscura(level, skeleton);
            break;
         case 3:
            clasePegajosa(level, skeleton);
      }
   }

   private static void claseAlucinogena(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Bogged Alucinógeno").withStyle(ChatFormatting.DARK_GREEN));
      skeleton.addTag("alucBogged");
      skeleton.addTag("classBogged");
      RegistryAccess registryAccess = level.registryAccess();
      ItemStack helmet = new ItemStack(Items.RED_MUSHROOM_BLOCK);
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(6192150, false));
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(6192150, false));
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(6192150, false));
      ItemStack bow = new ItemStack(Items.BOW);
      if (day >= 30) {
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         int powerLevel = day >= 50 ? 70 : 20;
         bow.enchant(power, powerLevel);
      }

      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      PotionContents contents = new PotionContents(
         Optional.of(Potions.WATER), Optional.of(657930), List.of(new MobEffectInstance(MobEffects.WATER_BREATHING, 0, -1))
      );
      arrow.set(DataComponents.POTION_CONTENTS, contents);
      skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      float healthValue = day >= 50 ? 60.0F : 40.0F;
      Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(healthValue);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void claseOscura(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Bogged Oscuro").withStyle(ChatFormatting.BLACK));
      skeleton.addTag("darkBogged");
      skeleton.addTag("classBogged");
      RegistryAccess registryAccess = level.registryAccess();
      ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
      helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(657930, false));
      ItemStack bow = new ItemStack(Items.BOW);
      ItemStack rose = new ItemStack(Items.WITHER_ROSE);
      skeleton.setItemSlot(EquipmentSlot.OFFHAND, rose);
      if (day >= 30) {
         Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
         Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
         int powerLevel = day >= 50 ? 60 : 50;
         int resistanceLevel = day >= 50 ? 1 : -1;
         int speedLevel = day >= 50 ? 0 : -1;
         bow.enchant(power, powerLevel);
         bow.enchant(punch, 5);
         if (speedLevel >= 0) {
            skeleton.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speedLevel, false, true));
         }

         if (resistanceLevel >= 0) {
            skeleton.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, speedLevel, false, true));
         }
      }

      float healthValue = day >= 30 ? 40.0F : 20.0F;
      Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(healthValue);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   private static void clasePegajosa(ServerLevel level, Mob skeleton) {
      int day = DayGlobalCount.CURRENT_DAY;
      Frog frog = (Frog)EntityType.FROG.create(level);
      if (frog != null) {
         ((MobSpawnTypeAccessor)frog).setSpawnType(MobSpawnType.MOB_SUMMONED);
         frog.setCustomName(Component.literal("Rana Super-Pegajosa").withStyle(ChatFormatting.GREEN));
         frog.moveTo(skeleton.getX(), skeleton.getY(), skeleton.getZ(), skeleton.getYRot(), skeleton.getXRot());
         frog.addTag("fromBogged");
         frog.addTag("shouldNaturallyDespawn");
         frog.startRiding(skeleton);
         level.addFreshEntity(frog);
      }

      MobPreventEquipmentDrops.preventAllEquipmentDrop(skeleton);
      skeleton.setCustomName(Component.literal("Bogged Pegajoso").withStyle(ChatFormatting.GREEN));
      skeleton.addTag("stickyBogged");
      skeleton.addTag("classBogged");
      RegistryAccess registryAccess = level.registryAccess();
      ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
      helmet.set(DataComponents.DYED_COLOR, new DyedItemColor(8439583, false));
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      chestplate.set(DataComponents.DYED_COLOR, new DyedItemColor(8439583, false));
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      leggings.set(DataComponents.DYED_COLOR, new DyedItemColor(8439583, false));
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      boots.set(DataComponents.DYED_COLOR, new DyedItemColor(8439583, false));
      ItemStack slimeBall = new ItemStack(Items.SLIME_BALL);
      ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
      PotionContents contents = new PotionContents(Optional.of(Potions.WATER), Optional.of(9630876), List.of(new MobEffectInstance(MobEffects.OOZING, 600, 0)));
      arrow.set(DataComponents.POTION_CONTENTS, contents);
      skeleton.setItemSlot(EquipmentSlot.OFFHAND, arrow);
      Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
      Holder<Enchantment> breach = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
      int sharpnessLevel = day >= 50 ? 20 : 10;
      int breachLevel = day >= 50 ? 4 : 3;
      if (day >= 30) {
         slimeBall.enchant(sharpness, sharpnessLevel);
         slimeBall.enchant(breach, breachLevel);
      } else {
         slimeBall.enchant(sharpness, 5);
         slimeBall.enchant(breach, 2);
      }

      float healthValue = day >= 50 ? 100.0F : 40.0F;
      Objects.requireNonNull(skeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(healthValue);
      skeleton.setHealth(skeleton.getMaxHealth());
      skeleton.setItemSlot(EquipmentSlot.MAINHAND, slimeBall);
      skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
      skeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
      skeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
      skeleton.setItemSlot(EquipmentSlot.FEET, boots);
   }

   @NotNull
   private static List<MobEffectInstance> getFrogEffects(int day) {
      List<MobEffectInstance> effects;
      if (day < 30) {
         effects = List.of(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, true), new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0, false, true)
         );
      } else {
         effects = List.of(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 2, false, true), new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, 2, false, true)
         );
      }

      return effects;
   }

   @NotNull
   private static List<MobEffectInstance> getBoggedEffects(int day) {
      List<MobEffectInstance> effects;
      if (day < 30) {
         effects = List.of(
            new MobEffectInstance(MobEffects.BLINDNESS, 160, 0, false, true),
            new MobEffectInstance(MobEffects.POISON, 240, 0, false, true),
            new MobEffectInstance(MobEffects.WEAKNESS, 180, 0, false, true),
            new MobEffectInstance(MobEffects.WITHER, 160, 0, false, true)
         );
      } else {
         effects = List.of(
            new MobEffectInstance(MobEffects.BLINDNESS, day >= 50 ? 1200 : 400, 0, false, true),
            new MobEffectInstance(MobEffects.POISON, 400, day >= 50 ? 2 : 1, false, true),
            new MobEffectInstance(MobEffects.WEAKNESS, 400, day >= 50 ? 2 : 1, false, true),
            new MobEffectInstance(MobEffects.WITHER, 400, day >= 50 ? 2 : 1, false, true)
         );
      }

      return effects;
   }
}
