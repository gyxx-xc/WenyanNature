package indi.wenyan.content.block.additional_module.paper;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import indi.wenyan.WenyanProgramming;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class BlockModuleRender implements BlockEntityRenderer<BlockModuleEntity> {
    public static final Identifier AABB_WALL = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/wall.png");

    public BlockModuleRender(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull BlockModuleEntity entity, float v, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource multiBufferSource, int i, int i1) {
        if (entity.getContinueCount() > 0)
            renderAABB(poseStack, multiBufferSource, i, entity.getRenderRange().found(),
                    new AABB(entity.getRenderRange().start(), entity.getRenderRange().end()));
    }

    public static final RenderType FRONT_LINES = RenderType.create("lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .setLineState(new LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .createCompositeState(false));

    public void renderAABB(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight,
                           boolean found, AABB aabb) {
        poseStack.pushPose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(AABB_WALL));
        int alpha = 80;
        Color color;
        if (found) {
            color = new Color(0xCCFFCC);
        } else {
            color = new Color(0xFFCCCC);
        }
// down
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
// up
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);

// north
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
// south
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
// west
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
// east
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                color, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                color, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                color, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);

        vertexconsumer = bufferSource.getBuffer(FRONT_LINES);
        LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                aabb, 0.9f, 0.9f, 0.9f, 0.9f);
        poseStack.popPose();
    }

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

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockModuleEntity blockEntity) {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull BlockModuleEntity blockEntity) {
        return AABB.INFINITE;
    }

}
