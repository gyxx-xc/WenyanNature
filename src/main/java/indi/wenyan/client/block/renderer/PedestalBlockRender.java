package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.content.block.pedestal.PedestalBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PedestalBlockRender implements BlockEntityRenderer<PedestalBlockEntity, PedestalBlockRender.PedestalBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public PedestalBlockRender(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(
            PedestalBlockEntity blockEntity,
            PedestalBlockEntityRenderState state,
            float partialTicks, @NonNull Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        var itemStack = ItemUtil.getStack(blockEntity.getItemHandler(), 0);
        if (!itemStack.isEmpty())
            itemModelResolver.updateForTopItem(state.itemState,
                    itemStack,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(), null, 0);
        else
            state.itemState.clear();
    }

    @Override
    public PedestalBlockEntityRenderState createRenderState() {
        return new PedestalBlockEntityRenderState();
    }

    @Override
    public void submit(PedestalBlockEntityRenderState blockEntityRenderState,
                       PoseStack poseStack,
                       @NonNull SubmitNodeCollector submitNodeCollector,
                       @NonNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        blockEntityRenderState.itemState.submit(poseStack, submitNodeCollector,
                blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class PedestalBlockEntityRenderState extends BlockEntityRenderState {
        public ItemStackRenderState itemState = new ItemStackRenderState();
    }
}
