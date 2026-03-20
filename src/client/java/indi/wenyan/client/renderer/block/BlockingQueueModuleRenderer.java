package indi.wenyan.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.client.renderer.block.utils.ICommunicateRendererState;
import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.additional_module.paper.BlockingQueueModuleEntity;
import lombok.Getter;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class BlockingQueueModuleRenderer implements BlockEntityRenderer<BlockingQueueModuleEntity, BlockingQueueModuleRenderer.RenderState> {

    public BlockingQueueModuleRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void extractRenderState(BlockingQueueModuleEntity blockEntity, RenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.communicates = blockEntity.getCommunicates();
        state.partialTicks = partialTicks;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void submit(RenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        renderState.renderCommunicates(poseStack, submitNodeCollector, cameraRenderState);
    }

    public static class RenderState extends BlockEntityRenderState implements ICommunicateRendererState {

        @Getter
        private List<ICommunicateHolder.CommunicationEffect> communicates;

        @Getter
        private float partialTicks;

        @Override
        public int getLightCoords() {
            return lightCoords;
        }
    }
}
