package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.block.renderer.utils.ICommunicateRendererState;
import indi.wenyan.client.block.renderer.utils.RenderUtils;
import indi.wenyan.content.block.ICommunicateEntity;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import lombok.Getter;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collection;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity, RunnerBlockRender.RunnerBlockRenderState> {
    private static final Identifier STATUE_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/state_icon.png");
    private static final RenderType RENDER_TYPE =
            RenderTypes.entityTranslucent(STATUE_TEXTURE_LOCATION);
    public static final float UV_OFFSET = 0.25F;

    public RunnerBlockRender(BlockEntityRendererProvider.Context ignore) {
    }

    private void renderStatus(RunnerBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(UV_OFFSET, UV_OFFSET, UV_OFFSET);
        poseStack.mulPose(state.face);
        int emissiveLight = LightCoordsUtil.FULL_BRIGHT;
        float uvOffset = state.stateOffset;
        collector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, vertexConsumer) -> RenderUtils.quad(
                vertexConsumer, pose,
                -1.0F, -1.0F, 1.0F, 1.0F,
                Color.WHITE, 0xE0,
                0.0F, uvOffset, 1.0F, uvOffset + UV_OFFSET,
                emissiveLight));
        poseStack.popPose();
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
        state.communicates = be.getCommunicates();
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(RunnerBlockRenderState runnerBlockRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        runnerBlockRenderState.renderCommunicates(poseStack, submitNodeCollector, cameraRenderState);
        renderStatus(runnerBlockRenderState, poseStack, submitNodeCollector);
    }

    public static class RunnerBlockRenderState extends BlockEntityRenderState implements ICommunicateRendererState {
        public Quaternionf face;
        public float stateOffset;

        @Getter
        private Collection<ICommunicateEntity.CommunicationEffect> communicates;

        @Getter
        public float partialTicks;

        @Override
        public int getLightCoords() {
            return lightCoords;
        }
    }
}
