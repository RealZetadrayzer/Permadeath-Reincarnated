package zeta.org.permadeath_reincarnated.systems;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningBlessingAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningBlessingAttachmentHelper;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningCurseAttachment;
import zeta.org.permadeath_reincarnated.systems.attachments.BeginningCurseAttachmentHelper;

@EventBusSubscriber
public class BeginningEffectsHandler {
   @SubscribeEvent
   public static void onInvisibilityTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  if (player.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                     Inventory inv = player.getInventory();
                     boolean hasEndOrb = hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_END_ORB.get()) && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()
                        || hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get()) && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                     if (!hasEndOrb) {
                        if (player.hasEffect(MobEffects.INVISIBILITY)) {
                           player.removeEffect(MobEffects.INVISIBILITY);
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_USE_INVISIBILITY_POTION_ID);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSlowfallingTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            if (event.getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  if (player.tickCount % 20 == 0) {
                     if (!player.isSpectator()) {
                        if (player.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                           Inventory inv = player.getInventory();
                           boolean hasEndOrb = hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_END_ORB.get())
                                 && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()
                              || hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get()) && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
                           if (!hasEndOrb) {
                              if (player.hasEffect(MobEffects.SLOW_FALLING)) {
                                 PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_GRAVITY_FALLS_FAIL_ID);
                                 player.removeEffect(MobEffects.SLOW_FALLING);
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
   public static void onBlessingTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  BeginningBlessingAttachment blessing = BeginningBlessingAttachmentHelper.get(player);
                  if (blessing.hasBlessing()) {
                     blessing.tick();
                     player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, true, true));
                     if (blessing.getRemainingTicks() <= 0) {
                        player.displayClientMessage(
                           Component.literal("¡La bendición de ")
                              .withStyle(ChatFormatting.LIGHT_PURPLE)
                              .append(
                                 Component.literal("The Beginning")
                                    .withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})
                                    .append(Component.literal(" acabó!").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(Style.EMPTY.withBold(false)))
                              ),
                           false
                        );
                        blessing.setClean();
                     } else {
                        blessing.setClean();
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onCurseTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (!player.isSpectator()) {
                  BeginningCurseAttachment curse = BeginningCurseAttachmentHelper.get(player);
                  if (curse.hasCurse()) {
                     curse.tick();
                     player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, true));
                     player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, true, true));
                     if (curse.getRemainingTicks() <= 0) {
                        player.displayClientMessage(
                           Component.literal("¡La maldición de ")
                              .withStyle(ChatFormatting.LIGHT_PURPLE)
                              .append(
                                 Component.literal("The Beginning")
                                    .withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})
                                    .append(Component.literal(" acabó!").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(Style.EMPTY.withBold(false)))
                              ),
                           false
                        );
                        curse.setClean();
                     } else {
                        curse.setClean();
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onMilkDrinkWithCurse(Finish event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.level().isClientSide) {
            if (player.level() instanceof ServerLevel level) {
               if (!player.isSpectator()) {
                  BeginningCurseAttachment curse = BeginningCurseAttachmentHelper.get(player);
                  if (event.getItem().getItem() == Items.MILK_BUCKET) {
                     if (curse.hasCurse()) {
                        long seed = level.getRandom().nextLong();
                        String name = player.getName().getString();
                        Objects.requireNonNull(player.level().getServer())
                           .getPlayerList()
                           .getPlayers()
                           .forEach(
                              p -> {
                                 p.displayClientMessage(
                                    Component.literal("¡" + name + " intento tomar un cubo de leche mientras tenia la maldición de ")
                                       .withStyle(ChatFormatting.LIGHT_PURPLE)
                                       .append(
                                          Component.literal("The Beginning")
                                             .withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD})
                                             .append(Component.literal("!").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(Style.EMPTY.withBold(false)))
                                       ),
                                    false
                                 );
                                 p.connection
                                    .send(
                                       new ClientboundSoundPacket(
                                          BuiltInRegistries.SOUND_EVENT.wrapAsHolder((SoundEvent)SoundEvents.TRIDENT_THUNDER.value()),
                                          SoundSource.VOICE,
                                          p.getX(),
                                          p.getY(),
                                          p.getZ(),
                                          0.5F,
                                          1.25F,
                                          seed
                                       )
                                    );
                              }
                           );
                        player.hurt(player.level().damageSources().genericKill(), Float.MAX_VALUE);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean hasItem(Inventory inv, Item item) {
      for (ItemStack stack : inv.items) {
         if (stack.is(item)) {
            return true;
         }
      }

      return false;
   }
}
