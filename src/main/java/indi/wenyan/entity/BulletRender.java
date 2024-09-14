package indi.wenyan.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanNature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BulletRender extends EntityRenderer<BulletEntity> {
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "entity/bullet");
    private static final RenderType RENDER_TYPE =
            RenderType.translucent();

    public BulletRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BulletEntity entity) {
        return TEXTURE_LOCATION;
    }

    public void render(BulletEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        TextureAtlasSprite sprite =
                Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURE_LOCATION);
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.3F, 0.3F, 0.3F);
        VertexConsumer vertexconsumer = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestack$pose = poseStack.last();
        vertex(vertexconsumer, posestack$pose, -0.5F, -0.25F, sprite.getU0(), sprite.getV0(), packedLight);
        vertex(vertexconsumer, posestack$pose, 0.5F, -0.25F, sprite.getU0(), sprite.getV1(), packedLight);
        vertex(vertexconsumer, posestack$pose, 0.5F, 0.75F, sprite.getU1(), sprite.getV1(), packedLight);
        vertex(vertexconsumer, posestack$pose, -0.5F, 0.75F, sprite.getU1(), sprite.getV0(), packedLight);
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
                .setColor(255, 255, 255, 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
