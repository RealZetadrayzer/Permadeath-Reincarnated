package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.entities.CustomGiant;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.timers.ShulkerDoubledRatesGlobalState;

@EventBusSubscriber
public class MobDrops {
   @SubscribeEvent
   public static void removeDrops(LivingDropsEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity instanceof IronGolem
               || entity instanceof ZombifiedPiglin
               || entity instanceof Ghast
               || entity instanceof Guardian
               || entity instanceof EnderMan
               || entity instanceof Witch
               || entity instanceof WitherSkeleton
               || entity instanceof Evoker
               || entity instanceof Phantom
               || entity instanceof Slime
               || entity instanceof Drowned | entity instanceof Blaze) {
               event.getDrops().clear();
            }
         }
      }
   }

   @SubscribeEvent
   public static void removeDropsShulker(LivingDropsEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity instanceof Shulker) {
               event.getDrops().clear();
            }
         }
      }
   }

   @SubscribeEvent
   public static void ravagerDrops(LivingDropsEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof Ravager) {
         if (!entity.level().isClientSide) {
            if (!entity.getTags().contains("fromZPiglin")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  int currentDay = DayGlobalCount.CURRENT_DAY;
                  int chancePercent = currentDay >= 25 ? 20 : (currentDay >= 20 ? 1 : 0);
                  if (chancePercent > 0) {
                     int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                     if (chance <= chancePercent) {
                        ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
                        ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), totem);
                        if (!(drop.level() instanceof ServerLevel level)) {
                           return;
                        }

                        Scoreboard scoreboard = level.getScoreboard();
                        PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                        if (team == null) {
                           team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                           team.setColor(ChatFormatting.YELLOW);
                        }

                        scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                        drop.setGlowingTag(true);
                        event.getDrops().add(drop);
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_RAVAGER_TOTEM_ID);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void shulkerDrops(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.getTags().contains("dropsShulkerShells")) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               int var14 = DayGlobalCount.CURRENT_DAY;
               if (entity instanceof Shulker shulker) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (var14 >= 30 && var14 < 40) {
                     boolean foughtDemon = player.getTags().contains("foughtDemon");
                     boolean eventOn = ShulkerDoubledRatesGlobalState.CURRENT_STATE == 1;
                     int finalChance;
                     if (eventOn) {
                        finalChance = foughtDemon ? 40 : 20;
                     } else {
                        finalChance = foughtDemon ? 20 : 0;
                     }

                     if (chance <= finalChance) {
                        ItemStack shell = new ItemStack(Items.SHULKER_SHELL, 1);
                        ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), shell);
                        if (!(drop.level() instanceof ServerLevel level)) {
                           return;
                        }

                        Scoreboard scoreboard = level.getScoreboard();
                        PlayerTeam team = scoreboard.getPlayerTeam("shulkerShellGlowingColor");
                        if (team == null) {
                           team = scoreboard.addPlayerTeam("shulkerShellGlowingColor");
                           team.setColor(ChatFormatting.LIGHT_PURPLE);
                        }

                        scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                        drop.setGlowingTag(true);
                        drop.setInvulnerable(true);
                        shulker.level().addFreshEntity(drop);
                     }
                  } else if (var14 >= 40) {
                     boolean eventOn = ShulkerDoubledRatesGlobalState.CURRENT_STATE == 1;
                     int finalChance;
                     if (eventOn) {
                        finalChance = 4;
                     } else {
                        finalChance = 2;
                     }

                     if (chance <= finalChance) {
                        ItemStack shell = new ItemStack(Items.SHULKER_SHELL, 1);
                        ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), shell);
                        if (!(drop.level() instanceof ServerLevel level)) {
                           return;
                        }

                        Scoreboard scoreboard = level.getScoreboard();
                        PlayerTeam team = scoreboard.getPlayerTeam("shulkerShellGlowingColor");
                        if (team == null) {
                           team = scoreboard.addPlayerTeam("shulkerShellGlowingColor");
                           team.setColor(ChatFormatting.LIGHT_PURPLE);
                        }

                        scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                        drop.setGlowingTag(true);
                        drop.setInvulnerable(true);
                        shulker.level().addFreshEntity(drop);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void mobEssenceDrops(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (event.getSource().getEntity() instanceof ServerPlayer) {
            int currentDay = DayGlobalCount.CURRENT_DAY;
            if ((Boolean)PermadeathConfig.DISABLE_REWORK.get()) {
               if (entity instanceof CaveSpider spider) {
                  if (!spider.getTags().contains("ultraAraña")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30 && chance <= 5) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_HELMET.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("netheritePiecesGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("netheritePiecesGlowingColor");
                        team.setColor(ChatFormatting.LIGHT_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     spider.level().addFreshEntity(drop);
                  }
               }

               if (entity instanceof Slime slimeOrmagma) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30) {
                     if (slimeOrmagma.getClass() == MagmaCube.class) {
                        if (!slimeOrmagma.getTags().contains("gigaMagma")) {
                           return;
                        }

                        if (slimeOrmagma.getSize() >= 2 && chance <= 2) {
                           ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS.get(), 1);
                           ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                           if (!(drop.level() instanceof ServerLevel level)) {
                              return;
                           }

                           Scoreboard scoreboard = level.getScoreboard();
                           PlayerTeam team = scoreboard.getPlayerTeam("netheritePiecesGlowingColor");
                           if (team == null) {
                              team = scoreboard.addPlayerTeam("netheritePiecesGlowingColor");
                              team.setColor(ChatFormatting.LIGHT_PURPLE);
                           }

                           scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                           drop.setGlowingTag(true);
                           slimeOrmagma.level().addFreshEntity(drop);
                        }
                     } else {
                        if (!slimeOrmagma.getTags().contains("gigaSlime")) {
                           return;
                        }

                        if (slimeOrmagma.getSize() >= 2) {
                           return;
                        }

                        if (chance <= 2) {
                           ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get(), 1);
                           ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                           if (!(drop.level() instanceof ServerLevel level)) {
                              return;
                           }

                           Scoreboard scoreboard = level.getScoreboard();
                           PlayerTeam team = scoreboard.getPlayerTeam("netheritePiecesGlowingColor");
                           if (team == null) {
                              team = scoreboard.addPlayerTeam("netheritePiecesGlowingColor");
                              team.setColor(ChatFormatting.LIGHT_PURPLE);
                           }

                           scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                           drop.setGlowingTag(true);
                           slimeOrmagma.level().addFreshEntity(drop);
                        }
                     }
                  }
               }

               if (entity instanceof Ghast ghast) {
                  if (!ghast.getTags().contains("ghastDemoniaco")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30 && chance <= 10) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("netheritePiecesGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("netheritePiecesGlowingColor");
                        team.setColor(ChatFormatting.LIGHT_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     ghast.level().addFreshEntity(drop);
                  }
               }
            } else {
               if (entity instanceof CaveSpider spider) {
                  if (!spider.getTags().contains("ultraAraña")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30 && chance <= 5) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_SPIDER_ESSENCE.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("spiderEssenceGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("spiderEssenceGlowingColor");
                        team.setColor(ChatFormatting.DARK_GREEN);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     drop.setInvulnerable(true);
                     spider.level().addFreshEntity(drop);
                  }
               }

               if (entity instanceof Slime slimeOrmagma) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30) {
                     if (slimeOrmagma.getClass() == MagmaCube.class) {
                        if (!slimeOrmagma.getTags().contains("gigaMagma")) {
                           return;
                        }

                        if (slimeOrmagma.getSize() <= 2 && chance <= 2) {
                           ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_MAGMA_ESSENCE.get(), 1);
                           ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                           if (!(drop.level() instanceof ServerLevel level)) {
                              return;
                           }

                           Scoreboard scoreboard = level.getScoreboard();
                           PlayerTeam team = scoreboard.getPlayerTeam("magmaEssenceGlowingColor");
                           if (team == null) {
                              team = scoreboard.addPlayerTeam("magmaEssenceGlowingColor");
                              team.setColor(ChatFormatting.RED);
                           }

                           scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                           drop.setGlowingTag(true);
                           drop.setInvulnerable(true);
                           slimeOrmagma.level().addFreshEntity(drop);
                        }
                     } else {
                        if (!slimeOrmagma.getTags().contains("gigaSlime")) {
                           return;
                        }

                        if (slimeOrmagma.getSize() <= 2 && chance <= 2) {
                           ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_SLIME_ESSENCE.get(), 1);
                           ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                           if (!(drop.level() instanceof ServerLevel level)) {
                              return;
                           }

                           Scoreboard scoreboard = level.getScoreboard();
                           PlayerTeam team = scoreboard.getPlayerTeam("slimeEssenceGlowingColor");
                           if (team == null) {
                              team = scoreboard.addPlayerTeam("slimeEssenceGlowingColor");
                              team.setColor(ChatFormatting.GREEN);
                           }

                           scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                           drop.setGlowingTag(true);
                           drop.setInvulnerable(true);
                           slimeOrmagma.level().addFreshEntity(drop);
                        }
                     }
                  }
               }

               if (entity instanceof Ghast ghast) {
                  if (!ghast.getTags().contains("ghastDemoniaco")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 25 && currentDay < 30 && chance <= 10) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_GHAST_ESSENCE.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("ghastEssenceGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("ghastEssenceGlowingColor");
                        team.setColor(ChatFormatting.GOLD);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     drop.setInvulnerable(true);
                     ghast.level().addFreshEntity(drop);
                  }
               }

               if (entity instanceof WitherBoss witherBoss) {
                  if (!witherBoss.getTags().contains("miniWither")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 50 && chance <= 20) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_WITHER_ESSENCE.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("witherEssenceGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("witherEssenceGlowingColor");
                        team.setColor(ChatFormatting.DARK_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     drop.setInvulnerable(true);
                     witherBoss.level().addFreshEntity(drop);
                  }
               }

               if (entity instanceof Shulker shulker) {
                  if (!shulker.getTags().contains("shulkerVoid")) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 55 && currentDay < 60 && chance <= 10) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_VOID_ESSENCE.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("epicItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("epicItemGlowingColor");
                        team.setColor(ChatFormatting.DARK_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     drop.setInvulnerable(true);
                     shulker.level().addFreshEntity(drop);
                  }
               }

               if (entity instanceof Evoker evoker) {
                  if (!evoker.canJoinRaid()) {
                     return;
                  }

                  if (!evoker.hasRaid()) {
                     return;
                  }

                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (currentDay >= 55 && currentDay < 60 && chance <= 5) {
                     ItemStack essence = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_IMMORTALITY_ESSENCE.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), essence);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                        team.setColor(ChatFormatting.YELLOW);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     drop.setInvulnerable(true);
                     evoker.level().addFreshEntity(drop);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void mobPermaDrops(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (event.getSource().getEntity() instanceof ServerPlayer) {
            int currentDay = DayGlobalCount.CURRENT_DAY;
            if (entity instanceof CustomGiant giant) {
               if (!giant.getTags().contains("giantZombie")) {
                  return;
               }

               if (currentDay >= 50 && currentDay < 60) {
                  RegistryAccess access = entity.level().registryAccess();
                  Holder<Enchantment> power = access.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
                  ItemStack bow = new ItemStack(Items.BOW, 1);
                  bow.enchant(power, 10);
                  bow.set(
                     DataComponents.CUSTOM_NAME, Component.literal("Arco del Gigante").withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withItalic(false))
                  );
                  ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), bow);
                  if (!(drop.level() instanceof ServerLevel level)) {
                     return;
                  }

                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                     team.setColor(ChatFormatting.YELLOW);
                  }

                  scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                  drop.setGlowingTag(true);
                  giant.level().addFreshEntity(drop);
               }
            }

            if (entity instanceof WitherSkeleton witherSkeleton) {
               if (!witherSkeleton.getTags().contains("witherEmperador")) {
                  return;
               }

               if (currentDay >= 50 && currentDay < 60) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (chance <= 50) {
                     ItemStack netheriteSword = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get(), 1);
                     netheriteSword.set(
                        DataComponents.CUSTOM_NAME,
                        Component.literal("Espada del Emperador").withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withItalic(false))
                     );
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), netheriteSword);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                        team.setColor(ChatFormatting.YELLOW);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     witherSkeleton.level().addFreshEntity(drop);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void mobCustomPermaDrops(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (event.getSource().getEntity() instanceof ServerPlayer player) {
            int var14 = DayGlobalCount.CURRENT_DAY;
            if (entity instanceof Warden warden) {
               if (!warden.getTags().contains("superWarden")) {
                  return;
               }

               if (var14 >= 25) {
                  boolean isDefinitive = warden.getTags().contains("definitiveWarden");
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  int chancePercent = isDefinitive ? 20 : 100;
                  if (chance <= chancePercent) {
                     int count = 10 + RandomUtil.RANDOM.nextInt(11);
                     ItemStack echoShard = new ItemStack(Items.ECHO_SHARD, count);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), echoShard);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("epicItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("epicItemGlowingColor");
                        team.setColor(ChatFormatting.DARK_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     warden.level().addFreshEntity(drop);
                  }
               }
            }

            if (entity instanceof PiglinBrute piglinBrute) {
               if (!piglinBrute.getTags().contains("piglinCommander")) {
                  return;
               }

               if (var14 >= 25) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  int chanceThreshold = var14 >= 40 ? 5 : 1;
                  if (chance <= chanceThreshold) {
                     ItemStack tcnd = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_TCND.get(), 1);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), tcnd);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                        team.setColor(ChatFormatting.YELLOW);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setGlowingTag(true);
                     piglinBrute.level().addFreshEntity(drop);
                  }
               }
            }

            if (entity instanceof WitherBoss witherBoss) {
               if (!witherBoss.getTags().contains("fromPlayer")) {
                  return;
               }

               if (!witherBoss.getTags().contains("ultraWither")) {
                  return;
               }

               if (var14 >= 40) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (chance <= 50) {
                     ItemStack diamondBlocks = new ItemStack(Items.DIAMOND_BLOCK, 4);
                     ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), diamondBlocks);
                     if (!(drop.level() instanceof ServerLevel level)) {
                        return;
                     }

                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("epicItemGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("epicItemGlowingColor");
                        team.setColor(ChatFormatting.DARK_PURPLE);
                     }

                     scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                     drop.setInvulnerable(true);
                     drop.setGlowingTag(true);
                     witherBoss.level().addFreshEntity(drop);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void jessTrioDrops(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.getTags().contains("fromZPiglin")) {
            if (event.getSource().getEntity() instanceof ServerPlayer) {
               int currentDay = DayGlobalCount.CURRENT_DAY;
               int chance = 1 + RandomUtil.RANDOM.nextInt(100);
               int chanceThreshold = currentDay >= 60 ? 0 : (currentDay >= 50 ? 33 : (currentDay >= 40 ? 100 : 0));
               if (entity instanceof Villager villager && currentDay >= 40 && chance <= chanceThreshold) {
                  ItemStack totem = new ItemStack(Items.GOLDEN_APPLE, 2);
                  ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), totem);
                  if (!(drop.level() instanceof ServerLevel level)) {
                     return;
                  }

                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                     team.setColor(ChatFormatting.YELLOW);
                  }

                  scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                  drop.setGlowingTag(true);
                  villager.level().addFreshEntity(drop);
               }

               if (entity instanceof ZombifiedPiglin zombifiedPiglin
                  && zombifiedPiglin.getTags().contains("slavePiglin")
                  && currentDay >= 40
                  && chance <= chanceThreshold) {
                  ItemStack totem = new ItemStack(Items.GOLD_INGOT, 32);
                  ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), totem);
                  if (!(drop.level() instanceof ServerLevel level)) {
                     return;
                  }

                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                     team.setColor(ChatFormatting.YELLOW);
                  }

                  scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                  drop.setGlowingTag(true);
                  zombifiedPiglin.level().addFreshEntity(drop);
               }

               if (entity instanceof CustomRavager ravager && currentDay >= 40 && chance <= chanceThreshold) {
                  ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
                  ItemEntity drop = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), totem);
                  if (!(drop.level() instanceof ServerLevel level)) {
                     return;
                  }

                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("rareItemGlowingColor");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("rareItemGlowingColor");
                     team.setColor(ChatFormatting.YELLOW);
                  }

                  scoreboard.addPlayerToTeam(drop.getStringUUID(), team);
                  drop.setGlowingTag(true);
                  ravager.level().addFreshEntity(drop);
               }
            }
         }
      }
   }
}
