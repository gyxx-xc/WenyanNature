package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.BlockRunner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class OutputInformationHandler implements IPayloadHandler<OutputInformationHandler.OutputInformationPacket> {
    public static final CustomPacketPayload.Type<OutputInformationPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "output_text"));
    public static final StreamCodec<FriendlyByteBuf, OutputInformationPacket> STREAM_CODEC =
            StreamCodec.of(OutputInformationHandler::encode, OutputInformationHandler::decode);

    public static OutputInformationPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        String output = buffer.readUtf();
        return new OutputInformationPacket(pos, output);
    }

    private static void encode(FriendlyByteBuf buffer, OutputInformationPacket packet) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.output);
    }

    @Override
    public void handle(@NotNull OutputInformationPacket packet, @NotNull IPayloadContext context) {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof BlockRunner runner) {
                runner.addOutput(packet.output());
            }
        }
    }

    public record OutputInformationPacket(BlockPos pos, String output) implements CustomPacketPayload {
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
