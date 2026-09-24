package zeta.org.permadeath_reincarnated.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class PermadeathLifeOrb extends Item {
   private static final ResourceLocation ORB_ITEM_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "has_life_orb");
   public static final String HAS_ORB_KEY = "permadeath:life_orb";

   public PermadeathLifeOrb(Properties properties) {
      super(properties);
   }
}
