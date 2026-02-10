package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.crafting_block.CraftingBlockEntity;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

/**
 * Packet for transmitting particle crafting data (String + BlockPos) between server and client
 */
public record CraftingParticlePacket(@NonNull BlockPos from,
                                     @NonNull String data) implements CustomPacketPayload {

    public static final Type<CraftingParticlePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "crafting_particle"));

    public static final StreamCodec<FriendlyByteBuf, CraftingParticlePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.from);
                        buffer.writeUtf(packet.data, 100);
                    },
                    buffer -> new CraftingParticlePacket(buffer.readBlockPos(), buffer.readUtf())
            );

    public static final IPayloadHandler<CraftingParticlePacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.from());
            if (entity instanceof CraftingBlockEntity craftingBlock) {
                craftingBlock.addResultParticle(packet.data());
            }
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
