package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.InformativeAdditionalModuleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record BlockOutputPacket(BlockPos pos, String output) implements CustomPacketPayload {
    public static final Type<BlockOutputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "output_text"));

    public static final StreamCodec<FriendlyByteBuf, BlockOutputPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.output);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String output1 = buffer.readUtf();
                        return new BlockOutputPacket(pos1, output1);
                    });

    public static final IPayloadHandler<BlockOutputPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof InformativeAdditionalModuleEntity module) {
                module.addOutput(packet.output());
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
