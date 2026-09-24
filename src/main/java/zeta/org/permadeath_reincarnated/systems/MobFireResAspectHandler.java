package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class MobFireResAspectHandler {
   @SubscribeEvent
   public static void onTick(Post event) {
      if (event.getEntity() instanceof Enemy enemy) {
         if (enemy instanceof Mob mob) {
            if (!mob.level().isClientSide) {
               if (mob.tickCount % 20 == 0) {
                  if (!mob.fireImmune()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50 && day < 60) {
                        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, true));
                     } else if (day >= 60) {
                        ItemStack weapon = getHeldWeapon(mob);
                        if (weapon.isEmpty()) {
                           return;
                        }

                        applyFireEnchant(mob, weapon);
                     }
                  }
               }
            }
         }
      }
   }

   @NotNull
   private static ItemStack getHeldWeapon(Mob mob) {
      ItemStack main = mob.getMainHandItem();
      if (!main.isEmpty()) {
         return main;
      }

      ItemStack off = mob.getOffhandItem();
      return !off.isEmpty() ? off : ItemStack.EMPTY;
   }

   private static void applyFireEnchant(Mob mob, ItemStack stack) {
      RegistryAccess access = mob.level().registryAccess();
      Item item = stack.getItem();
      if (item instanceof BowItem) {
         Holder<Enchantment> flame = access.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FLAME);
         int level = EnchantmentHelper.getTagEnchantmentLevel(flame, stack);
         if (level < 1) {
            stack.enchant(flame, 1);
         }
      } else {
         if (item instanceof SwordItem || item instanceof AxeItem) {
            Holder<Enchantment> fireAspect = access.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FIRE_ASPECT);
            int level = EnchantmentHelper.getTagEnchantmentLevel(fireAspect, stack);
            if (level < 2) {
               stack.enchant(fireAspect, 2);
            }
         }
      }
   }
}
