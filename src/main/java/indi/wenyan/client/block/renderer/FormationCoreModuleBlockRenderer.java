package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.block.FormationCoreModuleEntity;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collection;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FormationCoreModuleBlockRenderer implements BlockEntityRenderer<FormationCoreModuleEntity, FormationCoreModuleBlockRenderer.RenderState> {
    public static final Identifier COMMUNICATION_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/lazer.png");
    private static final RenderType COMMUNICATION_RENDER_TYPE =
            RenderTypes.entityTranslucentCullItemTarget(COMMUNICATION_TEXTURE_LOCATION);

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
        Vector3f cameraOriebtation = new Vector3f(0, 0, -1).rotate(cameraRenderState.orientation);
        for (var pos : renderState.blocks) {
            poseStack.pushPose();
            float time = (14-pos.life) + renderState.partialTicks;
            double adsr = RenderUtils.adsr(2, 1, 2, 0.7, 4, time, 14 - 4);
            int emissiveLight = LightCoordsUtil.addSmoothBlockEmission(renderState.lightCoords, (float) adsr);
            int alpha = (int) (255 * adsr);
            double fadeLength = time > 14 - 4 ?
                    (time - 14) / 4 + 1.0 : 0.0;
            double length = time < 2 ? time / 2 : 1.0;
            RenderUtils.shootLine(poseStack, cameraOriebtation, pos.pos, fadeLength, length, 0.1F);
            submitNodeCollector.submitCustomGeometry(poseStack, COMMUNICATION_RENDER_TYPE, (pose, vertexConsumer) -> RenderUtils.quad(
                    vertexConsumer, pose,
                    -1.0F, 0F, 1.0F, 1.0F,
                    Color.WHITE, alpha,
                    0, 0, 1, 1,
                    emissiveLight));
            poseStack.popPose();

        }
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        private float partialTicks;
        private Collection<FormationCoreModuleEntity.CommunicationEffect> blocks;
    }
}
