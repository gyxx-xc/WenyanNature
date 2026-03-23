package indi.wenyan.setup.network.client;

import indi.wenyan.content.block.additional_module.paper.piston.PistonModuleEntity;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record PistonMovePacket(BlockPos pos, Direction direction,
                               boolean extending) implements CustomPacketPayload {
    public static final Type<PistonMovePacket> TYPE =
            IWenyanPacketPayload.createType("piston_move");

    public static final StreamCodec<FriendlyByteBuf, PistonMovePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos());
                        buffer.writeEnum(packet.direction());
                        buffer.writeBoolean(packet.extending());
                    },
                    buffer -> {
                        BlockPos pos = buffer.readBlockPos();
                        Direction direction = buffer.readEnum(Direction.class);
                        boolean extending = buffer.readBoolean();
                        return new PistonMovePacket(pos, direction, extending);
                    }
            );

    public static final IPayloadHandler<PistonMovePacket> HANDLER = (packet, context) -> {
        if (context.flow().isClientbound()) {
            PistonModuleEntity.triggleMoveBlock(context.player().level(), packet.extending(), packet.direction(), packet.pos());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
