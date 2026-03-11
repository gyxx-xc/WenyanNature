package indi.wenyan.content.block;

import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

import java.util.List;

public interface ICommunicateEntity {
    int COMMUNICATE_EFFECT_LIFETIME = 12;

    List<CommunicationEffect> getCommunicates();

    default void tickCommunicate() {
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
