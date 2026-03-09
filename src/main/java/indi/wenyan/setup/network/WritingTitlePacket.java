package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record WritingTitlePacket(BlockPos pos, String name) implements CustomPacketPayload {
    /**
     * Packet type identifier
     */
    public static final Type<WritingTitlePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "writing_title_packet"));

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<FriendlyByteBuf, WritingTitlePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.name, 64);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String name1 = buffer.readUtf(64);
                        return new WritingTitlePacket(pos1, name1);
                    });

    /**
     * Handler for processing the packet
     */
    public static final IPayloadHandler<WritingTitlePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos());
            if (entity instanceof WritingBlockEntity runner) {
                var stack = runner.getItemStack();
                if (!stack.isEmpty()) {
                    stack.set(DataComponents.CUSTOM_NAME, Component.literal(packet.name));
                }
            }
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
