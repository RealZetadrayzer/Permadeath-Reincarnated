package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;

@EventBusSubscriber
public class WitherSkeletonChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof WitherSkeleton witherSkeleton) {
                  if (!witherSkeleton.getTags().contains("fromUniversal")) {
                     if (!witherSkeleton.getTags().contains("mount")
                        && !witherSkeleton.getTags().contains("fromSkeleton")
                        && !witherSkeleton.getTags().contains("fromWither")
                        && !witherSkeleton.getTags().contains("fromShulker")) {
                        if (!event.loadedFromDisk()) {
                           int day = DayGlobalCount.CURRENT_DAY;
                           if (witherSkeleton.level().dimension() == Level.NETHER && day >= 50) {
                              int chance = 1 + RANDOM.nextInt(100);
                              int chanceThreshold = day >= 60 ? 8 : (day >= 55 && PermadeathConfig.CUSTOM_CHANGES.get() ? 4 : 2);
                              if (chance <= chanceThreshold) {
                                 ItemStack helmet = new ItemStack(Items.YELLOW_BANNER);
                                 ItemStack chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
                                 ItemStack leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
                                 ItemStack boots = new ItemStack(Items.GOLDEN_BOOTS);
                                 ItemStack bow = new ItemStack(Items.BOW);
                                 ItemStack netheriteSword = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get());
                                 RegistryAccess registryAccess = witherSkeleton.level().registryAccess();
                                 Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
                                 Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
                                 bow.enchant(power, 100);
                                 bow.enchant(punch, 5);
                                 Registry<BannerPattern> bannerRegistry = registryAccess.registryOrThrow(Registries.BANNER_PATTERN);
                                 Holder<BannerPattern> bricks = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "bricks"))
                                 );
                                 Holder<BannerPattern> smallStripes = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "small_stripes"))
                                 );
                                 Holder<BannerPattern> stripeRight = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "stripe_right"))
                                 );
                                 Holder<BannerPattern> stripeLeft = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "stripe_left"))
                                 );
                                 Holder<BannerPattern> flower = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "flower"))
                                 );
                                 Holder<BannerPattern> triangleTop = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "triangle_top"))
                                 );
                                 Holder<BannerPattern> gradientUp = bannerRegistry.getHolderOrThrow(
                                    ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("minecraft", "gradient_up"))
                                 );
                                 Builder builder = new Builder();
                                 builder.add(bricks, DyeColor.BLACK);
                                 builder.add(smallStripes, DyeColor.BLACK);
                                 builder.add(stripeRight, DyeColor.BLACK);
                                 builder.add(stripeLeft, DyeColor.BLACK);
                                 builder.add(flower, DyeColor.YELLOW);
                                 builder.add(triangleTop, DyeColor.BLACK);
                                 builder.add(gradientUp, DyeColor.RED);
                                 helmet.set(DataComponents.BASE_COLOR, DyeColor.YELLOW);
                                 helmet.set(DataComponents.BANNER_PATTERNS, builder.build());
                                 witherSkeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
                                 witherSkeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
                                 witherSkeleton.setItemSlot(EquipmentSlot.LEGS, leggings);
                                 witherSkeleton.setItemSlot(EquipmentSlot.FEET, boots);
                                 witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
                                 witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, netheriteSword);
                                 Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(25.0);
                                 Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(80.0);
                                 witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
                                 witherSkeleton.addTag("arrowImmune");
                                 witherSkeleton.addTag("witherEmperador");
                                 witherSkeleton.setCustomName(Component.literal("Esqueleto Wither Emperador").withStyle(ChatFormatting.GOLD));
                                 MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
                              }
                           }

                           if (witherSkeleton.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                              ItemStack chestplate = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_CHESTPLATE.get());
                              ItemStack boots = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_ROSE_NETHERITE_BOOTS.get());
                              ItemStack netheriteSword = new ItemStack(Items.NETHERITE_SWORD);
                              RegistryAccess registryAccess = witherSkeleton.level().registryAccess();
                              Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
                              netheriteSword.enchant(sharpness, 15);
                              witherSkeleton.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                              witherSkeleton.setItemSlot(EquipmentSlot.CHEST, chestplate);
                              witherSkeleton.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
                              witherSkeleton.setItemSlot(EquipmentSlot.FEET, boots);
                              witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                              witherSkeleton.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                              Objects.requireNonNull(witherSkeleton.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(120.0);
                              witherSkeleton.setHealth(witherSkeleton.getMaxHealth());
                              witherSkeleton.setCustomName(Component.literal("Esqueleto Wither Rosáceo").withStyle(ChatFormatting.GOLD));
                              MobPreventEquipmentDrops.preventAllEquipmentDrop(witherSkeleton);
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
   public static void onLivingTick(Post event) {
      if (event.getEntity() instanceof WitherSkeleton skeleton) {
         Level level = skeleton.level();
         if (!level.isClientSide) {
            if (skeleton.getTags().contains("witherEmperador")) {
               CompoundTag data = skeleton.getPersistentData();
               String mode = data.getString("WeaponMode");
               LivingEntity target = skeleton.getTarget();
               double dist = target != null && target.isAlive() ? skeleton.distanceTo(target) : Double.MAX_VALUE;
               ItemStack bow = new ItemStack(Items.BOW);
               ItemStack netheriteSword = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get());
               RegistryAccess registryAccess = skeleton.level().registryAccess();
               Holder<Enchantment> power = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
               Holder<Enchantment> punch = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH);
               bow.enchant(power, 100);
               bow.enchant(punch, 5);
               if (dist <= 4.0) {
                  if (!"MELEE".equals(mode)) {
                     skeleton.setItemSlot(EquipmentSlot.MAINHAND, netheriteSword);
                     skeleton.setItemSlot(EquipmentSlot.OFFHAND, bow);
                     data.putString("WeaponMode", "MELEE");
                     level.playSound(null, skeleton.blockPosition(), (SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.HOSTILE, 1.0F, 1.0F);
                  }
               } else {
                  if (!"RANGED".equals(mode)) {
                     skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
                     skeleton.setItemSlot(EquipmentSlot.OFFHAND, netheriteSword);
                     data.putString("WeaponMode", "RANGED");
                     level.playSound(null, skeleton.blockPosition(), (SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.HOSTILE, 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }
}
