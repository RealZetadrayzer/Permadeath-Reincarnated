package zeta.org.permadeath_reincarnated.systems;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.entities.CustomGiant;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.entities.CustomSilverfish;

@EventBusSubscriber
public class MobReplacement {
   private static final Random RANDOM = new Random();
   private static final int MAX_VINDICATORS_PER_PLAYER = 20;
   private static final int MAX_ILLUSIONERS_PER_PLAYER = 20;
   private static final int MAX_EVOKERS_PER_PLAYER = 20;
   private static final int MAX_CREEPERS_PER_PLAYER = 20;
   private static final int MAX_CAVE_SPIDERS_PER_PLAYER = 20;
   private static final int MAX_PUFFERFISH_PER_PLAYER = 20;
   private static final int MAX_RAVAGERS_PER_PLAYER = 15;
   private static final int MAX_ZOGLINS_PER_PLAYER = 15;
   private static final int MAX_MAGMA_CUBES_PER_PLAYER = 10;
   private static final int MAX_ELDER_GUARDIANS_PER_PLAYER = 10;
   private static final int MAX_SILVERFISHS_PER_PLAYER = 10;
   private static final int MAX_CATS_PER_PLAYER = 10;
   private static final int MAX_GUARDIANS_PER_PLAYER = 10;
   private static final int MAX_BLAZES_PER_PLAYER = 10;
   private static final int MAX_GIANTS_PER_PLAYER_D50 = 2;
   private static final int MAX_GIANTS_PER_PLAYER_D60 = 5;
   private static final int RADIUS = 256;

