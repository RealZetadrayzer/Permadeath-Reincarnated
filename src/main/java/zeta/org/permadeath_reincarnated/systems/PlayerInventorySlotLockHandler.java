package zeta.org.permadeath_reincarnated.systems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import zeta.org.permadeath_reincarnated.items.PermadeathDataComponents;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;

@EventBusSubscriber
public class PlayerInventorySlotLockHandler {
   private static final String LOCK_TAG = "locked_slot";
   private static final int[] LOCKED_SLOTS_D40 = new int[]{4, 13, 22, 31, 40};
   private static final int[] LOCKED_SLOTS_D60 = new int[]{4, 13, 22, 31};
   private static final int[] EXTRA_SLOTS_D60 = new int[]{7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34, 40};

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (player.tickCount % 20 == 0) {
               Inventory inv = player.getInventory();
               int day = DayGlobalCount.CURRENT_DAY;
               boolean hasEndRelic = hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_END_RELIC.get());
               boolean hasBegRelic = hasItem(inv, (Item)PermadeathItemsRegistry.PERMA_BEG_RELIC.get());
               int[] currentLockedSlots;
               int[] currentBegSlots;
               if (day < 40) {
                  currentLockedSlots = new int[0];
                  currentBegSlots = new int[0];
               } else if (day < 60) {
                  currentLockedSlots = LOCKED_SLOTS_D40;
                  currentBegSlots = new int[0];
               } else {
                  currentLockedSlots = LOCKED_SLOTS_D60;
                  currentBegSlots = EXTRA_SLOTS_D60;
               }

               purgeObsoleteVoids(inv, currentLockedSlots, currentBegSlots);
               if (day >= 40 && day < 60) {
                  if (!hasEndRelic && !hasBegRelic) {
                     enforceSlots(player, inv, LOCKED_SLOTS_D40);
                  } else {
                     purgeSlots(inv, LOCKED_SLOTS_D40);
                  }
               } else if (day >= 60) {
                  if (hasEndRelic) {
                     purgeSlots(inv, LOCKED_SLOTS_D60);
                  }

                  if (hasBegRelic) {
                     purgeSlots(inv, LOCKED_SLOTS_D60);
                     purgeSlots(inv, EXTRA_SLOTS_D60);
                  }

                  Set<Integer> enforceSet = new HashSet<>();

                  for (int slot : EXTRA_SLOTS_D60) {
                     if (!hasBegRelic && (!hasEndRelic || !contains(LOCKED_SLOTS_D60, slot))) {
                        enforceSet.add(slot);
                     }
                  }

                  enforceSlots(player, inv, enforceSet.stream().mapToInt(i -> i).toArray());
                  if (!hasEndRelic && !hasBegRelic) {
                     enforceSlots(player, inv, LOCKED_SLOTS_D60);
                  }
               }

               player.inventoryMenu.broadcastChanges();
            }
         }
      }
   }

   @SubscribeEvent
   public static void onItemSpawn(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof ItemEntity item) {
         ItemStack stack = item.getItem();
         if (stack.is((Item)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get())) {
            CompoundTag tag = (CompoundTag)stack.get((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get());
            if (tag != null && tag.getBoolean("locked_slot")) {
               Objects.requireNonNull(item.level().getServer())
                  .tell(new TickTask(Objects.requireNonNull(item.level().getServer()).getTickCount(), () -> item.remove(RemovalReason.DISCARDED)));
            }
         }
      }
   }

   private static void purgeObsoleteVoids(Inventory inv, int[] currentLockedSlots, int[] currentBegSlots) {
      Set<Integer> allowed = new HashSet<>();
      Arrays.stream(currentLockedSlots).forEach(allowed::add);
      Arrays.stream(currentBegSlots).forEach(allowed::add);

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack stack = inv.getItem(i);
         if (isLockedVoid(stack) && !allowed.contains(i)) {
            inv.setItem(i, ItemStack.EMPTY);
         }
      }
   }

   private static void enforceSlots(ServerPlayer player, Inventory inv, int[] slots) {
      for (int slot : slots) {
         ItemStack stack = inv.getItem(slot);
         if (!isLockedVoid(stack)) {
            if (!stack.isEmpty()) {
               player.drop(stack.copy(), false);
            }

            inv.setItem(slot, createLockedVoid());
         }
      }
   }

   private static ItemStack createLockedVoid() {
      ItemStack stack = new ItemStack((ItemLike)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get());
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("locked_slot", true);
      stack.set((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get(), tag);
      return stack;
   }

   private static boolean isLockedVoid(ItemStack stack) {
      if (!stack.is((Item)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get())) {
         return false;
      }

      CompoundTag tag = (CompoundTag)stack.get((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get());
      return tag != null && tag.getBoolean("locked_slot");
   }

   private static void purgeSlots(Inventory inv, int[] slots) {
      for (int slot : slots) {
         ItemStack stack = inv.getItem(slot);
         if (isLockedVoid(stack)) {
            inv.setItem(slot, ItemStack.EMPTY);
         }
      }
   }

   private static boolean hasItem(Inventory inv, Item item) {
      for (ItemStack stack : inv.items) {
         if (stack.is(item)) {
            return true;
         }
      }

      return false;
   }

   private static boolean contains(int[] arr, int val) {
      for (int i : arr) {
         if (i == val) {
            return true;
         }
      }

      return false;
   }
}
