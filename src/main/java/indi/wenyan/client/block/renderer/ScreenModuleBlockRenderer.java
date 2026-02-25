package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import indi.wenyan.content.block.additional_module.block.ScreenModuleBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ScreenModuleBlockRenderer implements BlockEntityRenderer<ScreenModuleBlockEntity, ScreenModuleBlockRenderer.ScreenModuleBlockRendererState> {
    private final Font font;

    public ScreenModuleBlockRenderer(BlockEntityRendererProvider.Context context) {
        font = context.font();
    }

    @Override
    public ScreenModuleBlockRendererState createRenderState() {
        return new ScreenModuleBlockRendererState();
    }

    @Override
    public void extractRenderState(ScreenModuleBlockEntity blockEntity, ScreenModuleBlockRendererState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.output = blockEntity.getOutput().reversed();
    }

    @Override
    public void submit(ScreenModuleBlockRendererState screenModuleBlockRendererState, PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.2, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-cameraRenderState.yRot));
        poseStack.scale(-0.015F, -0.015F, -0.15F);

        // older part
        poseStack.pushPose();
        var out = screenModuleBlockRendererState.output;
        for (int i = 0; i < out.size(); i++) {
            String s = out.get(i);
            submitNodeCollector.submitText(
                    poseStack,
                    -font.width(s) >> 1, 0,
                    FormattedCharSequence.forward(s, Style.EMPTY), true,
                    Font.DisplayMode.NORMAL,
                    // not sure why lightCoords always 0, make it emission here
                    LightCoordsUtil.FULL_BRIGHT,
                    0xFFFFFFFF, 0, 0
            );
            poseStack.translate(0.0, -10, 0.0);
            if (i >= 4) // make latest 5 lines same size for better reading
                poseStack.scale(0.9F, 0.9F, 0.9F);
        }
        poseStack.popPose();

        poseStack.popPose();
    }

    public static class ScreenModuleBlockRendererState extends BlockEntityRenderState {
        public List<String> output;
    }

    @Override
    public @NonNull AABB getRenderBoundingBox(ScreenModuleBlockEntity blockEntity) {
        if (blockEntity.getOutput().isEmpty())
            return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        else
            return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).expandTowards(0, 1, 0);
    }
}
