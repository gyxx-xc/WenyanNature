package indi.wenyan.setup.network.server;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Packet for setting name of a device block entity
 */
public record DeviceRenamePacket(BlockPos pos, String name) implements IServerboundPacket {
    /**
     * Packet type identifier
     */
    public static final Type<DeviceRenamePacket> TYPE =
            IWenyanPacketPayload.createType("device_rename");

    /**
     * Codec for serializing and deserializing the packet
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, DeviceRenamePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, packet) -> {
                        buffer.writeBlockPos(packet.pos);
                        buffer.writeUtf(packet.name, 64);
                    },
                    buffer -> {
                        BlockPos pos1 = buffer.readBlockPos();
                        String name1 = buffer.readUtf(64);
                        return new DeviceRenamePacket(pos1, name1);
                    });

    /**
     * Handler for processing the packet
     */
    @Override
    public void handleOnServer(ServerPlayer player) {
        var entity = player.level().getBlockEntity(pos());
        if (entity instanceof AbstractModuleEntity device) {
            device.setPackageName(name());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
