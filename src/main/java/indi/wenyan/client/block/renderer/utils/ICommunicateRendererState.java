package indi.wenyan.client.block.renderer.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.ICommunicateEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collection;

@ParametersAreNonnullByDefault
public interface ICommunicateRendererState {
    Identifier COMMUNICATION_TEXTURE_LOCATION =
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "textures/block/lazer.png");
    RenderType COMMUNICATION_RENDER_TYPE =
                    RenderTypes.entityTranslucentCullItemTarget(COMMUNICATION_TEXTURE_LOCATION);

    Collection<ICommunicateEntity.CommunicationEffect> getCommunicates();

    float getPartialTicks();

    int getLightCoords();

    default void renderCommunicates(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (getCommunicates().isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        Vector3f lookVector = new Vector3f(0, 0, -1).rotate(cameraRenderState.orientation);
        for (var pos : getCommunicates()) {
            float time = (14-pos.life) + getPartialTicks();
            renderCommunicate(poseStack, submitNodeCollector, lookVector, pos.pos, time, getLightCoords());
        }
        poseStack.popPose();
    }

    private static void renderCommunicate(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Vector3f lookVector, Vector3f pos, float time, int lightCoords) {
        poseStack.pushPose();
        double adsr = RenderUtils.adsr(2, 1, 2, 0.7, 4, time, 14 - 4);
        int emissiveLight = LightCoordsUtil.addSmoothBlockEmission(lightCoords, (float) adsr);
        int alpha = (int) (255 * adsr);
        double fadeLength = time > 14 - 4 ?
                (time - 14) / 4 + 1.0 : 0.0;
        double length = time < 2 ? time / 2 : 1.0;
        var newY = pos.mul((float) length, new Vector3f());
        var offset = pos.mul((float) fadeLength, new Vector3f());
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(RenderUtils.axialCameraRotation(lookVector, newY.sub(offset)));
        poseStack.scale(0.1F, 1.0F, 1.0F);
        submitNodeCollector.submitCustomGeometry(poseStack, COMMUNICATION_RENDER_TYPE, (pose, vertexConsumer) -> RenderUtils.quad(
                vertexConsumer, pose,
                -1.0F, 0.0F, 1.0F, 1.0F,
                Color.WHITE, alpha,
                0, 0, 1, 1,
                emissiveLight));
        poseStack.popPose();
    }
}
