package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.data.ProgramCodeData;
import indi.wenyan.setup.Registration;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record RunnerCodePacket(int slot, String code) implements CustomPacketPayload {
    public static final Type<RunnerCodePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "runner_code"));

    public static final StreamCodec<ByteBuf, RunnerCodePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerCodePacket::slot,
            ByteBufCodecs.stringUtf8(8192),
            RunnerCodePacket::code,
            RunnerCodePacket::new
    );

    public static final IPayloadHandler<RunnerCodePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            ItemStack runner = player.getInventory().items.get(packet.slot());
            runner.set(Registration.PROGRAM_CODE_DATA.get(),
                    new ProgramCodeData(packet.code()));
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
