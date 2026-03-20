package indi.wenyan.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
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
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class WritingBlockRender implements BlockEntityRenderer<WritingBlockEntity, WritingBlockRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public WritingBlockRender(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(
            WritingBlockEntity blockEntity,
            RenderState state,
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
        state.amount = (itemStack.count() - 1) * 3 / 64 + 1;
        state.upperLightCoords = blockEntity.getLevel() != null ?
                LevelRenderer.getLightCoords(blockEntity.getLevel(),
                        // get the light above the block
                        blockEntity.getBlockPos().offset(0, 1, 0)) : 15728880;
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void submit(RenderState blockEntityRenderState,
                       PoseStack poseStack,
                       @NonNull SubmitNodeCollector submitNodeCollector,
                       @NonNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(new Quaternionf().rotateX((float) (Math.PI / 2)));
        Quaternionf rot = new Quaternionf().rotateZ((float) (Math.PI / 16));
        for (int i = 0; i < blockEntityRenderState.amount; i++) {
            poseStack.translate(0, 0, -1 / 16.0f);
            poseStack.mulPose(rot);
            blockEntityRenderState.itemState.submit(poseStack, submitNodeCollector,
                    blockEntityRenderState.upperLightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        private final ItemStackRenderState itemState = new ItemStackRenderState();
        private int amount;
        private int upperLightCoords;
    }
}
