package indi.wenyan.content.block;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import indi.wenyan.WenyanProgramming;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.OptionalDouble;
import java.util.Random;

import static net.minecraft.client.renderer.RenderStateShard.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {
    final ResourceLocation LIGHT = ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/entity/bullet.png");
    final ResourceLocation AABB_WALL = ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/wall.png");

    private final BlockEntityRenderDispatcher dispatcher;
    private final Font font;

    private int lastOutput;

    public RunnerBlockRender(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        dispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(@NotNull RunnerBlockEntity be, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
        Vec3 beam = be.communicate;
        if (beam != null) {
            renderBeam(poseStack, bufferSource, combinedLight, beam);
        }

        renderOutput(poseStack, be, partialTicks, bufferSource, combinedLight);
//        renderAABB(poseStack, bufferSource, combinedLight,
//                new AABB(-0.5, -0.25, -0.5, 2, 5, 0.5));
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

    public void renderOutput(PoseStack poseStack, RunnerBlockEntity be, float partialTicks,
                             MultiBufferSource bufferSource, int combinedLight) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
        poseStack.scale(-0.015F, -0.015F, -0.15F);

        // older part
        poseStack.pushPose();
        var out = be.getOutput().reversed();
        boolean animating = out.hashCode() != lastOutput;
        String insert = null;
        if (animating) {
            if (!out.isEmpty()) insert = out.getFirst();
            poseStack.translate(0.0, -10 * partialTicks, 0.0);
            poseStack.scale(1 - 0.1F * partialTicks, 1 - 0.1F * partialTicks, 1 - 0.1F * partialTicks);
        }
        for (int i = animating ? 1 : 0; i < out.size(); i++) {
            String s = out.get(i);
            font.drawInBatch(s, -font.width(s) >> 1, 0, 0xFFFFFF, true,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLight);
            poseStack.translate(0.0, -10, 0.0);
            poseStack.scale(0.9F, 0.9F, 0.9F);
        }
        poseStack.popPose();

        if (insert != null && partialTicks > 0.5F) {
            poseStack.scale(1 * partialTicks, 1 * partialTicks, 1 * partialTicks);
            font.drawInBatch(insert, -font.width(insert) >> 1, 0, 0xFFFFFF, true,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLight);
        }

        if (!out.isEmpty())
            lastOutput = out.hashCode();
        poseStack.popPose();
    }

    public void renderAABB(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, AABB aabb) {
        poseStack.pushPose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(AABB_WALL));
        int alpha = 80;
// down
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
// up
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxZ - aabb.minZ), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);

// north
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
// south
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxX - aabb.minX), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
// west
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
// east
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, 0, combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ,
                Color.WHITE, alpha, 0, (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxZ - aabb.minZ), (float) (aabb.maxY - aabb.minY), combinedLight);
        vertex(vertexconsumer, poseStack.last(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ,
                Color.WHITE, alpha, (float) (aabb.maxZ - aabb.minZ), 0, combinedLight);

        vertexconsumer = bufferSource.getBuffer(FRONT_LINES);
        LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                aabb, 0.9f, 0.9f, 0.9f, 0.9f);
        poseStack.popPose();
    }

    public void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, Vec3 beam) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(LIGHT));
        var random = new Random(beam.hashCode());
        Color color = Color.getHSBColor(random.nextFloat(), 0.8F, 0.9F);
        color = color.brighter().brighter();
        int l = (int) beam.x + (int) beam.y + (int) beam.z;
        for (int i = 0; i < l; i++) {
            poseStack.pushPose();
            Vec3 pos = beam.scale((double) i / l)
                    .add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            poseStack.translate(pos.x, pos.y, pos.z);
            poseStack.mulPose(dispatcher.camera.rotation());
            float t = random.nextFloat() * 0.2F + 0.5F;
            poseStack.scale(t, t, t);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -0.5F, -0.25F, 0.0F, color, 128, 0, 0, combinedLight);
            vertex(vertexconsumer, posestack$pose, 0.5F, -0.25F, 0.0F, color, 128, 0, 1, combinedLight);
            vertex(vertexconsumer, posestack$pose, 0.5F, 0.75F, 0.0F, color, 128, 1, 1, combinedLight);
            vertex(vertexconsumer, posestack$pose, -0.5F, 0.75F, 0.0F, color, 128, 1, 0, combinedLight);
            poseStack.popPose();
        }
        poseStack.popPose();

    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
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
    public boolean shouldRenderOffScreen(RunnerBlockEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(RunnerBlockEntity blockEntity) {
        return blockEntity.isCommunicating ? AABB.INFINITE : BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
