package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import indi.wenyan.content.block.crafting_block.CraftingBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Deque;

public class CraftingBlockRender implements BlockEntityRenderer<CraftingBlockEntity, CraftingBlockRender.CraftingBlockRenderState> {
    private final Font font;

    public CraftingBlockRender(BlockEntityRendererProvider.Context context) {
        font = context.font();
    }

    @Override
    public CraftingBlockRenderState createRenderState() {
        return new CraftingBlockRenderState();
    }

    @Override
    public void extractRenderState(CraftingBlockEntity blockEntity, CraftingBlockRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
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
            collector.submitText(poseStack,
                    -font.width(data) >> 1, 0,
                    data.getVisualOrderText(), true,
                    Font.DisplayMode.NORMAL, state.lightCoords, 0xFFFFFFFF, 0, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static class CraftingBlockRenderState extends BlockEntityRenderState {
        public Deque<CraftingBlockEntity.TextEffect> particles;
        float partialTick;
    }
}
