package zeta.org.permadeath_reincarnated.mobs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Builder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.entities.PermadeathEntityRegistry;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PermadeathDamageCalculator;
import zeta.org.permadeath_reincarnated.systems.PiglinVariantAccess;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;
import zeta.org.permadeath_reincarnated.systems.SkeletonRiderHelper;

@EventBusSubscriber
public class CatChanges {
   private static final String NOVA_SHOULD_EXPLODE = "SupernovaShouldExplode";
   private static final String NOVA_WARNED = "SupernovaWarned";
   private static final String NOVA_SCHEDULED = "SupernovaScheduled";
   private static final String NOVA_DESPAWN_TIMER = "SupernovaDespawnTimer";
   private static final Map<Holder<MobEffect>, Integer> EFFECTS;
   private static final int DESPAWN_SECONDS = 22;
   private static final Map<String, String> DIMENSION_NAMES = new HashMap<>();

   private static EntityType<?>[] getMobPoolUniversal() {
      return new EntityType[]{
         EntityType.CAT,
         EntityType.GHAST,
         EntityType.PIGLIN_BRUTE,
         EntityType.PHANTOM,
         EntityType.SHULKER,
         EntityType.WITHER_SKELETON,
         EntityType.WARDEN,
         EntityType.WITHER,
         EntityType.ENDER_DRAGON
      };
   }

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
         EntityType.WITHER,
         EntityType.ENDER_DRAGON,
         EntityType.IRON_GOLEM,
         (EntityType)PermadeathEntityRegistry.CUSTOM_GIANT.get()
      };
   }

   @SubscribeEvent
   public static void onEntityTick(Post event) {
      if (event.getEntity() instanceof Cat cat) {
         if (cat.getTags().contains("superNova")) {
            if (!cat.level().isClientSide) {
               if (cat.tickCount % 10 == 0) {
                  CompoundTag data = cat.getPersistentData();
                  ServerLevel level = (ServerLevel)cat.level();
                  if (data.getBoolean("SupernovaShouldExplode")) {
                     List<ServerPlayer> serverPlayerList = Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers();
                     List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(
                        ServerPlayer.class, cat.getBoundingBox().inflate(72.0), player -> !player.isSpectator()
                     );
                     if (nearbyPlayers.isEmpty()) {
                        return;
                     }

                     int timer = data.getInt("SupernovaDespawnTimer") + 1;
                     data.putInt("SupernovaDespawnTimer", timer);
                     if (timer > 22) {
                        cat.remove(RemovalReason.KILLED);
                        return;
                     }

                     ChunkPos chunkPos = cat.chunkPosition();
                     level.setChunkForced(chunkPos.x, chunkPos.z, true);
                     cat.setPersistenceRequired();
                     String dimId = level.dimension().location().toString();
                     String dimensionName = DIMENSION_NAMES.getOrDefault(dimId, dimId);
                     int x = Mth.floor(cat.getX());
                     int y = Mth.floor(cat.getY());
                     int z = Mth.floor(cat.getZ());
                     if (!data.getBoolean("SupernovaWarned")) {
                        nearbyPlayers.forEach(player -> PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_NEAR_SUPERNOVA_ID));
                        serverPlayerList.forEach(
                           player -> player.displayClientMessage(
                              Component.literal("¡Un Gato Supernova va a explotar en " + dimensionName + ": " + x + " " + y + " " + z + "! ")
                                 .withStyle(ChatFormatting.RED),
                              false
                           )
                        );
                        data.putBoolean("SupernovaWarned", true);
                     }

                     if (!data.getBoolean("SupernovaScheduled")) {
                        data.putBoolean("SupernovaScheduled", true);
                        ScheduleInTicks.schedule(() -> explodeSuperNova(level, cat, chunkPos), 200);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityTickSingularity(Post event) {
      if (event.getEntity() instanceof Cat cat) {
         if (cat.getTags().contains("singularity")) {
            if (!cat.level().isClientSide) {
               if (cat.tickCount % 10 == 0) {
                  CompoundTag data = cat.getPersistentData();
                  ServerLevel level = (ServerLevel)cat.level();
                  if (data.getBoolean("SupernovaShouldExplode")) {
                     List<ServerPlayer> serverPlayerList = Objects.requireNonNull(level.getServer()).getPlayerList().getPlayers();
                     List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(
                        ServerPlayer.class, cat.getBoundingBox().inflate(72.0), player -> !player.isSpectator()
                     );
                     if (nearbyPlayers.isEmpty()) {
                        return;
                     }

                     int timer = data.getInt("SupernovaDespawnTimer") + 1;
                     data.putInt("SupernovaDespawnTimer", timer);
                     if (timer > 22) {
                        cat.remove(RemovalReason.KILLED);
                        return;
                     }

                     ChunkPos chunkPos = cat.chunkPosition();
                     level.setChunkForced(chunkPos.x, chunkPos.z, true);
                     cat.setPersistenceRequired();
                     String dimId = level.dimension().location().toString();
                     String dimensionName = DIMENSION_NAMES.getOrDefault(dimId, dimId);
                     int x = Mth.floor(cat.getX());
                     int y = Mth.floor(cat.getY());
                     int z = Mth.floor(cat.getZ());
                     if (!data.getBoolean("SupernovaWarned")) {
                        nearbyPlayers.forEach(player -> PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_NEAR_SINGULARITY_ID));
                        serverPlayerList.forEach(
                           player -> player.displayClientMessage(
                              Component.literal("¡Un Gato Singularidad va a crear un agujero negro en " + dimensionName + ": " + x + " " + y + " " + z + "! ")
                                 .withStyle(ChatFormatting.LIGHT_PURPLE),
                              false
                           )
                        );
                        data.putBoolean("SupernovaWarned", true);
                     }

                     if (!data.getBoolean("SupernovaScheduled")) {
                        data.putBoolean("SupernovaScheduled", true);
                        ScheduleInTicks.schedule(() -> {
                           spawnBlackHole(level, cat.blockPosition(), 2400);
                           level.playSound(null, cat.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.5F, 0.6F);
                           level.playSound(null, cat.blockPosition(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.HOSTILE, 1.2F, 0.5F);
                           level.playSound(null, cat.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.HOSTILE, 1.0F, 1.0F);
                           level.playSound(null, cat.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.8F);
                           level.playSound(null, cat.blockPosition(), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.4F, 0.7F);
                        }, 200);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCatTick(Post event) {
      if (event.getEntity() instanceof Cat cat) {
         if (!cat.getTags().contains("galactic")
            && !cat.getTags().contains("superNova")
            && !cat.getTags().contains("universal")
            && !cat.getTags().contains("singularity")) {
            if (!cat.level().isClientSide) {
               if (cat.tickCount % 10 == 0) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  ServerLevel level = (ServerLevel)cat.level();
                  if (day >= 40 && day < 50) {
                     List<Cat> nearbyCats = level.getEntitiesOfClass(
                        Cat.class,
                        cat.getBoundingBox().inflate(72.0),
                        otherx -> otherx != cat
                           && !otherx.isRemoved()
                           && !otherx.getTags().contains("galactic")
                           && !otherx.getTags().contains("superNova")
                           && !otherx.getPersistentData().getBoolean("SupernovaShouldExplode")
                     );
                     if (nearbyCats.isEmpty()) {
                        return;
                     }

                     for (Cat other : nearbyCats) {
                        if (!other.getTags().contains("fromWolf")) {
                           other.setCustomName(Component.literal("Gato Supernova").withStyle(ChatFormatting.GOLD));
                        }

                        other.addTag("superNova");
                        CompoundTag becomeSuperNova = other.getPersistentData();
                        becomeSuperNova.putBoolean("SupernovaShouldExplode", true);
                        becomeSuperNova.putInt("SupernovaDespawnTimer", 0);
                        becomeSuperNova.putBoolean("SupernovaWarned", false);
                        becomeSuperNova.putBoolean("SupernovaScheduled", false);
                     }
                  } else if (day >= 50) {
                     if (day >= 60 && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
                        boolean flag = RandomUtil.RANDOM.nextBoolean();
                        if (flag) {
                           if (!cat.getTags().contains("fromWolf")) {
                              cat.setCustomName(Component.literal("Gato Singularidad").withStyle(ChatFormatting.DARK_PURPLE));
                           }

                           cat.addTag("singularity");
                           cat.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                        } else {
                           if (!cat.getTags().contains("fromWolf")) {
                              cat.setCustomName(Component.literal("Gato Universal").withStyle(ChatFormatting.RED));
                           }

                           cat.addTag("universal");
                        }
                     } else {
                        if (!cat.getTags().contains("fromWolf")) {
                           cat.setCustomName(Component.literal("Gato Galactico").withStyle(ChatFormatting.LIGHT_PURPLE));
                        }

                        cat.addTag("galactic");
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onUniversalTick(Post event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.tickCount % 10 == 0) {
            if (entity instanceof Cat cat) {
               if (cat.isAlive()) {
                  if (cat.getTags().contains("universal")) {
                     if (!cat.getPersistentData().getBoolean("Exploded")) {
                        LivingEntity target = cat.getTarget();
                        if (target != null) {
                           if (target.isAlive()) {
                              if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                                 double distance = cat.distanceToSqr(target);
                                 if (!(distance > 9.0)) {
                                    if (target.canBeSeenAsEnemy()) {
                                       cat.getPersistentData().putBoolean("Exploded", true);
                                       cat.kill();
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
   }

   private static void explodeSuperNova(ServerLevel level, Cat cat, ChunkPos chunkPos) {
      Vec3 center = cat.position();
      float spacing = 5.0F;
      float fakeRadius = 150.0F;
      float fakeDiameter = fakeRadius * 2.0F;
      Vec3[] offsets = new Vec3[]{
         new Vec3(center.x + spacing, center.y - 5.0, center.z + spacing),
         new Vec3(center.x + spacing, center.y - 5.0, center.z - spacing),
         new Vec3(center.x - spacing, center.y - 5.0, center.z + spacing),
         new Vec3(center.x - spacing, center.y - 5.0, center.z - spacing)
      };

      for (Vec3 pos : offsets) {
         spawnExplosion(level, cat, pos, 40.0F);
      }

      spawnExplosion(level, cat, center, 40.0F);

      for (Entity entity : level.getEntities(cat, cat.getBoundingBox().inflate(fakeDiameter))) {
         if (entity != cat) {
            double distance = Math.sqrt(entity.distanceToSqr(center));
            double exposure = Explosion.getSeenPercent(center, entity);
            if (!(exposure <= 0.0)) {
               double impact = (1.0 - distance / fakeDiameter) * exposure;
               float damage = (float)((impact * impact + impact) / 2.0 * 7.0 * fakeDiameter + 1.0);
               entity.hurt(level.damageSources().explosion(cat, cat), damage);
            }
         }
      }

      List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, cat.getBoundingBox().inflate(72.0));
      List<FallingBlockEntity> nearbyFallingBlocks = level.getEntitiesOfClass(FallingBlockEntity.class, cat.getBoundingBox().inflate(72.0));
      nearbyItems.forEach(item -> {
         if (item.getAge() < 200) {
            item.remove(RemovalReason.KILLED);
         }
      });
      nearbyFallingBlocks.forEach(fallingBlockEntity -> fallingBlockEntity.remove(RemovalReason.KILLED));
      cat.remove(RemovalReason.KILLED);
   }

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Cat cat) {
         if (!cat.level().isClientSide) {
            if (!cat.getTags().contains("galactic")
               && !cat.getTags().contains("superNova")
               && !cat.getTags().contains("universal")
               && !cat.getTags().contains("singularity")) {
               if (!event.loadedFromDisk()) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  if (day >= 40 && day < 50) {
                     if (!cat.getTags().contains("fromWolf")) {
                        cat.setCustomName(Component.literal("Gato Supernova").withStyle(ChatFormatting.GOLD));
                     }

                     cat.addTag("superNova");
                     cat.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                  } else if (day >= 50) {
                     if (day >= 60 && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
                        boolean flag = RandomUtil.RANDOM.nextBoolean();
                        if (flag) {
                           if (!cat.getTags().contains("fromWolf")) {
                              cat.setCustomName(Component.literal("Gato Singularidad").withStyle(ChatFormatting.DARK_PURPLE));
                           }

                           cat.addTag("singularity");
                           cat.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                        } else {
                           if (!cat.getTags().contains("fromWolf")) {
                              cat.setCustomName(Component.literal("Gato Universal").withStyle(ChatFormatting.RED));
                           }

                           cat.addTag("universal");
                        }
                     } else {
                        if (!cat.getTags().contains("fromWolf")) {
                           cat.setCustomName(Component.literal("Gato Galactico").withStyle(ChatFormatting.LIGHT_PURPLE));
                        }

                        cat.addTag("galactic");
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof Cat cat) {
         if (cat.level() instanceof ServerLevel level) {
            if (!cat.level().isClientSide) {
               int day = DayGlobalCount.CURRENT_DAY;
               if (day >= 50 && !cat.getPersistentData().getBoolean("SupernovaShouldExplode")) {
                  if (cat.getTags().contains("galactic")) {
                     EntityType<?>[] pool = getMobPool();
                     EntityType<?> chosenMob = pool[RandomUtil.RANDOM.nextInt(pool.length)];
                     if (!(chosenMob.create(cat.level()) instanceof Mob mob)) {
                        return;
                     }

                     if (mob instanceof Cat) {
                        mob.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                        mob.setCustomName(Component.literal("Gato Supernova").withStyle(ChatFormatting.GOLD));
                        mob.addTag("superNova");
                     }

                     ((MobSpawnTypeAccessor)mob).setSpawnType(MobSpawnType.MOB_SUMMONED);
                     mob.moveTo(cat.getX(), cat.getY() + 0.5, cat.getZ());
                     cat.level().addFreshEntity(mob);
                  } else if (cat.getTags().contains("universal")) {
                     EntityType<?>[] pool = getMobPoolUniversal();
                     EntityType<?> chosenMob = pool[RandomUtil.RANDOM.nextInt(pool.length)];
                     if (!(chosenMob.create(cat.level()) instanceof Mob mob)) {
                        return;
                     }

                     if (mob instanceof Cat) {
                        boolean flag = RandomUtil.RANDOM.nextBoolean();
                        if (flag) {
                           mob.setCustomName(Component.literal("Gato Singularidad").withStyle(ChatFormatting.DARK_PURPLE));
                           mob.addTag("singularity");
                           mob.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                        } else {
                           mob.getPersistentData().putBoolean("SupernovaShouldExplode", true);
                           mob.setCustomName(Component.literal("Gato Supernova").withStyle(ChatFormatting.GOLD));
                           mob.addTag("superNova");
                        }
                     }

                     if (mob instanceof Ghast ghast) {
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
                        } catch (Exception var29) {
                        }
                     }

                     if (mob instanceof PiglinBrute piglinBrute) {
                        MobPreventEquipmentDrops.preventAllEquipmentDrop(piglinBrute);
                        piglinBrute.setCustomName(Component.literal("Piglin Comandante").withStyle(ChatFormatting.GOLD));
                        piglinBrute.setBaby(false);
                        piglinBrute.setCanPickUpLoot(false);
                        piglinBrute.setImmuneToZombification(true);
                        ((MobSpawnTypeAccessor)piglinBrute).setSpawnType(MobSpawnType.MOB_SUMMONED);
                        piglinBrute.addTag("classPiglin");
                        piglinBrute.addTag("arrowImmune");
                        piglinBrute.addTag("piglinCommander");
                        RegistryAccess registryAccess = cat.level().registryAccess();
                        Holder<TrimPattern> pattern = registryAccess.registryOrThrow(Registries.TRIM_PATTERN).getHolderOrThrow(TrimPatterns.SILENCE);
                        Holder<TrimMaterial> material = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).getHolderOrThrow(TrimMaterials.NETHERITE);
                        ArmorTrim trim = new ArmorTrim(material, pattern);
                        if (piglinBrute instanceof PiglinVariantAccess access) {
                           access.permadeath$setPiglinVariant(1);
                        }

                        ItemStack helmet = new ItemStack(Items.BLACK_BANNER);
                        ItemStack chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
                        chestplate.set(DataComponents.TRIM, trim);
                        chestplate.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                        ItemStack leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
                        leggings.set(DataComponents.TRIM, trim);
                        leggings.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                        ItemStack boots = new ItemStack(Items.GOLDEN_BOOTS);
                        boots.set(DataComponents.TRIM, trim);
                        boots.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                        ItemStack netheriteAxe = new ItemStack(Items.NETHERITE_AXE);
                        Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
                        Holder<Enchantment> knockback = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.KNOCKBACK);
                        if (day >= 40) {
                           netheriteAxe.enchant(sharpness, 20);
                           netheriteAxe.enchant(knockback, 5);
                        } else {
                           netheriteAxe.enchant(sharpness, 5);
                           netheriteAxe.enchant(knockback, 2);
                        }

                        Registry<BannerPattern> bannerRegistry = registryAccess.registryOrThrow(Registries.BANNER_PATTERN);
                        Holder<BannerPattern> stripeTop = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "stripe_top"))
                        );
                        Holder<BannerPattern> triangleTop = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "triangle_top"))
                        );
                        Holder<BannerPattern> border = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "border"))
                        );
                        Holder<BannerPattern> curlyBorder = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "curly_border"))
                        );
                        Holder<BannerPattern> piglinBanner = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "piglin"))
                        );
                        Holder<BannerPattern> gradientUp = bannerRegistry.getHolderOrThrow(
                           ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "gradient_up"))
                        );
                        Builder builder = new Builder();
                        builder.add(stripeTop, DyeColor.RED);
                        builder.add(triangleTop, DyeColor.BLACK);
                        builder.add(border, DyeColor.BLACK);
                        builder.add(curlyBorder, DyeColor.BLACK);
                        builder.add(piglinBanner, DyeColor.RED);
                        builder.add(gradientUp, DyeColor.RED);
                        helmet.set(DataComponents.BASE_COLOR, DyeColor.BLACK);
                        helmet.set(DataComponents.BANNER_PATTERNS, builder.build());
                        if (day >= 60) {
                           piglinBrute.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                           piglinBrute.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 1, false, true));
                        }

                        Objects.requireNonNull(piglinBrute.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(0.75);
                        Objects.requireNonNull(piglinBrute.getAttribute(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)).setBaseValue(0.75);
                        Objects.requireNonNull(piglinBrute.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(200.0);
                        piglinBrute.setHealth(piglinBrute.getMaxHealth());
                        piglinBrute.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        piglinBrute.setItemSlot(EquipmentSlot.MAINHAND, netheriteAxe);
                        piglinBrute.setItemSlot(EquipmentSlot.HEAD, helmet);
                        piglinBrute.setItemSlot(EquipmentSlot.CHEST, chestplate);
                        piglinBrute.setItemSlot(EquipmentSlot.LEGS, leggings);
                        piglinBrute.setItemSlot(EquipmentSlot.FEET, boots);
                        piglinBrute.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05F);
                     }

                     if (mob instanceof Phantom phantom) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                        if (chance <= 75) {
                           phantom.setPhantomSize(18);
                           double baseHealth = Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
                           Objects.requireNonNull(phantom.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 2.0);
                           phantom.setHealth(phantom.getMaxHealth());
                           SkeletonRiderHelper.spawnRandomSkeleton(level, phantom, day);
                           List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS.entrySet());
                           Collections.shuffle(shuffled);
                           int count = 1;
                           count = Math.min(count, shuffled.size());

                           for (int i = 0; i < count; i++) {
                              Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                              phantom.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                           }
                        } else {
                           for (int i = 0; i < 4; i++) {
                              Ghast ghast = (Ghast)EntityType.GHAST.create(level);
                              if (ghast != null) {
                                 ((MobSpawnTypeAccessor)ghast).setSpawnType(MobSpawnType.MOB_SUMMONED);
                                 ghast.moveTo(phantom.getX(), phantom.getY(), phantom.getZ(), phantom.getYRot(), phantom.getXRot());
                                 ghast.setCustomName(Component.literal("Giga-Phantom-Transmutado").withStyle(ChatFormatting.LIGHT_PURPLE));
                                 ghast.addTag("fromPhantom");
                                 level.addFreshEntity(ghast);
                              }
                           }

                           level.getServer().tell(new TickTask(level.getServer().getTickCount(), () -> phantom.remove(RemovalReason.DISCARDED)));
                        }
                     }

                     if (mob instanceof Shulker shulker) {
                        AttributeInstance shulkerHealth = Objects.requireNonNull(shulker.getAttribute(Attributes.MAX_HEALTH));
                        float healthValue = day >= 50 ? 50.0F : 30.0F;
                        shulker.setCustomName(Component.literal("Shulker Void").withStyle(ChatFormatting.BLACK));
                        shulker.addTag("dropsShulkerShells");
                        shulker.addTag("shulkerBulletNoLevitation");
                        shulker.addTag("shulkerVoid");
                        shulker.setVariant(Optional.of(DyeColor.BLACK));
                        shulkerHealth.setBaseValue(healthValue);
                        shulker.setHealth(shulker.getMaxHealth());
                     }

                     if (mob instanceof WitherSkeleton witherSkeleton) {
                        AttributeInstance skeletonHealth = Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH));
                        Component skeletonName = day >= 60
                           ? Component.literal("Ultra Esqueleto Definitivo").withStyle(ChatFormatting.GOLD)
                           : Component.literal("Esqueleto Definitivo").withStyle(ChatFormatting.GOLD);
                        RegistryAccess registryAccess = level.registryAccess();
                        MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true);
                        ((MobSpawnTypeAccessor)witherSkeleton).setSpawnType(MobSpawnType.MOB_SUMMONED);
                        MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
                        witherSkeleton.setCustomName(skeletonName);
                        witherSkeleton.setPersistenceRequired();
                        witherSkeleton.addTag("claseDefinitiva");
                        witherSkeleton.addTag("fromSkeleton");
                        witherSkeleton.addTag("classSkeleton");
                        ItemStack bow = new ItemStack(Items.BOW);
                        Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FLAME);
                        bow.enchant(power, 1);
                        witherSkeleton.addEffect(speed);
                        skeletonHealth.setBaseValue(400.0);
                        witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
                        witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                        witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
                        witherSkeleton.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        witherSkeleton.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                        witherSkeleton.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
                        witherSkeleton.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
                        witherSkeleton.setLeftHanded(RandomUtil.RANDOM.nextFloat() < 0.05);
                     }

                     if (mob instanceof Warden warden) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                        if (day >= 40) {
                           WardenChanges.setupDefinitiveWarden(warden, chance);
                        } else {
                           WardenChanges.setupSuperWarden(warden, chance);
                        }
                     }

                     if (mob instanceof WitherBoss witherBoss) {
                        Objects.requireNonNull(witherBoss.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(witherBoss.getMaxHealth() * 2.0F);
                        witherBoss.setHealth(witherBoss.getMaxHealth());
                        witherBoss.setCustomName(Component.literal("Wither Reencarnado").withStyle(ChatFormatting.LIGHT_PURPLE));
                        witherBoss.addTag("fromPlayer");
                        if (day >= 40) {
                           witherBoss.setCustomName(Component.literal("Ultra Wither Reencarnado").withStyle(ChatFormatting.LIGHT_PURPLE));
                           Objects.requireNonNull(witherBoss.getAttribute(Attributes.ARMOR)).setBaseValue(20.0);
                           witherBoss.addTag("ultraWither");
                        }
                     }

                     ((MobSpawnTypeAccessor)mob).setSpawnType(MobSpawnType.MOB_SUMMONED);
                     mob.moveTo(cat.getX(), cat.getY() + 0.5, cat.getZ());
                     mob.addTag("fromUniversal");
                     cat.level().addFreshEntity(mob);
                  }
               }
            }
         }
      }
   }

   private static void spawnExplosion(ServerLevel level, Cat cat, Vec3 pos, float radius) {
      Explosion explosion = new Explosion(level, cat, pos.x, pos.y, pos.z, radius, true, BlockInteraction.DESTROY);

      try {
         Field damageCalculatorField = PermadeathDamageCalculator.getField(Explosion.class, "damageCalculator");
         damageCalculatorField.set(explosion, new PermadeathDamageCalculator());
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }

      explosion.explode();
      explosion.finalizeExplosion(true);
   }

   private static void spawnBlackHole(ServerLevel level, BlockPos centerPos, int duration) {
      double centerX = centerPos.getX() + 0.5;
      double centerY = centerPos.getY() + 0.5;
      double centerZ = centerPos.getZ() + 0.5;
      double scale = 10.0;
      double pullRadius = 240.0;
      double damageRadius = 30.0;

      for (int tick = 0; tick < duration; tick++) {
         int currentTick = tick;
         ScheduleInTicks.schedule(
            () -> {
               if (level.isLoaded(centerPos)) {
                  spawnBlackHoleParticles(level, centerX, centerY, centerZ, currentTick, duration, 10.0);
                  if (currentTick % 40 == 0) {
                     level.playSound(null, centerPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.2F, 0.5F);
                     level.playSound(null, centerPos, SoundEvents.PORTAL_AMBIENT, SoundSource.HOSTILE, 0.5F, 0.7F);
                     level.playSound(null, centerPos, SoundEvents.CONDUIT_AMBIENT, SoundSource.HOSTILE, 0.6F, 0.6F);
                  }

                  for (LivingEntity living : level.getEntitiesOfClass(
                     LivingEntity.class,
                     new AABB(centerX - 240.0, centerY - 240.0, centerZ - 240.0, centerX + 240.0, centerY + 240.0, centerZ + 240.0),
                     livingx -> livingx.isAlive() && !(livingx instanceof Shulker)
                  )) {
                     if (!(living instanceof ServerPlayer player && (player.isCreative() || player.isSpectator()))
                        && !(living instanceof WitherSkeleton witherSkeleton && witherSkeleton.getTags().contains("fromShulker"))) {
                        double dx = centerX - living.getX();
                        double dy = centerY - living.getY();
                        double dz = centerZ - living.getZ();
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (!(dist < 0.001)) {
                           double normX = dx / dist;
                           double normY = dy / dist;
                           double normZ = dz / dist;
                           double closeness = (240.0 - Math.min(dist, 240.0)) / 240.0;
                           double horizontalStrength = 0.02 + closeness * closeness * 0.2;
                           double verticalStrength = 0.18 + closeness * 0.42;
                           double antiStuckY = living.onGround() ? 0.22 + closeness * 0.35 : 0.05;
                           double burst = dist < 20.0 ? 0.18 : 0.0;
                           if (living.horizontalCollision || living.verticalCollision) {
                              antiStuckY += 0.25;
                           }

                           horizontalStrength *= 10.0;
                           verticalStrength *= 10.0;
                           Vec3 current = living.getDeltaMovement();
                           Vec3 newMotion = current.scale(0.78)
                              .add(normX * (horizontalStrength + burst), normY * verticalStrength + antiStuckY, normZ * (horizontalStrength + burst));
                           living.setDeltaMovement(newMotion);
                           living.hurtMarked = true;
                           living.hasImpulse = true;
                           if (dist <= 30.0 && !living.isDeadOrDying()) {
                              float damage = (float)Math.max(1.0, 1.0 + (30.0 - dist) * 2.0);
                              living.hurt(level.damageSources().fellOutOfWorld(), damage);
                           }
                        }
                     }
                  }
               }
            },
            tick
         );
      }
   }

   private static void spawnBlackHoleParticles(ServerLevel level, double x, double y, double z, int tick, int duration, double scale) {
      spawnSolidSphere(level, x, y, z, 1.55 * scale, (int)(180.0 * scale));
      spawnSolidSphere(level, x, y, z, 1.15 * scale, (int)(140.0 * scale));
      spawnSolidSphere(level, x, y, z, 0.8 * scale, (int)(100.0 * scale));
      level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, (int)(8.0 * scale), 0.12 * scale, 0.12 * scale, 0.12 * scale, 0.0);
      level.sendParticles(ParticleTypes.SQUID_INK, x, y, z, (int)(14.0 * scale), 0.16 * scale, 0.16 * scale, 0.16 * scale, 0.0);
      if (tick % 8 == 0) {
         spawnShinyParticles(level, x, y, z, 6, scale);
      }

      if (tick % 10 == 0) {
         level.sendParticles(ParticleTypes.FLASH, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private static void spawnSolidSphere(ServerLevel level, double cx, double cy, double cz, double radius, int count) {
      double r2 = radius * radius;

      for (int i = 0; i < count; i++) {
         double x = RandomUtil.RANDOM.nextDouble() * 2.0 - 1.0;
         double y = RandomUtil.RANDOM.nextDouble() * 2.0 - 1.0;
         double z = RandomUtil.RANDOM.nextDouble() * 2.0 - 1.0;
         if (x * x + y * y + z * z > 1.0) {
            i--;
         } else {
            double px = cx + x * radius;
            double py = cy + y * radius;
            double pz = cz + z * radius;
            level.sendParticles(ParticleTypes.SQUID_INK, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   private static void spawnShinyParticles(ServerLevel level, double cx, double cy, double cz, int count, double scale) {
      for (int i = 0; i < count; i++) {
         double theta = RandomUtil.RANDOM.nextDouble() * Math.PI * 2.0;
         double phi = Math.acos(2.0 * RandomUtil.RANDOM.nextDouble() - 1.0);
         double radius = (1.9 + RandomUtil.RANDOM.nextDouble() * 0.6) * scale;
         double px = cx + radius * Math.sin(phi) * Math.cos(theta);
         double py = cy + radius * Math.cos(phi);
         double pz = cz + radius * Math.sin(phi) * Math.sin(theta);
         double vx = (RandomUtil.RANDOM.nextDouble() - 0.5) * 0.01;
         double vy = (RandomUtil.RANDOM.nextDouble() - 0.5) * 0.01;
         double vz = (RandomUtil.RANDOM.nextDouble() - 0.5) * 0.01;
         level.sendParticles(ParticleTypes.END_ROD, px, py, pz, 1, vx, vy, vz, 0.0);
      }
   }

   static {
      Map<Holder<MobEffect>, Integer> map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 0);
      map.put(MobEffects.DAMAGE_BOOST, 0);
      map.put(MobEffects.REGENERATION, 0);
      map.put(MobEffects.INVISIBILITY, 0);
      EFFECTS = Collections.unmodifiableMap(map);
      DIMENSION_NAMES.put("minecraft:overworld", "Overworld");
      DIMENSION_NAMES.put("minecraft:the_nether", "Nether");
      DIMENSION_NAMES.put("minecraft:the_end", "End");
      DIMENSION_NAMES.put("permadeath_reincarnated:the_beginning", "The Beginning");
   }
}
