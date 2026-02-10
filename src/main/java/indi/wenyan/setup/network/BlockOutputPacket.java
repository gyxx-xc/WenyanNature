package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending output text to a block entity
 */
public record BlockOutputPacket(BlockPos pos, String output) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<BlockOutputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "output_text"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, BlockOutputPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.output);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String output1 = buffer.readUtf();
                        return new BlockOutputPacket(pos1, output1);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<BlockOutputPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof IDisplayable module) {
                module.addOutput(packet.output());
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public interface IDisplayable {
        void addOutput(String text);
    }
}
