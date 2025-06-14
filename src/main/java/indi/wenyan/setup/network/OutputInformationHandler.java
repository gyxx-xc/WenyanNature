package indi.wenyan.setup.network;

import indi.wenyan.content.block.BlockRunner;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class OutputInformationHandler implements IPayloadHandler<OutputInformationPacket> {
    @Override
    public void handle(@NotNull OutputInformationPacket packet, @NotNull IPayloadContext context) {
        if (context.flow().isClientbound()) {
            var entity = context.player().level().getBlockEntity(packet.pos);
            if (entity instanceof BlockRunner runner) {
                runner.addOutput(packet.output);
            }
        }
    }
}
