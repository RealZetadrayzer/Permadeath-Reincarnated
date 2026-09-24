package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class BrokenElytras {
   @SubscribeEvent
   public static void onShulkerSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof Shulker shulker) {
         if (shulker.level() instanceof ServerLevel level) {
            if (!level.isClientSide) {
               if (DayGlobalCount.CURRENT_DAY >= 40) {
                  if (shulker.getSpawnType() != MobSpawnType.SPAWN_EGG
                     && shulker.getSpawnType() != MobSpawnType.COMMAND
                     && shulker.getSpawnType() != MobSpawnType.SPAWNER
                     && shulker.getSpawnType() != MobSpawnType.TRIAL_SPAWNER
                     && shulker.getSpawnType() != MobSpawnType.MOB_SUMMONED) {
                     if (level.dimension() == Level.END) {
                        ScheduleInTicks.schedule(() -> markNearbyElytrasBroken(shulker), 20);
                     }
                  }
               }
            }
         }
      }
   }

   private static void markNearbyElytrasBroken(Shulker shulker) {
      ServerLevel world = (ServerLevel)shulker.level();
      BlockPos shulkerPos = shulker.blockPosition();

      for (ItemFrame frame : world.getEntitiesOfClass(ItemFrame.class, new AABB(shulkerPos).inflate(5.0), framex -> framex.getItem().getItem() == Items.ELYTRA)) {
         ItemStack elytra = frame.getItem();
         elytra.setDamageValue(elytra.getMaxDamage() - 1);
         frame.setItem(elytra, false);
      }
   }
}
