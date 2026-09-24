package zeta.org.permadeath_reincarnated.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

public class PermadeathSuperGoldenApple extends Item {
   public PermadeathSuperGoldenApple(Properties properties, FoodProperties food) {
      super(properties.food(food));
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      ItemStack result = super.finishUsingItem(stack, level, entity);
      if (!level.isClientSide && entity instanceof ServerPlayer player) {
         PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_CONSUME_SUPER_GOLDEN_APPLE_ID);
      }

      return result;
   }
}
