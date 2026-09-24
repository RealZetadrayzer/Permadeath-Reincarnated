package zeta.org.permadeath_reincarnated.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.BossEventProgress;
import org.joml.Quaternionf;
import zeta.org.permadeath_reincarnated.demonFight.ClientCrystalData;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
   private static final ResourceLocation DEMON_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "textures/mob_effect/demon_icon.png"
   );
   private static final ResourceLocation ENRAGED_DEMON_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "permadeath_reincarnated", "textures/mob_effect/enraged_demon_icon.png"
   );
   private static final ResourceLocation ENDER_CRYSTAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "minecraft", "textures/entity/end_crystal/end_crystal.png"
   );
   private static final RenderType CUBE_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURE);
   private static final RenderType GLASS_TYPE = RenderType.entityTranslucent(ENDER_CRYSTAL_TEXTURE);
   private static final float SIN_45 = (float)Math.sin(Math.PI / 4);
   private static final ModelPart glass;
   private static final ModelPart cube;

   private static void renderEndCrystal(Minecraft mc, PoseStack poseStack, int x, int y, float partialTick) {
      poseStack.pushPose();
      float bob = 0.1F * Mth.sin(partialTick * 0.05F);
      poseStack.translate(x, y + bob * 4.0F, 0.0F);
      poseStack.scale(18.0F, 18.0F, 18.0F);
      BufferSource buffers = mc.renderBuffers().bufferSource();
      VertexConsumer glassBuffer = buffers.getBuffer(GLASS_TYPE);
      poseStack.pushPose();
      poseStack.mulPose(Axis.YP.rotationDegrees(partialTick));
      poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      glass.render(poseStack, glassBuffer, 240, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      poseStack.pushPose();
      poseStack.scale(0.875F, 0.875F, 0.875F);
      poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      poseStack.mulPose(Axis.YP.rotationDegrees(partialTick));
      glass.render(poseStack, glassBuffer, 240, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      VertexConsumer cubeBuffer = buffers.getBuffer(CUBE_TYPE);
      poseStack.pushPose();
      poseStack.scale((float)Math.pow(0.875, 2.0), (float)Math.pow(0.875, 2.0), (float)Math.pow(0.875, 2.0));
      poseStack.mulPose(Axis.YP.rotationDegrees(partialTick));
      poseStack.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0.0F, SIN_45));
      cube.render(poseStack, cubeBuffer, 240, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      poseStack.popPose();
   }

   @SubscribeEvent
   public static void onBossBarRender(BossEventProgress event) {
      Minecraft mc = Minecraft.getInstance();
      GuiGraphics graphics = event.getGuiGraphics();
      LerpingBossEvent info = event.getBossEvent();
      if (info.getName().getString().contains("PERMADEATH DEMON")) {
         event.setCanceled(true);
         int barWidth = 182;
         int x = event.getX();
         int y = event.getY();
         ResourceLocation background = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/purple_background.png");
         ResourceLocation progress = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/purple_progress.png");
         ResourceLocation backgroundEnraged = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/red_background.png");
         ResourceLocation progressEnraged = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/boss_bar/red_progress.png");
         graphics.blit(ClientCrystalData.isEnraged ? backgroundEnraged : background, x, y, 0.0F, 0.0F, barWidth, 5, 256, 5);
         int progressWidth = (int)(info.getProgress() * barWidth);
         if (progressWidth > 0) {
            graphics.blit(ClientCrystalData.isEnraged ? progressEnraged : progress, x, y, 0.0F, 0.0F, progressWidth, 5, 256, 5);
         }

         Component name = info.getName();
         int nameWidth = mc.font.width(name);
         int nameX = x + barWidth / 2 - nameWidth / 2;
         int nameY = y - 9;
         graphics.drawString(mc.font, name, nameX, nameY, 16777215, false);
         int iconX = x - 20;
         int iconY = y - 12;
         graphics.blit(ClientCrystalData.isEnraged ? ENRAGED_DEMON_ICON_TEXTURE : DEMON_ICON_TEXTURE, iconX, iconY, 0.0F, 0.0F, 18, 18, 18, 18);
         int crystalCount = ClientCrystalData.crystalCount;
         String countText = "x" + crystalCount;
         int crystalY = y - 4;
         int crystalX = x + barWidth + 12;
         int countX = crystalX + 8;
         float partialTicks = (mc.level != null ? (float)mc.level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false) : 0.0F) * 3.0F;
         renderEndCrystal(mc, graphics.pose(), crystalX, crystalY, partialTicks);
         graphics.drawString(mc.font, Component.literal(countText), countX, crystalY - 3, 16777215, false);
      }
   }

   static {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      ModelPart modelRoot = LayerDefinition.create(mesh, 64, 32).bakeRoot();
      glass = modelRoot.getChild("glass");
      cube = modelRoot.getChild("cube");
   }
}
