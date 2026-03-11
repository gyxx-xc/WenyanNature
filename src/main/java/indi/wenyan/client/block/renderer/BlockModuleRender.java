package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.block.renderer.utils.RenderUtils;
import indi.wenyan.content.block.additional_module.paper.BlockModuleEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

import static net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET;

@ParametersAreNonnullByDefault
public class BlockModuleRender implements BlockEntityRenderer<BlockModuleEntity, BlockModuleRender.BlockModuleRenderState> {
    public static final Identifier AABB_WALL = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/wall.png");

    public BlockModuleRender(BlockEntityRendererProvider.Context ignoredContext) {
    }

    public static final RenderType FRONT_LINES = RenderType.create("lines",
            RenderSetup.builder(RenderPipeline
                            .builder(LINES_SNIPPET)
                            .withDepthStencilState(
                                    new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true,
                                            -3.0F, -3.0F))
                            .withLocation("pipeline/lines").build())
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup());

    @Override
    public void extractRenderState(BlockModuleEntity blockEntity, BlockModuleRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if (blockEntity.getContinueCount() > 0) {
            state.shouldRender = true;
            var aabb = new AABB(blockEntity.getRenderRange().start(), blockEntity.getRenderRange().end());
            state.aabb = aabb.inflate(0.1); // avoid z confliect
            if (blockEntity.getRenderRange().found()) {
                state.color = new Color(0xCCFFCC);
            } else {
                state.color = new Color(0xFFCCCC);
            }
        } else {
            state.shouldRender = false;
        }
    }

    @Override
    public void submit(BlockModuleRenderState blockModuleRenderState, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!blockModuleRenderState.shouldRender) return;
        poseStack.pushPose();
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(AABB_WALL),
                ((pose, vertexConsumer) -> renderFaces(vertexConsumer, pose,
                        blockModuleRenderState.aabb, blockModuleRenderState.color)));

        submitNodeCollector.submitCustomGeometry(poseStack, FRONT_LINES,
                ((pose, vertexConsumer) ->
                        renderEdges(vertexConsumer, pose, blockModuleRenderState.aabb)));
        poseStack.popPose();
    }

    private static void renderFaces(VertexConsumer vertexconsumer, PoseStack.Pose pose, AABB aabb, Color color) {
        int combinedLight = LightCoordsUtil.FULL_BRIGHT;
        int alpha = 80;
        // down
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
        // up
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);

        // north
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        // south
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        // west
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        // east
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        RenderUtils.vertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);
    }

    private static void renderEdges(VertexConsumer vertexconsumer, PoseStack.Pose pose, AABB aabb) {
        int combinedLight = LightCoordsUtil.FULL_BRIGHT;
        int alpha = 255;
        Color color = Color.WHITE;

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);


        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);


        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);

        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ, color, alpha, combinedLight, 1.0f);
        RenderUtils.lineVertex(vertexconsumer, pose, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, color, alpha, combinedLight, 1.0f);
    }

    @Override
    public BlockModuleRenderState createRenderState() {
        return new BlockModuleRenderState();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(BlockModuleEntity blockEntity) {
        return AABB.INFINITE;
    }

    public static class BlockModuleRenderState extends BlockEntityRenderState {
        private boolean shouldRender;
        private AABB aabb;
        private Color color;
    }
}
