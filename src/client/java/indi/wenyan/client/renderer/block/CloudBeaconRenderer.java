package indi.wenyan.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import indi.wenyan.content.block.cloud_beacon.ICloudBeaconRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

@NotNullByDefault
public class CloudBeaconRenderer<T extends BlockEntity & ICloudBeaconRenderable> implements BlockEntityRenderer<T, CloudBeaconRenderer.CloudBeaconRenderState> {
    public static final Identifier BEAM_LOCATION = Identifier.withDefaultNamespace("textures/entity/beacon/beacon_beam.png");
    public static final int MAX_RENDER_Y = 2048;
    private static final float BEAM_SCALE_THRESHOLD = 64F;
    public static final float SOLID_BEAM_RADIUS = 0.2F;
    public static final float BEAM_GLOW_RADIUS = 0.25F;

    public CloudBeaconRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    public CloudBeaconRenderState createRenderState() {
        return new CloudBeaconRenderState();
    }

    public void extractRenderState(
            T blockEntity, CloudBeaconRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.animationTime = blockEntity.getLevel() != null ? Math.floorMod(blockEntity.getLevel().getGameTime(), 40) + partialTicks : 0.0F;
        state.signalPos = blockEntity.getTransmitAnimationTime() + partialTicks;
        LocalPlayer player = Minecraft.getInstance().player;
        float distanceToBeacon = (float) cameraPosition.subtract(state.blockPos.getCenter()).horizontalDistance();
        state.beamRadiusScale = player != null && player.isScoping() ? 1.0F : Math.max(1.0F, distanceToBeacon / BEAM_SCALE_THRESHOLD);
    }

    public void submit(CloudBeaconRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        float beamRadiusScale = state.beamRadiusScale;
        float animationTime = state.animationTime;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.scale(beamRadiusScale, beamRadiusScale, beamRadiusScale);
        float offset = -Mth.frac(animationTime / 5);
        float distanceScale = MAX_RENDER_Y / 2f; // /2 for scale the texture in longer
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) (animationTime * 2.25F - 45.0F + disturbance(state.signalPos))));
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.beaconBeam(BEAM_LOCATION, false),
                (pose, buffer) -> renderPart(
                        pose, buffer,
                        -1,
                        0, MAX_RENDER_Y,
                        0.0F, SOLID_BEAM_RADIUS, SOLID_BEAM_RADIUS, 0.0F,
                        -SOLID_BEAM_RADIUS, 0.0F, 0.0F, -SOLID_BEAM_RADIUS,
                        0.0F, 1.0F,
                        distanceScale / SOLID_BEAM_RADIUS + offset, offset
                )
        );

        // communicate effect (w/ 绿皮科技)
        double tPos = posTrans(state.signalPos);
        if (0 < tPos && tPos < 1) {
            poseStack.pushPose();
            float transmitScale = (float) Math.min(1.3, 0.9 + beamRadiusScale / 10);
            float scaleRate = (float) (tPos * tPos * tPos * 64 + tPos * tPos * 16);
            double moveRate = tPos * 24 + 0.5;
            float endScale = (float) Math.min(1, tPos * 8);
            poseStack.scale(transmitScale * endScale, scaleRate, transmitScale * endScale);
            poseStack.translate(0, moveRate, 0);
            float[] rads = new float[]{0.21F, 0.4F, 0.5F, 0.4F, 0.21F};
            for (int i = 0; i < 5; i++) {
                int pos = i;
                float rad = rads[i];
                submitNodeCollector.submitCustomGeometry(
                        poseStack,
                        RenderTypes.beaconBeam(BEAM_LOCATION, false),
                        (pose, buffer) -> renderPart(
                                pose, buffer,
                                0xFFDDFFDD,
                                pos, pos + 1,
                                0.0F, rad, rad, 0.0F,
                                -rad, 0.0F, 0.0F, -rad,
                                0.0F, 1.0F,
                                0.5f / rad, 0
                        )
                );
            }
            poseStack.popPose();
        }
        poseStack.popPose();
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.beaconBeam(BEAM_LOCATION, true),
                (pose, buffer) -> renderPart(
                        pose, buffer,
                        ARGB.color(32, -1),
                        0, MAX_RENDER_Y,
                        -BEAM_GLOW_RADIUS, -BEAM_GLOW_RADIUS, BEAM_GLOW_RADIUS, -BEAM_GLOW_RADIUS,
                        -BEAM_GLOW_RADIUS, BEAM_GLOW_RADIUS, BEAM_GLOW_RADIUS, BEAM_GLOW_RADIUS,
                        0.0F, 1.0F,
                        distanceScale / BEAM_GLOW_RADIUS + offset, offset
                )
        );
        poseStack.popPose();
    }

    private static double posTrans(double p) {
        return Math.abs(p / 40);
    }

    private static double disturbance(double p) {
        // a function that has f(x) and f'(x) is 0
        // at 10 and 30 (the segment that is ensured continuous)
        // (x/5 - 2)^2 * (x/5 - 6)^2
        double i = Math.abs(p);
        if (i < 0 || i > 30) return 0;
        return -0.2 * (i / 5) * (i / 5) * (i / 5 - 6) * (i / 5 - 6);
    }

    @Override

    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    @Override
    public boolean shouldRender(T blockEntity, Vec3 cameraPosition) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(cameraPosition.multiply(1.0, 0.0, 1.0), this.getViewDistance());
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(T blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, MAX_RENDER_Y, pos.getZ() + 1.0);
    }

    public static class CloudBeaconRenderState extends BlockEntityRenderState {
        private float animationTime;
        private float beamRadiusScale;
        private double signalPos; // in (-1, )]
    }

    // shit by mojang
    private static void renderPart(PoseStack.Pose pose, VertexConsumer builder, int color, int beamStart, int beamEnd, float wnx, float wnz, float enx, float enz, float wsx, float wsz, float esx, float esz, float uu1, float uu2, float vv1, float vv2) {
        renderQuad(pose, builder, color, beamStart, beamEnd, wnx, wnz, enx, enz, uu1, uu2, vv1, vv2);
        renderQuad(pose, builder, color, beamStart, beamEnd, esx, esz, wsx, wsz, uu1, uu2, vv1, vv2);
        renderQuad(pose, builder, color, beamStart, beamEnd, enx, enz, esx, esz, uu1, uu2, vv1, vv2);
        renderQuad(pose, builder, color, beamStart, beamEnd, wsx, wsz, wnx, wnz, uu1, uu2, vv1, vv2);
    }

    private static void renderQuad(PoseStack.Pose pose, VertexConsumer builder, int color, int beamStart, int beamEnd, float wnx, float wnz, float enx, float enz, float uu1, float uu2, float vv1, float vv2) {
        addVertex(builder, pose, color, wnx, beamEnd, wnz, uu2, vv1);
        addVertex(builder, pose, color, wnx, beamStart, wnz, uu2, vv2);
        addVertex(builder, pose, color, enx, beamStart, enz, uu1, vv2);
        addVertex(builder, pose, color, enx, beamEnd, enz, uu1, vv1);
    }

    private static void addVertex(VertexConsumer builder, PoseStack.Pose pose, int color, float x, int y, float z, float u, float v) {
        builder.addVertex(pose, x, (float) y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
