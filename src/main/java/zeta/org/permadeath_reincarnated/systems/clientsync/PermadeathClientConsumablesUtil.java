package zeta.org.permadeath_reincarnated.systems.clientsync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public final class PermadeathClientConsumablesUtil {
   private PermadeathClientConsumablesUtil() {
   }

   public static boolean isConsumableLike(ItemStack stack, LocalPlayer player) {
      if (stack.isEmpty()) {
         return false;
      }

      if (stack.get(DataComponents.FOOD) != null) {
         return true;
      }

      if (stack.getUseDuration(player) <= 0) {
         return false;
      }

      UseAnim useAnimation = stack.getUseAnimation();
      return useAnimation == UseAnim.EAT || useAnimation == UseAnim.DRINK;
   }

   public static boolean hasConsumableInHands() {
      LocalPlayer player = Minecraft.getInstance().player;
      return player == null ? false : isConsumableLike(player.getMainHandItem(), player) || isConsumableLike(player.getOffhandItem(), player);
   }
}
