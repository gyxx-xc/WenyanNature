package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending code to a runner block
 */
public record WritingCodePacket(BlockPos pos, String code) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<WritingCodePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "writing_code_packet"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, WritingCodePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.code, 16384);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String output = buffer.readUtf(16384);
                        return new WritingCodePacket(pos1, output);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<WritingCodePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof WritingBlockEntity runner) {
                var stack = runner.getItemStack();
                if (!stack.isEmpty()) {
                    stack.set(WyRegistration.PROGRAM_CODE_DATA, packet.code);
                }
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
