package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

/**
 * Packet for communication location data between blocks
 */
public record CommunicationLocationPacket(@NonNull BlockPos from, @NonNull Vec3 to) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<CommunicationLocationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "communication_location"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, CommunicationLocationPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.from);
                        buffer.writeVec3(packet.to);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        Vec3 output1 = buffer.readVec3();
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
