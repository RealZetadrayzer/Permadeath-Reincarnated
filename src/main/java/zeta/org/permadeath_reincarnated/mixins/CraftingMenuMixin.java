package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.items.PermadeathItemsRegistry;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.timers.LifeOrbTimerGlobalState;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {
   @Shadow
   @Final
   private ResultContainer resultSlots;
   @Shadow
   @Final
   private Player player;

   @Inject(method = "slotsChanged(Lnet/minecraft/world/Container;)V", at = @At("TAIL"))
   private void permadeath$blockItemPreview(Container container, CallbackInfo ci) {
      ItemStack result = this.resultSlots.getItem(0);
      int day = DayGlobalCount.CURRENT_DAY;
      if (!result.isEmpty()) {
         if (!this.player.isSpectator() && !this.player.isCreative()) {
            if (day < 20 && (result.is(Items.SPLASH_POTION) || result.is(Items.LINGERING_POTION))) {
               CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               CompoundTag tag = data.copyTag();
               if (tag.getInt("permadeath_wither_rage") == 1 || tag.getInt("permadeath_reincarnation") == 1 || tag.getInt("permadeath_shock") == 1) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
               }
            }

            if (day < 25
               && (
                  result.is((Item)PermadeathItemsRegistry.CUSTOM_NETHERITE_UPGRADE_TEMPLATE.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_HELMET.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_LEGGINGS.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_NETHERITE_BOOTS.get())
                     || result.is(Items.MACE)
               )) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

            if (day < 40
               && (
                  result.is((Item)PermadeathItemsRegistry.PERMA_END_RELIC.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_HYPER_GOLDEN_APPLE.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_SUPER_GOLDEN_APPLE.get())
               )) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

            if (day < 45) {
               if (result.is((Item)PermadeathItemsRegistry.PERMA_ENHANCED_TCND.get())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
               }

               if (result.is(Items.POTION)) {
                  CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                  CompoundTag tag = data.copyTag();
                  if (tag.getInt("permadeath_resurrection") == 1 || tag.getInt("permadeath_beginning") == 1) {
                     this.resultSlots.setItem(0, ItemStack.EMPTY);
                  }
               }

               if (result.is(Items.SPLASH_POTION) || result.is(Items.LINGERING_POTION)) {
                  CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                  CompoundTag tag = data.copyTag();
                  if (tag.getInt("permadeath_resurrection") == 1 || tag.getInt("permadeath_beginning") == 1) {
                     this.resultSlots.setItem(0, ItemStack.EMPTY);
                  }
               }
            }

            if (day < 50
               && (
                  result.is((Item)PermadeathItemsRegistry.CUSTOM_INFERNAL_NETHERITE_UPGRADE_TEMPLATE.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_HELMET.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_CHESTPLATE.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_LEGGINGS.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_NETHERITE_BOOTS.get())
               )) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

            if (day < 55) {
               if (result.is((Item)PermadeathItemsRegistry.PERMA_END_ORB.get())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
               }

               if (result.is(Items.SHIELD)) {
                  CustomData data = (CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                  CompoundTag tag = data.copyTag();
                  if (tag.getInt("permadeath_beg_shield") == 1) {
                     this.resultSlots.setItem(0, ItemStack.EMPTY);
                  }
               }
            }

            if (day < 60
               && (
                  result.is((Item)PermadeathItemsRegistry.PERMA_INFERNAL_ELYTRA.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_BEG_RELIC.get())
                     || result.is((Item)PermadeathItemsRegistry.PERMA_EXTRA_HYPER_GOLDEN_APPLE.get())
               )) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

            if (day >= 40 && (result.is(Items.TORCH) || result.is(Items.REDSTONE_TORCH))) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
            }

            if (result.is((Item)PermadeathItemsRegistry.PERMA_LIFE_ORB.get())) {
               if (day < 60) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
               }

               if (day >= 60 && LifeOrbTimerGlobalState.CURRENT_STATE == 2) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
               }
            }
         }
      }
   }
}
