package zeta.org.permadeath_reincarnated.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.items.PermadeathInfernalElytra;

@OnlyIn(Dist.CLIENT)
@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
   @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
   private void permadeath$disableCapeForCustomElytra(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      AbstractClientPlayer player,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      CallbackInfo ci
   ) {
      ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
      if (chest.getItem() instanceof PermadeathInfernalElytra) {
         ci.cancel();
      }
   }
}
