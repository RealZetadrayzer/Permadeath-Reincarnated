package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.entities.CustomGiant;

@EventBusSubscriber
public class GiantChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof CustomGiant giant) {
                  if (!event.loadedFromDisk()) {
                     giant.setCustomName(Component.literal("Zombi Gigante").withStyle(ChatFormatting.DARK_GREEN));
                     giant.addTag("giantZombie");
                  }
               }
            }
         }
      }
   }
}
