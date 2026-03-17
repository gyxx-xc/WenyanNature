package indi.wenyan.content.block;

import indi.wenyan.setup.network.client.CommunicationLocationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import java.util.List;

public interface ICommunicateHolder {
    int COMMUNICATE_EFFECT_LIFETIME = 12;

    List<CommunicationEffect> getCommunicates();

    default void tickCommunicate() {
        getCommunicates().removeIf(c -> c.life-- <= 0);
    }

    default void addCommunicate(BlockPos pos) {
        if (!pos.closerThan(Vec3i.ZERO, 1.2))
            getCommunicates().add(new ICommunicateHolder.CommunicationEffect(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
    }

    default void addCommunicateServer(ServerLevel sl, BlockPos from, BlockPos pos) {
        if (!pos.closerThan(Vec3i.ZERO, 1.2))
            PacketDistributor.sendToPlayersTrackingChunk(sl,
                    ChunkPos.containing(from),
                    new CommunicationLocationPacket(from, pos));
    }

    class CommunicationEffect {
        public final Vector3f pos;
        public int life = COMMUNICATE_EFFECT_LIFETIME;

        public CommunicationEffect(Vector3f pos) {
            this.pos = pos;
        }
    }
}
