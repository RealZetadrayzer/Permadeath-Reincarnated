package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerAdvancementsHandler;
import zeta.org.permadeath_reincarnated.systems.RandomUtil;

@Mixin(FireworkRocketItem.class)
public class FireworkCooldownMixin {
   @Inject(method = "use", at = @At("RETURN"))
   private void permadeath$addCooldown(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
      if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
         if (DayGlobalCount.CURRENT_DAY >= 55) {
            int chance = 1 + RandomUtil.RANDOM.nextInt(100);
            int duration = DayGlobalCount.CURRENT_DAY >= 60 ? 200 : 60;
            int chanceThreshold = DayGlobalCount.CURRENT_DAY >= 60 ? 20 : 10;
            if (chance <= chanceThreshold && ((InteractionResultHolder)cir.getReturnValue()).getResult().consumesAction()) {
               player.getCooldowns().addCooldown((FireworkRocketItem)(Object)this, duration);
               if (player instanceof ServerPlayer serverPlayer) {
                  PlayerAdvancementsHandler.award(serverPlayer, PlayerAdvancementsHandler.PLAYER_LACK_OF_GUNPOWDER_ID);
               }
            }
         }
      }
   }
}
