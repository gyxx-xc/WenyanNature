package indi.wenyan.content.handler;

import indi.wenyan.WenyanNature;
import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.setup.network.OutputInformationPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

public class OutputHandler implements IJavacallHandler {

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        StringBuilder result = new StringBuilder();
        for (IWenyanValue arg : context.args()) {
            result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
        }

        if (context.runnerWarper().runner() instanceof BlockRunner runner && runner.getLevel() instanceof ServerLevel sl) {
            PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(runner.getBlockPos()),
                    new OutputInformationPacket(runner.getBlockPos(), result.toString()));
        } else if (context.runnerWarper().runner() instanceof HandRunnerEntity) {
            context.holder().displayClientMessage(Component.literal(result.toString()), true);
        } else if (context.runnerWarper().runner() instanceof CraftingAnswerChecker checker){
            checker.accept(context.args());
        } else {
            WenyanNature.LOGGER.warn("Lost Output: {}", result);
        }
        return WenyanNull.NULL;
    }

    @Override
    public boolean isLocal(JavacallContext context) {
        if (context.runnerWarper().runner() instanceof BlockRunner) {
            return false;
        } else if (context.runnerWarper().runner() instanceof HandRunnerEntity) {
            return false;
        } else if (context.runnerWarper().runner() instanceof CraftingAnswerChecker){
            return true;
        } else {
            return true;
        }
    }
}
