package indi.wenyan.setup.network.server;

import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending code to a runner block
 */
public record WritingCodePacket(BlockPos pos, String code) implements IServerboundPacket {
    /**
     * Packet type identifier
     */
    public static final Type<WritingCodePacket> TYPE =
            IWenyanPacketPayload.createType("writing_code_packet");

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WritingCodePacket> STREAM_CODEC =
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
    @Override
    public void handleOnServer(ServerPlayer player) {
        var entity = player.level().getBlockEntity(pos());
        if (entity instanceof WritingBlockEntity runner) {
            var stack = runner.getItemStack();
            if (!stack.isEmpty()) {
                stack.set(WyRegistration.PROGRAM_CODE_DATA, code());
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