   @SubscribeEvent
   public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
      if (event.getLevel() instanceof ServerLevel level) {
         if (event.getSpawnType() == MobSpawnType.NATURAL) {
            Mob mob = event.getEntity();
            int day = DayGlobalCount.CURRENT_DAY;
            if (day >= 10) {
               int chance = 1 + RANDOM.nextInt(100);
               int chanceThreshold = day >= 30 ? 20 : 10;
               boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
               if (chance <= chanceThreshold && flag && mob instanceof Witch witch) {
                  int nearby = level.getEntitiesOfClass(Illusioner.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  boolean witchFromRaid = witch.canJoinRaid();
                  if (!witchFromRaid) {
                     mob.addTag("cannotJoinRaid");
                  }

                  mob.addTag("fromWitch");
                  mob.removeAllEffects();
                  mob.setCustomName(Component.literal("Bruja-Transmutada").withStyle(ChatFormatting.LIGHT_PURPLE));
                  convertToWithData(mob, EntityType.ILLUSIONER, false);
                  return;
               }
            }

            if (day >= 30) {
               if (mob instanceof Squid squid) {
                  int nearby = level.getEntitiesOfClass(Guardian.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  int nearbyElder = level.getEntitiesOfClass(ElderGuardian.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  if (nearbyElder >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  if (squid.getClass() != GlowSquid.class) {
                     mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                     mob.addTag("fromSquid");
                     mob.setCustomName(Component.literal("Calamar-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                     convertToWithData(mob, EntityType.GUARDIAN, false);
                     return;
                  }

                  boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                  if (flag) {
                     mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                     mob.addTag("fromSquid");
                     mob.setCustomName(Component.literal("Calamar-Brillante-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                     convertToWithData(mob, EntityType.GUARDIAN, false);
                     return;
                  }
               }

               if (mob instanceof Bat) {
                  int nearby = level.getEntitiesOfClass(Blaze.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                  mob.addTag("fromBat");
                  mob.setCustomName(Component.literal("Murciélago-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.BLAZE, false);
                  return;
               }
            }

            if (day >= 40) {
               if (mob instanceof Spider && mob.getClass() == Spider.class) {
                  int nearby = level.getEntitiesOfClass(CaveSpider.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.setCustomName(Component.literal("Araña-Transmutada").withStyle(ChatFormatting.DARK_GREEN));
                  mob.addTag("fromSpider");
                  convertToWithData(mob, EntityType.CAVE_SPIDER, false);
                  return;
               }

               if (mob instanceof Zombie && mob.getClass() == Zombie.class) {
                  int nearby = level.getEntitiesOfClass(Vindicator.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  int nearbyEvoker = level.getEntitiesOfClass(Evoker.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  if (nearbyEvoker >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  if (!mob.getTags().contains("cannotBeTransmuted")) {
                     mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, true));
                     mob.addTag("fromZombie");
                     mob.setCustomName(Component.literal("Zombie-Transmutado").withStyle(ChatFormatting.DARK_GREEN));
                     convertToWithData(mob, EntityType.VINDICATOR, false);
                     return;
                  }
               }

               if (mob instanceof Wolf && mob.getClass() == Wolf.class) {
                  int nearby = level.getEntitiesOfClass(Cat.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromWolf");
                  mob.setCustomName(Component.literal("Lobo-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.CAT, false);
                  return;
               }

               if (mob instanceof EnderMan && mob.level().dimension() == Level.NETHER) {
                  int nearby = level.getEntitiesOfClass(Creeper.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromEnderman");
                  mob.setCustomName(Component.literal("Enderman-Transmutado").withStyle(ChatFormatting.DARK_PURPLE));
                  convertToWithData(mob, EntityType.CREEPER, false);
                  return;
               }

               if (mob instanceof CustomRavager) {
                  int nearby = level.getEntitiesOfClass(CustomRavager.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 15) {
                     event.setSpawnCancelled(true);
                     return;
                  }
               }

               if (mob instanceof Armadillo) {
                  boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                  if (flag) {
                     int nearby = level.getEntitiesOfClass(Zoglin.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                     if (nearby >= 15) {
                        event.setSpawnCancelled(true);
                        return;
                     }

                     mob.addTag("fromArmadillo");
                     mob.setCustomName(Component.literal("Armadillo-Transmutado").withStyle(ChatFormatting.GOLD));
                     convertToWithData(mob, EntityType.ZOGLIN, false);
                     return;
                  }
               }
            }

            if (day >= 50) {
               if (mob instanceof CustomSilverfish) {
                  int nearby = level.getEntitiesOfClass(CustomSilverfish.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }
               }

               if (mob instanceof Salmon) {
                  int nearby = level.getEntitiesOfClass(Pufferfish.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 20) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromSalmon");
                  mob.setInvulnerable(true);
                  mob.setCustomName(Component.literal("Salmon-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.PUFFERFISH, false);
               }

               if (mob instanceof Pillager) {
                  int chance = 1 + RANDOM.nextInt(100);
                  if (chance <= 1) {
                     int nearby = level.getEntitiesOfClass(Vindicator.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                     if (nearby >= 20) {
                        event.setSpawnCancelled(true);
                        return;
                     }

                     mob.addTag("fromIllager");
                     mob.removeAllEffects();
                     mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                     convertToWithData(mob, EntityType.EVOKER, false);
                  }
               }
            }

            if (mob instanceof Cow || mob instanceof Pig || mob instanceof Sheep || mob instanceof MushroomCow) {
               if (day >= 40 && day < 50) {
                  int nearby = level.getEntitiesOfClass(Ravager.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 15) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromAnimal");
                  mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.RAVAGER, false);
               } else if (day >= 50) {
                  int nearby = level.getEntitiesOfClass(Ravager.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 15) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromAnimal");
                  mob.setCustomName(Component.literal("Ultra Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.RAVAGER, false);
               }
            }

            if (mob instanceof Chicken) {
               if (day >= 40 && day < 50) {
                  int nearby = level.getEntitiesOfClass(Ravager.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 15) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromAnimal");
                  mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.RAVAGER, false);
               } else if (day >= 50) {
                  int nearby = level.getEntitiesOfClass(Silverfish.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 10) {
                     event.setSpawnCancelled(true);
                     return;
                  }

                  mob.addTag("fromChicken");
                  mob.setCustomName(Component.literal("Pollo-Transmutado").withStyle(ChatFormatting.GOLD));
                  convertToWithData(mob, EntityType.SILVERFISH, false);
               }
            }

            if (mob instanceof CustomGiant) {
               if (day >= 50 && day < 60) {
                  int nearby = level.getEntitiesOfClass(CustomGiant.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 2) {
                     event.setSpawnCancelled(true);
                     return;
                  }
               } else if (day >= 60) {
                  int nearby = level.getEntitiesOfClass(CustomGiant.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                  if (nearby >= 5) {
                     event.setSpawnCancelled(true);
                     return;
                  }
               }
            }

            if (mob instanceof MagmaCube && day >= 25) {
               int nearby = level.getEntitiesOfClass(MagmaCube.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
               if (nearby >= 10) {
                  event.setSpawnCancelled(true);
                  return;
               }
            }

            if (mob instanceof Vindicator) {
               if (day >= 50 && day < 60) {
                  int chance = 1 + RANDOM.nextInt(100);
                  if (chance <= 1) {
                     int nearby = level.getEntitiesOfClass(Vindicator.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                     if (nearby >= 20) {
                        event.setSpawnCancelled(true);
                        return;
                     }

                     mob.addTag("fromIllager");
                     mob.removeAllEffects();
                     mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                     convertToWithData(mob, EntityType.EVOKER, false);
                  }
               } else if (day >= 60) {
                  int chance = 1 + RANDOM.nextInt(100);
                  if (chance <= 50) {
                     int nearby = level.getEntitiesOfClass(Vindicator.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
                     if (nearby >= 20) {
                        event.setSpawnCancelled(true);
                        return;
                     }

                     mob.addTag("fromIllager");
                     mob.removeAllEffects();
                     mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                     mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                     convertToWithData(mob, EntityType.EVOKER, false);
                  }
               }
            }

            if (mob instanceof Guardian && mob.getClass() != ElderGuardian.class && day >= 60) {
               int nearby = level.getEntitiesOfClass(ElderGuardian.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
               if (nearby >= 10) {
                  event.setSpawnCancelled(true);
                  return;
               }

               mob.addTag("fromGuardian");
               mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
               mob.setCustomName(Component.literal("Guardian-Transmutado").withStyle(ChatFormatting.GOLD));
               convertToWithData(mob, EntityType.ELDER_GUARDIAN, false);
            }

            if (mob instanceof Villager && day >= 60 && !mob.getTags().contains("fromZPiglin")) {
               int nearby = level.getEntitiesOfClass(Vindicator.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
               int nearbyEvoker = level.getEntitiesOfClass(Evoker.class, mob.getBoundingBox().inflate(256.0), e -> true).size();
               if (nearby >= 20) {
                  event.setSpawnCancelled(true);
                  return;
               }

               if (nearbyEvoker >= 20) {
                  event.setSpawnCancelled(true);
                  return;
               }

               mob.addTag("fromVillager");
               mob.setCustomName(Component.literal("Aldeano-Transmutado").withStyle(ChatFormatting.GOLD));
               convertToWithData(mob, EntityType.VINDICATOR, false);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSpawnWithPercentage(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (mob.level() instanceof ServerLevel level) {
               if (mob.getSpawnType() != MobSpawnType.NATURAL && mob.getSpawnType() != MobSpawnType.CHUNK_GENERATION) {
                  if (!event.loadedFromDisk()) {
                     if (!mob.getTags().contains("fromLevelJoin")) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 10) {
                           int chance = 1 + RANDOM.nextInt(100);
                           int chanceThreshold = day >= 30 ? 20 : 10;
                           boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                           if (chance <= chanceThreshold && flag && mob instanceof Witch witch) {
                              boolean witchFromRaid = witch.canJoinRaid();
                              if (!witchFromRaid) {
                                 mob.addTag("cannotJoinRaid");
                              }

                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromWitch");
                              mob.removeAllEffects();
                              mob.setCustomName(Component.literal("Bruja-Transmutada").withStyle(ChatFormatting.LIGHT_PURPLE));
                              convertToWithData(mob, EntityType.ILLUSIONER, false);
                              return;
                           }
                        }

                        if (day >= 50 && mob instanceof Pillager) {
                           int chance = 1 + RANDOM.nextInt(100);
                           if (chance <= 1) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromIllager");
                              mob.removeAllEffects();
                              mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.EVOKER, false);
                           }
                        }

                        if (mob instanceof Vindicator) {
                           if (day >= 50 && day < 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 1) {
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromIllager");
                                 mob.removeAllEffects();
                                 mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.EVOKER, false);
                              }
                           } else if (day >= 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 50) {
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromIllager");
                                 mob.removeAllEffects();
                                 mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                                 mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.EVOKER, false);
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
   public static void onSpawnNoPercentage(Post event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (!mob.touchingUnloadedChunk()) {
               if (mob.level() instanceof ServerLevel level) {
                  if (mob.getSpawnType() != MobSpawnType.NATURAL && mob.getSpawnType() != MobSpawnType.CHUNK_GENERATION) {
                     if (!mob.getTags().contains("fromLevelJoin")) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 30) {
                           if (mob instanceof Squid squid) {
                              if (squid.getClass() != GlowSquid.class) {
                                 mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromSquid");
                                 mob.setCustomName(Component.literal("Calamar-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                                 convertToWithData(mob, EntityType.GUARDIAN, false);
                                 return;
                              }

                              boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                              if (flag) {
                                 mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromSquid");
                                 mob.setCustomName(Component.literal("Calamar-Brillante-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                                 convertToWithData(mob, EntityType.GUARDIAN, false);
                                 return;
                              }
                           }

                           if (mob instanceof Bat) {
                              mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromBat");
                              mob.setCustomName(Component.literal("Murciélago-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.BLAZE, false);
                              return;
                           }
                        }

                        if (day >= 40) {
                           if (mob instanceof Spider && mob.getClass() == Spider.class) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromSpider");
                              mob.setCustomName(Component.literal("Araña-Transmutada").withStyle(ChatFormatting.DARK_GREEN));
                              convertToWithData(mob, EntityType.CAVE_SPIDER, false);
                              return;
                           }

                           if (mob instanceof Zombie && mob.getClass() == Zombie.class && !mob.getTags().contains("cannotBeTransmuted")) {
                              mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, true));
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromZombie");
                              mob.setCustomName(Component.literal("Zombie-Transmutado").withStyle(ChatFormatting.DARK_GREEN));
                              convertToWithData(mob, EntityType.VINDICATOR, false);
                              return;
                           }

                           if (mob instanceof Wolf && mob.getClass() == Wolf.class) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromWolf");
                              mob.setCustomName(Component.literal("Lobo-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.CAT, false);
                              return;
                           }

                           if (mob instanceof EnderMan && mob.level().dimension() == Level.NETHER) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromEnderman");
                              mob.setCustomName(Component.literal("Enderman-Transmutado").withStyle(ChatFormatting.DARK_PURPLE));
                              convertToWithData(mob, EntityType.CREEPER, false);
                              return;
                           }

                           if (mob instanceof Armadillo) {
                              boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                              if (flag) {
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromArmadillo");
                                 mob.setCustomName(Component.literal("Armadillo-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.ZOGLIN, false);
                                 return;
                              }
                           }
                        }

                        if (day >= 50 && mob instanceof Salmon) {
                           mob.addTag("fromLevelJoin");
                           mob.addTag("fromSalmon");
                           mob.setInvulnerable(true);
                           mob.setCustomName(Component.literal("Salmon-Transmutado").withStyle(ChatFormatting.GOLD));
                           convertToWithData(mob, EntityType.PUFFERFISH, false);
                        }

                        if (mob instanceof Cow || mob instanceof Pig || mob instanceof Sheep || mob instanceof MushroomCow) {
                           if (day >= 40 && day < 50) {
                              if (!mob.getTags().contains("fromZPiglin")) {
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromAnimal");
                                 mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.RAVAGER, false);
                              }
                           } else if (day >= 50 && !mob.getTags().contains("fromZPiglin")) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromAnimal");
                              mob.setCustomName(Component.literal("Ultra Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.RAVAGER, false);
                           }
                        }

                        if (mob instanceof Chicken) {
                           if (day >= 40 && day < 50) {
                              if (!mob.getTags().contains("fromZPiglin")) {
                                 mob.addTag("fromLevelJoin");
                                 mob.addTag("fromAnimal");
                                 mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.RAVAGER, false);
                              }
                           } else if (day >= 50 && !mob.getTags().contains("fromZPiglin")) {
                              mob.addTag("fromLevelJoin");
                              mob.addTag("fromChicken");
                              mob.setCustomName(Component.literal("Pollo-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.SILVERFISH, false);
                           }
                        }

                        if (mob instanceof Guardian && mob.getClass() != ElderGuardian.class && day >= 60) {
                           mob.addTag("fromLevelJoin");
                           mob.addTag("fromGuardian");
                           mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                           mob.setCustomName(Component.literal("Guardian-Transmutado").withStyle(ChatFormatting.GOLD));
                           convertToWithData(mob, EntityType.ELDER_GUARDIAN, false);
                        }

                        if (mob instanceof Villager && day >= 60 && !mob.getTags().contains("fromZPiglin")) {
                           mob.addTag("fromLevelJoin");
                           mob.addTag("fromVillager");
                           mob.setCustomName(Component.literal("Aldeano-Transmutado").withStyle(ChatFormatting.GOLD));
                           convertToWithData(mob, EntityType.VINDICATOR, false);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTickChunkGen(Post event) {
      if (event.getEntity() instanceof Mob mob) {
         if (!mob.level().isClientSide) {
            if (!mob.touchingUnloadedChunk()) {
               if (mob.tickCount % 20 == 0) {
                  if (mob.level() instanceof ServerLevel level) {
                     int var10 = DayGlobalCount.CURRENT_DAY;
                     if (mob.getSpawnType() == MobSpawnType.CHUNK_GENERATION && !mob.getTags().contains("fromChunkGen")) {
                        if (var10 >= 10) {
                           int chance = 1 + RANDOM.nextInt(100);
                           int chanceThreshold = var10 >= 30 ? 20 : 10;
                           boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                           if (chance <= chanceThreshold && flag && mob instanceof Witch witch) {
                              boolean witchFromRaid = witch.canJoinRaid();
                              if (!witchFromRaid) {
                                 mob.addTag("cannotJoinRaid");
                              }

                              mob.addTag("fromChunkGen");
                              mob.addTag("fromWitch");
                              mob.removeAllEffects();
                              mob.setCustomName(Component.literal("Bruja-Transmutada").withStyle(ChatFormatting.LIGHT_PURPLE));
                              convertToWithData(mob, EntityType.ILLUSIONER, false);
                              return;
                           }
                        }

                        if (var10 >= 30) {
                           if (mob instanceof Squid squid) {
                              if (squid.getClass() != GlowSquid.class) {
                                 mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromSquid");
                                 mob.setCustomName(Component.literal("Calamar-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                                 convertToWithData(mob, EntityType.GUARDIAN, false);
                                 return;
                              }

                              boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                              if (flag) {
                                 mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromSquid");
                                 mob.setCustomName(Component.literal("Calamar-Brillante-Transmutado").withStyle(ChatFormatting.DARK_AQUA));
                                 convertToWithData(mob, EntityType.GUARDIAN, false);
                                 return;
                              }
                           }

                           if (mob instanceof Bat) {
                              mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, true));
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromBat");
                              mob.setCustomName(Component.literal("Murciélago-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.BLAZE, false);
                              return;
                           }
                        }

                        if (var10 >= 40) {
                           if (mob instanceof Spider && mob.getClass() == Spider.class) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromSpider");
                              mob.setCustomName(Component.literal("Araña-Transmutada").withStyle(ChatFormatting.DARK_GREEN));
                              convertToWithData(mob, EntityType.CAVE_SPIDER, false);
                              return;
                           }

                           if (mob instanceof Zombie && mob.getClass() == Zombie.class && !mob.getTags().contains("cannotBeTransmuted")) {
                              mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, true));
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromZombie");
                              mob.setCustomName(Component.literal("Zombie-Transmutado").withStyle(ChatFormatting.DARK_GREEN));
                              convertToWithData(mob, EntityType.VINDICATOR, false);
                              return;
                           }

                           if (mob instanceof Wolf && mob.getClass() == Wolf.class) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromWolf");
                              mob.setCustomName(Component.literal("Lobo-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.CAT, false);
                              return;
                           }

                           if (mob instanceof EnderMan && mob.level().dimension() == Level.NETHER) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromEnderman");
                              mob.setCustomName(Component.literal("Enderman-Transmutado").withStyle(ChatFormatting.DARK_PURPLE));
                              convertToWithData(mob, EntityType.CREEPER, false);
                              return;
                           }

                           if (mob instanceof Armadillo) {
                              boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                              if (flag) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromArmadillo");
                                 mob.setCustomName(Component.literal("Armadillo-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.ZOGLIN, false);
                                 return;
                              }
                           }
                        }

                        if (var10 >= 50) {
                           if (mob instanceof Salmon) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromSalmon");
                              mob.setInvulnerable(true);
                              mob.setCustomName(Component.literal("Salmon-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.PUFFERFISH, false);
                           }

                           if (mob instanceof Pillager) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 1) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromIllager");
                                 mob.removeAllEffects();
                                 mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.EVOKER, false);
                              }
                           }
                        }

                        if (mob instanceof Cow || mob instanceof Pig || mob instanceof Sheep || mob instanceof MushroomCow) {
                           if (var10 >= 40 && var10 < 50) {
                              if (!mob.getTags().contains("fromZPiglin")) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromAnimal");
                                 mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.RAVAGER, false);
                              }
                           } else if (var10 >= 50 && !mob.getTags().contains("fromZPiglin")) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromAnimal");
                              mob.setCustomName(Component.literal("Ultra Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.RAVAGER, false);
                           }
                        }

                        if (mob instanceof Chicken) {
                           if (var10 >= 40 && var10 < 50) {
                              if (!mob.getTags().contains("fromZPiglin")) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromAnimal");
                                 mob.setCustomName(Component.literal("Animal-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.RAVAGER, false);
                              }
                           } else if (var10 >= 50 && !mob.getTags().contains("fromZPiglin")) {
                              mob.addTag("fromChunkGen");
                              mob.addTag("fromChicken");
                              mob.setCustomName(Component.literal("Pollo-Transmutado").withStyle(ChatFormatting.GOLD));
                              convertToWithData(mob, EntityType.SILVERFISH, false);
                           }
                        }

                        if (mob instanceof Vindicator) {
                           if (var10 >= 50 && var10 < 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 1) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromIllager");
                                 mob.removeAllEffects();
                                 mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.EVOKER, false);
                              }
                           } else if (var10 >= 60) {
                              int chance = 1 + RANDOM.nextInt(100);
                              if (chance <= 50) {
                                 mob.addTag("fromChunkGen");
                                 mob.addTag("fromIllager");
                                 mob.removeAllEffects();
                                 mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                                 mob.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                                 convertToWithData(mob, EntityType.EVOKER, false);
                              }
                           }
                        }

                        if (mob instanceof Guardian && mob.getClass() != ElderGuardian.class && var10 >= 60) {
                           mob.addTag("fromChunkGen");
                           mob.addTag("fromGuardian");
                           mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, true));
                           mob.setCustomName(Component.literal("Guardian-Transmutado").withStyle(ChatFormatting.GOLD));
                           convertToWithData(mob, EntityType.ELDER_GUARDIAN, false);
                        }

                        if (mob instanceof Villager && var10 >= 60 && !mob.getTags().contains("fromZPiglin")) {
                           mob.addTag("fromChunkGen");
                           mob.addTag("fromVillager");
                           mob.setCustomName(Component.literal("Aldeano-Transmutado").withStyle(ChatFormatting.GOLD));
                           convertToWithData(mob, EntityType.VINDICATOR, false);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static <T extends Mob> void convertToWithData(Mob original, EntityType<T> type, boolean copyLoot) {
      if (!original.isRemoved()) {
         T transmuted = (T)type.create(original.level());
         int day = DayGlobalCount.CURRENT_DAY;
         if (transmuted != null) {
            transmuted.copyPosition(original);
            transmuted.finalizeSpawn(
               (ServerLevelAccessor)original.level(), original.level().getCurrentDifficultyAt(original.blockPosition()), MobSpawnType.CONVERSION, null
            );
            transmuted.setBaby(original.isBaby());
            transmuted.setNoAi(original.isNoAi());

            for (MobEffectInstance effect : original.getActiveEffects()) {
               transmuted.addEffect(new MobEffectInstance(effect));
            }

            for (String tag : original.getTags()) {
               transmuted.addTag(tag);
            }

            if (original.hasCustomName()) {
               transmuted.setCustomName(original.getCustomName());
               transmuted.setCustomNameVisible(original.isCustomNameVisible());
            }

            if (original.isPersistenceRequired()) {
               transmuted.setPersistenceRequired();
            }

            transmuted.setInvulnerable(original.isInvulnerable());
            if (copyLoot) {
               transmuted.setCanPickUpLoot(original.canPickUpLoot());

               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  ItemStack stack = original.getItemBySlot(slot);
                  if (!stack.isEmpty()) {
                     transmuted.setItemSlot(slot, stack.copyAndClear());
                  }
               }
            }

            transmuted.getPersistentData().merge(original.getPersistentData());
            if (original.isPassenger()) {
               Entity vehicle = original.getVehicle();
               original.stopRiding();
               assert vehicle != null;
               transmuted.startRiding(vehicle, true);
            }

            if (original.isVehicle()) {
               List<Entity> passengers = List.copyOf(original.getPassengers());
               original.ejectPassengers();

               for (Entity passenger : passengers) {
                  passenger.startRiding(transmuted, true);
               }
            }

            if (transmuted instanceof Zoglin zoglin) {
               float attackKnockback = day >= 50 ? 8.0F : 2.0F + RANDOM.nextFloat(1.0F);
               Objects.requireNonNull(zoglin.getAttribute(Attributes.ATTACK_KNOCKBACK)).setBaseValue(attackKnockback);
               Objects.requireNonNull(zoglin.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(zoglin.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 2.0);
               if (day >= 50) {
                  Objects.requireNonNull(zoglin.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(80.0);
                  zoglin.setHealth(zoglin.getMaxHealth());
               }
            }

            if (original instanceof Spider && type == EntityType.CAVE_SPIDER) {
               int chance = 1 + RANDOM.nextInt(100);
               if (chance <= 98) {
                  SkeletonRiderHelper.spawnRandomSkeleton((ServerLevel)original.level(), transmuted, day);
               } else {
                  ModulesSetupHelper.spawnDeathModule((ServerLevel)original.level(), transmuted);
               }
            }

            if (transmuted instanceof Illusioner illusioner) {
               if (illusioner.getTags().contains("cannotJoinRaid")) {
                  illusioner.setCanJoinRaid(false);
                  illusioner.setCurrentRaid(null);
                  illusioner.setWave(0);
                  illusioner.setTicksOutsideRaid(0);
               }

               if (day >= 30) {
                  RegistryAccess registryAccess = illusioner.level().registryAccess();
                  Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
                  ItemStack bow = new ItemStack(Items.BOW);
                  bow.enchant(power, 20);
                  illusioner.setItemSlot(EquipmentSlot.MAINHAND, bow);
                  Objects.requireNonNull(illusioner.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(illusioner.getMaxHealth() * 2.0F);
                  illusioner.setHealth(illusioner.getMaxHealth());
                  illusioner.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                  illusioner.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                  illusioner.addTag("strongIllusioner");
               }
            }

            if (transmuted instanceof Vindicator vindicator) {
               Objects.requireNonNull(vindicator.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(vindicator.getMaxHealth() * 2.0F);
               vindicator.setHealth(vindicator.getMaxHealth());
               vindicator.setCanJoinRaid(false);
               vindicator.setCurrentRaid(null);
               vindicator.setWave(0);
               vindicator.setTicksOutsideRaid(0);
               if (day >= 50 && day < 60) {
                  if (transmuted.getTags().contains("fromZombie")) {
                     int chance = 1 + RANDOM.nextInt(100);
                     if (chance <= 1) {
                        vindicator.addTag("fromIllager");
                        vindicator.removeTag("fromZombie");
                        vindicator.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                        vindicator.removeAllEffects();
                        convertToWithData(vindicator, EntityType.EVOKER, false);
                     }
                  }
               } else if (day >= 60 && transmuted.getTags().contains("fromZombie")) {
                  int chance = 1 + RANDOM.nextInt(100);
                  if (chance <= 50) {
                     vindicator.addTag("fromIllager");
                     vindicator.removeTag("fromZombie");
                     vindicator.setCustomName(Component.literal("Illager-Transmutado").withStyle(ChatFormatting.GOLD));
                     vindicator.removeAllEffects();
                     vindicator.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
                     convertToWithData(vindicator, EntityType.EVOKER, false);
                  }
               }
            }

            if (transmuted instanceof Evoker evoker && transmuted.getTags().contains("fromIllager")) {
               evoker.setCanJoinRaid(false);
               evoker.setCurrentRaid(null);
               evoker.setWave(0);
               evoker.setTicksOutsideRaid(0);
            }

            if (transmuted instanceof Ravager ravager && transmuted.getTags().contains("fromAnimal")) {
               ravager.setCanJoinRaid(false);
               ravager.setCurrentRaid(null);
               ravager.setWave(0);
               ravager.setTicksOutsideRaid(0);
            }

            if (transmuted instanceof Guardian guardian && day >= 60 && transmuted.getTags().contains("fromSquid")) {
               guardian.addTag("fromGuardian");
               guardian.removeTag("fromSquid");
               guardian.setCustomName(Component.literal("Guardian-Transmutado").withStyle(ChatFormatting.GOLD));
               convertToWithData(guardian, EntityType.ELDER_GUARDIAN, false);
            }

            original.level().addFreshEntity(transmuted);
            Objects.requireNonNull(original.level().getServer())
               .tell(new TickTask(Objects.requireNonNull(original.level().getServer()).getTickCount(), () -> original.remove(RemovalReason.DISCARDED)));
         }
      }
   }
}
