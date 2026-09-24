package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

@EventBusSubscriber
public class EndEffectsHandler {
   @SubscribeEvent
   public static void onSlowfallingTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            if (event.getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  if (player.tickCount % 20 == 0) {
                     if (!player.isSpectator()) {
                        if (player.level().dimension() == Level.END) {
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
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               if (player.level().dimension().equals(Level.END)) {
                  if (!player.isSpectator()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     long seed = player.level().getRandom().nextLong();
                     if (day >= 60) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(25000);
                        if (chance <= 1) {
                           player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 4, false, true));
                           player.connection
                              .send(
                                 new ClientboundSoundPacket(
                                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.TRIDENT_RETURN),
                                    SoundSource.VOICE,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    100.0F,
                                    0.5F,
                                    seed
                                 )
                              );
                           PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_INSTANT_DAMAGE_PROBABILITY_ID);
                        }
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
