package zeta.org.permadeath_reincarnated.systems;

import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

@EventBusSubscriber
public class PlayerBlockBreakDamageHandler {
   private static final Set<String> ALLOWED_ITEMS = Set.of(
      "permadeath_reincarnated:netherite_pickaxe",
      "permadeath_reincarnated:netherite_axe",
      "permadeath_reincarnated:netherite_sword",
      "permadeath_reincarnated:netherite_hoe",
      "permadeath_reincarnated:netherite_shovel",
      "minecraft:air"
   );

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      Player player = event.getPlayer();
      if (!player.level().isClientSide) {
         if (player.level() instanceof ServerLevel level) {
            int var6 = DayGlobalCount.CURRENT_DAY;
            String heldItemId = player.getMainHandItem().getItem().toString();
            if (!ALLOWED_ITEMS.contains(heldItemId)) {
               if (var6 < 50) {
                  return;
               }

               player.hurt(level.damageSources().generic(), var6 >= 60 ? 16.0F : 1.0F);
               if (player instanceof ServerPlayer serverPlayer && var6 >= 60) {
                  PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_BLOCK_BREAK_DAMAGE_RECEIVED_ID);
               }
            }
         }
      }
   }
}
