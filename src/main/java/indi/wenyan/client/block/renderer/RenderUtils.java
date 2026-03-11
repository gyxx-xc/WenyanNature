package indi.wenyan.client.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import indi.wenyan.WenyanProgramming;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

public enum RenderUtils {
    ;

    public static final Identifier COMMUNICATION_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/lazer.png");
    private static final RenderType COMMUNICATION_RENDER_TYPE =
            RenderTypes.entityTranslucentCullItemTarget(COMMUNICATION_TEXTURE_LOCATION);

    public static void shootLine(PoseStack poseStack, Vector3f lookVector, Vector3f direction, double start, double end, float radious) {
        var newY = direction.mul((float) end, new Vector3f());
        var offset = direction.mul((float) start, new Vector3f());
        poseStack.translate(offset.x, offset.y, offset.z);
        axisFacedToCamera(poseStack, lookVector, newY.sub(offset));
        poseStack.scale(radious, 1F, 1F);
    }

    public static void axisFacedToCamera(PoseStack poseStack, Vector3f lookVector, Vector3f axisDirection) {
        Vector3f newX = axisDirection.cross(lookVector, new Vector3f())
                .normalize();
        var newZ = axisDirection.cross(newX, new Vector3f()).normalize();
        poseStack.mulPose(new Matrix4f(
                newX.x, newX.y, newX.z, 0,
                axisDirection.x, axisDirection.y, axisDirection.z, 0,
                newZ.x, newZ.y, newZ.z, 0,
                0, 0, 0, 1
        ));
    }

    public static void quad(
            VertexConsumer consumer, PoseStack.Pose pose,
            float x1, float y1, float x2, float y2,
            Color color, int alpha,
            float u1, float v1, float u2, float v2,
            int packedLight) {
        vertex(consumer, pose, x1, y1, 0.0F, color, alpha, u1, v1, packedLight);
        vertex(consumer, pose, x1, y2, 0.0F, color, alpha, u1, v2, packedLight);
        vertex(consumer, pose, x2, y2, 0.0F, color, alpha, u2, v2, packedLight);
        vertex(consumer, pose, x2, y1, 0.0F, color, alpha, u2, v1, packedLight);
    }

    public static void vertex(
            VertexConsumer consumer, PoseStack.Pose pose,
            float x, float y, float z,
            Color color, int alpha,
            float u, float v, int packedLight) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    public static void lineVertex(
            VertexConsumer consumer, PoseStack.Pose pose,
            float x, float y, float z,
            Color color, int alpha, int packedLight,
            float lineWidth) {
        vertex(consumer, pose, x, y, z, color, alpha, 0, 0, packedLight);
        consumer.setLineWidth(lineWidth);
    }

    @SuppressWarnings("SameParameterValue")
    public static double adsr(double attack, double hold, double decay, double sustain, double release, double time, double releasedTime) {
        if (time < attack) {
            return time / attack;
        } else if (time < attack + hold) {
            return 1.0;
        } else if (time < attack + hold + decay) {
            return 1.0 - (time - attack - hold) / decay * (1.0 - sustain);
        } else if (time < releasedTime) {
            return sustain;
        } else if (time - releasedTime < release) {
            return sustain * (time - releasedTime) / release;
        } else {
            return 0.0;
        }
    }

    public static void renderCommunicate(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Vector3f lookVector, Vector3f pos, float time, int lightCoords) {
        poseStack.pushPose();
        double adsr = adsr(2, 1, 2, 0.7, 4, time, 14 - 4);
        int emissiveLight = LightCoordsUtil.addSmoothBlockEmission(lightCoords, (float) adsr);
        int alpha = (int) (255 * adsr);
        double fadeLength = time > 14 - 4 ?
                (time - 14) / 4 + 1.0 : 0.0;
        double length = time < 2 ? time / 2 : 1.0;
        shootLine(poseStack, lookVector, pos, fadeLength, length, 0.1F);
        submitNodeCollector.submitCustomGeometry(poseStack, COMMUNICATION_RENDER_TYPE, (pose, vertexConsumer) -> quad(
                vertexConsumer, pose,
                -1.0F, 0F, 1.0F, 1.0F,
                Color.WHITE, alpha,
                0, 0, 1, 1,
                emissiveLight));
        poseStack.popPose();
    }
}
