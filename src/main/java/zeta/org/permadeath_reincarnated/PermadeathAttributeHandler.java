package zeta.org.permadeath_reincarnated;

import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(modid = "permadeath_reincarnated")
public class PermadeathAttributeHandler {
   @SubscribeEvent
   public static void applyAttributes(EntityAttributeModificationEvent entity) {
      entity.getTypes().forEach(type -> addAll(type, entity::add, PermadeathAttributes.ARMOR_BREACH, PermadeathAttributes.ECHO_GUARD));
   }

   @SafeVarargs
   private static void addAll(
      EntityType<? extends LivingEntity> type, BiConsumer<EntityType<? extends LivingEntity>, Holder<Attribute>> add, Holder<Attribute>... attribs
   ) {
      for (Holder<Attribute> a : attribs) {
         add.accept(type, a);
      }
   }
}
