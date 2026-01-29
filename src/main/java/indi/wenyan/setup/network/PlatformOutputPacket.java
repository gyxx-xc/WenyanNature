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
 * Packet for sending output from a platform to the client
 */
public record PlatformOutputPacket(BlockPos pos, String output, OutputStyle style) implements CustomPacketPayload {
    public enum OutputStyle {
        NORMAL,
        ERROR
    }

    /**
     * Packet type identifier
     */
    public static final Type<PlatformOutputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "platform_output"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, PlatformOutputPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.output);
                        buffer.writeEnum(packet.style);
                    },
                    buffer ->
                        new PlatformOutputPacket(
                                buffer.readBlockPos(),
                                buffer.readUtf(),
                                buffer.readEnum(OutputStyle.class))
            );

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<PlatformOutputPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var level = context.player().level();
            var entity = level.getBlockEntity(packet.pos());
            if (entity instanceof RunnerBlockEntity runner) {
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
