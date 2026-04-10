package indi.wenyan.setup.network.client;

import indi.wenyan.setup.network.IClientsidePacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import org.jspecify.annotations.NonNull;

public record FloatNoteSetScreenPacket(BlockPos pos, InteractionHand hand) implements IClientsidePacket {
    public static final Type<FloatNoteSetScreenPacket> TYPE =
            IWenyanPacketPayload.createType("float_note_set_screen");

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatNoteSetScreenPacket> STREAM_CODEC = StreamCodec.of(
            (bytebuf, packet) -> {
                bytebuf.writeBlockPos(packet.pos);
                bytebuf.writeEnum(packet.hand);
            },
            bytebuf -> new FloatNoteSetScreenPacket(bytebuf.readBlockPos(),
                    bytebuf.readEnum(InteractionHand.class))
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
