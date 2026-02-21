package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.joml.Vector3f;

/**
 * Packet for communication location data between blocks
 */
public record CommunicationLocationPacket(@NonNull BlockPos from, @NonNull Vector3f to) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<CommunicationLocationPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "communication_location"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, CommunicationLocationPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.from);
                        buffer.writeVector3f(packet.to());
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        Vector3f output1 = buffer.readVector3f();
                        return new CommunicationLocationPacket(pos1, output1);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<CommunicationLocationPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.from());
            if (entity instanceof RunnerBlockEntity runner) {
                runner.setCommunicate(packet.to());
            }
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
