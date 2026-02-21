package indi.wenyan.content.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanProgramming;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BulletRender extends EntityRenderer<BulletEntity> {
    private static final Identifier TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/entity/bullet.png");
    private static final RenderType RENDER_TYPE =
            RenderType.entityTranslucent(TEXTURE_LOCATION);

    public BulletRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull Identifier getTextureLocation(BulletEntity entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(BulletEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestackPose = poseStack.last();
        vertex(vertexconsumer, posestackPose, -0.5F, -0.25F, 0, 0, packedLight);
        vertex(vertexconsumer, posestackPose, 0.5F, -0.25F, 0, 1, packedLight);
        vertex(vertexconsumer, posestackPose, 0.5F, 0.75F, 1, 1, packedLight);
        vertex(vertexconsumer, posestackPose, -0.5F, 0.75F, 1, 0, packedLight);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y,
            float u, float v, int packedLight
    ) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(255, 255, 255, 128).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
