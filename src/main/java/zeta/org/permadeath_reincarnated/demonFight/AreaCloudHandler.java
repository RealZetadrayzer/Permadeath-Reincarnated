package zeta.org.permadeath_reincarnated.demonFight;

import com.mojang.brigadier.ParseResults;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@EventBusSubscriber
public class AreaCloudHandler {
   private static final Random RANDOM = new Random();
   private static final Set<Block> BLACKLISTED_BLOCKS_BEDROCK = Set.of(
      Blocks.AIR,
      Blocks.BEDROCK,
      Blocks.REINFORCED_DEEPSLATE,
      Blocks.END_PORTAL,
      Blocks.END_GATEWAY,
      Blocks.NETHER_PORTAL,
      Blocks.BARRIER,
      Blocks.STRUCTURE_VOID,
      Blocks.COMMAND_BLOCK,
      Blocks.CHAIN_COMMAND_BLOCK,
      Blocks.REPEATING_COMMAND_BLOCK,
      Blocks.JIGSAW,
      Blocks.STRUCTURE_BLOCK,
      Blocks.SPAWNER,
      Blocks.MOVING_PISTON,
      Blocks.LIGHT,
      Blocks.TRIAL_SPAWNER,
      Blocks.FIRE,
      Blocks.LAVA
   );
   private static final Set<Block> BLACKLISTED_BLOCKS_LAVA = Set.of(
      Blocks.BEDROCK,
      Blocks.REINFORCED_DEEPSLATE,
      Blocks.END_PORTAL,
      Blocks.END_GATEWAY,
      Blocks.NETHER_PORTAL,
      Blocks.BARRIER,
      Blocks.STRUCTURE_VOID,
      Blocks.COMMAND_BLOCK,
      Blocks.CHAIN_COMMAND_BLOCK,
      Blocks.REPEATING_COMMAND_BLOCK,
      Blocks.JIGSAW,
      Blocks.STRUCTURE_BLOCK,
      Blocks.SPAWNER,
      Blocks.MOVING_PISTON,
      Blocks.LIGHT,
      Blocks.TRIAL_SPAWNER,
      Blocks.FIRE,
      Blocks.LAVA
   );
   private static final BlockPos[] OFFSETS_TNT = new BlockPos[]{
      new BlockPos(8, 0, 0),
      new BlockPos(7, 0, 4),
      new BlockPos(3, 0, 7),
      new BlockPos(-3, 0, 7),
      new BlockPos(-7, 0, 4),
      new BlockPos(-8, 0, 0),
      new BlockPos(-7, 0, -4),
      new BlockPos(-3, 0, -7),
      new BlockPos(3, 0, -7),
      new BlockPos(7, 0, -4)
   };
   private static final BlockPos[] OFFSETS = new BlockPos[]{new BlockPos(5, 0, 0), new BlockPos(-5, 0, 0), new BlockPos(0, 0, 5), new BlockPos(0, 0, -5)};
   private static final BlockPos[] OFFSETS_ENRAGED = new BlockPos[]{
      new BlockPos(7, 0, 7), new BlockPos(7, 0, -7), new BlockPos(-7, 0, 7), new BlockPos(-7, 0, -7)
   };
   private static final EntityType<?>[] MOB_POOL = new EntityType[]{
      EntityType.SPIDER, EntityType.SKELETON, EntityType.SILVERFISH, EntityType.ENDERMITE, EntityType.CREEPER
   };

   public static boolean isBlockBlacklistedLava(BlockState state) {
      return BLACKLISTED_BLOCKS_LAVA.contains(state.getBlock());
   }

   public static boolean isBlockBlacklistedBedrock(BlockState state) {
      return BLACKLISTED_BLOCKS_BEDROCK.contains(state.getBlock());
   }

