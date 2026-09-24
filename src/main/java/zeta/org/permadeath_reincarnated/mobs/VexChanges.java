package zeta.org.permadeath_reincarnated.mobs;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class VexChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof Vex vex) {
         if (!event.loadedFromDisk()) {
            if (!vex.level().isClientSide) {
               if (day >= 50) {
                  vex.setCustomName(Component.literal("Súper Vex").withStyle(ChatFormatting.DARK_PURPLE));
                  vex.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, true));
               }

               if (day >= 60) {
                  vex.setCustomName(Component.literal("Mega Vex").withStyle(ChatFormatting.DARK_PURPLE));
                  vex.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, true));
                  Objects.requireNonNull(vex.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(7.0);
               }

               if (vex.level().dimension().location().toString().equals("permadeath_reincarnated:the_beginning")) {
                  ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
                  RegistryAccess registryAccess = vex.level().registryAccess();
                  Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
                  Holder<Enchantment> knockback = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.KNOCKBACK);
                  endCrystal.enchant(sharpness, 10);
                  endCrystal.enchant(knockback, 50);
                  vex.setItemSlot(EquipmentSlot.MAINHAND, endCrystal);
                  vex.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                  vex.setCustomName(Component.literal("Vex Definitivo").withStyle(ChatFormatting.GOLD));
                  MobPreventEquipmentDrops.preventAllEquipmentDrop(vex);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      LivingEntity target = event.getEntity();
      Entity attacker = event.getSource().getEntity();
      if (attacker != null) {
         if (!target.level().isClientSide) {
            if (attacker instanceof Vex) {
               if (DayGlobalCount.CURRENT_DAY >= 60) {
                  int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                  if (chance <= 10) {
                     target.level().explode(attacker, target.getX(), target.getY(), target.getZ(), 7.0F, false, ExplosionInteraction.MOB);
                     if (target instanceof ServerPlayer player) {
                        PlayerAdvancementsHandler.award(player, PlayerAdvancementsHandler.PLAYER_VEX_EXPLOSION_DAMAGE_RECEIVED_ID);
                     }
                  }
               }
            }
         }
      }
   }
}
