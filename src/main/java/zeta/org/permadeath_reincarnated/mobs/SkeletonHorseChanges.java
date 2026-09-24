package zeta.org.permadeath_reincarnated.mobs;

import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class SkeletonHorseChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof SkeletonHorse skeletonHorse) {
                     if (!skeletonHorse.getTags().contains("invisibleSkeletonHorse")) {
                        if (!event.loadedFromDisk()) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           if (day >= 10) {
                              int chance = 1 + RANDOM.nextInt(100);
                              int chanceThreshold = day >= 40 ? 10 : 1;
                              if (chance <= chanceThreshold) {
                                 skeletonHorse.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                                 skeletonHorse.setCustomName(Component.literal("Caballo Esqueleto Fantasma").withStyle(ChatFormatting.DARK_GRAY));
                                 skeletonHorse.addTag("invisibleSkeletonHorse");
                                 if (day >= 40) {
                                    skeletonHorse.addTag("onDeathTrap");
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

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof SkeletonHorse skeletonHorse) {
               if (skeletonHorse.getTags().contains("invisibleSkeletonHorse")) {
                  if (skeletonHorse.getTags().contains("onDeathTrap")) {
                     if (!skeletonHorse.getTags().contains("fromSecondTrap")) {
                        LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                        if (lightningBolt != null) {
                           lightningBolt.setPos(skeletonHorse.getX(), skeletonHorse.getY(), skeletonHorse.getZ());
                           lightningBolt.setVisualOnly(true);
                           level.addFreshEntity(lightningBolt);

                           for (int i = 0; i < 4; i++) {
                              SkeletonHorse skeletonHorse1 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(level);
                              Skeleton skeleton = (Skeleton)EntityType.SKELETON.create(level);
                              if (skeletonHorse1 != null) {
                                 skeletonHorse1.setPos(skeletonHorse.getX(), skeletonHorse.getY(), skeletonHorse.getZ());
                                 skeletonHorse1.addTag("fromSecondTrap");
                                 skeletonHorse1.finalizeSpawn(level, level.getCurrentDifficultyAt(skeletonHorse.blockPosition()), MobSpawnType.TRIGGERED, null);
                                 skeletonHorse1.invulnerableTime = 60;
                                 skeletonHorse1.setPersistenceRequired();
                                 skeletonHorse1.push(skeletonHorse.getRandom().triangle(0.0, 1.1485), 0.0, skeletonHorse.getRandom().triangle(0.0, 1.1485));
                                 level.addFreshEntity(skeletonHorse1);
                              }

                              if (skeleton != null) {
                                 DifficultyInstance difficultyinstance = level.getCurrentDifficultyAt(skeletonHorse.blockPosition());
                                 skeleton.setPos(skeletonHorse.getX(), skeletonHorse.getY(), skeletonHorse.getZ());
                                 skeleton.addTag("fromSecondTrap");
                                 skeleton.finalizeSpawn(level, level.getCurrentDifficultyAt(skeletonHorse.blockPosition()), MobSpawnType.TRIGGERED, null);
                                 skeleton.invulnerableTime = 60;
                                 skeleton.setPersistenceRequired();
                                 if (skeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                                    skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                                 }

                                 enchant(skeleton, EquipmentSlot.MAINHAND, difficultyinstance);
                                 enchant(skeleton, EquipmentSlot.HEAD, difficultyinstance);
                                 if (skeletonHorse1 != null) {
                                    skeleton.startRiding(skeletonHorse1);
                                 }

                                 level.addFreshEntity(skeleton);
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

   private static void enchant(Skeleton skeleton, EquipmentSlot slot, DifficultyInstance difficulty) {
      ItemStack itemstack = skeleton.getItemBySlot(slot);
      itemstack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      EnchantmentHelper.enchantItemFromProvider(
         itemstack, skeleton.level().registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, difficulty, skeleton.getRandom()
      );
      skeleton.setItemSlot(slot, itemstack);
   }
}
