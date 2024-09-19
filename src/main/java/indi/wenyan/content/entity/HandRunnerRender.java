package indi.wenyan.content.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.WenyanNature;
import indi.wenyan.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HandRunnerRender extends EntityRenderer<HandRunnerEntity> {
    public HandRunnerRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HandRunnerEntity handRunnerEntity) {
        return ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "item/hand_runner");
    }

    @Override
    public void render(@NotNull HandRunnerEntity entityIn, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStackIn, @NotNull MultiBufferSource bufferSourceIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferSourceIn, packedLightIn);
        poseStackIn.pushPose();
        poseStackIn.translate(0, 0.5, 0);
        poseStackIn.scale(0.8F, 0.8F, 0.8F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(Registration.HAND_RUNNER_1.get()),
                ItemDisplayContext.FIXED,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                poseStackIn,
                bufferSourceIn,
                Minecraft.getInstance().level, 0);
        poseStackIn.popPose();
    }
}
