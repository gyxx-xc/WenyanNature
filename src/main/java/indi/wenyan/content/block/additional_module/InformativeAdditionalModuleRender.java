package indi.wenyan.content.block.additional_module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class InformativeAdditionalModuleRender implements BlockEntityRenderer<InformativeAdditionalModuleEntity> {
    private final BlockEntityRenderDispatcher dispatcher;
    private final Font font;

    private int lastOutput;

    public InformativeAdditionalModuleRender(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        dispatcher = context.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(@NotNull InformativeAdditionalModuleEntity be, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
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
}
