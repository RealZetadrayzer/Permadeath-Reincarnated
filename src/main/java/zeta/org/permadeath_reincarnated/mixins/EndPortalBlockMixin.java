package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;
import zeta.org.permadeath_reincarnated.systems.PlayerSleepingHandler;
import zeta.org.permadeath_reincarnated.systems.ScheduleInTicks;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {
   @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
   private void blockEndPortalBeforeDay30(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
      if (DayGlobalCount.CURRENT_DAY < 30 && entity instanceof ServerPlayer player) {
         ci.cancel();
         long seed = level.getRandom().nextLong();
         entity.setDeltaMovement(0.0, entity.getDeltaMovement().y, 0.0);
         player.displayClientMessage(
            Component.literal("!El ")
               .withStyle(ChatFormatting.LIGHT_PURPLE)
               .append(Component.literal("Permadeath Demon").withStyle(ChatFormatting.DARK_PURPLE).withStyle(Style.EMPTY.withBold(true)))
               .append(Component.literal(" ha bloqueado la entrada!").withStyle(ChatFormatting.LIGHT_PURPLE)),
            true
         );
         player.hurtMarked = true;
         player.addTag("hide");
         PlayerSleepingHandler.HIDE_TIMER.put(player.getUUID(), 40);
         ScheduleInTicks.schedule(
            () -> {
               player.teleportTo(level.getSharedSpawnPos().getX(), level.getSharedSpawnPos().getY() + 1, level.getSharedSpawnPos().getZ());
               player.connection
                  .send(
                     new ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BEACON_DEACTIVATE),
                        SoundSource.MASTER,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        1.0F,
                        0.8F,
                        seed
                     )
                  );
            },
            1
         );
      }
   }
}
