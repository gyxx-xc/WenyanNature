package indi.wenyan.client.gui;

import indi.wenyan.client.block.behaviour.RunnerBlockBehaviour;
import indi.wenyan.client.block.behaviour.WritingBlockBehaviour;
import indi.wenyan.setup.network.client.BlockSetScreenPacket;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public enum ScreenOpenerFactroy {;
    public static final IPayloadHandler<BlockSetScreenPacket> BLOCK_HANDLER = (packet, context) -> {
        if (context.flow().isClientbound())
            switch (packet.screenId()) {
                case "runner_block_set_screen" -> RunnerBlockBehaviour.openGui(packet.pos(), context.player());
                case "writing_block_set_screen" -> WritingBlockBehaviour.openGui(packet.pos(), context.player());
                default -> throw new IllegalStateException();
            }
    };
}
