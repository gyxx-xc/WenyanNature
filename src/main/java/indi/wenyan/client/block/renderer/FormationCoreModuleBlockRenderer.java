package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.block.FormationCoreModuleEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FormationCoreModuleBlockRenderer implements BlockEntityRenderer<FormationCoreModuleEntity, FormationCoreModuleBlockRenderer.RenderState> {

    public FormationCoreModuleBlockRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void extractRenderState(FormationCoreModuleEntity blockEntity, RenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.blocks = blockEntity.getEffects();
        state.partialTicks = partialTicks;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void submit(RenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        Vector3f lookVector = new Vector3f(0, 0, -1).rotate(cameraRenderState.orientation);
        for (var pos : renderState.blocks) {
            float time = (14-pos.life) + renderState.partialTicks;
            RenderUtils.renderCommunicate(poseStack, submitNodeCollector, lookVector, pos.pos, time, renderState.lightCoords);
        }
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        private float partialTicks;
        private Collection<FormationCoreModuleEntity.CommunicationEffect> blocks;
    }
}
