package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(EnderpearlItem.class)
public class EnderPearlItemMixin {
   @Inject(
      method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void permadeath$useAdjustCooldown(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
      ItemStack itemstack = player.getItemInHand(hand);
      int cooldown = DayGlobalCount.CURRENT_DAY >= 60 ? 40 : 20;
      player.getCooldowns().addCooldown(itemstack.getItem(), cooldown);
      level.playSound(
         null,
         player.getX(),
         player.getY(),
         player.getZ(),
         SoundEvents.ENDER_PEARL_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      if (!level.isClientSide) {
         ThrownEnderpearl thrown = new ThrownEnderpearl(level, player);
         thrown.setItem(itemstack);
         thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
         level.addFreshEntity(thrown);
      }

      player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
      if (!player.isCreative() && !player.isSpectator()) {
         itemstack.shrink(1);
      }

      cir.setReturnValue(InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide()));
   }
}
