package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record RunnerTitlePacket(int slot, String title) implements CustomPacketPayload {
    public static final Type<RunnerTitlePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "runner_title"));

    public static final StreamCodec<ByteBuf, RunnerTitlePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerTitlePacket::slot,
            ByteBufCodecs.stringUtf8(16384),
            RunnerTitlePacket::title,
            RunnerTitlePacket::new
    );

    public static final IPayloadHandler<RunnerTitlePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            ItemStack runner = player.getInventory().getItem(packet.slot());
            runner.set(DataComponents.CUSTOM_NAME, Component.literal(packet.title()));
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}