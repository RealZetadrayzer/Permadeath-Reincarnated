package zeta.org.permadeath_reincarnated.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class VindicatorChanges {
   private static final Map<Holder<MobEffect>, Integer> EFFECTS_NO_GLOWING;

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (!entity.level().isClientSide) {
         if (entity.level() instanceof ServerLevel level) {
            if (entity instanceof LivingEntity) {
               if (entity instanceof Vindicator vindicator) {
                  if (!event.loadedFromDisk()) {
                     int day = DayGlobalCount.CURRENT_DAY;
                     if (day >= 50) {
                        ItemStack diamondAxe = new ItemStack(Items.DIAMOND_AXE);
                        RegistryAccess registryAccess = vindicator.level().registryAccess();
                        Holder<Enchantment> sharpness = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS);
                        diamondAxe.enchant(sharpness, 5);
                        vindicator.setItemSlot(EquipmentSlot.MAINHAND, diamondAxe);
                        vindicator.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                        vindicator.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                     }

                     if (day >= 60) {
                        List<Entry<Holder<MobEffect>, Integer>> shuffled = new ArrayList<>(EFFECTS_NO_GLOWING.entrySet());
                        Collections.shuffle(shuffled);
                        int count = 5;
                        count = Math.min(count, shuffled.size());

                        for (int i = 0; i < count; i++) {
                           Entry<Holder<MobEffect>, Integer> entry = shuffled.get(i);
                           vindicator.addEffect(new MobEffectInstance(entry.getKey(), -1, entry.getValue(), false, true));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static {
      Map<Holder<MobEffect>, Integer> map = new HashMap<>();
      map.put(MobEffects.MOVEMENT_SPEED, 2);
      map.put(MobEffects.DAMAGE_BOOST, 3);
      map.put(MobEffects.JUMP, 4);
      map.put(MobEffects.REGENERATION, 3);
      map.put(MobEffects.INVISIBILITY, 0);
      map.put(MobEffects.SLOW_FALLING, 0);
      map.put(MobEffects.DAMAGE_RESISTANCE, 2);
      EFFECTS_NO_GLOWING = Collections.unmodifiableMap(map);
   }
}
