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
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {
    public static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/state_icon.png");
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
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.25F, 0.25F, 0.25F);
        var face = be.getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        switch (face) {
            case NORTH -> poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
            case SOUTH -> poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(0)));
            case EAST -> poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(90)));
            case WEST -> poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(-90)));
        }
        int emissiveLight = getEmissiveLight(combinedLight);
        var state = be.getBlockState().getValue(RUNNING_STATE);
        float uvOffset = switch (state) {
            case RUNNING -> 0.0F;
            case ERROR -> 0.25F;
            case IDLE -> 0.5F;
            case NOT_RUNNING -> 0.75F;
        };
        vertex(vertexconsumer, poseStack.last(), -1.0F, -1.0F, 0.0F, Color.WHITE, 125, 0.0F, uvOffset + 0.25F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), -1.0F,  1.0F, 0.0F, Color.WHITE, 125, 0.0F, uvOffset + 0.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(),  1.0F,  1.0F, 0.0F, Color.WHITE, 125, 1.0F, uvOffset + 0.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(),  1.0F, -1.0F, 0.0F, Color.WHITE, 125, 1.0F, uvOffset + 0.25F, emissiveLight);
        poseStack.popPose();
    }

    private static int getEmissiveLight(int combinedLight) {
        int blockLight = (combinedLight >> 4) & 0xf;
        int skyLight = (combinedLight >> 20) & 0xf;
        // make it slightly lighter
        skyLight = Math.min(0xf, skyLight + 5);
        blockLight = Math.min(0xf, blockLight + 5);
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
