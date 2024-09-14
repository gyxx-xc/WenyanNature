package indi.wenyan.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HandRunnerRender extends EntityRenderer<HandRunnerEntity> {
    public HandRunnerRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(HandRunnerEntity handRunnerEntity) {
        return null;
    }

    @Override
    public void render(HandRunnerEntity entityIn, float entityYaw, float partialTicks,
                       PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferSourceIn, packedLightIn);
        poseStackIn.pushPose();
        poseStackIn.translate(0, 0.5, 0);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(Registration.HAND_RUNNER.get()),
                ItemDisplayContext.FIXED,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                poseStackIn,
                bufferSourceIn,
                Minecraft.getInstance().level, 0);
        poseStackIn.popPose();
    }
}
