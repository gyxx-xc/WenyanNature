package indi.wenyan.content.block;

import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

import java.util.Collection;

public interface ICommunicateEntity {
    int COMMUNICATE_EFFECT_LIFETIME = 12;

    Collection<CommunicationEffect> getCommunicates();

    default void tickUpdate() {
        getCommunicates().removeIf(c -> c.life -- <= 0);
    }

    default void addCommunicate(BlockPos pos) {
        getCommunicates().add(new ICommunicateEntity.CommunicationEffect(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
    }

    class CommunicationEffect {
        public final Vector3f pos;
        public int life = COMMUNICATE_EFFECT_LIFETIME;

        public CommunicationEffect(Vector3f pos) {
            this.pos = pos;
        }
    }
}
