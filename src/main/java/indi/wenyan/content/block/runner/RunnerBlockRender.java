package indi.wenyan.content.block.runner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {

    public RunnerBlockRender() {
    }

    @Override
    public void render(RunnerBlockEntity be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
    }
}
