package indi.wenyan.content.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CommunicationParticle extends TextureSheetParticle {
    private final Vec3 from;
    private final Vec3 to;

    protected CommunicationParticle(ClientLevel level, double x, double y, double z,
                                    double ex, double ey, double ez, SpriteSet spriteSet) {
        super(level, x, y, z);
        lifetime = 80;
        from = new Vec3(x, y, z);
        to = new Vec3(ex, ey, ez);
        rCol = 1.0f;
        gCol = 1.0f;
        bCol = 1.0f;
        alpha = 0.5f;
        pickSprite(spriteSet);
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        Quaternionf quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, camera, partialTicks);

        Vec3 vec3 = camera.getPosition();
        float offsetX = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float offsetY = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float offsetZ = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        float uMin = this.getU0();
        float uMax = this.getU1();
        float vStart = this.getV0();
        float vEnd = this.getV1();

        float quadSize = this.getQuadSize(partialTicks);
        int lightColor = this.getLightColor(partialTicks);
        this.renderVertex(buffer, quaternionf, offsetX, offsetY, offsetZ, 1.0F, -1.0F, quadSize, uMax, vEnd, lightColor);
        this.renderVertex(buffer, quaternionf, offsetX, offsetY, offsetZ, 1.0F, 1.0F, quadSize, uMax, vStart, lightColor);
        this.renderVertex(buffer, quaternionf, offsetX, offsetY, offsetZ, -1.0F, 1.0F, quadSize, uMin, vStart, lightColor);
        this.renderVertex(buffer, quaternionf, offsetX, offsetY, offsetZ, -1.0F, -1.0F, quadSize, uMin, vEnd, lightColor);
    }

    private void renderVertex(VertexConsumer buffer, Quaternionf quaternion,
                              float x, float y, float z, float xOffset, float yOffset, float quadSize,
                              float u, float v, int packedLight) {
        Vector3f vector3f = (new Vector3f(xOffset, yOffset, 0.0F))
                .rotate(quaternion).mul(quadSize).add(x, y, z);
        buffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(packedLight);
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            double progress = (double) this.age / this.lifetime;
            this.x = from.x + (to.x - from.x) * progress;
            this.y = from.y + (to.y - from.y) * progress;
            this.z = from.z + (to.z - from.z) * progress;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel,
                                       double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new CommunicationParticle(clientLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, spriteSet);
        }
    }
}
