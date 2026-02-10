package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.additional_module.paper.BlockModuleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for sending block position range data
 */
public record BlockPosRangePacket(BlockPos pos, BlockPos start, BlockPos end, boolean found) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<BlockPosRangePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "output_pos_text"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, BlockPosRangePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeBlockPos(packet.start);
                        buffer.writeBlockPos(packet.end);
                        buffer.writeBoolean(packet.found);
                    },
                    buffer -> {
                        BlockPos pos = buffer.readBlockPos();
                        BlockPos start = buffer.readBlockPos();
                        BlockPos end = buffer.readBlockPos();
                        boolean found = buffer.readBoolean();
                        return new BlockPosRangePacket(pos, start, end, found);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<BlockPosRangePacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof BlockModuleEntity module) {
                Vec3 startVec = packet.start.getCenter();
                Vec3 start = new Vec3(Math.floor(startVec.x), Math.floor(startVec.y), Math.floor(startVec.z));
                Vec3 endVec = packet.end.getCenter();
                Vec3 end = new Vec3(Math.ceil(endVec.x), Math.ceil(endVec.y), Math.ceil(endVec.z));
                module.addRenderRange(start, end, packet.found);
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
