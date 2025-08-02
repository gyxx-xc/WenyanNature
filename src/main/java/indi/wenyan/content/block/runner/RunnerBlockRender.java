package indi.wenyan.content.block.runner;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RunnerBlockRender implements BlockEntityRenderer<RunnerBlockEntity> {

    public RunnerBlockRender(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull RunnerBlockEntity be, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
    }
}
