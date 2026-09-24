package zeta.org.permadeath_reincarnated.systems;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.mixins.BaseSpawnerAccessor;
import zeta.org.permadeath_reincarnated.mixins.MobSpawnTypeAccessor;

@EventBusSubscriber
public class ModulesSetupHelper {
   public static void spawnDeathModule(ServerLevel level, Mob mob) {
      Zombie zombie = (Zombie)EntityType.ZOMBIE.create(level);
      Shulker shulker = (Shulker)EntityType.SHULKER.create(level);
      MinecartSpawner spawner = (MinecartSpawner)EntityType.SPAWNER_MINECART.create(level);
      if (zombie != null) {
         if (shulker != null) {
            if (spawner != null) {
               zombie.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
               shulker.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
               spawner.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());
               mob.addTag("fromModule");
               zombie.addTag("fromModule");
               zombie.addTag("cannotBeTransmuted");
               shulker.addTag("fromModule");
               shulker.addTag("shulkerExplosivo");
               shulker.addTag("shulkerBulletNoLevitation");
               shulker.addTag("shouldNaturallyDespawn");
               spawner.addTag("fromModule");
               spawner.addTag("shouldNaturallyDespawn");
               mob.setCustomName(Component.literal("Araña de Cueva de la Muerte").withStyle(ChatFormatting.DARK_RED));
               zombie.setCustomName(Component.literal("Zombi de la Muerte").withStyle(ChatFormatting.DARK_RED));
               shulker.setCustomName(Component.literal("Shulker Explosivo de la Muerte").withStyle(ChatFormatting.DARK_RED));
               spawner.setCustomName(Component.literal("Minecart con Spawner de la Muerte").withStyle(ChatFormatting.DARK_RED));
               ((MobSpawnTypeAccessor)zombie).setSpawnType(MobSpawnType.MOB_SUMMONED);
               ((MobSpawnTypeAccessor)shulker).setSpawnType(MobSpawnType.MOB_SUMMONED);
               double baseHealth = Objects.requireNonNull(zombie.getAttribute(Attributes.MAX_HEALTH)).getBaseValue();
               Objects.requireNonNull(zombie.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(baseHealth * 4.0);
               zombie.setHealth(zombie.getMaxHealth());
               zombie.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
               zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, true));
               zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
               zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
               zombie.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TNT));
               zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TNT));
               shulker.setVariant(Optional.of(DyeColor.RED));
               shulker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
               spawner.setInvulnerable(true);
               setupPotionSpawner(level, spawner);
               zombie.startRiding(mob);
               shulker.startRiding(zombie);
               spawner.startRiding(shulker);
               level.addFreshEntity(zombie);
               level.addFreshEntity(shulker);
               level.addFreshEntity(spawner);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTick(Post event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity.tickCount % 10 == 0) {
            if (entity instanceof Zombie zombie) {
               if (zombie.isAlive()) {
                  if (zombie.getTags().contains("fromModule")) {
                     if (!zombie.getPersistentData().getBoolean("Exploded")) {
                        LivingEntity target = zombie.getTarget();
                        if (target != null) {
                           if (target.isAlive()) {
                              if (!(target instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                                 double distance = zombie.distanceToSqr(target);
                                 if (!(distance > 9.0)) {
                                    if (target.canBeSeenAsEnemy()) {
                                       zombie.getPersistentData().putBoolean("Exploded", true);
                                       level.explode(zombie, zombie.getX(), zombie.getY(), zombie.getZ(), 12.0F, true, ExplosionInteraction.MOB);
                                       zombie.discard();
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

   @SubscribeEvent
   public static void onDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      Level level = entity.level();
      if (!level.isClientSide) {
         if (entity instanceof Zombie zombie) {
            if (zombie.getTags().contains("fromModule")) {
               level.explode(zombie, zombie.getX(), zombie.getY(), zombie.getZ(), 6.0F, true, ExplosionInteraction.MOB);
            }
         }
      }
   }

   public static void setupPotionSpawner(ServerLevel level, MinecartSpawner spawner) {
      BaseSpawnerAccessor accessor = (BaseSpawnerAccessor)spawner.getSpawner();
      ItemStack splash = new ItemStack(Items.SPLASH_POTION);
      PotionContents contents = new PotionContents(Optional.of(Potions.WATER), Optional.of(6553600), List.of(new MobEffectInstance(MobEffects.HARM, 1, 3)));
      splash.set(DataComponents.POTION_CONTENTS, contents);
      SpawnData potionSpawn = new SpawnData();
      potionSpawn.getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.POTION).toString());
      Tag itemTag = splash.save(level.registryAccess());
      potionSpawn.getEntityToSpawn().put("Item", itemTag);
      accessor.setNextSpawnData(potionSpawn);
      accessor.setSpawnCount(1 + RandomUtil.RANDOM.nextInt(4));
      accessor.setSpawnRange(3 + RandomUtil.RANDOM.nextInt(4));
      accessor.setMinSpawnDelay(60 + RandomUtil.RANDOM.nextInt(61));
      accessor.setMaxSpawnDelay(300);
   }
}