   @SubscribeEvent
   public static void onCloudSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof AreaEffectCloud areaEffectCloud) {
         if (!areaEffectCloud.level().isClientSide) {
            ServerLevel level = (ServerLevel)areaEffectCloud.level();
            double SOUND_DISTANCE = 28.0;
            double SOUND_SQR = 784.0;
            Vec3 cloudPos = areaEffectCloud.position();
            if (level.dimension().equals(Level.END)) {
               PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.getIfEnd(level);
               if (!areaEffectCloud.getTags().contains("demonFight") && handler != null && handler.isFightOn()) {
                  if (areaEffectCloud.getTags().contains("fromShulkerPlaga")) {
                     return;
                  }

                  if (areaEffectCloud.getDuration() != 200) {
                     areaEffectCloud.setDuration(0);
                     areaEffectCloud.setRadius(0.0F);
                     areaEffectCloud.setRadiusOnUse(0.0F);
                     ScheduleInTicks.schedule(() -> areaEffectCloud.remove(RemovalReason.DISCARDED), 1);
                  }
               }

               if (areaEffectCloud.getTags().contains("meteorite_rain_cloud")) {
                  String[] summonMobArray = new String[]{"minecraft:fireball", "minecraft:small_fireball", "minecraft:small_fireball"};
                  double[] expArray = new double[]{2.0, 2.5, 3.0, 3.5, 4.5, 5.0};
                  Supplier<String> getRandomMob = () -> summonMobArray[RANDOM.nextInt(summonMobArray.length)];
                  Supplier<Double> getRandomExp = () -> expArray[RANDOM.nextInt(expArray.length)];
                  ScheduleInTicks.schedule(() -> {
                     playLocalReferenceSound(level, SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.VOICE, 0.65F, 1.5F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.BAT_TAKEOFF, SoundSource.VOICE, 0.5F, 0.25F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.VOICE, 0.15F, 0.25F, cloudPos, 784.0);
                  }, 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof LivingEntity living && !(inBB instanceof Player player && (player.isSpectator() || !player.isAlive()))) {
                                 double dx = living.getX() - cx;
                                 double dz = living.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    if (!living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                                       living.igniteForSeconds(30.0F);
                                       living.hurt(living.level().damageSources().onFire(), 10.0F);
                                    }

                                    living.hurtMarked = true;
                                 }
                              }
                           }
                        }
                     }, delay);
                  }

                  for (int i = 1; i <= 60; i++) {
                     int delay = i * 10;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           AABB aabb = areaEffectCloud.getBoundingBox();
                           int x = (int)(aabb.minX + RANDOM.nextDouble() * (aabb.maxX - aabb.minX));
                           int z = (int)(aabb.minZ + RANDOM.nextDouble() * (aabb.maxZ - aabb.minZ));
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(x, y, z);

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           double mobY = pos.getY() + 25;
                           String mob = getRandomMob.get();
                           double exp = getRandomExp.get();
                           ScheduleInTicks.schedule(() -> {
                              playLocalSound(level, SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.VOICE, 0.5F, 1.2F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.GHAST_SHOOT, SoundSource.VOICE, 0.5F, 1.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.DRIPSTONE_BLOCK_FALL, SoundSource.VOICE, 0.35F, 1.0F, cloudPos, 784.0);
                           }, 1);
                           if ("minecraft:fireball".equals(mob)) {
                              assert handler != null;
                              EnderDragon dragon = handler.getDragon(level);
                              if (dragon == null || !dragon.isAlive()) {
                                 return;
                              }

                              Vec3 motion = new Vec3(0.0, -0.1, 0.0);
                              LargeFireball fireball = new LargeFireball(level, dragon, motion, (int)exp);
                              fireball.setPos(x + 0.5, mobY, z + 0.5);
                              level.addFreshEntity(fireball);
                           } else if ("minecraft:small_fireball".equals(mob)) {
                              Vec3 motion = new Vec3(0.0, -0.1, 0.0);
                              SmallFireball fireball = new SmallFireball(level, x + 0.5, mobY, z + 0.5, motion);
                              fireball.setPos(x + 0.5, mobY, z + 0.5);
                              level.addFreshEntity(fireball);
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("enraged_meteorite_rain_cloud")) {
                  String[] summonMobArray = new String[]{"minecraft:fireball", "minecraft:fireball", "minecraft:small_fireball"};
                  double[] expArray = new double[]{3.0, 3.5, 4.5, 5.0, 5.5, 6.0};
                  Supplier<String> getRandomMob = () -> summonMobArray[RANDOM.nextInt(summonMobArray.length)];
                  Supplier<Double> getRandomExp = () -> expArray[RANDOM.nextInt(expArray.length)];
                  ScheduleInTicks.schedule(() -> {
                     playLocalReferenceSound(level, SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.VOICE, 0.65F, 1.5F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.BAT_TAKEOFF, SoundSource.VOICE, 0.5F, 0.25F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.VOICE, 0.15F, 0.25F, cloudPos, 784.0);
                  }, 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof LivingEntity living && !(inBB instanceof Player player && (player.isSpectator() || !player.isAlive()))) {
                                 double dx = living.getX() - cx;
                                 double dz = living.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    if (!living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                                       living.igniteForSeconds(60.0F);
                                       living.hurt(living.level().damageSources().onFire(), 15.0F);
                                    }

                                    living.hurtMarked = true;
                                 }
                              }
                           }
                        }
                     }, delay);
                  }

                  for (int i = 1; i <= 120; i++) {
                     int delay = i * 5;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           AABB aabb = areaEffectCloud.getBoundingBox();
                           int x = (int)(aabb.minX + RANDOM.nextDouble() * (aabb.maxX - aabb.minX));
                           int z = (int)(aabb.minZ + RANDOM.nextDouble() * (aabb.maxZ - aabb.minZ));
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(x, y, z);

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           double mobY = pos.getY() + 25;
                           String mob = getRandomMob.get();
                           double exp = getRandomExp.get();
                           ScheduleInTicks.schedule(() -> {
                              playLocalSound(level, SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.VOICE, 0.5F, 1.2F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.GHAST_SHOOT, SoundSource.VOICE, 0.5F, 1.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.DRIPSTONE_BLOCK_FALL, SoundSource.VOICE, 0.35F, 1.0F, cloudPos, 784.0);
                           }, 1);
                           if ("minecraft:fireball".equals(mob)) {
                              assert handler != null;
                              EnderDragon dragon = handler.getDragon(level);
                              if (dragon == null || !dragon.isAlive()) {
                                 return;
                              }

                              Vec3 motion = new Vec3(0.0, -0.1, 0.0);
                              LargeFireball fireball = new LargeFireball(level, dragon, motion, (int)exp);
                              fireball.setPos(x + 0.5, mobY, z + 0.5);
                              level.addFreshEntity(fireball);
                           } else if ("minecraft:small_fireball".equals(mob)) {
                              Vec3 motion = new Vec3(0.0, -0.1, 0.0);
                              SmallFireball fireball = new SmallFireball(level, x + 0.5, mobY, z + 0.5, motion);
                              fireball.setPos(x + 0.5, mobY, z + 0.5);
                              level.addFreshEntity(fireball);
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("evocation_cloud")) {
                  areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.INFESTED, 300, 0, false, true));
                  String[] summonMobArray = new String[]{
                     "minecraft:skeleton", "minecraft:spider", "minecraft:silverfish", "minecraft:endermite", "minecraft:enderman", "minecraft:creeper"
                  };
                  Supplier<String> getRandomMob = () -> summonMobArray[RANDOM.nextInt(summonMobArray.length)];
                  ScheduleInTicks.schedule(() -> {
                     playLocalSound(level, SoundEvents.WARDEN_DIG, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                  }, 1);

                  for (int i = 1; i <= 15; i++) {
                     int delay = i * 40;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           boolean flagx = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                           int chancex = 1 + RANDOM.nextInt(100);
                           AABB aabb = areaEffectCloud.getBoundingBox();
                           int x = (int)(aabb.minX + RANDOM.nextDouble() * (aabb.maxX - aabb.minX));
                           int z = (int)(aabb.minZ + RANDOM.nextDouble() * (aabb.maxZ - aabb.minZ));
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(x, y, z);

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           double mobY = pos.getY() + 1;
                           String mobId = getRandomMob.get();
                           if (mobId.equals("minecraft:skeleton") && chancex <= 15 && flagx) {
                              mobId = RANDOM.nextBoolean() ? "minecraft:stray" : "minecraft:bogged";
                           }

                           ScheduleInTicks.schedule(() -> {
                              playLocalSound(level, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.VOICE, 0.25F, 2.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.VOICE, 0.25F, 2.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.PLAYER_BREATH, SoundSource.VOICE, 0.25F, 0.25F, cloudPos, 784.0);
                           }, 1);
                           EntityType.byString(mobId).ifPresent(type -> {
                              Entity mob = type.create(level);
                              if (mob != null) {
                                 mob.moveTo(x + 0.5, mobY, z + 0.5, RANDOM.nextFloat() * 360.0F, 0.0F);
                                 level.addFreshEntity(mob);
                              }
                           });
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("enraged_evocation_cloud")) {
                  areaEffectCloud.addEffect(new MobEffectInstance(MobEffects.INFESTED, 300, 0, false, true));
                  String[] summonMobArray = new String[]{"minecraft:skeleton", "minecraft:spider", "minecraft:creeper"};
                  Supplier<String> getRandomMob = () -> summonMobArray[RANDOM.nextInt(summonMobArray.length)];
                  ScheduleInTicks.schedule(() -> {
                     playLocalSound(level, SoundEvents.WARDEN_DIG, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                  }, 1);

                  for (int i = 1; i <= 15; i++) {
                     int delay = i * 40;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           boolean flagx = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                           int chancex = 1 + RANDOM.nextInt(100);
                           AABB aabb = areaEffectCloud.getBoundingBox();
                           int x = (int)(aabb.minX + RANDOM.nextDouble() * (aabb.maxX - aabb.minX));
                           int z = (int)(aabb.minZ + RANDOM.nextDouble() * (aabb.maxZ - aabb.minZ));
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(x, y, z);

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           double mobY = pos.getY() + 1;
                           String mobId = getRandomMob.get();
                           if (mobId.equals("minecraft:skeleton") && chancex <= 20 && flagx) {
                              mobId = RANDOM.nextBoolean() ? "minecraft:stray" : "minecraft:bogged";
                           }

                           ScheduleInTicks.schedule(() -> {
                              playLocalSound(level, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.VOICE, 0.25F, 2.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.VOICE, 0.25F, 2.0F, cloudPos, 784.0);
                              playLocalSound(level, SoundEvents.PLAYER_BREATH, SoundSource.VOICE, 0.25F, 0.25F, cloudPos, 784.0);
                           }, 1);
                           EntityType.byString(mobId).ifPresent(type -> {
                              Entity mob = type.create(level);
                              if (mob != null) {
                                 mob.moveTo(x + 0.5, mobY, z + 0.5, RANDOM.nextFloat() * 360.0F, 0.0F);
                                 level.addFreshEntity(mob);
                              }
                           });
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("black_cloud")) {
                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.VOICE, 0.7F, 1.4F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof Player player && !player.isSpectator() && player.isAlive()) {
                                 double dx = player.getX() - cx;
                                 double dz = player.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 2, false, true));
                                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2, false, true));
                                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0, false, true));
                                 }
                              }
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("rage_cloud")) {
                  boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                  ScheduleInTicks.schedule(() -> {
                     playLocalSound(level, SoundEvents.BELL_RESONATE, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.VOICE, 0.5F, 0.85F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.VOICE, 2.0F, 0.75F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.VOICE, 0.25F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.BREWING_STAND_BREW, SoundSource.VOICE, 2.0F, 2.0F, cloudPos, 784.0);
                  }, 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(
                        () -> {
                           if (areaEffectCloud.isAlive()) {
                              double radius = areaEffectCloud.getRadius();
                              double cx = areaEffectCloud.getX();
                              double cz = areaEffectCloud.getZ();

                              for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                                 if (inBB instanceof LivingEntity living && !living.isSpectator() && living.isAlive()) {
                                    double dx = living.getX() - cx;
                                    double dz = living.getZ() - cz;
                                    double distance = Math.sqrt(dx * dx + dz * dz);
                                    if (!(distance > radius)) {
                                       if (!(living instanceof Player)) {
                                          if (living.isAffectedByPotions()) {
                                             if (flag) {
                                                living.addEffect(
                                                   new MobEffectInstance(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate(), 200, 0, false, true)
                                                );
                                             }

                                             living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2, false, true));
                                             living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, true));
                                             living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 2, false, true));
                                             living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, true));
                                             living.heal(2.0F);
                                          }
                                       } else {
                                          living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1, false, true));
                                          living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0, false, true));
                                       }
                                    }
                                 }
                              }
                           }
                        },
                        delay
                     );
                  }
               }

               if (areaEffectCloud.getTags().contains("gray_cloud")) {
                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.EVOKER_CAST_SPELL, SoundSource.VOICE, 0.9F, 0.6F, cloudPos, 784.0), 1);
                  DeferredHolder<MobEffect, MobEffect>[] effectsToRemove = new DeferredHolder[]{
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:haste"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:jump_boost"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:speed"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:strength"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:absorption"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:fire_resistance"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:regeneration"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:resistance"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:invisibility"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:slow_falling"))),
                     DeferredHolder.create(Registries.MOB_EFFECT, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:water_breathing")))
                  };

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof Player player && !player.isSpectator() && player.isAlive()) {
                                 double dx = player.getX() - cx;
                                 double dz = player.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    for (DeferredHolder<MobEffect, MobEffect> deferred : effectsToRemove) {
                                       player.removeEffect(deferred);
                                    }
                                 }
                              }
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("green_cloud")) {
                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.TOTEM_USE, SoundSource.VOICE, 1.0F, 0.85F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof EnderMan enderman && enderman.isAlive()) {
                                 double dx = enderman.getX() - cx;
                                 double dz = enderman.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    enderman.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 3, false, true));
                                    enderman.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
                                 }
                              }
                           }
                        }
                     }, delay);
                  }

                  ScheduleInTicks.schedule(
                     () -> {
                        if (areaEffectCloud.isAlive()) {
                           String uuid = areaEffectCloud.getUUID().toString();
                           String clearCommand = "execute in minecraft:the_end as "
                              + uuid
                              + " at @s run execute as @e[type=minecraft:enderman,distance=..800] run effect clear @s minecraft:resistance";
                           String removeTotemCommand = "execute in minecraft:the_end as "
                              + uuid
                              + " at @s run execute as @e[type=minecraft:enderman,distance=..800] run item replace entity @s weapon.offhand with minecraft:air 1";
                           CommandSourceStack source = level.getServer().createCommandSourceStack().withPermission(2);
                           ParseResults<CommandSourceStack> parseClear = level.getServer().getCommands().getDispatcher().parse(clearCommand, source);
                           level.getServer().getCommands().performCommand(parseClear, clearCommand);
                           ParseResults<CommandSourceStack> parseRemove = level.getServer().getCommands().getDispatcher().parse(removeTotemCommand, source);
                           level.getServer().getCommands().performCommand(parseRemove, removeTotemCommand);
                        }
                     },
                     600
                  );
               }

               if (areaEffectCloud.getTags().contains("purple_cloud")) {
                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.VOICE, 1.0F, 0.5F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof Player player && !player.isSpectator() && player.isAlive()) {
                                 double dx = player.getX() - cx;
                                 double dz = player.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 2400, 1, false, true));
                                 }
                              }
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("white_cloud")) {
                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.VOICE, 0.8F, 1.4F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof Player player && !player.isSpectator() && player.isAlive()) {
                                 double dx = player.getX() - cx;
                                 double dz = player.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 2, false, true));
                                 }
                              }
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("thunder_cloud_variant_1")) {
                  ScheduleInTicks.schedule(() -> playLocalHolderSound(level, SoundEvents.TRIDENT_THUNDER, SoundSource.VOICE, 1.0F, 1.0F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 30; i++) {
                     int delay = i * 20;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double x = areaEffectCloud.getX();
                           double z = areaEffectCloud.getZ();
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(Mth.floor(x), y, Mth.floor(z));

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                           if (lightning != null) {
                              lightning.moveTo(Vec3.atBottomCenterOf(pos.above()));
                              lightning.setDamage(15.0F);
                              level.addFreshEntity(lightning);
                           }
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("thunder_cloud_variant_2")) {
                  ScheduleInTicks.schedule(() -> playLocalHolderSound(level, SoundEvents.TRIDENT_THUNDER, SoundSource.VOICE, 1.0F, 0.85F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 30; i++) {
                     int delay = i * 20;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double offsetX = RANDOM.nextInt(9) - 4;
                           double offsetZ = RANDOM.nextInt(9) - 4;
                           double x = areaEffectCloud.getX() + offsetX;
                           double z = areaEffectCloud.getZ() + offsetZ;
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(Mth.floor(x), y, Mth.floor(z));

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                           if (lightning != null) {
                              lightning.moveTo(Vec3.atBottomCenterOf(pos.above()));
                              lightning.setDamage(25.0F);
                              level.addFreshEntity(lightning);
                           }

                           CrystalRespawnData data = CrystalRespawnData.get(level);

                           for (CrystalRespawnData.RespawnEntry entry : data.respawns.values()) {
                              entry.ticksLeft = Math.max(1, entry.ticksLeft - (RANDOM.nextInt(4) + 1));
                           }

                           data.setDirty();
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("thunder_cloud_variant_3")) {
                  ScheduleInTicks.schedule(() -> playLocalHolderSound(level, SoundEvents.TRIDENT_THUNDER, SoundSource.VOICE, 1.0F, 0.7F, cloudPos, 784.0), 1);

                  for (int i = 1; i <= 30; i++) {
                     int delay = i * 20;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double offsetX = RANDOM.nextInt(17) - 8;
                           double offsetZ = RANDOM.nextInt(17) - 8;
                           double x = areaEffectCloud.getX() + offsetX;
                           double z = areaEffectCloud.getZ() + offsetZ;
                           int y = level.getMaxBuildHeight() - 1;
                           BlockPos pos = new BlockPos(Mth.floor(x), y, Mth.floor(z));

                           while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                              pos = pos.below();
                           }

                           LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                           if (lightning != null) {
                              lightning.moveTo(Vec3.atBottomCenterOf(pos.above()));
                              lightning.setDamage(35.0F);
                              level.addFreshEntity(lightning);
                           }

                           CrystalRespawnData data = CrystalRespawnData.get(level);

                           for (CrystalRespawnData.RespawnEntry entry : data.respawns.values()) {
                              entry.ticksLeft = Math.max(1, entry.ticksLeft - (RANDOM.nextInt(8) + 2));
                           }

                           data.setDirty();
                        }
                     }, delay);
                  }
               }

               if (areaEffectCloud.getTags().contains("thunder_cloud_variant_ultimate")) {
                  ScheduleInTicks.schedule(() -> {
                     playGlobalHolderSound(level, SoundEvents.TRIDENT_THUNDER, SoundSource.VOICE, 1.0F, 0.85F);
                     playGlobalSound(level, SoundEvents.WITHER_SPAWN, SoundSource.VOICE, 1.0F, 0.9F);
                     playGlobalSound(level, SoundEvents.BEACON_ACTIVATE, SoundSource.VOICE, 1.0F, 0.85F);
                  }, 1);

                  for (int i = 1; i <= 120; i++) {
                     int delay = i * 10;
                     ScheduleInTicks.schedule(
                        () -> {
                           if (areaEffectCloud.isAlive()) {
                              double offsetX = RANDOM.nextInt(101) - 50;
                              double offsetZ = RANDOM.nextInt(101) - 50;
                              double x = areaEffectCloud.getX() + offsetX;
                              double z = areaEffectCloud.getZ() + offsetZ;
                              int y = level.getMaxBuildHeight() - 1;
                              BlockPos pos = new BlockPos(Mth.floor(x), y, Mth.floor(z));

                              while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
                                 pos = pos.below();
                              }

                              LightningBolt lightning = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
                              if (lightning != null) {
                                 lightning.moveTo(Vec3.atBottomCenterOf(pos.above()));
                                 lightning.setDamage(30.0F);
                                 level.addFreshEntity(lightning);
                              }

                              EnderDragon dragon = (EnderDragon)level.getEntitiesOfClass(EnderDragon.class, areaEffectCloud.getBoundingBox().inflate(512.0))
                                 .stream()
                                 .findFirst()
                                 .orElse(null);
                              if (dragon != null && dragon.isAlive()) {
                                 areaEffectCloud.teleportTo(dragon.getX(), dragon.getY(), dragon.getZ());
                              }

                              CrystalRespawnData data = CrystalRespawnData.get(level);

                              for (CrystalRespawnData.RespawnEntry entry : data.respawns.values()) {
                                 entry.ticksLeft = Math.max(1, entry.ticksLeft - (RANDOM.nextInt(16) + 3));
                              }

                              data.setDirty();
                           }
                        },
                        delay
                     );
                  }
               }

               if (areaEffectCloud.getTags().contains("night_vision_cloud_trigger")) {
                  ScheduleInTicks.schedule(() -> playGlobalSound(level, SoundEvents.ALLAY_DEATH, SoundSource.VOICE, 0.7F, 1.3F), 1);

                  for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
                     if (player.isAlive() && player.level().dimension().equals(Level.END)) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0, false, true));
                        ScheduleInTicks.schedule(() -> {
                           if (!player.isSpectator() && !player.isCreative()) {
                              AreaEffectCloud cloud = (AreaEffectCloud)EntityType.AREA_EFFECT_CLOUD.create(level);
                              if (cloud != null) {
                                 cloud.setRadius(2.0F);
                                 cloud.setDuration(600);
                                 cloud.setParticle(ParticleTypes.DAMAGE_INDICATOR);
                                 cloud.addTag("demonFight");
                                 cloud.addTag("night_vision_cloud");
                                 cloud.setPos(player.getX(), player.getY(), player.getZ());
                                 level.addFreshEntity(cloud);
                              }
                           }
                        }, 100);
                     }
                  }
               }

               if (areaEffectCloud.getTags().contains("night_vision_cloud")) {
                  for (int i = 1; i <= 40; i++) {
                     int delay = i * 15;
                     ScheduleInTicks.schedule(() -> {
                        if (areaEffectCloud.isAlive()) {
                           double radius = areaEffectCloud.getRadius();
                           double cx = areaEffectCloud.getX();
                           double cz = areaEffectCloud.getZ();

                           for (Entity inBB : level.getEntities(areaEffectCloud, areaEffectCloud.getBoundingBox())) {
                              if (inBB instanceof Player player && !player.isSpectator() && player.isAlive()) {
                                 double dx = player.getX() - cx;
                                 double dz = player.getZ() - cz;
                                 double distance = Math.sqrt(dx * dx + dz * dz);
                                 if (distance <= radius) {
                                    player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1, false, true));
                                 }
                              }
                           }
                        }
                     }, delay);
                  }
               }

               if (entity.getTags().contains("block_lava_replace_trigger")) {
                  ScheduleInTicks.schedule(() -> {
                     playLocalSound(level, SoundEvents.NETHERRACK_BREAK, SoundSource.VOICE, 0.5F, 0.5F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.LAVA_EXTINGUISH, SoundSource.VOICE, 0.5F, 2.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.BREWING_STAND_BREW, SoundSource.VOICE, 0.35F, 0.65F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.VOICE, 0.65F, 1.0F, cloudPos, 784.0);
                  }, 1);
                  int offsetX = RANDOM.nextInt(3) + 1;
                  int offsetZ = RANDOM.nextInt(3) + 1;
                  int MoffsetX = -RANDOM.nextInt(3) - 1;
                  int MoffsetZ = -RANDOM.nextInt(3) - 1;
                  BlockPos origin = entity.blockPosition();
                  BlockPos min = origin.offset(MoffsetX, -1, MoffsetZ);
                  BlockPos max = origin.offset(offsetX, 0, offsetZ);
                  ScheduleInTicks.schedule(() -> BlockPos.betweenClosed(min, max).forEach(pos -> {
                     BlockState state = level.getBlockState(pos);
                     if (!state.isAir() && !isBlockBlacklistedLava(state)) {
                        level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                     }
                  }), 2);
               }

               if (entity.getTags().contains("block_bedrock_replace_trigger")) {
                  ScheduleInTicks.schedule(() -> {
                     playLocalSound(level, SoundEvents.ANCIENT_DEBRIS_BREAK, SoundSource.VOICE, 5.0F, 0.5F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.VOICE, 5.0F, 1.0F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.DECORATED_POT_BREAK, SoundSource.VOICE, 5.0F, 0.5F, cloudPos, 784.0);
                     playLocalSound(level, SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.VOICE, 0.65F, 2.0F, cloudPos, 784.0);
                  }, 1);
                  int offsetX = RANDOM.nextInt(3) + 1;
                  int offsetZ = RANDOM.nextInt(3) + 1;
                  int MoffsetX = -RANDOM.nextInt(3) - 1;
                  int MoffsetZ = -RANDOM.nextInt(3) - 1;
                  BlockPos origin = entity.blockPosition();
                  BlockPos min = origin.offset(MoffsetX, -1, MoffsetZ);
                  BlockPos max = origin.offset(offsetX, 0, offsetZ);
                  ScheduleInTicks.schedule(() -> BlockPos.betweenClosed(min, max).forEach(pos -> {
                     BlockState state = level.getBlockState(pos);
                     if (state.isAir()) {
                        level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                     } else if (!isBlockBlacklistedBedrock(state)) {
                        level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                     }
                  }), 2);
               }

               if (entity.getTags().contains("spawn_tnt_trigger")) {
                  BlockPos origin = areaEffectCloud.blockPosition();
                  ScheduleInTicks.schedule(() -> playGlobalSound(level, SoundEvents.WITHER_SPAWN, SoundSource.VOICE, 0.7F, 1.5F), 1);
                  spawnTNT(level, origin, OFFSETS_TNT);
               }

               if (entity.getTags().contains("spawn_mob_trigger")) {
                  boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                  int chance = 1 + RANDOM.nextInt(100);
                  BlockPos origin = areaEffectCloud.blockPosition();
                  EntityType<?> chosenMob = MOB_POOL[RANDOM.nextInt(MOB_POOL.length)];
                  if (chosenMob == EntityType.SKELETON && chance <= 15 && flag) {
                     chosenMob = RANDOM.nextBoolean() ? EntityType.STRAY : EntityType.BOGGED;
                  }

                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.BEACON_ACTIVATE, SoundSource.VOICE, 4.0F, 2.0F, cloudPos, 784.0), 1);
                  spawnEntities(level, origin, chosenMob, OFFSETS);
               }

               if (entity.getTags().contains("enraged_spawn_mob_trigger")) {
                  boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                  int chance = 1 + RANDOM.nextInt(100);
                  BlockPos origin = areaEffectCloud.blockPosition();
                  EntityType<?>[] ENRAGED_MOB_POOL = new EntityType[]{EntityType.SPIDER, EntityType.SKELETON, EntityType.CREEPER};
                  EntityType<?> chosenMob = ENRAGED_MOB_POOL[RANDOM.nextInt(ENRAGED_MOB_POOL.length)];
                  if (chosenMob == EntityType.SKELETON && chance <= 20 && flag) {
                     chosenMob = RANDOM.nextBoolean() ? EntityType.STRAY : EntityType.BOGGED;
                  }

                  ScheduleInTicks.schedule(() -> playLocalSound(level, SoundEvents.BEACON_ACTIVATE, SoundSource.VOICE, 4.0F, 2.0F, cloudPos, 784.0), 1);
                  spawnEntities(level, origin, chosenMob, OFFSETS);
                  spawnEntities(level, origin, chosenMob, OFFSETS_ENRAGED);
               }
            }
         }
      }
   }

   private static void playGlobalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
         }
      });
   }

   private static void playGlobalReferenceSound(ServerLevel level, Reference<SoundEvent> sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(sound, source, px, py, pz, volume, pitch, seed));
         }
      });
   }

   private static void playGlobalHolderSound(ServerLevel level, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(sound, source, px, py, pz, volume, pitch, seed));
         }
      });
   }

   private static void playLocalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch, Vec3 cloudPos, double soundDistance) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (!(player.position().distanceToSqr(cloudPos) > soundDistance)) {
            if (player.level() == level) {
               double px = player.getX();
               double py = player.getY();
               double pz = player.getZ();
               player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
            }
         }
      });
   }

   private static void playLocalReferenceSound(
      ServerLevel level, Reference<SoundEvent> sound, SoundSource source, float volume, float pitch, Vec3 cloudPos, double soundDistance
   ) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (!(player.position().distanceToSqr(cloudPos) > soundDistance)) {
            if (player.level() == level) {
               double px = player.getX();
               double py = player.getY();
               double pz = player.getZ();
               player.connection.send(new ClientboundSoundPacket(sound, source, px, py, pz, volume, pitch, seed));
            }
         }
      });
   }

   private static void playLocalHolderSound(
      ServerLevel level, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, Vec3 cloudPos, double soundDistance
   ) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (!(player.position().distanceToSqr(cloudPos) > soundDistance)) {
            if (player.level() == level) {
               double px = player.getX();
               double py = player.getY();
               double pz = player.getZ();
               player.connection.send(new ClientboundSoundPacket(sound, source, px, py, pz, volume, pitch, seed));
            }
         }
      });
   }

   private static void spawnTNT(ServerLevel level, BlockPos origin, BlockPos[] offsets) {
      for (BlockPos offset : offsets) {
         PrimedTnt tnt = (PrimedTnt)EntityType.TNT.create(level);
         if (tnt != null) {
            tnt.moveTo(origin.getX() + offset.getX(), origin.getY() + 15, origin.getZ() + offset.getZ());
            tnt.setFuse(60);
            tnt.addTag("demonFight");
            level.addFreshEntity(tnt);
         }
      }
   }

   private static void spawnEntities(ServerLevel level, BlockPos origin, EntityType<?> type, BlockPos[] offsets) {
      for (BlockPos offset : offsets) {
         Entity spawned = type.create(level);
         if (spawned != null) {
            spawned.moveTo(origin.getX() + offset.getX(), origin.getY() + 2, origin.getZ() + offset.getZ());
            level.addFreshEntity(spawned);
         }
      }
   }
}
