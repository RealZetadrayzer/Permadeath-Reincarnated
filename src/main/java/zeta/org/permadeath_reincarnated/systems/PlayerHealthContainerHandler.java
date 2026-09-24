package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalHandler;

@EventBusSubscriber
public class PlayerHealthContainerHandler {
   private static final ResourceLocation HEALTH_PENALTY_D40 = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "health_penalty_day40");
   private static final ResourceLocation HEALTH_PENALTY_D60 = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "health_penalty_day60");
   private static final ResourceLocation HYPER_APPLE_BOOST = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "hyper_golden_apple_one");
   private static final ResourceLocation EXTRA_HYPER_APPLE_BOOST = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "hyper_golden_apple_two");
   private static final ResourceLocation HEALTH_PENALTY_LIFE_ORB = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "health_penalty_life_orb");

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
               if (health != null) {
                  applyModifier(health, player, DayGlobalCount.CURRENT_DAY >= 40, HEALTH_PENALTY_D40, -8.0);
                  applyModifier(health, player, DayGlobalCount.CURRENT_DAY >= 60, HEALTH_PENALTY_D60, -8.0);
                  boolean shouldHaveAppleBoost = DayGlobalCount.CURRENT_DAY >= 40 && player.getPersistentData().getBoolean("permadeath:hyper_golden_apple");
                  applyModifier(health, player, shouldHaveAppleBoost, HYPER_APPLE_BOOST, 4.0);
                  boolean shouldHaveExtraAppleBoost = DayGlobalCount.CURRENT_DAY >= 60
                     && player.getPersistentData().getBoolean("permadeath:extra_hyper_golden_apple");
                  applyModifier(health, player, shouldHaveExtraAppleBoost, EXTRA_HYPER_APPLE_BOOST, 4.0);
                  updateOrbPersistentState(player);
                  boolean orbPenalty = shouldApplyOrbPenalty(player);
                  applyModifier(health, player, orbPenalty, HEALTH_PENALTY_LIFE_ORB, -16.0);
                  if (shouldInstantKill(player)) {
                     MinecraftServer server = player.server;
                     player.hurt(player.level().damageSources().genericKill(), Float.MAX_VALUE);
                     server.getPlayerList()
                        .broadcastSystemMessage(
                           Component.literal(player.getName().getString())
                              .withStyle(ChatFormatting.DARK_RED)
                              .append(Component.literal(" no contenia la ").withStyle(ChatFormatting.RED))
                              .append(Component.literal("Orbe de Vida").withStyle(ChatFormatting.DARK_RED))
                              .append(Component.literal(" en su inventario.").withStyle(ChatFormatting.RED)),
                           false
                        );
                  }
               }
            }
         }
      }
   }

   private static void applyModifier(AttributeInstance attr, ServerPlayer player, boolean shouldHave, ResourceLocation id, double amount) {
      boolean has = attr.getModifier(id) != null;
      if (shouldHave && !has) {
         attr.addPermanentModifier(new AttributeModifier(id, amount, Operation.ADD_VALUE));
         if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
         }
      } else if (!shouldHave && has) {
         attr.removeModifier(id);
      }
   }

   private static boolean playerHasLifeOrb(ServerPlayer player) {
      return player.getInventory().contains(new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_LIFE_ORB.get()));
   }

   private static boolean playerHasOrbFlag(ServerPlayer player) {
      return player.getPersistentData().getBoolean("permadeath:life_orb");
   }

   private static void updateOrbPersistentState(ServerPlayer player) {
      CompoundTag tag = player.getPersistentData();
      if (DayGlobalCount.CURRENT_DAY < 60) {
         tag.putBoolean("permadeath:life_orb", false);
      } else {
         boolean hasOrbItem = playerHasLifeOrb(player);
         boolean stored = tag.getBoolean("permadeath:life_orb");
         if (hasOrbItem && !stored) {
            tag.putBoolean("permadeath:life_orb", true);
         } else if (!hasOrbItem && stored) {
            tag.putBoolean("permadeath:life_orb", false);
         }
      }
   }

   private static boolean shouldApplyOrbPenalty(ServerPlayer player) {
      return DayGlobalCount.CURRENT_DAY >= 60 && LifeOrbTimerGlobalHandler.orbTimerFinished() && !playerHasOrbFlag(player);
   }

   private static boolean shouldInstantKill(ServerPlayer player) {
      return DayGlobalCount.CURRENT_DAY >= 60
         && LifeOrbTimerGlobalHandler.orbTimerFinished()
         && !playerHasOrbFlag(player)
         && player.getMaxHealth() <= 1.0F
         && !player.isSpectator()
         && !player.isCreative();
   }
}
