package indi.wenyan.client.gui;

import indi.wenyan.client.gui.behaviour.RunnerBlockBehaviour;
import indi.wenyan.client.gui.behaviour.WritingBlockBehaviour;
import indi.wenyan.setup.network.client.BlockSetScreenPacket;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public enum ScreenOpenerFactroy {;
    public static final IPayloadHandler<BlockSetScreenPacket> BLOCK_HANDLER = (packet, context) -> {
        if (context.flow().isClientbound())
            switch (packet.screenId()) {
                case RUNNER_BLOCK -> RunnerBlockBehaviour.openGui(packet.pos(), context.player());
                case WRITING_BLOCK -> WritingBlockBehaviour.openGui(packet.pos(), context.player());
                default -> throw new IllegalStateException();
            }
    };
}
