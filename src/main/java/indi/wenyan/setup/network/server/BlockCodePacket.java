package indi.wenyan.setup.network.server;

import indi.wenyan.content.block.ICodeHolder;
import indi.wenyan.content.block.ICodeOutputHolder;
import indi.wenyan.setup.network.IServersidePacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/// Packet for sending code to a runner block
public record BlockCodePacket(BlockPos pos, String code, CodeTarget target) implements IServersidePacket {

    /// Which field the code targets
    public enum CodeTarget {
        RUN,
        VIEW
    }

    public BlockCodePacket(BlockPos pos, String code) {
        this(pos, code, CodeTarget.RUN);
    }

    /// Packet type identifier
    public static final Type<BlockCodePacket> TYPE =
            IWenyanPacketPayload.createType("block_code");

    /// Codec for serializing and deserializing the packet
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockCodePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.code, 16384);
                        buffer.writeEnum(packet.target);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String output = buffer.readUtf(16384);
                        CodeTarget target = buffer.readEnum(CodeTarget.class);
                        return new BlockCodePacket(pos1, output, target);
                    });

    /// Handler for processing the packet
    @Override
    public void handleOnServer(ServerPlayer player) {
        var entity = player.level().getBlockEntity(pos());
        if (entity instanceof ICodeOutputHolder holder) {
            if (target() == CodeTarget.VIEW)
                holder.setViewCode(code());
            else
                holder.setCode(code());
        } else if (entity instanceof ICodeHolder runner) {
            runner.setCode(code());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
