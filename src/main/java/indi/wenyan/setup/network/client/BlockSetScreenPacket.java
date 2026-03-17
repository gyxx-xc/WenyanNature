package indi.wenyan.setup.network.client;

import indi.wenyan.setup.network.IClientboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record BlockSetScreenPacket(BlockPos pos, String screenId) implements IClientboundPacket {
    public static final Type<BlockSetScreenPacket> TYPE = IWenyanPacketPayload.createType("set_screen");

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockSetScreenPacket> STREAM_CODEC = StreamCodec.of(
            (bytebuf, packet) -> {
                bytebuf.writeBlockPos(packet.pos);
                bytebuf.writeUtf(packet.screenId);
            },
            bytebuf -> new BlockSetScreenPacket(bytebuf.readBlockPos(), bytebuf.readUtf())
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
