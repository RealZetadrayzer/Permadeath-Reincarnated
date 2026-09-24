package zeta.org.permadeath_reincarnated.mobEffects;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import zeta.org.permadeath_reincarnated.PermadeathAttributes;

public class PermadeathMobEffectBuilder {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "permadeath_reincarnated");
   public static final Holder<MobEffect> POST_MORTEM_EFFECT = MOB_EFFECTS.register(
      "post_mortem",
      () -> new PostMortemEffect(MobEffectCategory.BENEFICIAL, 6353558)
         .addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "post_mortem"), 8.0, Operation.ADD_VALUE)
         .addAttributeModifier(
            Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "post_mortem"), 0.15F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> INFERNAL_MORTEM_EFFECT = MOB_EFFECTS.register(
      "infernal_mortem",
      () -> new InfernalMortemEffect(MobEffectCategory.BENEFICIAL, 8204968)
         .addAttributeModifier(
            Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_mortem"), 10.0, Operation.ADD_VALUE
         )
         .addAttributeModifier(
            Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "infernal_mortem"), 0.2F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final Holder<MobEffect> ARMOR_BREACH_EFFECT = MOB_EFFECTS.register(
      "armor_breach",
      () -> new ArmorBreachEffect(MobEffectCategory.BENEFICIAL, 16121855)
         .addAttributeModifier(
            PermadeathAttributes.ARMOR_BREACH.getDelegate(),
            ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "armor_breach"),
            0.2F,
            Operation.ADD_VALUE
         )
   );
   public static final Holder<MobEffect> WITHER_TIMER_EFFECT = MOB_EFFECTS.register(
      "wither_timer", () -> new WitherTimerEffect(MobEffectCategory.BENEFICIAL, 14745855)
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECTS.register(eventBus);
   }
}
