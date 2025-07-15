package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record CommunicationLocationPacket(@NonNull BlockPos from, @NonNull BlockPos to) implements CustomPacketPayload {
    public static final Type<BlockOutputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "communication_location"));

    public static final StreamCodec<FriendlyByteBuf, CommunicationLocationPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.from);
                        buffer.writeBlockPos(packet.to);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        BlockPos output1 = buffer.readBlockPos();
                        return new CommunicationLocationPacket(pos1, output1);
                    });

    public static final IPayloadHandler<CommunicationLocationPacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.from());
            if (entity instanceof RunnerBlockEntity runner) {
                runner.setCommunicate(packet.to());
            }
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
