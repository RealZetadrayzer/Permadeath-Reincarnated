package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class DrownedChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Drowned drowned) {
         if (!event.loadedFromDisk()) {
            if (!drowned.level().isClientSide) {
               if (day >= 50) {
                  boolean hasTrident = drowned.getMainHandItem().is(Items.TRIDENT) || drowned.getOffhandItem().is(Items.TRIDENT);
                  ItemStack trident = new ItemStack(Items.TRIDENT);
                  if (!drowned.hasCustomName()) {
                     drowned.setCustomName(Component.literal("Drowned con Tridente").withStyle(ChatFormatting.AQUA));
                  }

                  if (!hasTrident) {
                     drowned.setItemSlot(EquipmentSlot.MAINHAND, trident);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onTridentSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof ThrownTrident trident) {
         if (!event.loadedFromDisk()) {
            if (!trident.level().isClientSide) {
               if (trident.getOwner() instanceof Drowned) {
                  int day = DayGlobalCount.CURRENT_DAY;
                  if (day >= 60) {
                     trident.setBaseDamage(trident.getBaseDamage() * 3.0);
                  }
               }
            }
         }
      }
   }
}
