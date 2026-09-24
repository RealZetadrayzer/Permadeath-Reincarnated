package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

@EventBusSubscriber
public class PlayerFallFlyingHandler {
   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            if (!player.level().isClientSide) {
               if (player.tickCount % 20 == 0) {
                  if (player.isFallFlying()) {
                     if (!player.isSpectator()) {
                        int day = DayGlobalCount.CURRENT_DAY;
                        if (day >= 60) {
                           int chance = 1 + RandomUtil.RANDOM.nextInt(1000);
                           if (chance <= 1) {
                              player.stopFallFlying();
                              String elytraName = player.getItemBySlot(EquipmentSlot.CHEST).is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_ELYTRA.get())
                                 ? "elytras de netherite infernal"
                                 : "elytras";
                              player.displayClientMessage(Component.literal("¡Tus " + elytraName + " se cerraron!").withStyle(ChatFormatting.GRAY), false);
                              level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.MASTER, 1.0F, 1.2F);
                              level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.MASTER, 0.8F, 1.0F);
                              level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.MASTER, 0.7F, 0.7F);
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
