package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.Registration;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending code to a handheld runner
 */
public record RunnerCodePacket(int slot, String code) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<RunnerCodePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "runner_code"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<ByteBuf, RunnerCodePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerCodePacket::slot,
            ByteBufCodecs.stringUtf8(16384),
            RunnerCodePacket::code,
            RunnerCodePacket::new
    );

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<RunnerCodePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            ItemStack runner = player.getInventory().getItem(packet.slot());
            runner.set(Registration.PROGRAM_CODE_DATA.get(), packet.code());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
