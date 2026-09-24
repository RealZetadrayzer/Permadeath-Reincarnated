package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class PillagerChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Pillager pillager) {
         if (!event.loadedFromDisk()) {
            if (!pillager.level().isClientSide) {
               if (day >= 30) {
                  ItemStack crossbow = new ItemStack(Items.CROSSBOW);
                  RegistryAccess registryAccess = pillager.level().registryAccess();
                  Holder<Enchantment> quickCharge = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.QUICK_CHARGE);
                  crossbow.enchant(quickCharge, 5);
                  pillager.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, true));
                  pillager.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
                  pillager.setCustomName(Component.literal("Pillager Invisible").withStyle(ChatFormatting.GRAY));
                  pillager.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                  pillager.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onArrowSpawn(EntityJoinLevelEvent event) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (event.getEntity() instanceof AbstractArrow arrow) {
            if (!event.loadedFromDisk()) {
               if (!arrow.level().isClientSide) {
                  if (arrow.getOwner() instanceof Pillager) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 45) {
                        double extraDamage = 5.0;
                        double finalDamage = arrow.getBaseDamage() + extraDamage;
                        arrow.setBaseDamage(finalDamage);
                     }
                  }
               }
            }
         }
      }
   }
}
