package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Map;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;
import static indi.wenyan.content.block.runner.RunnerBlockEntity.COMMUNICATE_EFFECT_LIFETIME;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity, RunnerBlockRender.RunnerBlockRenderState> {
    public static final Identifier STATUE_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/state_icon.png");
    public static final Identifier COMMUNICATION_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/lazer.png");
    private static final RenderType RENDER_TYPE =
            RenderTypes.entityTranslucent(STATUE_TEXTURE_LOCATION);
    private static final RenderType COMMUNICATION_RENDER_TYPE =
            RenderTypes.entityTranslucentEmissive(COMMUNICATION_TEXTURE_LOCATION);
    public static final float UV_OFFSET = 0.25F;

    public RunnerBlockRender(BlockEntityRendererProvider.Context ignore) {
    }

    private void renderCommunications(RunnerBlockRenderState state, CameraRenderState cameraState, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.communications.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        for (var entry : state.communications.entrySet()) {
            poseStack.pushPose();
            float time = entry.getValue() + state.partialTicks;
            double adsr = adsr(2, 1, 2, 0.7, 4, time);
            int emissiveLight = LightCoordsUtil.addSmoothBlockEmission(state.lightCoords, (float) adsr);
            int alpha = (int) (255 * adsr);
            transferPoint(poseStack, entry.getKey(), cameraState.orientation.positiveY(new Vector3f()), 2, 4, time);
            collector.submitCustomGeometry(poseStack, COMMUNICATION_RENDER_TYPE, (pose, vertexConsumer) -> quad(
                    vertexConsumer, pose,
                    -1.0F, 0F, 1.0F, 1.0F,
                    Color.WHITE, alpha,
                    0, 0, 1, 1,
                    emissiveLight));
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private void transferPoint(PoseStack poseStack, Vector3f direction, Vector3f lookVector, double attack, double release, double time) {
        double fadeLength = time > COMMUNICATE_EFFECT_LIFETIME - release ?
                (time - COMMUNICATE_EFFECT_LIFETIME) / release + 1.0 : 0.0;
        double length = time < attack ? time / attack : 1.0;
        var newY = direction.mul((float) (length - fadeLength), new Vector3f());
        Vector3f newX = newY.cross(lookVector, new Vector3f())
                .normalize().mul(0.1F); // radious
        var newZ = newY.cross(newX, new Vector3f()).normalize();
        var offset = direction.mul((float) fadeLength, new Vector3f());
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(new Matrix4f(
                newX.x, newX.y, newX.z, 0,
                newY.x, newY.y, newY.z, 0,
                newZ.x, newZ.y, newZ.z, 0,
                0, 0, 0, 1
        ));
    }

    private void renderStatus(RunnerBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(UV_OFFSET, UV_OFFSET, UV_OFFSET);
        poseStack.mulPose(state.face);
        int emissiveLight = LightCoordsUtil.lightCoordsWithEmission(state.lightCoords, 5);
        float uvOffset = state.stateOffset;
        collector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, vertexConsumer) -> quad(
                vertexConsumer, pose,
                -1.0F, -1.0F, 1.0F, 1.0F,
                Color.WHITE, 125,
                0.0F, uvOffset, 1.0F, uvOffset + UV_OFFSET,
                emissiveLight));
        poseStack.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private static void quad(
            VertexConsumer consumer, PoseStack.Pose pose,
            float x1, float y1, float x2, float y2,
            Color color, int alpha,
            float u1, float v1, float u2, float v2,
            int packedLight) {
        vertex(consumer, pose, x1, y1, 0.0F, color, alpha, u1, v1, packedLight);
        vertex(consumer, pose, x1, y2, 0.0F, color, alpha, u1, v2, packedLight);
        vertex(consumer, pose, x2, y2, 0.0F, color, alpha, u2, v2, packedLight);
        vertex(consumer, pose, x2, y1, 0.0F, color, alpha, u2, v1, packedLight);
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

    @Override
    public RunnerBlockRenderState createRenderState() {
        return new RunnerBlockRenderState();
    }

    @Override
    public void extractRenderState(RunnerBlockEntity be, RunnerBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
        var face = be.getBlockState().getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        state.face = switch (face) {
            case NORTH -> new Quaternionf().rotateY((float) Math.toRadians(180));
            case SOUTH -> new Quaternionf().rotateY((float) Math.toRadians(0));
            case EAST -> new Quaternionf().rotateY((float) Math.toRadians(90));
            case WEST -> new Quaternionf().rotateY((float) Math.toRadians(-90));
            default -> throw new IllegalStateException("Unexpected value: " + face);
        };
        state.stateOffset = UV_OFFSET * be.getBlockState().getValue(RUNNING_STATE).getUvOrder();
        state.communications = be.getCommunications();
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(RunnerBlockRenderState runnerBlockRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        renderCommunications(runnerBlockRenderState, cameraRenderState, poseStack, submitNodeCollector);
        renderStatus(runnerBlockRenderState, poseStack, submitNodeCollector);
    }

    public static class RunnerBlockRenderState extends BlockEntityRenderState {
        public Quaternionf face;
        public float stateOffset;
        public Map<Vector3f, Integer> communications;
        public float partialTicks;
    }
}
