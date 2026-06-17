package indi.wenyan.setup.network.client;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/// Packet for sending output from a platform to the client
public record BlockDebugContextPacket(BlockPos pos, RunnerBlockEntity.DebugContext debugContext) implements CustomPacketPayload {

    /// Packet type identifier
    public static final Type<BlockDebugContextPacket> TYPE =
            IWenyanPacketPayload.createType("block_debug_context");

    /// Codec for serializing and deserializing the packet
    public static final StreamCodec<FriendlyByteBuf, BlockDebugContextPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeInt(packet.debugContext.start());
                        buffer.writeInt(packet.debugContext.end());
                    },
                    buffer ->
                        new BlockDebugContextPacket(
                                buffer.readBlockPos(),
                                new RunnerBlockEntity.DebugContext(buffer.readInt(), buffer.readInt())
                        )
            );

    /// Handler for processing the packet
    public static final IPayloadHandler<BlockDebugContextPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof RunnerBlockEntity runner) {
                runner.setDebugContext(packet.debugContext);
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
