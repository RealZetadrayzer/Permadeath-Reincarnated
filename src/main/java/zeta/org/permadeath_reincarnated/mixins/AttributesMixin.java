package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Attributes.class)
public abstract class AttributesMixin {
   @Mutable
   @Final
   @Shadow
   public static Holder<Attribute> ARMOR;
   @Mutable
   @Final
   @Shadow
   public static Holder<Attribute> ARMOR_TOUGHNESS;

   @Shadow
   private static Holder<Attribute> register(String name, Attribute attribute) {
      throw new UnsupportedOperationException();
   }

   @Inject(method = "<clinit>", at = @At("TAIL"))
   private static void permadeathReincarnated$increaseArmorCap(CallbackInfo ci) {
      ARMOR = register("generic.armor", new RangedAttribute("attribute.name.generic.armor", 0.0, 0.0, 1024.0).setSyncable(true));
      ARMOR_TOUGHNESS = register("generic.armor_toughness", new RangedAttribute("attribute.name.generic.armor_toughness", 0.0, 0.0, 1024.0).setSyncable(true));
   }
}
