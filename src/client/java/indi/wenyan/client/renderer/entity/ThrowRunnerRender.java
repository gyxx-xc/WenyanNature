package indi.wenyan.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import com.mojang.math.Axis;
import indi.wenyan.content.entity.ThrowRunnerEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrowRunnerRender extends EntityRenderer<ThrowRunnerEntity, ThrowRunnerRender.RenderState> {

    private final ItemModelResolver resolver;

    public ThrowRunnerRender(EntityRendererProvider.Context context) {
        super(context);
        resolver = context.getItemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ThrowRunnerEntity entity, RenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        resolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
        state.rotation = new Quaternionf();
        state.rotation.mul(Axis.YP.rotationDegrees(entity.getYRot(partialTicks)));
        state.rotation.mul(Axis.ZP.rotationDegrees(entity.getXRot(partialTicks)));
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.pushPose();
        poseStack.mulPose(state.rotation);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
    }

    public static class RenderState extends EntityRenderState {
        ItemStackRenderState item = new ItemStackRenderState();
        Quaternionf rotation = new Quaternionf();
    }
}
