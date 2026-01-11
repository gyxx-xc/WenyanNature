package indi.wenyan.content.block.crafting_block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class CraftingBlockRender implements BlockEntityRenderer<CraftingBlockEntity> {
    private final Font font;

    public CraftingBlockRender(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(@NotNull CraftingBlockEntity craftingBlock, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        for (var particle : craftingBlock.getParticles()) {
//        var particle = new CraftingBlockEntity.TextParticle(new Vec3(0, 2, 0), new Vec3(0, 2, 0), 0, "qqqq");
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
}
