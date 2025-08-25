package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending code to a runner block
 */
public record BlockRunnerCodePacket(BlockPos pos, String code) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<BlockRunnerCodePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "block_runner_code"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, BlockRunnerCodePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.code, 16384);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String output = buffer.readUtf(16384);
                        return new BlockRunnerCodePacket(pos1, output);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<BlockRunnerCodePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof RunnerBlockEntity runner) {
                runner.pages = packet.code();
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
