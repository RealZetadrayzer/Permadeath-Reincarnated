package zeta.org.permadeath_reincarnated.mobs;

import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.slf4j.Logger;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobReplacement;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;
import zeta.org.permadeath_reincarnated.systems.attachments.GuardianAllayTotemAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.GuardianAllayTotemAttachmentHelper;

@EventBusSubscriber
public class AllayChanges {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, String> SOURCE_STRINGS = Map.ofEntries(
      Map.entry("anvil", "yunque"),
      Map.entry("arrow", "flecha"),
      Map.entry("badRespawnPoint", "explosión"),
      Map.entry("cactus", "cactus"),
      Map.entry("cramming", "aplastamiento"),
      Map.entry("dragonBreath", "aliento de dragón"),
      Map.entry("drown", "ahogamiento"),
      Map.entry("dryOut", "deshidratamiento"),
      Map.entry("explosion", "explosión"),
      Map.entry("explosion.player", "explosión"),
      Map.entry("explosion.crystal", "end crystal"),
      Map.entry("fall", "caída"),
      Map.entry("fallingBlock", "bloque cayendo"),
      Map.entry("fallingStalactite", "estalactita cayendo"),
      Map.entry("outOfWorld", "caerse al vacío"),
      Map.entry("fireball", "bola de fuego"),
      Map.entry("fireworks", "fuegos artificiales"),
      Map.entry("flyIntoWall", "chocarse contra una pared"),
      Map.entry("freeze", "congelación"),
      Map.entry("generic", "daño genérico"),
      Map.entry("genericKill", "daño genérico"),
      Map.entry("hotFloor", "suelo caliente"),
      Map.entry("inFire", "quemarse"),
      Map.entry("inWall", "sofocarse en bloques"),
      Map.entry("lava", "quemarse con lava"),
      Map.entry("lightningBolt", "rayo"),
      Map.entry("magic", "daño mágico"),
      Map.entry("mob", "mob"),
      Map.entry("mobProjectile", "proyectil"),
      Map.entry("noAggroMobAttack", "mob"),
      Map.entry("onFire", "quemarse"),
      Map.entry("outsideBorder", "borde del mundo"),
      Map.entry("player", "jugador"),
      Map.entry("sonic_boom", "explosión sónica"),
      Map.entry("stalagmite", "estalagmita"),
      Map.entry("starve", "hambre"),
      Map.entry("sting", "picadura de abeja"),
      Map.entry("sweetBerryBush", "arbusto de bayas"),
      Map.entry("thorns", "espinas"),
      Map.entry("trident", "tridente"),
      Map.entry("wither", "efecto wither"),
      Map.entry("witherSkull", "calavera de Wither")
   );

   private static String resolveSource(DamageSource source) {
      Entity attacker = source.getEntity();
      return attacker != null ? sanitize(attacker.getDisplayName().getString()) : SOURCE_STRINGS.getOrDefault(source.getMsgId(), "daño desconocido");
   }

   private static String sanitize(String text) {
      return text.replaceAll("§.", "");
   }

