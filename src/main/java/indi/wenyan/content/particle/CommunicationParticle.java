package indi.wenyan.content.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
