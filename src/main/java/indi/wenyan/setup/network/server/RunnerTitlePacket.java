package indi.wenyan.setup.network.server;

import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record RunnerTitlePacket(int slot, String title) implements IServerboundPacket {
    public static final Type<RunnerTitlePacket> TYPE =
            IWenyanPacketPayload.createType("runner_title");

    public static final StreamCodec<RegistryFriendlyByteBuf, RunnerTitlePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerTitlePacket::slot,
            ByteBufCodecs.stringUtf8(16384),
            RunnerTitlePacket::title,
            RunnerTitlePacket::new
    );

    @Override
    public void handleOnServer(ServerPlayer player) {
        ItemStack runner = player.getInventory().getItem(slot());
        runner.set(DataComponents.CUSTOM_NAME, Component.literal(title()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}