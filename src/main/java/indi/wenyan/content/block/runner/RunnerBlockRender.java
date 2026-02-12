package indi.wenyan.content.block.runner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanProgramming;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {
    public static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/wall.png");
    private static final RenderType RENDER_TYPE =
            RenderType.entityTranslucent(TEXTURE_LOCATION);

    public RunnerBlockRender() {
    }

    @Override
    public void render(RunnerBlockEntity be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        poseStack.pushPose();
        poseStack.translate(0.5F, 2.5/16.0F, 0.5F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        var face = be.getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACE);
        int emissiveLight = getEmissiveLight(combinedLight);
        vertex(vertexconsumer, poseStack.last(), -1.0F, 0.0F, -1.0F, Color.WHITE, 125, 0.0F, 0.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), -1.0F, 0.0F, 1.0F, Color.WHITE, 125, 0.0F, 1.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), 1.0F, 0.0F, 1.0F, Color.WHITE, 125, 1.0F, 1.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), 1.0F, 0.0F, -1.0F, Color.WHITE, 125, 1.0F, 0.0F, emissiveLight);
        poseStack.popPose();
    }

    private static int getEmissiveLight(int combinedLight) {
        int blockLight = (combinedLight >> 4) & 0xf;
        int skyLight = (combinedLight >> 20) & 0xf;
        blockLight = Math.min(0xf, blockLight + 3); // make it slightly lighter
        return skyLight << 20 | blockLight << 4;
    }

    @SuppressWarnings("SameParameterValue")
    private static void vertex(
            VertexConsumer consumer, PoseStack.Pose pose,
            float x, float y, float z,
            Color color, int alpha,
            float u, float v, int packedLight) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