   private static void broadcast(Player player, String msg) {
      Objects.requireNonNull(player.getServer()).getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(Component.literal(msg), false));
   }

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Allay allay) {
                  if (!event.loadedFromDisk()) {
                     if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                        if (day >= 20 && day < 30 && chance <= 5) {
                           allay.setCustomName(Component.literal("Allay Guardián").withStyle(ChatFormatting.AQUA));
                           allay.addTag("isGuardian");
                           allay.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 1, false, true));
                           allay.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
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
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof Allay allay) {
               if (allay.tickCount % 5 == 0) {
                  if (allay.getTags().contains("isGuardian")) {
                     allay.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 1, false, true));
                     allay.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                     Optional<UUID> ownerOpt = allay.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
                     if (!ownerOpt.isEmpty()) {
                        Player owner = level.getPlayerByUUID(ownerOpt.get());
                        if (owner != null) {
                           if (!(allay.distanceToSqr(owner) > 576.0)) {
                              ItemStack stack = allay.getItemInHand(InteractionHand.MAIN_HAND);
                              if (!stack.isEmpty()) {
                                 if (stack.is(Items.HEAVY_CORE)) {
                                    owner.addEffect(new MobEffectInstance(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate(), 40, 0, true, true));
                                 } else if (stack.is(Items.GOLDEN_APPLE)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, true));
                                 } else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, true));
                                 } else if (stack.is(Items.GOLDEN_PICKAXE)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 0, true, true));
                                 } else if (stack.is(Items.TURTLE_HELMET)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, true, true));
                                 } else if (stack.is(Items.ENDER_EYE)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, true));
                                 } else if (stack.is(Items.NETHER_STAR)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, true));
                                 } else if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, true, true));
                                    owner.removeEffect(MobEffects.WITHER);
                                    level.sendParticles(
                                       ParticleTypes.SOUL_FIRE_FLAME, owner.getX(), owner.getY() + 0.5, owner.getZ(), 1, 0.15, 0.15, 0.15, 0.03
                                    );
                                 } else if (stack.is(Items.DIAMOND_BOOTS)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, true));
                                 } else if (stack.is(Items.RABBIT_FOOT)) {
                                    owner.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, true, true));
                                 } else if (stack.is(Items.RECOVERY_COMPASS)) {
                                    owner.removeEffect(MobEffects.DARKNESS);
                                    owner.removeEffect(MobEffects.BLINDNESS);
                                    level.sendParticles(ParticleTypes.SCULK_SOUL, owner.getX(), owner.getY() + 0.5, owner.getZ(), 1, 0.15, 0.15, 0.15, 0.03);
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
   public static void onTickConvert(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            if (event.getEntity() instanceof Allay allay) {
               if (!allay.level().isClientSide) {
                  if (allay.level() instanceof ServerLevel level) {
                     if (allay.isAlive()) {
                        EntityType<? extends Mob> chosen = RandomUtil.RANDOM.nextFloat() < 0.1 ? EntityType.WARDEN : EntityType.PARROT;
                        allay.addTag("fromAllay");
                        allay.setCustomName(Component.literal("Allay-Transmutado").withStyle(ChatFormatting.GOLD));
                        MobReplacement.convertToWithData(allay, chosen, false);
                        allay.discard();
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onOwnerLethalDamage(Pre event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (!player.isCreative() && !player.isSpectator()) {
               if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                  float damage = event.getNewDamage();
                  if (!(damage < player.getHealth())) {
                     Level level = player.level();
                     GuardianAllayTotemAttachment cooldown = GuardianAllayTotemAttachmentHelper.get(player);
                     if (cooldown.getRemainingTicks() <= 0) {
                        for (Allay allay : level.getEntitiesOfClass(Allay.class, player.getBoundingBox().inflate(24.0))) {
                           if (allay.getTags().contains("isGuardian")) {
                              Optional<UUID> ownerOpt = allay.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
                              if (!ownerOpt.isEmpty() && ownerOpt.get().equals(player.getUUID())) {
                                 ItemStack stack = allay.getItemInHand(InteractionHand.MAIN_HAND);
                                 if (stack.is(Items.TOTEM_OF_UNDYING)) {
                                    boolean isMedalla = stack.getComponents().toString().contains("medalla");
                                    int day = DayGlobalCount.CURRENT_DAY;
                                    int consumedTotems = 1;
                                    int failThreshold = -1;
                                    if (day >= 60) {
                                       consumedTotems = isMedalla ? 1 : 3;
                                       failThreshold = 93;
                                    } else if (day >= 50) {
                                       consumedTotems = isMedalla ? 1 : 2;
                                       failThreshold = 95;
                                    } else if (day >= 40) {
                                       consumedTotems = isMedalla ? 1 : 2;
                                       failThreshold = 97;
                                    } else if (day >= 30) {
                                       failThreshold = 99;
                                    }

                                    String cause = resolveSource(event.getSource());
                                    int roll = RandomUtil.RANDOM.nextInt(100);
                                    String totemText = formatTotems(consumedTotems);
                                    int requiredFromPlayer = consumedTotems - 1;
                                    if (countTotems(player) < requiredFromPlayer) {
                                       allay.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                       player.addTag("totem_failed");
                                       String msg;
                                       if (isMedalla) {
                                          msg = String.format(
                                             "§7El Allay Guardián de %s no pudo activar su §4§l[§r§c☠§4§l] §f§kℑ§r §6§lMedalla de Superviviente §r§f§kℑ §4§l[§r§c☠§4§l]§r §7por no tener suficientes tótems.§r",
                                             player.getName().getString()
                                          );
                                       } else {
                                          msg = String.format(
                                             "§7El Allay Guardián de %s falló al activar su tótem por no tener suficientes tótems.§r",
                                             player.getName().getString()
                                          );
                                       }

                                       broadcast(player, msg);
                                       return;
                                    }

                                    if (!isMedalla && failThreshold != -1 && roll >= failThreshold) {
                                       allay.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                       player.addTag("totem_failed");
                                       String msg = String.format(
                                          "§7El Allay Guardián de %s intentó consumir %s por %s, pero falló. (Probabilidad: §c%d§7 >= %d)§r",
                                          player.getName().getString(),
                                          totemText,
                                          cause,
                                          roll,
                                          failThreshold
                                       );
                                       broadcast(player, msg);
                                       return;
                                    }

                                    event.setNewDamage(0.0F);
                                    consumeTotemsSmart(player, requiredFromPlayer);
                                    allay.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                    String msg;
                                    if (isMedalla) {
                                       msg = String.format(
                                          "§7El Allay Guardián de %s consumio una §4§l[§r§c☠§4§l] §f§kℑ§r §6§lMedalla de Superviviente §r§f§kℑ §4§l[§r§c☠§4§l]§r§7 por %s.§r",
                                          player.getName().getString(),
                                          cause
                                       );
                                    } else if (failThreshold != -1) {
                                       msg = String.format(
                                          "§7El Allay Guardián de %s consumio %s por %s. (Probabilidad: §a%d§7 < %d)§r",
                                          player.getName().getString(),
                                          totemText,
                                          cause,
                                          roll,
                                          failThreshold
                                       );
                                    } else {
                                       msg = String.format("§7El Allay Guardián de %s consumio %s por %s.§r", player.getName().getString(), totemText, cause);
                                    }

                                    broadcast(player, msg);
                                    cooldown.startCooldown();
                                    player.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                                    CriteriaTriggers.USED_TOTEM.trigger(player, stack);
                                    player.setHealth(1.0F);
                                    player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                                    level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                                    level.broadcastEntityEvent(player, (byte)35);
                                    level.broadcastEntityEvent(allay, (byte)35);
                                    player.invulnerableTime = 10;
                                    player.hurtTime = 10;
                                    player.hurtDuration = 10;
                                    player.hurtMarked = true;
                                    double dx = player.getX() - allay.getX();
                                    double dz = player.getZ() - allay.getZ();
                                    double norm = Math.sqrt(dx * dx + dz * dz);
                                    if (norm != 0.0) {
                                       dx /= norm;
                                       dz /= norm;
                                    }

                                    player.knockback(0.4F, dx, dz);
                                    level.broadcastDamageEvent(player, event.getSource());
                                    return;
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
   public static void onCooldownTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  if (player.level() instanceof ServerLevel level) {
                     GuardianAllayTotemAttachment totemCooldown = GuardianAllayTotemAttachmentHelper.get(player);
                     if (totemCooldown.isOnCooldown()) {
                        totemCooldown.tick();
                        if (totemCooldown.getRemainingTicks() <= 0) {
                           player.displayClientMessage(
                              Component.literal("¡Tu ")
                                 .withStyle(ChatFormatting.YELLOW)
                                 .append(Component.literal("Allay Guardián").withStyle(ChatFormatting.GOLD))
                                 .append(" ya puede usar ")
                                 .withStyle(ChatFormatting.YELLOW)
                                 .withStyle(Style.EMPTY.withBold(false))
                                 .append(
                                    Component.literal("Totems")
                                       .withStyle(ChatFormatting.GOLD)
                                       .append(Component.literal(" otra vez!").withStyle(ChatFormatting.YELLOW).withStyle(Style.EMPTY.withBold(false)))
                                 ),
                              false
                           );
                           LOGGER.info("[Permadeath Monitor] El Allay Guardián de {} puede usar Totems otra vez.", player.getName().getString());
                           level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
                           level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.0, 0.0, 0.0, 0.5);
                           level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.15, 0.15, 0.15, 0.05);
                           totemCooldown.setClean();
                        } else {
                           totemCooldown.setClean();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static String formatTotems(int amount) {
      return switch (amount) {
         case 1 -> "un tótem";
         case 2 -> "dos tótems";
         case 3 -> "tres tótems";
         default -> amount + " tótems";
      };
   }

   private static int countTotems(Player player) {
      int count = 0;

      for (ItemStack stack : player.getInventory().items) {
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            count += stack.getCount();
         }
      }

      for (ItemStack stack : player.getInventory().offhand) {
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            count += stack.getCount();
         }
      }

      return count;
   }

   private static void consumeTotemsSmart(Player player, int amount) {
      if (amount > 0) {
         for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
               int remove = Math.min(stack.getCount(), amount);
               stack.shrink(remove);
               amount -= remove;
               if (amount <= 0) {
                  return;
               }
            }
         }

         ItemStack main = player.getMainHandItem();
         if (main.is(Items.TOTEM_OF_UNDYING)) {
            int remove = Math.min(main.getCount(), amount);
            main.shrink(remove);
            amount -= remove;
            if (amount <= 0) {
               return;
            }
         }

         ItemStack off = player.getOffhandItem();
         if (off.is(Items.TOTEM_OF_UNDYING)) {
            int remove = Math.min(off.getCount(), amount);
            off.shrink(remove);
         }
      }
   }
}
