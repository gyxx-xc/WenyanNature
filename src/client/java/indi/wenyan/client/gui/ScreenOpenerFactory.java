package indi.wenyan.client.gui;

import indi.wenyan.client.gui.behaviour.FloatNoteBehaviour;
import indi.wenyan.client.gui.behaviour.RunnerBlockBehaviour;
import indi.wenyan.client.gui.behaviour.WritingBlockBehaviour;
import indi.wenyan.setup.network.client.BlockSetScreenPacket;
import indi.wenyan.setup.network.client.FloatNoteSetScreenPacket;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public enum ScreenOpenerFactory {;
    public static final IPayloadHandler<BlockSetScreenPacket> BLOCK_HANDLER = (packet, context) -> {
        if (context.flow().isClientbound())
            switch (packet.screenId()) {
                case RUNNER_BLOCK -> RunnerBlockBehaviour.openGui(packet.pos(), context.player());
                case RUNNER_BLOCK_RO -> RunnerBlockBehaviour.openGuiRo(packet.pos(), context.player());
                case WRITING_BLOCK -> WritingBlockBehaviour.openGui(packet.pos(), context.player());
                case RUNNER_BLOCK_DEBUG -> RunnerBlockBehaviour.openDebugGui(packet.pos(), context.player());
                case LLM_BLOCK -> RunnerBlockBehaviour.openLLMGui(packet.pos(), context.player());
                default -> throw new IllegalStateException();
            }
    };
    public static final IPayloadHandler<FloatNoteSetScreenPacket> FLOAT_NOTE_HANDLER = (packet, context) -> {
        if (context.flow().isClientbound())
            FloatNoteBehaviour.openGui(packet.pos(), packet.hand(), context.player());
    };
}
