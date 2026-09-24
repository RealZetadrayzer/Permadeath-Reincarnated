package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.attachments.NetherMobRainAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.NetherMobRainAttachmentHelper;

@EventBusSubscriber
public class PlayerNetherMobRainHandler {
   private static EntityType<?>[] getMobPool() {
      return new EntityType[]{
         EntityType.ALLAY,
         EntityType.AXOLOTL,
         EntityType.BAT,
         EntityType.BEE,
         EntityType.CAMEL,
         EntityType.CAT,
         EntityType.CHICKEN,
         EntityType.COD,
         EntityType.COW,
         EntityType.DOLPHIN,
         EntityType.DONKEY,
         EntityType.FOX,
         EntityType.FROG,
         EntityType.GOAT,
         EntityType.GLOW_SQUID,
         EntityType.HORSE,
         EntityType.PANDA,
         EntityType.PARROT,
         EntityType.PIG,
         EntityType.POLAR_BEAR,
         EntityType.RABBIT,
         EntityType.SHEEP,
         EntityType.SQUID,
         EntityType.TURTLE,
         EntityType.TRADER_LLAMA,
         EntityType.WANDERING_TRADER,
         EntityType.WOLF,
         EntityType.STRIDER,
         EntityType.BLAZE,
         EntityType.BOGGED,
         EntityType.BREEZE,
         EntityType.CREEPER,
         EntityType.DROWNED,
         EntityType.ENDERMITE,
         EntityType.EVOKER,
         EntityType.GHAST,
         EntityType.GUARDIAN,
         EntityType.HOGLIN,
         EntityType.HUSK,
         EntityType.MAGMA_CUBE,
         EntityType.PIGLIN,
         EntityType.PIGLIN_BRUTE,
         EntityType.PHANTOM,
         EntityType.PILLAGER,
         EntityType.RAVAGER,
         EntityType.SHULKER,
         EntityType.SILVERFISH,
         EntityType.SKELETON,
         EntityType.SLIME,
         EntityType.SPIDER,
         EntityType.STRAY,
         EntityType.VEX,
         EntityType.VINDICATOR,
         EntityType.WITCH,
         EntityType.WITHER_SKELETON,
         EntityType.ZOGLIN,
         EntityType.ZOMBIE,
         EntityType.ZOMBIE_VILLAGER,
         EntityType.ELDER_GUARDIAN,
         EntityType.WARDEN,
         EntityType.IRON_GOLEM
      };
   }

   @SubscribeEvent
   public static void netherMobRain(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            if (!level.isClientSide) {
               if (!player.isSpectator() && !player.isCreative()) {
                  if (player.tickCount % 20 == 0) {
                     if (DayGlobalCount.CURRENT_DAY >= 50) {
                        if (player.level().dimension() == Level.NETHER) {
                           NetherMobRainAttachment cooldown = NetherMobRainAttachmentHelper.get(player);
                           if (cooldown.isOnCooldown()) {
                              cooldown.tick();
                           }

                           RandomSource random = level.getRandom();
                           int chance = PermadeathConfig.CUSTOM_CHANGES.get() && DayGlobalCount.CURRENT_DAY >= 55
                              ? 1 + random.nextInt(5000)
                              : 1 + random.nextInt(10000);
                           if (chance <= 1 && !cooldown.isOnCooldown()) {
                              long seed = random.nextLong();
                              player.connection
                                 .send(
                                    new ClientboundSoundPacket(
                                       BuiltInRegistries.SOUND_EVENT.wrapAsHolder((SoundEvent)SoundEvents.TRIDENT_THUNDER.value()),
                                       SoundSource.HOSTILE,
                                       player.getX(),
                                       player.getY(),
                                       player.getZ(),
                                       100.0F,
                                       1.0F,
                                       seed
                                    )
                                 );
                              level.getServer()
                                 .getPlayerList()
                                 .broadcastSystemMessage(
                                    Component.literal("¡Comienza la lluvia de mobs para ")
                                       .withStyle(ChatFormatting.RED)
                                       .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.RED))
                                       .append(Component.literal("!").withStyle(ChatFormatting.RED)),
                                    false
                                 );
                              level.sendParticles(ParticleTypes.OMINOUS_SPAWNING, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.05);
                              level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.15, 0.15, 0.15, 0.05);
                              int mobCount = 20 + random.nextInt(21);
                              int durationSeconds = 20;
                              int intervalSeconds = Math.max(1, durationSeconds / mobCount);
                              cooldown.startCooldown(durationSeconds);

                              for (int i = 0; i < mobCount; i++) {
                                 int delaySeconds = i * intervalSeconds;
                                 ScheduleInTicks.schedule(() -> {
                                    if (player.level().dimension() == Level.NETHER) {
                                       spawnRandomMob(player);
                                    }
                                 }, delaySeconds * 20);
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

   public static void spawnRandomMob(ServerPlayer player) {
      ServerLevel level = player.serverLevel();
      RandomSource random = level.random;
      EntityType<?>[] pool = getMobPool();
      EntityType<?> type = pool[random.nextInt(pool.length)];
      int radius = 12;
      int x = player.getBlockX() + random.nextInt(radius * 2 + 1) - radius;
      int z = player.getBlockZ() + random.nextInt(radius * 2 + 1) - radius;
      BlockPos basePos = new BlockPos(x, player.getBlockY(), z);
      if (basePos.getY() >= 110) {
         spawn(type, level, new BlockPos(x, 128, z));
      } else {
         int airCount = 0;
         MutableBlockPos cursor = basePos.mutable();

         while (cursor.getY() < 128 && level.getBlockState(cursor).isAir()) {
            airCount++;
            cursor.move(0, 1, 0);
         }

         if (airCount >= 15) {
            cursor.set(basePos);

            while (cursor.getY() < 128) {
               if (!level.getBlockState(cursor).isAir() && level.getBlockState(cursor.below()).isAir()) {
                  spawn(type, level, cursor.below());
                  return;
               }

               cursor.move(0, 1, 0);
            }

            BlockPos roof = new BlockPos(x, 127, z);
            if (level.getBlockState(roof).is(Blocks.BEDROCK)) {
               spawn(type, level, roof.above());
            } else {
               spawn(type, level, new BlockPos(x, 128, z));
            }
         }
      }
   }

   private static void spawn(EntityType<?> type, ServerLevel level, BlockPos pos) {
      if (type.spawn(level, pos, MobSpawnType.MOB_SUMMONED) instanceof Mob mob) {
         mob.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0, false, false, false));
         mob.setDeltaMovement(new Vec3(0.0, -2.0, 0.0));
      }
   }
}
