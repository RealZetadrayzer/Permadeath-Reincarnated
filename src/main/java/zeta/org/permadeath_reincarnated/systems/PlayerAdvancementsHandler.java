package zeta.org.permadeath_reincarnated.systems;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.entities.CustomGiant;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.entities.CustomZombieHorse;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;
import zeta.org.permadeath_reincarnated.mobs.PassiveHostileMobs;
import zeta.org.permadeath_reincarnated.systems.attachments.DesperateMeasuresAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.DesperateMeasuresAttachmentHelper;

@EventBusSubscriber
public class PlayerAdvancementsHandler {
   public static final ResourceLocation ROOT_DAY_60_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_60");
   public static final ResourceLocation PLAYER_NEAR_SINGULARITY_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_near_singularity"
   );
   public static final ResourceLocation UNIVERSAL_CAT_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/universal_cat_kill"
   );
   public static final ResourceLocation PLAYER_UNLOCK_LOCKED_SLOTS_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_unlock_locked_slots"
   );
   public static final ResourceLocation QUANTUM_CREEPER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/quantum_creeper_kill"
   );
   public static final ResourceLocation PLAYER_DROWNING_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_drowning_damage_received"
   );
   public static final ResourceLocation PLAYER_CONSUME_EXTRA_HYPER_GOLDEN_APPLE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_consume_extra_hyper_golden_apple"
   );
   public static final ResourceLocation PLAYER_FALL_DAMAGE_RECEIVED_DAY_60_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_fall_damage_received_day_60"
   );
   public static final ResourceLocation PLAYER_BLOCK_BREAK_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_block_break_damage_received"
   );
   public static final ResourceLocation PLAYER_CRAFT_LIFE_ORB_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_craft_life_orb"
   );
   public static final ResourceLocation CLASS_SKELETON_KILL_DAY_60_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/class_skeleton_kill_day_60"
   );
   public static final ResourceLocation PLAYER_VEX_EXPLOSION_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_vex_explosion_damage_received"
   );
   public static final ResourceLocation BLAZE_KILL_DAY_60_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/blaze_kill_day_60"
   );
   public static final ResourceLocation PLAYER_PUFFERFISH_POISON_25_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_pufferfish_poison_25"
   );
   public static final ResourceLocation PLAYER_LIGHTNING_PROBABILITY_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_lightning_probability"
   );
   public static final ResourceLocation PLAYER_INSTANT_DAMAGE_PROBABILITY_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_instant_damage_probability"
   );
   public static final ResourceLocation PLAYER_TRIDENT_BREAK_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_trident_break"
   );
   public static final ResourceLocation PLAYER_TRY_OPEN_BEACON_DAY_60_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_60_advancements/player_try_open_beacon_day_60"
   );
   public static final ResourceLocation ROOT_DAY_55_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_55");
   public static final ResourceLocation PLAYER_CRAFT_BEG_SHIELD_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_craft_beg_shield"
   );
   public static final ResourceLocation PLAYER_GET_VOID_ESSENCE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_get_void_essence"
   );
   public static final ResourceLocation PLAYER_GET_IMMORTALITY_ESSENCE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_get_immortality_essence"
   );
   public static final ResourceLocation PLAYER_IMPERIAL_STRENGTH_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_imperial_strength"
   );
   public static final ResourceLocation PLAYER_CONDUIT_FAILED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_conduit_failed"
   );
   public static final ResourceLocation PLAYER_PHANTOM_ROULETTE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_phantom_roulette"
   );
   public static final ResourceLocation PLAYER_GRAVITY_FALLS_FAIL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_gravity_falls_fail"
   );
   public static final ResourceLocation VOID_SHOCK_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/void_shock_kill"
   );
   public static final ResourceLocation PLAYER_LACK_OF_GUNPOWDER_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_lack_of_gunpowder"
   );
   public static final ResourceLocation PLAYER_PERSONAL_SPACE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_personal_space"
   );
   public static final ResourceLocation PLAYER_INJURED_FEET_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_injured_feet"
   );
   public static final ResourceLocation PLAYER_REINCARNATED_SURVIVOR_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_reincarnated_survivor"
   );
   public static final ResourceLocation PLAYER_GET_END_ORB_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_get_end_orb"
   );
   public static final ResourceLocation PLAYER_LEGENDARY_HERO_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_55_advancements/player_legendary_hero"
   );
   public static final ResourceLocation ROOT_DAY_50_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_50");
   public static final ResourceLocation PLAYER_COVER_ME_WITH_HELL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_cover_me_with_hell"
   );
   public static final ResourceLocation PLAYER_EFFECTS_GONE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_effects_gone"
   );
   public static final ResourceLocation PLAYER_ZOGLIN_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_zoglin_damage_received"
   );
   public static final ResourceLocation PLAYER_DARKNESS_FISH_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_darkness_fish"
   );
   public static final ResourceLocation PLAYER_ENTER_BEGINNING_PORTAL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_enter_beginning_portal"
   );
   public static final ResourceLocation WITHER_KILL_NEW_BEGINNING_AGAIN_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/wither_kill_new_beginning_again"
   );
   public static final ResourceLocation MINI_WITHER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/mini_wither_kill"
   );
   public static final ResourceLocation QUANTUM_KILL_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "day_50_advancements/quantum_kill");
   public static final ResourceLocation GIANT_ZOMBIE_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/giant_zombie_kill"
   );
   public static final ResourceLocation WITHER_EMPEROR_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/wither_emperor_kill"
   );
   public static final ResourceLocation PLAYER_GET_NETHERITE_SWORD_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_get_netherite_sword"
   );
   public static final ResourceLocation PLAYER_GET_NETHERITE_TOOL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_get_netherite_tool"
   );
   public static final ResourceLocation PLAYER_GET_INFERNAL_NETHERITE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_get_infernal_netherite"
   );
   public static final ResourceLocation PLAYER_USE_INVISIBILITY_POTION_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_50_advancements/player_use_invisibility_potion"
   );
   private static final ResourceLocation ROOT_DAY_45_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_45");
   public static final ResourceLocation PLAYER_GOAT_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_goat_damage_received"
   );
   private static final ResourceLocation PLAYER_FIRE_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_fire_damage_received"
   );
   public static final ResourceLocation PLAYER_BOMB_PARROT_TRIGGERED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_bomb_parrot_triggered"
   );
   private static final ResourceLocation PLAYER_FALL_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_fall_damage_received"
   );
   public static final ResourceLocation PLAYER_BEG_RES_POTION_CONSUMED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_beg_res_potion_consumed"
   );
   private static final ResourceLocation PLAYER_DARK_IDOL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_dark_idol"
   );
   private static final ResourceLocation PLAYER_PILLAGER_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_45_advancements/player_pillager_damage_received"
   );
   public static final ResourceLocation ROOT_DAY_40_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_40");
   public static final ResourceLocation PLAYER_NEAR_SUPERNOVA_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_near_supernova"
   );
   private static final ResourceLocation PLAYER_GET_END_RELIC_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_get_end_relic"
   );
   private static final ResourceLocation ZOMBIFIED_PIGLIN_JOCKEY_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/zombified_piglin_jockey_kill"
   );
   public static final ResourceLocation PLAYER_TOW_OR_MORE_ENDERMITE_SUMMON_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_tow_or_more_endermite_summon"
   );
   private static final ResourceLocation FLOATING_DEMON_GHAST_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/floating_demon_ghast_kill"
   );
   private static final ResourceLocation SHULKER_KILL_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "day_40_advancements/shulker_kill");
   private static final ResourceLocation PLAYER_HIT_ANOTHER_PLAYER_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_hit_another_player"
   );
   public static final ResourceLocation PLAYER_FIND_PROTECTOR_AXOLOTL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_find_protector_axolotl"
   );
   public static final ResourceLocation HUSK_INANITION_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/husk_inanition"
   );
   public static final ResourceLocation PLAYER_LIGHTNING_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_lightning_damage_received"
   );
   private static final ResourceLocation ULTRA_REINCARNATED_WITHER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/ultra_reincarnated_wither_kill"
   );
   private static final ResourceLocation ZOMBIE_DEATH_RIDER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/zombie_death_rider_kill"
   );
   private static final ResourceLocation IMPOSSIBLE_WITCH_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/impossible_witch_kill"
   );
   private static final ResourceLocation DEPTH_DOLPHIN_RIDER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/depth_dolphin_rider_kill"
   );
   private static final ResourceLocation DEFINITIVE_WARDEN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/definitive_warden_kill"
   );
   public static final ResourceLocation PLAYER_CONSUME_SUPER_GOLDEN_APPLE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_consume_super_golden_apple"
   );
   public static final ResourceLocation PLAYER_CONSUME_HYPER_GOLDEN_APPLE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_consume_hyper_golden_apple"
   );
   private static final ResourceLocation ZOMBIE_HORSE_AGAIN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/zombie_horse_again_kill"
   );
   private static final ResourceLocation TRANSMUTED_ANIMAL_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/transmuted_animal_kill"
   );
   public static final ResourceLocation PLAYER_GET_BLINDNESS_UNDER_RAIN_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_get_blindness_under_rain"
   );
   private static final ResourceLocation TRANSMUTED_ARMADILLO_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/transmuted_armadillo_kill"
   );
   private static final ResourceLocation PIGLIN_CLASS_AGAIN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/piglin_class_again_kill"
   );
   private static final ResourceLocation PLAYER_FIND_BEGINNING_PORTAL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_find_beginning_portal"
   );
   public static final ResourceLocation PLAYER_FIND_DEATH_MODULE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_find_death_module"
   );
   public static final ResourceLocation PLAYER_TURTLE_NO_OXYGEN_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_40_advancements/player_turtle_no_oxygen"
   );
   public static final ResourceLocation ROOT_DAY_30_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_30");
   public static final ResourceLocation PLAYER_MEET_DEMON_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_meet_demon"
   );
   private static final ResourceLocation CLASS_SKELETON_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/class_skeleton_kill"
   );
   private static final ResourceLocation CHARGED_CREEPER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/charged_creeper_kill"
   );
   private static final ResourceLocation ENDER_CREEPER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/ender_creeper_kill"
   );
   private static final ResourceLocation PLAYER_GET_SHULKER_SHELL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_get_shulker_shell"
   );
   private static final ResourceLocation PLAYER_PLAGUE_SHULKER_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_plague_shulker_damage_received"
   );
   private static final ResourceLocation SHULKER_VOID_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/shulker_void_kill"
   );
   private static final ResourceLocation ENDER_GHAST_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/ender_ghast_kill"
   );
   private static final ResourceLocation PLAYER_PUNCH_VOID_SKELETON_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_punch_void_skeleton"
   );
   private static final ResourceLocation DEATH_SILVERFISH_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/death_silverfish_kill"
   );
   public static final ResourceLocation PLAYER_SUMMON_DEATH_ENDERMITE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_summon_death_endermite"
   );
   private static final ResourceLocation PLAYER_RICHMC_STRAT_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_richmc_strat"
   );
   private static final ResourceLocation NO_ESSENCES_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/no_essences_kill"
   );
   private static final ResourceLocation CLASS_STRAY_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/class_stray_kill"
   );
   private static final ResourceLocation CLASS_BOGGED_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/class_bogged_kill"
   );
   public static final ResourceLocation PLAYER_SURVIVE_DEMON_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/player_survive_demon"
   );
   private static final ResourceLocation ZOMBIE_RIDER_LEADER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/zombie_rider_leader_kill"
   );
   private static final ResourceLocation ZOMBIE_HORSE_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/zombie_horse_kill"
   );
   private static final ResourceLocation ILLUSIONER_AGAIN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_30_advancements/illusioner_again_kill"
   );
   public static final ResourceLocation ROOT_DAY_25_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_25");
   private static final ResourceLocation PIGLIN_COMMANDER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/piglin_commander_kill"
   );
   private static final ResourceLocation CLASS_PIGLIN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/piglin_class_kill"
   );
   public static final ResourceLocation PLAYER_LEGENDARY_IDOL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_legendary_idol"
   );
   private static final ResourceLocation PLAYER_SKULL_BREAKER_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_skull_breaker"
   );
   private static final ResourceLocation PLAYER_SKULL_BREAKER_SQUARED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_skull_breaker_squared"
   );
   private static final ResourceLocation PLAYER_ARMOR_BREACHER_SQUARED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_armor_breacher_squared"
   );
   private static final ResourceLocation PLAYER_ARMOR_BREACHER_CUBED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_armor_breacher_cubed"
   );
   private static final ResourceLocation SUPER_WARDEN_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/kill_super_warden"
   );
   private static final ResourceLocation DEMONIC_GHAST_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/kill_demonic_ghast"
   );
   private static final ResourceLocation GIGASLIME_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/kill_gigaslime"
   );
   private static final ResourceLocation MAGMACUBE_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/kill_magmacube"
   );
   private static final ResourceLocation CAVE_SPIDER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/kill_cave_spider"
   );
   private static final ResourceLocation PLAYER_GET_ALL_ESSENCES_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_get_all_essences"
   );
   private static final ResourceLocation PLAYER_GET_ALL_NETHERITE_GEAR_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_get_all_netherite_gear"
   );
   private static final ResourceLocation PLAYER_GET_NETHERITE_TEMPLATE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_get_netherite_upgrade"
   );
   public static final ResourceLocation PLAYER_SHIELD_FAIL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/player_shield_fail"
   );
   private static final ResourceLocation BREEZE_SUPPORT_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_25_advancements/breeze_support_kill"
   );
   public static final ResourceLocation ROOT_DAY_20_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_20");
   private static final ResourceLocation CLASS_SKELETON_RIDER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/class_skeleton_rider_kill"
   );
   private static final ResourceLocation PLAYER_PUNCH_SKELETON_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_punch_skeleton"
   );
   public static final ResourceLocation PLAYER_FROST_EXPLOSION_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_frost_explosion_received"
   );
   public static final ResourceLocation PLAYER_RAVAGER_TOTEM_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_ravager_totem"
   );
   public static final ResourceLocation PLAYER_INSOMNIA_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_insomnia"
   );
   public static final ResourceLocation PLAYER_BAD_TRIP_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_bad_trip"
   );
   public static final ResourceLocation PLAYER_STICKY_FROG_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_sticky_frog"
   );
   public static final ResourceLocation PLAYER_GUARDIAN_ALLAY_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_guardian_allay"
   );
   private static final ResourceLocation PLAYER_ARMOR_BREACHER_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_armor_breacher"
   );
   private static final ResourceLocation TADPOLE_KILL_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "day_20_advancements/tadpole_kill");
   private static final ResourceLocation PHANTOM_KILL_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "day_20_advancements/phantom_kill");
   public static final ResourceLocation PLAYER_CHEMISTRY_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_chemistry"
   );
   private static final ResourceLocation NO_DROPS_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/no_drops_kill"
   );
   private static final ResourceLocation PLAYER_PASSIVE_DAMAGE_RECEIVED_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_20_advancements/player_passive_damage_received"
   );
   public static final ResourceLocation ROOT_DAY_15_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_15");
   public static final ResourceLocation HOGLIN_WHO_NEEDS_WIND_CHARGES_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/hoglin_who_needs_wind_charges"
   );
   public static final ResourceLocation HUSK_MALNUTRITION_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/husk_malnutrition"
   );
   public static final ResourceLocation ILLUSIONER_MAGICAL_BLINDNESS_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/illusioner_magical_blindness"
   );
   private static final ResourceLocation WITHER_NEW_BEGINNING_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/wither_kill_new_beginning"
   );
   private static final ResourceLocation WITHER_LETHAL_GUARDS_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/wither_skeleton_lethal_guards_kill"
   );
   private static final ResourceLocation ZOMBIE_RIDER_ARMED_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_15_advancements/zombie_rider_armed_infection_kill"
   );
   public static final ResourceLocation ROOT_DAY_10_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "root_day_10");
   private static final ResourceLocation ARMAND_ZOMBIE_HORSE_RIDE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/armand_zombie_horse_ride"
   );
   private static final ResourceLocation INVISIBLE_SKELETON_HORSE_RIDE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/invisible_skeleton_horse_ride"
   );
   private static final ResourceLocation SPIDER_BOA_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/spider_boa_kill"
   );
   private static final ResourceLocation ZOMBIE_HORSE_JOCKEY_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/zombie_horse_jockey_kill"
   );
   private static final ResourceLocation ILLUSIONER_KILL_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/illusioner_kill"
   );
   public static final ResourceLocation ZOMBIE_VILLAGER_TRY_CURE_ID = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "day_10_advancements/zombie_villager_try_cure"
   );
   private static final String CRITERION = "grant";
   private static final int DOLPHIN = 1;
   private static final int DROWNED = 2;
   private static final int HORSE = 1;
   private static final int ZOMBIE = 2;
   private static final Map<UUID, Map<UUID, PlayerAdvancementsHandler.PairProgress>> PROGRESS = new ConcurrentHashMap<>();
   private static final Set<Item> ALLAY_ITEMS = Set.of(
      Items.HEAVY_CORE,
      Items.GOLDEN_APPLE,
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.GOLDEN_PICKAXE,
      Items.TURTLE_HELMET,
      Items.ENDER_EYE,
      Items.NETHER_STAR,
      Items.WITHER_SKELETON_SKULL,
      Items.DIAMOND_BOOTS,
      Items.RABBIT_FOOT,
      Items.RECOVERY_COMPASS,
      Items.TOTEM_OF_UNDYING
   );

   @SubscribeEvent
   public static void onPlayerLoginTick(Post event) {
      int day = DayGlobalCount.CURRENT_DAY;
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.isCreative() && !player.isSpectator()) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 60 == 0) {
                  if (day >= 15) {
                     award(player, ROOT_DAY_15_ID);
                  }

                  if (day >= 25) {
                     award(player, ROOT_DAY_25_ID);
                  }

                  if (day >= 45) {
                     award(player, ROOT_DAY_45_ID);
                  }

                  if (day >= 55) {
                     award(player, ROOT_DAY_55_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerSkeletonHorseRideTick(Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 10) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (player.getVehicle() instanceof SkeletonHorse skeletonHorse) {
                     if (skeletonHorse.getTags().contains("invisibleSkeletonHorse")) {
                        award(player, INVISIBLE_SKELETON_HORSE_RIDE_ID);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerZombieHorseRideTick(Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 10) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (player.getVehicle() instanceof CustomZombieHorse zombieHorse) {
                     if (zombieHorse.getTags().contains("plainsZombieHorse")) {
                        if (zombieHorse.getTags().contains("armandZombieHorse")) {
                           award(player, ARMAND_ZOMBIE_HORSE_RIDE_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathSpider(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 10) {
         if (event.getEntity() instanceof Spider || event.getEntity() instanceof CaveSpider) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  ItemStack weapon = player.getMainHandItem();
                  if (!weapon.isEmpty()) {
                     if (weapon.getItem() instanceof SwordItem || weapon.getItem() instanceof AxeItem) {
                        Holder<Enchantment> boa = player.level()
                           .registryAccess()
                           .registryOrThrow(Registries.ENCHANTMENT)
                           .getHolderOrThrow(Enchantments.BANE_OF_ARTHROPODS);
                        if (EnchantmentHelper.getTagEnchantmentLevel(boa, weapon) > 0) {
                           award(player, SPIDER_BOA_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathIllusioner(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 10) {
         if (event.getEntity() instanceof Illusioner) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  award(player, ILLUSIONER_KILL_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathZombieHorseJockey(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 10) {
         if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel level) {
               if (!level.isClientSide) {
                  Map<UUID, PlayerAdvancementsHandler.PairProgress> playerMap = PROGRESS.computeIfAbsent(player.getUUID(), k -> new ConcurrentHashMap<>());
                  long now = level.getGameTime();
                  playerMap.entrySet().removeIf(entry -> entry.getValue().expiresAt <= now);
                  UUID pairZombieId = null;
                  int killedFlag = 0;
                  if (event.getEntity() instanceof CustomZombieHorse horse) {
                     UUID owner = horse.getOwnerUUID();
                     if (owner != null) {
                        pairZombieId = owner;
                        killedFlag = 1;
                     } else {
                        for (Entity passenger : horse.getPassengers()) {
                           if (passenger instanceof Zombie zombie) {
                              pairZombieId = zombie.getUUID();
                              killedFlag = 1;
                              break;
                           }
                        }
                     }

                     if (pairZombieId == null) {
                        return;
                     }
                  } else {
                     if (!(event.getEntity() instanceof Zombie zombie)) {
                        return;
                     }

                     UUID zombieRider = zombie.getUUID();
                     PlayerAdvancementsHandler.PairProgress existing = playerMap.get(zombieRider);
                     boolean horseAlreadyKilledForThisPair = existing != null && (existing.flags & 1) == 1;
                     boolean linkedHorseAliveNearby = !level.getEntitiesOfClass(
                           CustomZombieHorse.class,
                           zombie.getBoundingBox().inflate(16.0),
                           zombieHorse -> zombieRider.equals(zombieHorse.getOwnerUUID()) && zombieHorse.isAlive()
                        )
                        .isEmpty();
                     if (!horseAlreadyKilledForThisPair && !linkedHorseAliveNearby) {
                        return;
                     }

                     pairZombieId = zombieRider;
                     killedFlag = 2;
                  }

                  PlayerAdvancementsHandler.PairProgress progress = playerMap.computeIfAbsent(
                     pairZombieId, killed -> new PlayerAdvancementsHandler.PairProgress()
                  );
                  progress.flags |= killedFlag;
                  progress.expiresAt = now + 6000L;
                  if ((progress.flags & 3) == 3) {
                     award(player, ZOMBIE_HORSE_JOCKEY_KILL_ID);
                     playerMap.remove(pairZombieId);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathWither(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 15) {
         if (event.getEntity() instanceof WitherBoss witherBoss) {
            if (witherBoss.getTags().contains("fromPlayer")) {
               if (witherBoss.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           AABB box = witherBoss.getBoundingBox().inflate(20.0);

                           for (ServerPlayer players : level.getEntitiesOfClass(ServerPlayer.class, box, p -> !p.isSpectator())) {
                              award(players, WITHER_NEW_BEGINNING_KILL_ID);
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
   public static void onLivingDeathWitherGuard(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 15) {
         if (event.getEntity() instanceof WitherSkeleton witherSkeleton) {
            if (witherSkeleton.getTags().contains("fromWither")) {
               if (witherSkeleton.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, WITHER_LETHAL_GUARDS_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathArmedZombieRider(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 15) {
         if (event.getEntity() instanceof Zombie zombie) {
            if (zombie.getTags().contains("zombieRider")) {
               if (zombie.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           ItemStack stack = zombie.getMainHandItem();
                           if (!stack.isEmpty()) {
                              Item item = stack.getItem();
                              if (item == Items.IRON_SWORD || item == Items.IRON_AXE || item == Items.IRON_SHOVEL) {
                                 award(player, ZOMBIE_RIDER_ARMED_KILL_ID);
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
   public static void onLivingDeathMountClassSkeleton(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof WitherSkeleton) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (event.getEntity().getTags().contains("mount")) {
                  if (event.getEntity().getVehicle() != null) {
                     if (event.getEntity().getVehicle() instanceof Spider || event.getEntity().getVehicle() instanceof CaveSpider) {
                        if (!player.level().isClientSide) {
                           award(player, CLASS_SKELETON_RIDER_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerKnockbackPunchSkeleton(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (!target.level().isClientSide) {
            if (target instanceof ServerPlayer player) {
               if (attacker instanceof WitherSkeleton witherSkeleton) {
                  if (witherSkeleton.getTags().contains("punchClassSkeleton")) {
                     double startX = player.getX();
                     double startZ = player.getZ();
                     UUID id = player.getUUID();
                     ScheduleInTicks.schedule(() -> {
                        ServerPlayer serverPlayer = player.server.getPlayerList().getPlayer(id);
                        if (serverPlayer != null) {
                           double dx = serverPlayer.getX() - startX;
                           double dz = serverPlayer.getZ() - startZ;
                           double horizontalDisplacement = Math.sqrt(dx * dx + dz * dz);
                           if (horizontalDisplacement >= 16.0) {
                              award(serverPlayer, PLAYER_PUNCH_SKELETON_ID);
                           }
                        }
                     }, 20);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onAllayItemReceived(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         Entity entity = event.getEntity();
         if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
               if (entity instanceof Allay allay) {
                  if (allay.tickCount % 20 == 0) {
                     if (allay.getTags().contains("isGuardian")) {
                        Optional<UUID> ownerOpt = allay.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
                        if (!ownerOpt.isEmpty()) {
                           Player owner = level.getPlayerByUUID(ownerOpt.get());
                           if (owner != null) {
                              ItemStack stack = allay.getItemInHand(InteractionHand.MAIN_HAND);
                              if (!stack.isEmpty()) {
                                 if (ALLAY_ITEMS.contains(stack.getItem())) {
                                    award((ServerPlayer)owner, PLAYER_GUARDIAN_ALLAY_ID);
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
   public static void onPlayerArmorBreachTick(Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (player.hasEffect(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate())) {
                     award(player, PLAYER_ARMOR_BREACHER_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathTadpole(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         if (event.getEntity() instanceof Tadpole tadpole) {
            if (tadpole.getTags().contains("deathTadpole")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, TADPOLE_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathPhantom(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         if (event.getEntity() instanceof Phantom phantom) {
            if (phantom.getTags().contains("megaPhantom")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, PHANTOM_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathNoDrops(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         LivingEntity entity = event.getEntity();
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
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  award(player, NO_DROPS_KILL_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedPassive(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 20) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (attacker != null) {
            if (!target.level().isClientSide) {
               if (PassiveHostileMobs.isPassiveHostileType(attacker.getType())) {
                  if (target instanceof ServerPlayer player) {
                     award(player, PLAYER_PASSIVE_DAMAGE_RECEIVED_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathCommanderPiglin(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 25) {
         if (event.getEntity() instanceof PiglinBrute piglinBrute) {
            if (piglinBrute.getTags().contains("piglinCommander")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, PIGLIN_COMMANDER_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathClassPiglin(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 25) {
         if (event.getEntity() instanceof AbstractPiglin piglin) {
            if (piglin.getTags().contains("classPiglin")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, CLASS_PIGLIN_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerInventoryTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               int day = DayGlobalCount.CURRENT_DAY;
               Inventory inventory = player.getInventory();
               if (day >= 25) {
                  RegistryAccess registryAccess = player.level().registryAccess();
                  Holder<Enchantment> density = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.DENSITY);
                  Holder<Enchantment> breach = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.BREACH);
                  Set<Item> requiredArmor = Set.of(
                     (Item)PermadeathItemsRegistry.PERMA_NETHERITE_HELMET.get(),
                     (Item)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get(),
                     (Item)PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS.get(),
                     (Item)PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS.get()
                  );
                  Set<Item> requiredEssences = Set.of(
                     (Item)PermadeathItemsRegistry.PERMA_GHAST_ESSENCE.get(),
                     (Item)PermadeathItemsRegistry.PERMA_SLIME_ESSENCE.get(),
                     (Item)PermadeathItemsRegistry.PERMA_MAGMA_ESSENCE.get(),
                     (Item)PermadeathItemsRegistry.PERMA_SPIDER_ESSENCE.get()
                  );
                  Set<Item> foundArmor = new HashSet<>();
                  Set<Item> foundEssences = new HashSet<>();

                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty()) {
                        Item item = stack.getItem();
                        if (requiredArmor.contains(item)) {
                           foundArmor.add(item);
                        }

                        if (requiredEssences.contains(item)) {
                           foundEssences.add(item);
                        }

                        if (stack.is(Items.MACE)) {
                           award(player, PLAYER_SKULL_BREAKER_ID);
                           if (EnchantmentHelper.getTagEnchantmentLevel(density, stack) >= 5) {
                              award(player, PLAYER_SKULL_BREAKER_SQUARED_ID);
                           }

                           if (EnchantmentHelper.getTagEnchantmentLevel(breach, stack) >= 4) {
                              award(player, PLAYER_ARMOR_BREACHER_SQUARED_ID);
                              if (player.hasEffect(PermadeathMobEffectBuilder.ARMOR_BREACH_EFFECT.getDelegate())) {
                                 award(player, PLAYER_ARMOR_BREACHER_CUBED_ID);
                              }
                           }
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get())) {
                           award(player, PLAYER_GET_NETHERITE_TEMPLATE_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_TCND.get())) {
                           award(player, PLAYER_LEGENDARY_IDOL_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_GHAST_ESSENCE.get())) {
                           award(player, DEMONIC_GHAST_KILL_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_SLIME_ESSENCE.get())) {
                           award(player, GIGASLIME_KILL_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_MAGMA_ESSENCE.get())) {
                           award(player, MAGMACUBE_KILL_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_SPIDER_ESSENCE.get())) {
                           award(player, CAVE_SPIDER_KILL_ID);
                        }
                     }
                  }

                  if (foundArmor.containsAll(requiredArmor)) {
                     award(player, PLAYER_GET_ALL_NETHERITE_GEAR_ID);
                  }

                  if (foundEssences.containsAll(requiredEssences)) {
                     award(player, PLAYER_GET_ALL_ESSENCES_ID);
                  }
               }

               if (day >= 30) {
                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty() && stack.is(Items.SHULKER_SHELL)) {
                        award(player, PLAYER_GET_SHULKER_SHELL_ID);
                     }
                  }
               }

               if (day >= 40) {
                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty() && stack.is((Item)PermadeathItemsRegistry.PERMA_END_RELIC.get())) {
                        award(player, PLAYER_GET_END_RELIC_ID);
                     }
                  }
               }

               if (day >= 45) {
                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty() && stack.is((Item)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get())) {
                        award(player, PLAYER_DARK_IDOL_ID);
                     }
                  }
               }

               if (day >= 50) {
                  Set<Item> requiredArmor = Set.of(
                     (Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_HELMET.get(),
                     (Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE.get(),
                     (Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_LEGGINGS.get(),
                     (Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_BOOTS.get()
                  );
                  Set<Item> foundArmor = new HashSet<>();

                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty()) {
                        Item item = stack.getItem();
                        if (requiredArmor.contains(item)) {
                           foundArmor.add(item);
                        }

                        if (foundArmor.containsAll(requiredArmor)) {
                           award(player, PLAYER_COVER_ME_WITH_HELL_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.INFERNAL_NETHERTITE_BLOCK_ITEM.get())) {
                           award(player, PLAYER_GET_INFERNAL_NETHERITE_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get())) {
                           award(player, PLAYER_GET_NETHERITE_SWORD_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_AXE.get())
                           || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_PICKAXE.get())
                           || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_SHOVEL.get())
                           || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_HOE.get())) {
                           award(player, PLAYER_GET_NETHERITE_TOOL_ID);
                        }
                     }
                  }
               }

               if (day >= 55) {
                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty()) {
                        if (stack.is(Items.SHIELD) && stack.has(DataComponents.CUSTOM_DATA)) {
                           CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
                           if (data != null) {
                              CompoundTag tag = data.copyTag();
                              if (tag.getInt("permadeath_beg_shield") == 1) {
                                 award(player, PLAYER_CRAFT_BEG_SHIELD_ID);
                              }
                           }
                        }

                        if (stack.is(Items.TOTEM_OF_UNDYING)) {
                           boolean isMedalla = stack.getComponents().toString().contains("medalla");
                           if (isMedalla) {
                              award(player, PLAYER_REINCARNATED_SURVIVOR_ID);
                           }
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_VOID_ESSENCE.get())) {
                           award(player, PLAYER_GET_VOID_ESSENCE_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_IMMORTALITY_ESSENCE.get())) {
                           award(player, PLAYER_GET_IMMORTALITY_ESSENCE_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_END_ORB.get())) {
                           award(player, PLAYER_GET_END_ORB_ID);
                        }
                     }
                  }
               }

               if (day >= 60) {
                  for (ItemStack stack : inventory.items) {
                     if (!stack.isEmpty()) {
                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_BEG_RELIC.get())) {
                           award(player, PLAYER_UNLOCK_LOCKED_SLOTS_ID);
                        }

                        if (stack.is((Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get())) {
                           award(player, PLAYER_CRAFT_LIFE_ORB_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathSuperWarden(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 25) {
         if (event.getEntity() instanceof Warden warden) {
            if (warden.getTags().contains("superWarden")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, SUPER_WARDEN_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathBreezeSupport(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 25) {
         if (event.getEntity() instanceof Breeze breeze) {
            if (breeze.getTags().contains("breezeSupport")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, BREEZE_SUPPORT_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathClassSkeleton(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof WitherSkeleton) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (event.getEntity().getTags().contains("classSkeleton")) {
                  if (!player.level().isClientSide) {
                     award(player, CLASS_SKELETON_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingChargedCreeper(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Creeper creeper) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  if (creeper.getTags().contains("energeticCreeper")) {
                     award(player, CHARGED_CREEPER_KILL_ID);
                  } else if (creeper.getTags().contains("ender")) {
                     award(player, ENDER_CREEPER_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedShulker(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (!target.level().isClientSide) {
            if (attacker instanceof Shulker shulker) {
               if (shulker.getTags().contains("shulkerPlaga")) {
                  if (target instanceof ServerPlayer player) {
                     award(player, PLAYER_PLAGUE_SHULKER_DAMAGE_RECEIVED_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathShulkerVoid(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Shulker shulker) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (shulker.getTags().contains("shulkerVoid")) {
                  if (!player.level().isClientSide) {
                     award(player, SHULKER_VOID_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerKnockbackPunchVoidSkeleton(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (!target.level().isClientSide) {
            if (target instanceof ServerPlayer player) {
               if (attacker instanceof WitherSkeleton witherSkeleton) {
                  if (witherSkeleton.getTags().contains("fromShulker")) {
                     double startX = player.getX();
                     double startZ = player.getZ();
                     UUID id = player.getUUID();
                     ScheduleInTicks.schedule(() -> {
                        ServerPlayer serverPlayer = player.server.getPlayerList().getPlayer(id);
                        if (serverPlayer != null) {
                           double dx = serverPlayer.getX() - startX;
                           double dz = serverPlayer.getZ() - startZ;
                           double horizontalDisplacement = Math.sqrt(dx * dx + dz * dz);
                           if (horizontalDisplacement >= 16.0) {
                              award(serverPlayer, PLAYER_PUNCH_VOID_SKELETON_ID);
                           }
                        }
                     }, 20);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathSilverfish(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Silverfish silverfish) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (silverfish.getTags().contains("deathSilverOrEndermite")) {
                  if (!player.level().isClientSide) {
                     award(player, DEATH_SILVERFISH_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               DesperateMeasuresAttachment data = DesperateMeasuresAttachmentHelper.get(player);
               boolean inEnd = player.level().dimension() == Level.END;
               boolean noArmor = player.getInventory().armor.stream().allMatch(ItemStack::isEmpty);
               boolean effects = player.hasEffect(MobEffects.INVISIBILITY) && player.hasEffect(MobEffects.SLOW_FALLING);
               if (inEnd && noArmor && effects) {
                  data.tickProgress();
                  if (data.isReadyToComplete()) {
                     data.complete();
                     if (player instanceof ServerPlayer serverPlayer) {
                        award(serverPlayer, PLAYER_RICHMC_STRAT_ID);
                     }
                  }
               } else {
                  data.reset();
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathNoEssence(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Mob mob) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (mob.getTags().contains("ultraAraña")
                  || mob.getTags().contains("gigaMagma")
                  || mob.getTags().contains("gigaSlime")
                  || mob.getTags().contains("ghastDemoniaco")) {
                  if (!player.level().isClientSide) {
                     award(player, NO_ESSENCES_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathClassStray(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Stray stray) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (stray.getTags().contains("classStray")) {
                  if (!player.level().isClientSide) {
                     award(player, CLASS_STRAY_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathClassBogged(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Bogged bogged) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (bogged.getTags().contains("classBogged")) {
                  if (!player.level().isClientSide) {
                     award(player, CLASS_BOGGED_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathArmedDiamondZombieRider(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Zombie zombie) {
            if (zombie.getTags().contains("zombieRiderLeader")) {
               if (zombie.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           ItemStack stack = zombie.getMainHandItem();
                           if (!stack.isEmpty()) {
                              Item item = stack.getItem();
                              if (item == Items.DIAMOND_SWORD || item == Items.DIAMOND_AXE) {
                                 award(player, ZOMBIE_RIDER_LEADER_KILL_ID);
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
   public static void onLivingDeathZombieHorse(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof ZombieHorse zombieHorse) {
            if (zombieHorse.getTags().contains("plainsZombieHorse")) {
               if (zombieHorse.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           if (zombieHorse.hasEffect(MobEffects.DAMAGE_RESISTANCE) && zombieHorse.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                              award(player, ZOMBIE_HORSE_KILL_ID);
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
   public static void onLivingDeathIllusionerAgain(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Illusioner illusioner) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (illusioner.getTags().contains("strongIllusioner")) {
                  if (!player.level().isClientSide) {
                     award(player, ILLUSIONER_AGAIN_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEnderGhast(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 30) {
         if (event.getEntity() instanceof Ghast ghast) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (ghast.getTags().contains("ender")) {
                  if (!player.level().isClientSide) {
                     award(player, ENDER_GHAST_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathZPiglinJockey(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof ZombifiedPiglin zombifiedPiglin) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (zombifiedPiglin.getTags().contains("fromZPiglin")) {
                  if (zombifiedPiglin.getVehicle() instanceof Ravager
                     || zombifiedPiglin.getVehicle() instanceof Bee
                     || zombifiedPiglin.getVehicle() instanceof Pig
                     || zombifiedPiglin.getVehicle() instanceof MagmaCube
                     || zombifiedPiglin.getVehicle() instanceof Ghast) {
                     if (zombifiedPiglin.getVehicle().getTags().contains("fromZPiglin")) {
                        if (!player.level().isClientSide) {
                           award(player, ZOMBIFIED_PIGLIN_JOCKEY_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathFloatingDemon(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Ghast ghast) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (ghast.getTags().contains("floatingDemon")) {
                  if (!player.level().isClientSide) {
                     award(player, FLOATING_DEMON_GHAST_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathShulker(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Shulker shulker) {
            if (!shulker.getTags().contains("fromModule")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, SHULKER_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedAnotherPlayer(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (attacker != null) {
            if (!target.level().isClientSide) {
               if (attacker instanceof ServerPlayer player) {
                  if (target instanceof ServerPlayer && target != attacker) {
                     award(player, PLAYER_HIT_ANOTHER_PLAYER_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathUltraWither(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof WitherBoss witherBoss) {
            if (witherBoss.getTags().contains("fromPlayer")) {
               if (witherBoss.getTags().contains("ultraWither")) {
                  if (witherBoss.level() instanceof ServerLevel level) {
                     if (event.getSource().getEntity() instanceof ServerPlayer player) {
                        if (!level.isClientSide) {
                           if (!player.level().isClientSide) {
                              AABB box = witherBoss.getBoundingBox().inflate(20.0);

                              for (ServerPlayer players : level.getEntitiesOfClass(ServerPlayer.class, box, p -> !p.isSpectator())) {
                                 award(players, ULTRA_REINCARNATED_WITHER_KILL_ID);
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
   public static void onLivingDeathZombieRiderDeath(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Zombie zombie) {
            if (zombie.getTags().contains("zombieDeathRider")) {
               if (zombie.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, ZOMBIE_DEATH_RIDER_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathWitch(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Witch witch) {
            if (witch.getTags().contains("impossibleWitch")) {
               if (witch.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, IMPOSSIBLE_WITCH_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathDolphinJockey(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel level) {
               if (!level.isClientSide) {
                  LivingEntity entity = event.getEntity();
                  Map<UUID, PlayerAdvancementsHandler.PairProgress> playerMap = PROGRESS.computeIfAbsent(player.getUUID(), k -> new ConcurrentHashMap<>());
                  long now = level.getGameTime();
                  playerMap.entrySet().removeIf(entry -> entry.getValue().expiresAt <= now);
                  UUID pairId;
                  int killedFlag;
                  if (entity instanceof Dolphin dolphin && dolphin.getTags().contains("dolphinDepths")) {
                     pairId = dolphin.getPersistentData().getUUID("depthsPair");
                     killedFlag = 1;
                  } else {
                     if (!(entity instanceof Drowned drowned) || !drowned.getTags().contains("fromDolphin")) {
                        return;
                     }

                     pairId = drowned.getPersistentData().getUUID("depthsPair");
                     killedFlag = 2;
                  }

                  PlayerAdvancementsHandler.PairProgress progress = playerMap.computeIfAbsent(pairId, k -> new PlayerAdvancementsHandler.PairProgress());
                  progress.flags |= killedFlag;
                  progress.expiresAt = now + 6000L;
                  if ((progress.flags & 3) == 3) {
                     award(player, DEPTH_DOLPHIN_RIDER_KILL_ID);
                     playerMap.remove(pairId);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathDefinitiveWarden(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Warden warden) {
            if (warden.getTags().contains("definitiveWarden")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, DEFINITIVE_WARDEN_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathAgainZombieHorse(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof ZombieHorse zombieHorse) {
            if (zombieHorse.getTags().contains("plainsZombieHorse")) {
               if (zombieHorse.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, ZOMBIE_HORSE_AGAIN_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathRavager(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Ravager ravager) {
            if (ravager.getTags().contains("fromAnimal")) {
               if (ravager.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, TRANSMUTED_ANIMAL_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathCRavager(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof CustomRavager ravager) {
            if (ravager.level() instanceof ServerLevel level) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!level.isClientSide) {
                     if (!player.level().isClientSide) {
                        award(player, TRANSMUTED_ANIMAL_KILL_ID);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathZoglin(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof Zoglin zoglin) {
            if (zoglin.getTags().contains("fromArmadillo")) {
               if (zoglin.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, TRANSMUTED_ARMADILLO_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathAgainClassPiglin(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof AbstractPiglin piglin) {
            if (piglin.getTags().contains("classPiglin")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, PIGLIN_CLASS_AGAIN_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTickForMarker(Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (!player.isSpectator()) {
                     List<Marker> markers = player.level()
                        .getEntitiesOfClass(Marker.class, player.getBoundingBox().inflate(24.0), marker -> marker.getTags().contains("begPortalLocation"));
                     if (!markers.isEmpty()) {
                        award(player, PLAYER_FIND_BEGINNING_PORTAL_ID);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTickForDeathModule(Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (!player.isSpectator()) {
                     ServerLevel level = (ServerLevel)player.level();
                     List<Entity> moduleEntities = level.getEntities(
                        (Entity)null, player.getBoundingBox().inflate(12.0), e -> e.getTags().contains("fromModule")
                     );
                     boolean hasSpider = moduleEntities.stream().anyMatch(e -> e.getType() == EntityType.CAVE_SPIDER);
                     boolean hasZombie = moduleEntities.stream().anyMatch(e -> e instanceof Zombie);
                     boolean hasShulker = moduleEntities.stream().anyMatch(e -> e instanceof Shulker);
                     boolean hasSpawner = moduleEntities.stream().anyMatch(e -> e instanceof MinecartSpawner);
                     if (hasSpider && hasZombie && hasShulker && hasSpawner) {
                        award(player, PLAYER_FIND_DEATH_MODULE_ID);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedFire(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 45) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide()) {
            if (entity.isAlive()) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_FIRE) && entity instanceof ServerPlayer player) {
                  award(player, PLAYER_FIRE_DAMAGE_RECEIVED_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedFall(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 45) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide()) {
            if (entity.isAlive()) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_FALL) && entity instanceof ServerPlayer player) {
                  award(player, PLAYER_FALL_DAMAGE_RECEIVED_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedPillager(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 45) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (!target.level().isClientSide) {
            if (attacker instanceof Pillager pillager) {
               if (pillager.hasEffect(MobEffects.INVISIBILITY)) {
                  if (target instanceof ServerPlayer player) {
                     award(player, PLAYER_PILLAGER_DAMAGE_RECEIVED_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedZoglin(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         LivingEntity target = event.getEntity();
         Entity attacker = event.getSource().getEntity();
         if (!target.level().isClientSide) {
            if (attacker instanceof Zoglin zoglin) {
               if (zoglin.getTags().contains("fromArmadillo")) {
                  if (target instanceof ServerPlayer player) {
                     award(player, PLAYER_ZOGLIN_DAMAGE_RECEIVED_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathBegWither(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (event.getEntity() instanceof WitherBoss witherBoss) {
            if (witherBoss.getTags().contains("beginningWither")) {
               if (witherBoss.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           AABB box = witherBoss.getBoundingBox().inflate(20.0);

                           for (ServerPlayer players : level.getEntitiesOfClass(ServerPlayer.class, box, p -> !p.isSpectator())) {
                              award(players, WITHER_KILL_NEW_BEGINNING_AGAIN_ID);
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
   public static void onLivingDeathMiniWither(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (event.getEntity() instanceof WitherBoss witherBoss) {
            if (witherBoss.getTags().contains("beginningWither")) {
               if (witherBoss.getTags().contains("miniWither")) {
                  if (witherBoss.level() instanceof ServerLevel level) {
                     if (event.getSource().getEntity() instanceof ServerPlayer player) {
                        if (!level.isClientSide) {
                           if (!player.level().isClientSide) {
                              AABB box = witherBoss.getBoundingBox().inflate(20.0);

                              for (ServerPlayer players : level.getEntitiesOfClass(ServerPlayer.class, box, p -> !p.isSpectator())) {
                                 award(players, MINI_WITHER_KILL_ID);
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
   public static void onLivingDeathQuantumCreeper(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (event.getEntity() instanceof Creeper creeper) {
            if (!creeper.getTags().contains("ender") && !creeper.getTags().contains("energeticCreeper")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, QUANTUM_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathGiantZombie(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (event.getEntity() instanceof CustomGiant giant) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  award(player, GIANT_ZOMBIE_KILL_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEmperador(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 50) {
         if (event.getEntity() instanceof WitherSkeleton skeleton) {
            if (skeleton.getTags().contains("witherEmperador")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, WITHER_EMPEROR_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathShulkerVoidAgain(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 55) {
         if (event.getEntity() instanceof Shulker shulker) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (shulker.getTags().contains("shulkerVoid")) {
                  if (!player.level().isClientSide) {
                     award(player, VOID_SHOCK_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedFallAgain(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 55) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide()) {
            if (entity.isAlive()) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_FALL) && entity instanceof ServerPlayer player) {
                  award(player, PLAYER_INJURED_FEET_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathUniversalCat(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         if (event.getEntity() instanceof Cat cat) {
            if (cat.getTags().contains("universal")) {
               if (cat.level() instanceof ServerLevel level) {
                  if (event.getSource().getEntity() instanceof ServerPlayer player) {
                     if (!level.isClientSide) {
                        if (!player.level().isClientSide) {
                           award(player, UNIVERSAL_CAT_KILL_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedFallAgainAgain(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide()) {
            if (entity.isAlive()) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_FALL) && entity instanceof ServerPlayer player) {
                  award(player, PLAYER_FALL_DAMAGE_RECEIVED_DAY_60_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingPlayerDamageReceivedDrowning(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         LivingEntity entity = event.getEntity();
         if (!entity.level().isClientSide()) {
            if (entity.isAlive()) {
               DamageSource source = event.getSource();
               if (source.is(DamageTypeTags.IS_DROWNING) && entity instanceof ServerPlayer player) {
                  award(player, PLAYER_DROWNING_DAMAGE_RECEIVED_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         if (event.getLevel() instanceof ServerLevel level) {
            if (event.getEntity() instanceof ServerPlayer player) {
               BlockPos var6 = event.getPos();
               BlockState state = level.getBlockState(var6);
               if (state.is(Blocks.BEACON)) {
                  award(player, PLAYER_TRY_OPEN_BEACON_DAY_60_ID);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathEnderQuantumCreeper(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         if (event.getEntity() instanceof Creeper creeper) {
            if (creeper.getTags().contains("ender_quantum")) {
               if (event.getSource().getEntity() instanceof ServerPlayer player) {
                  if (!player.level().isClientSide) {
                     award(player, QUANTUM_CREEPER_KILL_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathClassSkeletonNew(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof WitherSkeleton) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (event.getEntity().getTags().contains("claseCientifica")
                  || event.getEntity().getTags().contains("claseDemoniaca")
                  || event.getEntity().getTags().contains("claseDefinitiva")) {
                  if (!player.level().isClientSide) {
                     award(player, CLASS_SKELETON_KILL_DAY_60_ID);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeathBlaze(LivingDeathEvent event) {
      if (DayGlobalCount.CURRENT_DAY >= 60) {
         if (event.getEntity() instanceof Blaze blaze) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  award(player, BLAZE_KILL_DAY_60_ID);
               }
            }
         }
      }
   }

   public static void award(ServerPlayer player, ResourceLocation id) {
      AdvancementHolder advancement = player.server.getAdvancements().get(id);
      if (advancement != null) {
         if (!player.getAdvancements().getOrStartProgress(advancement).isDone()) {
            player.getAdvancements().award(advancement, "grant");
         }
      }
   }

   private static class PairProgress {
      int flags;
      long expiresAt;
   }
}
