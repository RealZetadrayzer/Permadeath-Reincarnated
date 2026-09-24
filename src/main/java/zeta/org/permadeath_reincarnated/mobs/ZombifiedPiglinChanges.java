package zeta.org.permadeath_reincarnated.mobs;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.MobPreventEquipmentDrops;
import zeta.org.permadeath_reincarnated.systems.PiglinRiderHelper;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@EventBusSubscriber
public class ZombifiedPiglinChanges {
   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof ZombifiedPiglin zombifiedPiglin) {
         if (!zombifiedPiglin.level().isClientSide) {
            if (zombifiedPiglin.level() instanceof ServerLevel level) {
               if (!event.loadedFromDisk()) {
                  if (!zombifiedPiglin.level().isClientSide) {
                     if (day >= 30) {
                        int chance = 1 + RandomUtil.RANDOM.nextInt(100);
                        int chanceThreshold = day >= 50 ? 80 : (day >= 40 ? 95 : 100);
                        if (chance <= chanceThreshold) {
                           setupFullDiamondZPiglin(zombifiedPiglin);
                        } else {
                           PiglinRiderHelper.spawnRandomZPiglin(level, zombifiedPiglin);
                        }

                        zombifiedPiglin.setCanPickUpLoot(false);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onInteract(EntityInteract event) {
      if (!event.getLevel().isClientSide()) {
         if (event.getTarget() instanceof Villager villager) {
            if (villager.getTags().contains("fromZPiglin")) {
               event.setCanceled(true);
               event.setCancellationResult(InteractionResult.FAIL);
            }
         }
      }
   }

   private static void setupFullDiamondZPiglin(ZombifiedPiglin zombifiedPiglin) {
      ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
      ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
      ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
      MobPreventEquipmentDrops.preventAllEquipmentDrop(zombifiedPiglin);
      zombifiedPiglin.setItemSlot(EquipmentSlot.HEAD, helmet);
      zombifiedPiglin.setItemSlot(EquipmentSlot.CHEST, chestplate);
      zombifiedPiglin.setItemSlot(EquipmentSlot.LEGS, leggings);
      zombifiedPiglin.setItemSlot(EquipmentSlot.FEET, boots);
      zombifiedPiglin.setCustomName(Component.literal("Zombie-Piglin Full Diamante").withStyle(ChatFormatting.AQUA));
   }
}
