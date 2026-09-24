package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

@EventBusSubscriber
public class PlayerEmperorSwordHandler {
   @SubscribeEvent
   private static void onEffectTick(Post event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            if (event.getEntity() instanceof ServerPlayer player) {
               if (!player.level().isClientSide) {
                  if (player.tickCount % 10 == 0) {
                     if (hasEmperorSword(player)) {
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, true, true, true));
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_IMPERIAL_STRENGTH_ID);
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean hasEmperorSword(ServerPlayer player) {
      return player.getMainHandItem().is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get())
         || player.getOffhandItem().is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_SWORD.get());
   }
}
