package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;

@EventBusSubscriber
public class CodChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Cod cod) {
                  if (!event.loadedFromDisk()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50) {
                        ItemStack woodenSword = new ItemStack(Items.WOODEN_SWORD);
                        RegistryAccess registryAccess = cod.level().registryAccess();
                        Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
                        Holder<Enchantment> knockback = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.KNOCKBACK);
                        woodenSword.enchant(sharpness, 50);
                        woodenSword.enchant(knockback, 100);
                        cod.setItemSlot(EquipmentSlot.MAINHAND, woodenSword);
                        cod.setCustomName(Component.literal("Antonio de la Muerte").withStyle(ChatFormatting.GOLD));
                        MobPreventEquipmentDrops.preventAllHandsDrop(cod);
                     }
                  }
               }
            }
         }
      }
   }
}
