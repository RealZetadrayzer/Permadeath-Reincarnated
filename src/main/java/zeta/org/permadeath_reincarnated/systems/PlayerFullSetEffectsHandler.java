package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathAttributes;
import zeta.org.permadeath_reincarnated.items.PermadeathArmorMaterials;
import zeta.org.permadeath_reincarnated.items.PermadeathInfernalElytra;
import zeta.org.permadeath_reincarnated.mobEffects.PermadeathMobEffectBuilder;

@EventBusSubscriber
public class PlayerFullSetEffectsHandler {
   @SubscribeEvent
   private static void onEffectTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 10 == 0) {
               if (hasFullPermaNetherite(player)) {
                  player.addEffect(new MobEffectInstance(PermadeathMobEffectBuilder.POST_MORTEM_EFFECT, 40, 0, true, true, true));
               }

               if (hasFullPermaInfernalNetherite(player)) {
                  player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, true, true));
                  player.addEffect(new MobEffectInstance(PermadeathMobEffectBuilder.INFERNAL_MORTEM_EFFECT, 40, 0, true, true, true));
               }
            }
         }
      }
   }

   @SubscribeEvent
   private static void onDamageReceived(LivingIncomingDamageEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            DamageSource source = event.getSource();
            if (source.is(DamageTypes.MAGIC)
               || source.is(DamageTypes.INDIRECT_MAGIC)
               || source.is(DamageTypes.SONIC_BOOM)
               || source.is(DamageTypes.DRAGON_BREATH)
               || source.is(DamageTypeTags.IS_FALL)) {
               double totalEchoGuard = getTotalEchoGuard(player);
               float reduction;
               if (source.is(DamageTypes.SONIC_BOOM)) {
                  reduction = (float)Math.min(totalEchoGuard, 0.8F);
               } else {
                  reduction = (float)Math.min(totalEchoGuard * 0.5, 0.2F);
               }

               float originalDamage = event.getOriginalAmount();
               float newDamage = Math.max(1.0F, originalDamage * (1.0F - reduction));
               event.setAmount(newDamage);
            }
         }
      }
   }

   private static boolean hasFullPermaNetherite(ServerPlayer player) {
      for (ItemStack stack : player.getArmorSlots()) {
         if (!(stack.getItem() instanceof ArmorItem armor)) {
            return false;
         }

         if (armor.getMaterial() != PermadeathArmorMaterials.PERMA_NETHERITE.getDelegate()) {
            return false;
         }
      }

      return true;
   }

   private static boolean hasFullPermaInfernalNetherite(ServerPlayer player) {
      if (!isPermaInfernalNetherite(player.getItemBySlot(EquipmentSlot.HEAD))) {
         return false;
      }

      if (!isPermaInfernalNetherite(player.getItemBySlot(EquipmentSlot.LEGS))) {
         return false;
      }

      if (!isPermaInfernalNetherite(player.getItemBySlot(EquipmentSlot.FEET))) {
         return false;
      }

      ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
      return chest.getItem() instanceof ArmorItem armor && armor.getMaterial() == PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate()
         || chest.getItem() instanceof PermadeathInfernalElytra;
   }

   private static boolean isPermaInfernalNetherite(ItemStack stack) {
      return stack.getItem() instanceof ArmorItem armor && armor.getMaterial() == PermadeathArmorMaterials.PERMA_INFERNAL_NETHERITE.getDelegate();
   }

   private static double getTotalEchoGuard(ServerPlayer player) {
      double[] total = new double[]{0.0};
      EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
      int i = 0;

      for (ItemStack stack : player.getArmorSlots()) {
         if (stack.isEmpty()) {
            i++;
         } else {
            EquipmentSlot slot = slots[i++];
            ItemAttributeModifiers modifiers = stack.getAttributeModifiers();
            modifiers.forEach(slot, (attributeHolder, modifier) -> {
               if (attributeHolder == PermadeathAttributes.ECHO_GUARD.value()) {
                  total[0] += modifier.amount();
               }
            });
         }
      }

      return total[0];
   }
}
