package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

@EventBusSubscriber
public class ZombieVillagerChanges {
   @SubscribeEvent
   public static void onEntityInteract(EntityInteract event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 10) {
            Player player = event.getEntity();
            if (!player.level().isClientSide) {
               if (!player.isSpectator() && !player.isCreative()) {
                  if (event.getTarget() instanceof ZombieVillager zombieVillager) {
                     InteractionHand hand = event.getHand();
                     if (player.getItemInHand(hand).is(Items.GOLDEN_APPLE) && zombieVillager.hasEffect(MobEffects.WEAKNESS)) {
                        PlayerAdvancementsHandler.award((ServerPlayer)player, PlayerAdvancementsHandler.ZOMBIE_VILLAGER_TRY_CURE_ID);
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }
   }
}
