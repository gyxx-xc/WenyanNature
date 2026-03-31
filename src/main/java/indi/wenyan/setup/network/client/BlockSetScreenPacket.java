package indi.wenyan.setup.network.client;

import indi.wenyan.content.gui_impl.ScreenEnum;
import indi.wenyan.setup.network.IClientsidePacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record BlockSetScreenPacket(BlockPos pos, ScreenEnum screenId) implements IClientsidePacket {
    public static final Type<BlockSetScreenPacket> TYPE =
            IWenyanPacketPayload.createType("set_screen");

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockSetScreenPacket> STREAM_CODEC = StreamCodec.of(
            (bytebuf, packet) -> {
                bytebuf.writeBlockPos(packet.pos);
                bytebuf.writeEnum(packet.screenId);
            },
            bytebuf -> new BlockSetScreenPacket(bytebuf.readBlockPos(),
                    bytebuf.readEnum(ScreenEnum.class))
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
