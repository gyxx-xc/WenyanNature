package indi.wenyan.content.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class PedestalBlockRender implements BlockEntityRenderer<PedestalBlockEntity> {

    public PedestalBlockRender(BlockEntityRendererProvider.Context context) {
    }

    // copy from arsnouveau
    @Override
    public void render(PedestalBlockEntity tileEntityIn, float pPartialTick, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (tileEntityIn.getStack().isEmpty()) return;
        if(!(tileEntityIn.getBlockState().getBlock() instanceof PedestalBlock)){
            return;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5, 1.5, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        assert tileEntityIn.getLevel() != null;
        Minecraft.getInstance().getItemRenderer().renderStatic(tileEntityIn.getStack(),
                ItemDisplayContext.FIXED,
                pPackedLight,
                OverlayTexture.NO_OVERLAY,
                matrixStack,
                pBufferSource,
                tileEntityIn.getLevel(),
                (int) tileEntityIn.getBlockPos().asLong());

        matrixStack.popPose();
    }
}
