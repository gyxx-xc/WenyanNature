package indi.wenyan.content.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import indi.wenyan.WenyanNature;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HandRunnerRender extends EntityRenderer<HandRunnerEntity> {
    private final EntityRenderDispatcher dispatcher;
    private final Font font;

    public HandRunnerRender(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getEntityRenderDispatcher();
        this.font = context.getFont();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HandRunnerEntity handRunnerEntity) {
        return ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "item/hand_runner");
    }

    @Override
    public void render(@NotNull HandRunnerEntity entityIn, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferSource, packedLightIn);
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
        poseStack.pushPose();
        poseStack.scale(0.8F, 0.8F, 0.8F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(Registration.HAND_RUNNER_1.get()),
                ItemDisplayContext.FIXED,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                Minecraft.getInstance().level, 0);
        poseStack.popPose();

        poseStack.scale(-0.025F, -0.025F, -0.25F);
        poseStack.translate(0.0, 0.0, 0.2);
        font.drawInBatch(entityIn.output, -15, 0, 255*256*256+255*256+255, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightIn);
        poseStack.popPose();
    }
}
