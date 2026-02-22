package indi.wenyan.content.block.additional_module.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.jetbrains.annotations.NotNull;

public class ScreenModuleBlockRenderer implements BlockEntityRenderer<ScreenModuleBlockEntity, ScreenModuleBlockRenderer.ScreenModuleBlockRendererState> {
    private final BlockEntityRenderDispatcher dispatcher;
    private final Font font;

    public ScreenModuleBlockRenderer(BlockEntityRendererProvider.Context context) {
        font = context.font();
        dispatcher = context.blockEntityRenderDispatcher();
    }

    public void render(@NotNull ScreenModuleBlockEntity be, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
//        poseStack.pushPose();
//        poseStack.translate(0.5, 1.2, 0.5);
//        poseStack.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
//        poseStack.scale(-0.015F, -0.015F, -0.15F);
//
//        // older part
//        poseStack.pushPose();
//        var out = be.getOutput().reversed();
//        for (int i = 0; i < out.size(); i++) {
//            String s = out.get(i);
//            font.drawInBatch(s, -font.width(s) >> 1, 0, 0xFFFFFF, true,
//                    poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, 0xFFFFFF);
//            poseStack.translate(0.0, -10, 0.0);
//            if (i >= 4) // make latest 5 lines same size for better reading
//                poseStack.scale(0.9F, 0.9F, 0.9F);
//        }
//        poseStack.popPose();
//
//        poseStack.popPose();
    }

    @Override
    public ScreenModuleBlockRendererState createRenderState() {
        return new ScreenModuleBlockRendererState();
    }

    @Override
    public void submit(ScreenModuleBlockRendererState screenModuleBlockRendererState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

    }

    public static class ScreenModuleBlockRendererState extends BlockEntityRenderState {}
}
