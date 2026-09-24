package zeta.org.permadeath_reincarnated.items.essences;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class PermadeathImmortalityEssence extends Item {
   public PermadeathImmortalityEssence(Properties properties) {
      super(properties);
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }
}
