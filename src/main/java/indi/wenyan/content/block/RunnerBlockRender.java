package indi.wenyan.content.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanNature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class RunnerBlockRender implements BlockEntityRenderer<BlockRunner> {
    ResourceLocation LIGHT = ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "textures/entity/bullet.png");

    public RunnerBlockRender(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(@NotNull BlockRunner be, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay) {
        Vec3 beam = be.communicate;
        if (beam == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(LIGHT));
        Random random = new Random(beam.hashCode());
        Color color = Color.getHSBColor(random.nextFloat(), 0.8F, 0.9F);
        color = color.brighter().brighter();
        int l = (int) beam.x + (int) beam.y + (int) beam.z;
        for (int i = 0; i < l; i ++) {
            poseStack.pushPose();
            Vec3 pos = beam.scale((double) i / l)
                    .add(random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            poseStack.translate(pos.x, pos.y, pos.z);
            poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
            float t = random.nextFloat() * 0.2F+0.5F;
            poseStack.scale(t, t, t);
            PoseStack.Pose posestack$pose = poseStack.last();
            vertex(vertexconsumer, posestack$pose, -0.5F, -0.25F, (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), 0, 0, combinedLight);
            vertex(vertexconsumer, posestack$pose, 0.5F, -0.25F, (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), 0, 1, combinedLight);
            vertex(vertexconsumer, posestack$pose, 0.5F, 0.75F, (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), 1, 1, combinedLight);
            vertex(vertexconsumer, posestack$pose, -0.5F, 0.75F, (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), 1, 0, combinedLight);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y,
            float r, float g, float b,
            float u, float v, int packedLight
    ) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(r, g, b, 128).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
