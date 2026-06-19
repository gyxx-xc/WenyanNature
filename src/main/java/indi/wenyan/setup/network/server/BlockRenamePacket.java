package indi.wenyan.setup.network.server;

import indi.wenyan.content.block.IRenamable;
import indi.wenyan.setup.network.IServersidePacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/// Packet for setting name of a block entity
public record BlockRenamePacket(BlockPos pos, String name) implements IServersidePacket {
    /// Packet type identifier
    public static final Type<BlockRenamePacket> TYPE =
            IWenyanPacketPayload.createType("set_name");

    /// Codec for serializing and deserializing the packet
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockRenamePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.name, 64);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String name1 = buffer.readUtf(64);
                        return new BlockRenamePacket(pos1, name1);
                    });

    /// Handler for processing the packet
    @Override
    public void handleOnServer(ServerPlayer player) {
        var entity = player.level().getBlockEntity(pos());
        if (entity instanceof IRenamable runner) {
            runner.setName(name());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
