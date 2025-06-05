package indi.wenyan.setup.network;

import indi.wenyan.WenyanNature;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RunnerTextPacket(int slot, List<String> pages) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RunnerTextPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WenyanNature.MODID, "runner_text"));

    public static final StreamCodec<ByteBuf, RunnerTextPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerTextPacket::slot,
            ByteBufCodecs.stringUtf8(8192).apply(ByteBufCodecs.list(200)),
            RunnerTextPacket::pages,
            RunnerTextPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
