package zeta.org.permadeath_reincarnated;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathAttributes {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "permadeath_reincarnated");
   public static final Holder<Attribute> ARMOR_BREACH = ATTRIBUTES.register(
      "armor_breach", () -> new PercentageAttribute("attribute.permadeath_reincarnated.armor_breach", 0.0, -1024.0, 1024.0).setSyncable(true)
   );
   public static final Holder<Attribute> ECHO_GUARD = ATTRIBUTES.register(
      "echo_guard", () -> new PercentageAttribute("attribute.permadeath_reincarnated.echo_guard", 0.0, -1024.0, 1024.0).setSyncable(true)
   );
}
