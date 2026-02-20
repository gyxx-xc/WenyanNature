package indi.wenyan.content.block.runner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanProgramming;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Map;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;
import static indi.wenyan.content.block.runner.RunnerBlockEntity.COMMUNICATE_EFFECT_LIFETIME;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {
    public static final ResourceLocation STATUE_TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/state_icon.png");
    public static final ResourceLocation COMMUNICATION_TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/lazer.png");
    private static final RenderType RENDER_TYPE =
            RenderType.entityTranslucent(STATUE_TEXTURE_LOCATION);
    private static final RenderType COMMUNICATION_RENDER_TYPE =
            RenderType.entityTranslucentEmissive(COMMUNICATION_TEXTURE_LOCATION);

    private final BlockEntityRenderDispatcher dispatcher;

    public RunnerBlockRender(BlockEntityRendererProvider.Context context) {
        dispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(RunnerBlockEntity be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
        renderStatus(be, poseStack, bufferSource, combinedLight);
        renderCommunications(be, partialTicks, poseStack, bufferSource, combinedLight);
    }

    private void renderCommunications(RunnerBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
        if (be.getCommunications().isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(COMMUNICATION_RENDER_TYPE);
        for (var entry : be.getCommunications().entrySet()) {
            poseStack.pushPose();
            float time = entry.getValue() + partialTicks;
            double adsr = adsr(2, 1, 2, 0.7, 4, time);
            int emissiveLight = getEmissiveLight(combinedLight, (int) (15 * adsr));
            int alpha = (int) (255 * adsr);
            transferPoint(poseStack, entry, 2, 4, time);
            vertex(vertexconsumer, poseStack.last(), -1F, 1F, 0F, Color.WHITE,
                    alpha, 0, 1, emissiveLight);
            vertex(vertexconsumer, poseStack.last(), -1F, 0, 0F, Color.WHITE,
                    alpha, 0, 0, emissiveLight);
            vertex(vertexconsumer, poseStack.last(), 1F, 0, 0F, Color.WHITE,
                    alpha, 1, 0, emissiveLight);
            vertex(vertexconsumer, poseStack.last(), 1F, 1F, 0F, Color.WHITE,
                    alpha, 1, 1, emissiveLight);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private void transferPoint(PoseStack poseStack, Map.Entry<Vec3, Integer> entry, double attack, double release, double time) {
        double fadeLength = time > COMMUNICATE_EFFECT_LIFETIME - release ?
                (time - COMMUNICATE_EFFECT_LIFETIME) / release + 1.0 : 0.0;
        double length = time < attack ? time / attack : 1.0;
        var newY = entry.getKey().toVector3f().mul((float) (length - fadeLength));
        var newX = newY.cross(dispatcher.camera.getLookVector(), new Vector3f())
                .normalize().mul(0.1F); // radious
        var newZ = newY.cross(newX, new Vector3f()).normalize();
        var offset = entry.getKey().toVector3f().mul((float) fadeLength);
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(new Matrix4f(
                newX.x, newX.y, newX.z, 0,
                newY.x, newY.y, newY.z, 0,
                newZ.x, newZ.y, newZ.z, 0,
                0, 0, 0, 1
        ));
    }

    private void renderStatus(RunnerBlockEntity be, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
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
        int emissiveLight = getEmissiveLight(combinedLight, 5);
        var state = be.getBlockState().getValue(RUNNING_STATE);
        float uvOffset = switch (state) {
            case RUNNING -> 0.0F;
            case ERROR -> 0.25F;
            case IDLE -> 0.5F;
            case NOT_RUNNING -> 0.75F;
        };
        vertex(vertexconsumer, poseStack.last(), -1.0F, -1.0F, 0.0F, Color.WHITE, 125, 0.0F, uvOffset + 0.25F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), -1.0F, 1.0F, 0.0F, Color.WHITE, 125, 0.0F, uvOffset + 0.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), 1.0F, 1.0F, 0.0F, Color.WHITE, 125, 1.0F, uvOffset + 0.0F, emissiveLight);
        vertex(vertexconsumer, poseStack.last(), 1.0F, -1.0F, 0.0F, Color.WHITE, 125, 1.0F, uvOffset + 0.25F, emissiveLight);
        poseStack.popPose();
    }

    private static int getEmissiveLight(int combinedLight, int emission) {
        int blockLight = (combinedLight >> 4) & 0xf;
        int skyLight = (combinedLight >> 20) & 0xf;
        // make it slightly lighter
        skyLight = Math.min(0xf, skyLight + emission);
        blockLight = Math.min(0xf, blockLight + emission);
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

    @SuppressWarnings("SameParameterValue")
    private static double adsr(double attack, double hold, double decay, double sustain, double release, double time) {
        if (time < attack) {
            return time / attack;
        } else if (time < attack + hold) {
            return 1.0;
        } else if (time < attack + hold + decay) {
            return 1.0 - (time - attack - hold) / decay * (1.0 - sustain);
        } else if (time < COMMUNICATE_EFFECT_LIFETIME - release) {
            return sustain;
        } else if (time < COMMUNICATE_EFFECT_LIFETIME) {
            return sustain * (COMMUNICATE_EFFECT_LIFETIME - time) / release;
        } else {
            return 0.0;
        }
    }
}
