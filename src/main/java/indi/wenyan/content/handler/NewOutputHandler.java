package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.setup.network.OutputInformationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class NewOutputHandler implements JavacallHandler {
    private final BlockRunner runner;

    public NewOutputHandler(BlockRunner runner) {
        this.runner = runner;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        StringBuilder result = new StringBuilder();
        for (WenyanNativeValue arg : args) {
            result.append(result.isEmpty() ? "" : " ").append(arg.toString());
        }
        if (runner.getLevel() instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(runner.getBlockPos()),
                    new OutputInformationPacket(runner.getBlockPos(), result.toString()));
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
