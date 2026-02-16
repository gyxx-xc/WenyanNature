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
 * Packet for setting name of a block entity
 */
public record PlatformRenamePacket(BlockPos pos, String name) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<PlatformRenamePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "set_name"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, PlatformRenamePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.name, 64);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String name1 = buffer.readUtf(64);
                        return new PlatformRenamePacket(pos1, name1);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<PlatformRenamePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof RunnerBlockEntity runner) {
                runner.setPlatformName(packet.name());
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
