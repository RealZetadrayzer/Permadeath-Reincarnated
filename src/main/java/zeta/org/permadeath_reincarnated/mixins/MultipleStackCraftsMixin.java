package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.recipes.BegRelicRecipe;
import zeta.org.permadeath_reincarnated.recipes.BegShieldRecipe;
import zeta.org.permadeath_reincarnated.recipes.EndOrbRecipe;
import zeta.org.permadeath_reincarnated.recipes.EndRelicRecipe;
import zeta.org.permadeath_reincarnated.recipes.EnhancedTCNDRecipe;
import zeta.org.permadeath_reincarnated.recipes.ExtraHyperGoldenAppleRecipe;
import zeta.org.permadeath_reincarnated.recipes.HyperGoldenAppleRecipe;
import zeta.org.permadeath_reincarnated.recipes.InfernalElytraRecipe;
import zeta.org.permadeath_reincarnated.recipes.InfernalNetheriteUpgradeTemplateDupeRecipe;
import zeta.org.permadeath_reincarnated.recipes.InfernalNetheriteUpgradeTemplateRecipe;
import zeta.org.permadeath_reincarnated.recipes.LifeOrbRecipe;
import zeta.org.permadeath_reincarnated.recipes.MaceRecipe;
import zeta.org.permadeath_reincarnated.recipes.NetheriteUpgradeTemplateDupeRecipe;
import zeta.org.permadeath_reincarnated.recipes.NetheriteUpgradeTemplateRecipe;
import zeta.org.permadeath_reincarnated.recipes.SuperGoldenAppleRecipe;
import zeta.org.permadeath_reincarnated.recipes.original_recipes.BegRelicOriginalRecipe;
import zeta.org.permadeath_reincarnated.recipes.original_recipes.LifeOrbOriginalRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringReincarnationRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringShockRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.lingering.LingeringWitherRageRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.regular.RegularBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.regular.RegularResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashBeginningRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashReincarnationRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashResurrectionRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashShockRecipe;
import zeta.org.permadeath_reincarnated.recipes.potions.splash.SplashWitherRageRecipe;

@Mixin(ResultSlot.class)
public abstract class MultipleStackCraftsMixin {
   @Shadow
   @Final
   private CraftingContainer craftSlots;

   @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
   private void onTakeOverride(Player player, ItemStack stack, CallbackInfo ci) {
      if (ExtraHyperGoldenAppleRecipe.matchesContainer(this.craftSlots)) {
         for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
            ItemStack slotStack = this.craftSlots.getItem(i);
            if (!slotStack.isEmpty()) {
               int toConsume = ExtraHyperGoldenAppleRecipe.COUNTS[i];
               slotStack.shrink(toConsume);
               this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
            }
         }

         ci.cancel();
      } else {
         if (HyperGoldenAppleRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = HyperGoldenAppleRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SuperGoldenAppleRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SuperGoldenAppleRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (EndRelicRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = EndRelicRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (BegRelicRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = BegRelicRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (BegRelicOriginalRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = BegRelicOriginalRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LifeOrbRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LifeOrbRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LifeOrbOriginalRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LifeOrbOriginalRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (MaceRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = MaceRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (NetheriteUpgradeTemplateRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = NetheriteUpgradeTemplateRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (NetheriteUpgradeTemplateDupeRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = NetheriteUpgradeTemplateDupeRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (InfernalNetheriteUpgradeTemplateRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = InfernalNetheriteUpgradeTemplateRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (InfernalNetheriteUpgradeTemplateDupeRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = InfernalNetheriteUpgradeTemplateDupeRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SplashWitherRageRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SplashWitherRageRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SplashReincarnationRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SplashReincarnationRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SplashShockRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SplashShockRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LingeringWitherRageRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LingeringWitherRageRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LingeringShockRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LingeringShockRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LingeringReincarnationRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LingeringReincarnationRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (RegularResurrectionRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = RegularResurrectionRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (RegularBeginningRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = RegularBeginningRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SplashResurrectionRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SplashResurrectionRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (SplashBeginningRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = SplashBeginningRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LingeringResurrectionRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LingeringResurrectionRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (LingeringBeginningRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = LingeringBeginningRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (EnhancedTCNDRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = EnhancedTCNDRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (BegShieldRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = BegShieldRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (EndOrbRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = EndOrbRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }

         if (InfernalElytraRecipe.matchesContainer(this.craftSlots)) {
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
               ItemStack slotStack = this.craftSlots.getItem(i);
               if (!slotStack.isEmpty()) {
                  int toConsume = InfernalElytraRecipe.COUNTS[i];
                  slotStack.shrink(toConsume);
                  this.craftSlots.setItem(i, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
               }
            }

            ci.cancel();
         }
      }
   }
}
