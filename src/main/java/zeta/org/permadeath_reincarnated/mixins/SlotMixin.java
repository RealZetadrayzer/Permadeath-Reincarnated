package zeta.org.permadeath_reincarnated.mixins;

import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.items.PermadeathDataComponents;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalState;

@Mixin(Slot.class)
public abstract class SlotMixin {
   @Shadow
   @Final
   public Container container;

   @Shadow
   public abstract ItemStack getItem();

   @Shadow
   public abstract int getSlotIndex();

   @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
   private void permadeath$blockItemPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
      if (!player.isCreative() && !player.isSpectator()) {
         int day = DayGlobalCount.CURRENT_DAY;
         if (day < 20) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
               CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_wither_rage") == 1 || tag.getInt("permadeath_reincarnation") == 1 || tag.getInt("permadeath_shock") == 1) {
                  cir.setReturnValue(false);
               }
            }
         }

         if (day < 25) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_HELMET.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS.get())
               || stack.is(Items.MACE)) {
               cir.setReturnValue(false);
            }
         }

         if (day < 40) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.PERMA_HYPER_GOLDEN_APPLE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_SUPER_GOLDEN_APPLE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_END_RELIC.get())) {
               cir.setReturnValue(false);
            }
         }

         if (day < 45) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get())) {
               cir.setReturnValue(false);
            }

            if (stack.is(Items.POTION)) {
               CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_resurrection") == 1 || tag.getInt("permadeath_beginning") == 1) {
                  cir.setReturnValue(false);
               }
            }

            if (stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) {
               CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_resurrection") == 1 || tag.getInt("permadeath_beginning") == 1) {
                  cir.setReturnValue(false);
               }
            }
         }

         if (day < 50) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.CUSTOM_INFERNAL_NETHERITE_UPGRADE_TEMPLATE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_HELMET.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_LEGGINGS.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_BOOTS.get())) {
               cir.setReturnValue(false);
            }
         }

         if (day < 55) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.PERMA_END_ORB.get())) {
               cir.setReturnValue(false);
            }

            if (stack.is(Items.SHIELD)) {
               CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_beg_shield") == 1) {
                  cir.setReturnValue(false);
               }
            }
         }

         if (day < 60) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.PERMA_EXTRA_HYPER_GOLDEN_APPLE.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_BEG_RELIC.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get())
               || stack.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_ELYTRA.get())) {
               cir.setReturnValue(false);
            }
         }

         if (day >= 40) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is(Items.TORCH) || stack.is(Items.REDSTONE_TORCH)) {
               cir.setReturnValue(false);
            }
         }

         if (day >= 60 && LifeOrbTimerGlobalState.CURRENT_STATE == 2) {
            if (!(this.container instanceof ResultContainer)) {
               return;
            }

            if (this.getSlotIndex() != 0) {
               return;
            }

            ItemStack stack = this.getItem();
            if (stack.isEmpty()) {
               return;
            }

            if (stack.is((Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get())) {
               cir.setReturnValue(false);
            }
         }
      }
   }

   @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
   private void blockPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
      ItemStack stack = this.getItem();
      if (stack.is((Item)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get())) {
         CompoundTag tag = (CompoundTag)stack.get((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get());
         if (tag != null && tag.getBoolean("locked_slot")) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
   private void blockPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      ItemStack current = this.getItem();
      if (current.is((Item)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get())) {
         CompoundTag tag = (CompoundTag)current.get((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get());
         if (tag != null && tag.getBoolean("locked_slot")) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(method = "tryRemove", at = @At("HEAD"), cancellable = true)
   private void blockLockedVoids(int count, int decrement, Player player, CallbackInfoReturnable<Optional<ItemStack>> cir) {
      ItemStack stack = this.getItem();
      if (stack.is((Item)PermadeathItemsRegistry.PERMA_STRUCTURE_VOID.get())) {
         CompoundTag tag = (CompoundTag)stack.get((DataComponentType)PermadeathDataComponents.LOCKED_SLOT_MARKER.get());
         if (tag != null && tag.getBoolean("locked_slot")) {
            cir.setReturnValue(Optional.empty());
         }
      }
   }
}
