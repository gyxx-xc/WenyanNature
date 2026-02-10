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
 * Packet for clearing particle data at a specific position
 */
public record CraftClearParticlePacket(@NonNull BlockPos pos) implements CustomPacketPayload {

    public static final Type<CraftClearParticlePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "clear_particle"));

    public static final StreamCodec<FriendlyByteBuf, CraftClearParticlePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> buffer.writeBlockPos(packet.pos),
                    buffer -> new CraftClearParticlePacket(buffer.readBlockPos())
            );

    public static final IPayloadHandler<CraftClearParticlePacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof CraftingBlockEntity craftingBlock) {
                craftingBlock.clearParticles();
            }
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}