package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;

@EventBusSubscriber
public class PotionsVFXHandler {
   @SubscribeEvent
   public static void onPotionDrink(Finish event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.level().isClientSide) {
            if (player.level() instanceof ServerLevel level) {
               if (DayGlobalCount.CURRENT_DAY >= 20) {
                  ItemStack stack = event.getItem();
                  PotionsVFXHandler.PermaPotionType type = getPermaPotionType(stack);
                  if (type != PotionsVFXHandler.PermaPotionType.NONE) {
                     playVfx(type, level, player.position().add(0.0, 0.75, 0.0));
                     if (player instanceof ServerPlayer serverPlayer) {
                        PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_CHEMISTRY_ID);
                        if (type == PotionsVFXHandler.PermaPotionType.RESURRECTION || type == PotionsVFXHandler.PermaPotionType.BEGINNING) {
                           PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_BEG_RES_POTION_CONSUMED_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPotionImpact(ProjectileImpactEvent event) {
      Entity projectile = event.getProjectile();
      if (projectile.level() instanceof ServerLevel level) {
         if (DayGlobalCount.CURRENT_DAY >= 20) {
            if (projectile instanceof ThrownPotion thrown) {
               if (thrown.getOwner() instanceof ServerPlayer serverPlayer) {
                  ItemStack stack = thrown.getItem();
                  PotionsVFXHandler.PermaPotionType type = getPermaPotionType(stack);
                  if (type != PotionsVFXHandler.PermaPotionType.NONE) {
                     HitResult hit = event.getRayTraceResult();
                     Vec3 hitPos = hit.getLocation();
                     playVfx(type, level, hitPos);
                     PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_CHEMISTRY_ID);
                     if (type == PotionsVFXHandler.PermaPotionType.RESURRECTION || type == PotionsVFXHandler.PermaPotionType.BEGINNING) {
                        PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_BEG_RES_POTION_CONSUMED_ID);
                     }
                  }
               }
            }
         }
      }
   }

   private static PotionsVFXHandler.PermaPotionType getPermaPotionType(ItemStack stack) {
      CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      CompoundTag tag = data.copyTag();
      if (tag.getInt("permadeath_wither_rage") == 1) {
         return PotionsVFXHandler.PermaPotionType.WITHER_RAGE;
      } else if (tag.getInt("permadeath_reincarnation") == 1) {
         return PotionsVFXHandler.PermaPotionType.REINCARNATION;
      } else if (tag.getInt("permadeath_shock") == 1) {
         return PotionsVFXHandler.PermaPotionType.SHOCK;
      } else if (tag.getInt("permadeath_resurrection") == 1) {
         return PotionsVFXHandler.PermaPotionType.RESURRECTION;
      } else {
         return tag.getInt("permadeath_beginning") == 1 ? PotionsVFXHandler.PermaPotionType.BEGINNING : PotionsVFXHandler.PermaPotionType.NONE;
      }
   }

   private static void playVfx(PotionsVFXHandler.PermaPotionType type, ServerLevel level, Vec3 pos) {
      switch (type) {
         case WITHER_RAGE:
            playVfxWitherRage(level, pos);
            break;
         case REINCARNATION:
            playVfxReincarnation(level, pos);
            break;
         case SHOCK:
            playVfxShock(level, pos);
            break;
         case RESURRECTION:
            playVfxResurrection(level, pos);
            break;
         case BEGINNING:
            playVfxBeginning(level, pos);
      }
   }

   private static void playVfxWitherRage(ServerLevel level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0F, 0.5F);
      level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.25, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.SMALL_FLAME, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.TRIAL_OMEN, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.05);
   }

   private static void playVfxReincarnation(ServerLevel level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.PLAYERS, 1.0F, 2.0F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0F, 0.5F);
      level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.25, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, 20, 0.0, 0.0, 0.0, 0.5);
      level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, 40, 0.0, 0.0, 0.0, 0.3);
      level.sendParticles(ParticleTypes.TRIAL_OMEN, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.05);
   }

   private static void playVfxShock(ServerLevel level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1.0F, 2.0F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0F, 0.5F);
      level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.25, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.TRIAL_OMEN, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.05);
   }

   private static void playVfxResurrection(ServerLevel level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.0F, 2.0F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0F, 0.5F);
      level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.25, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.SMALL_FLAME, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, 20, 0.0, 0.0, 0.0, 0.5);
      level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, 40, 0.0, 0.0, 0.0, 0.3);
   }

   private static void playVfxBeginning(ServerLevel level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ALLAY_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.8F);
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0F, 0.5F);
      level.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.25, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
      level.sendParticles(ParticleTypes.SCULK_SOUL, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.1);
      level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 40, 0.15, 0.15, 0.15, 0.05);
   }

   private enum PermaPotionType {
      NONE,
      WITHER_RAGE,
      REINCARNATION,
      SHOCK,
      RESURRECTION,
      BEGINNING;
   }
}
