package indi.wenyan.setup.network.server;

import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending code to a handheld runner
 */
public record RunnerCodePacket(int slot, String code) implements IServerboundPacket {
    /**
     * Packet type identifier
     */
    public static final Type<RunnerCodePacket> TYPE =
            IWenyanPacketPayload.createType("runner_code");

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, RunnerCodePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerCodePacket::slot,
            ByteBufCodecs.stringUtf8(16384),
            RunnerCodePacket::code,
            RunnerCodePacket::new
    );

    /**
     * Handler for processing the packet
     */
    @Override
    public void handleOnServer(ServerPlayer player) {
        ItemStack runner = player.getInventory().getItem(slot());
        runner.set(WyRegistration.PROGRAM_CODE_DATA.get(), code());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
