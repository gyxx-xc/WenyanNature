package indi.wenyan.setup.network.client;

import indi.wenyan.content.block.IOutputAccepter;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending output from a platform to the client
 */
public record BlockOutputPacket(BlockPos pos, String output, IOutputAccepter.OutputStyle style) implements CustomPacketPayload {

    /**
     * Packet type identifier
     */
    public static final Type<BlockOutputPacket> TYPE =
            IWenyanPacketPayload.createType("platform_output");

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, BlockOutputPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.output, 512);
                        buffer.writeEnum(packet.style);
                    },
                    buffer ->
                        new BlockOutputPacket(
                                buffer.readBlockPos(),
                                buffer.readUtf(512),
                                buffer.readEnum(IOutputAccepter.OutputStyle.class))
            );

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<BlockOutputPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof IOutputAccepter runner) {
                // Process the output on the client side
                runner.addOutput(packet.output(), packet.style());
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
