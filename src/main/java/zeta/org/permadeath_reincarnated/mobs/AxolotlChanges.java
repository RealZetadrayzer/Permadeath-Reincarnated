package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobReplacement;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class AxolotlChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof LivingEntity) {
                  if (entity instanceof Axolotl axolotl) {
                     if (axolotl.getSpawnType() != MobSpawnType.BUCKET) {
                        if (!event.loadedFromDisk()) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                           if (day >= 40 && day < 60 && chance <= 10) {
                              axolotl.addTag("isGuardian");
                              axolotl.addTag("protectorAxolotl");
                              axolotl.setCustomName(Component.literal("Ajolote Protector").withStyle(ChatFormatting.DARK_AQUA));
                              axolotl.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 3, false, true));
                              axolotl.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 2, false, true));
                              axolotl.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
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
      if (event.getEntity() instanceof Axolotl axolotl) {
         if (!axolotl.level().isClientSide) {
            if (axolotl.getTags().contains("protectorAxolotl")) {
               if (axolotl.level() instanceof ServerLevel level) {
                  if (axolotl.tickCount % 10 == 0) {
                     Player nearest = level.getNearestPlayer(axolotl, 48.0);
                     axolotl.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 3, false, true));
                     axolotl.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 2, false, true));
                     axolotl.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
                     if (nearest instanceof ServerPlayer player) {
                        if (player.isInWaterOrRain() && axolotl.isInWaterOrRain()) {
                           player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
                           player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, true));
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_FIND_PROTECTOR_AXOLOTL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTickTransform(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 60) {
            if (event.getEntity() instanceof Axolotl axolotl) {
               if (!axolotl.getTags().contains("protectorAxolotl")) {
                  if (!axolotl.level().isClientSide) {
                     if (axolotl.level() instanceof ServerLevel level) {
                        if (axolotl.isAlive()) {
                           axolotl.addTag("fromAxolotl");
                           axolotl.setCustomName(Component.literal("Ajolote-Transmutado").withStyle(ChatFormatting.GOLD));
                           MobReplacement.convertToWithData(axolotl, EntityType.DOLPHIN, false);
                           axolotl.discard();
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
