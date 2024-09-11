package indi.wenyan.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanNature;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class HandRunnerRender extends EntityRenderer<HandRunnerEntity> {
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "textures/entity/plug.png");
    protected final EntityModel<HandRunnerEntity> model;

    public HandRunnerRender(EntityRendererProvider.Context context) {
        super(context);
        model = new HandRunnerModel(context.bakeLayer(HandRunnerModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(HandRunnerEntity handRunnerEntity) {
        return TEXTURE;
    }

    @Override
    public void render(HandRunnerEntity entityIn, float entityYaw, float partialTicks,
                       PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferSourceIn, packedLightIn);
        poseStackIn.pushPose();
        VertexConsumer vertexConsumer = bufferSourceIn.getBuffer(this.model.renderType(this.getTextureLocation(entityIn)));
        this.model.renderToBuffer(poseStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY);
        poseStackIn.popPose();
    }
}
