package zeta.org.permadeath_reincarnated.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;

public class PermadeathHyperGoldenApple extends Item {
   private static final ResourceLocation HEALTH_BOOST_ID = ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "hyper_golden_apple_one");
   public static final String EATEN_KEY = "permadeath:hyper_golden_apple";

   public PermadeathHyperGoldenApple(Properties properties, FoodProperties food) {
      super(properties.food(food));
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      ItemStack result = super.finishUsingItem(stack, level, entity);
      if (!level.isClientSide && entity instanceof ServerPlayer player) {
         CompoundTag tag = player.getPersistentData();
         boolean alreadyEaten = tag.getBoolean("permadeath:hyper_golden_apple");
         if (!alreadyEaten) {
            tag.putBoolean("permadeath:hyper_golden_apple", true);
            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null && healthAttr.getModifier(HEALTH_BOOST_ID) == null) {
               AttributeModifier modifier = new AttributeModifier(HEALTH_BOOST_ID, 4.0, Operation.ADD_VALUE);
               healthAttr.addPermanentModifier(modifier);
               if (player.getHealth() > player.getMaxHealth()) {
                  player.setHealth(player.getMaxHealth());
               }
            }

            PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_CONSUME_HYPER_GOLDEN_APPLE_ID);
            player.sendSystemMessage(Component.literal("¡Has aumentado tus contenedores!").withStyle(ChatFormatting.GREEN));
         } else {
            player.sendSystemMessage(Component.literal("¡Solo puedes comer una Hyper Golden Apple+!").withStyle(ChatFormatting.RED));
         }
      }

      return result;
   }
}
