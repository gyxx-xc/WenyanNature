package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import indi.wenyan.content.block.crafting_block.CraftingBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Deque;

public class CraftingBlockRender implements BlockEntityRenderer<CraftingBlockEntity, CraftingBlockRender.CraftingBlockRenderState> {
    private final Font font;

    public CraftingBlockRender(BlockEntityRendererProvider.Context context) {
        font = context.font();
    }

    public void render(@NotNull CraftingBlockEntity craftingBlock, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        for (var particle : craftingBlock.getParticles()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            var pos = particle.getPosition(partialTick);
            poseStack.translate(pos.x(), pos.y(), pos.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(particle.rot()));
            poseStack.scale(-0.03f, -0.03f, -0.03f);
            Component data = Component.literal(particle.data()).withStyle(Style.EMPTY.withBold(true));
            font.drawInBatch(data, -font.width(data) >> 1, 0, 0xFFFFFF, true,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 0xFFFFFF);
            poseStack.scale(-1, 1, 1);
            font.drawInBatch(data, -font.width(data) >> 1, 0, 0xFFFFFF, true,
                    poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, 0xFFFFFF);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    public CraftingBlockRenderState createRenderState() {
        return new CraftingBlockRenderState();
    }

    @Override
    public void extractRenderState(CraftingBlockEntity blockEntity, CraftingBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.partialTick = partialTicks;
        state.particles = blockEntity.getParticles();
    }

    @Override
    public void submit(CraftingBlockRenderState state,
                       PoseStack poseStack,
                       @NonNull SubmitNodeCollector collector,
                       @NonNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        for (var particle : state.particles) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            var pos = particle.getPosition(state.partialTick);
            poseStack.translate(pos.x(), pos.y(), pos.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(particle.rot()));
            poseStack.scale(-0.03f, -0.03f, -0.03f);
            Component data = Component.literal(particle.data()).withStyle(Style.EMPTY.withBold(true));
            // FIXME: submitText not work in test
            collector.submitText(poseStack,
                    -font.width(data) >> 1, 0,
                    data.getVisualOrderText(), true,
                    Font.DisplayMode.NORMAL, state.lightCoords, 0xFFFFFF, 0, 0);
//            poseStack.scale(-1, 1, 1);
//            font.drawInBatch(data, -font.width(data) >> 1, 0, 0xFFFFFF, true,
//                    poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, 0xFFFFFF);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static class CraftingBlockRenderState extends BlockEntityRenderState {
        public Deque<CraftingBlockEntity.TextEffect> particles;
        float partialTick;
    }
}
