package indi.wenyan.setup.network;

import indi.wenyan.WenyanNature;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class OutputInformationPacket implements CustomPacketPayload {
    public static final Type<OutputInformationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "output_text"));

    public static final StreamCodec<FriendlyByteBuf, OutputInformationPacket> STREAM_CODEC =
            StreamCodec.of(OutputInformationPacket::write, OutputInformationPacket::new);

    public final BlockPos pos;
    public final String output;

    public OutputInformationPacket(BlockPos pos, String output) {
        this.pos = pos;
        this.output = output;
    }

    public OutputInformationPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.output = buffer.readUtf();
    }

    private static void write(FriendlyByteBuf buffer, OutputInformationPacket packet) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.output);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OutputInformationPacket) obj;
        return Objects.equals(this.pos, that.pos) &&
                Objects.equals(this.output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, output);
    }
}
