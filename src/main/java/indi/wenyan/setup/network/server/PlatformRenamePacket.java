package indi.wenyan.setup.network.server;

import indi.wenyan.content.block.runner.ICodeOutputHolder;
import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for setting name of a block entity
 */
public record PlatformRenamePacket(BlockPos pos, String name) implements IServerboundPacket {
    /**
     * Packet type identifier
     */
    public static final Type<PlatformRenamePacket> TYPE =
            IWenyanPacketPayload.createType("set_name");

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, PlatformRenamePacket> STREAM_CODEC =
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
    @Override
    public void handleOnServer(ServerPlayer player) {
        var entity = player.level().getBlockEntity(pos());
        if (entity instanceof ICodeOutputHolder runner) {
            runner.setPlatformName(name());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
