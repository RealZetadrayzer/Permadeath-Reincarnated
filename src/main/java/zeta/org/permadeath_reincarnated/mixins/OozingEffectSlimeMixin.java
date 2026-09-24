package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(targets = "net.minecraft.world.effect.OozingMobEffect")
public class OozingEffectSlimeMixin {
   @Inject(method = "spawnSlimeOffspring", at = @At("HEAD"), cancellable = true)
   private void onSpawnSlimeOffspring(Level level, double x, double y, double z, CallbackInfo ci) {
      int day = DayGlobalCount.CURRENT_DAY;
      if (day >= 25) {
         if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            Slime slime = (Slime)EntityType.SLIME.create(level);
            if (slime == null) {
               return;
            }

            slime.addTag("fromOozingSlime");
            slime.finalizeSpawn((ServerLevelAccessor)level, level.getCurrentDifficultyAt(slime.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(slime);
            ci.cancel();
         } else if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            Slime slime = (Slime)EntityType.SLIME.create(level);
            if (slime == null) {
               return;
            }

            String slimeName = day >= 50 ? "Ultra Slime Pegajoso" : "Slime Pegajoso";
            slime.addTag("fromOozingStickySlime");
            slime.setSize(6, true);
            slime.setCustomName(Component.literal(slimeName).withStyle(ChatFormatting.GREEN));
            slime.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(slime);
            ci.cancel();
         }
      }
   }
}
