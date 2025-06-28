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
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class RunnerTextServerPayloadHandler implements IPayloadHandler<RunnerTextServerPayloadHandler.RunnerTextPacket> {
    public static final CustomPacketPayload.Type<RunnerTextPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "runner_text"));
    public static final StreamCodec<ByteBuf, RunnerTextPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RunnerTextPacket::slot,
            ByteBufCodecs.stringUtf8(8192),
            RunnerTextPacket::code,
            RunnerTextPacket::new
    );

    @Override
    public void handle(@NotNull RunnerTextPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            ItemStack runner = player.getInventory().items.get(payload.slot());
            runner.set(Registration.PROGRAM_CODE_DATA.get(),
                    new ProgramCodeData(payload.code()));
        }
    }

    public record RunnerTextPacket(int slot, String code) implements CustomPacketPayload {
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
