package indi.wenyan.interpreter.structure;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record JavacallContext(RunnerWarper<?> runnerWarper, IWenyanValue self, List<IWenyanValue> args,
                              WenyanThread thread, IExecCallHandler handler, Player holder) {

    public interface RunnerWarper<T> {
        T runner();
    }

    public record BlockRunnerWarper(BlockRunner runner) implements RunnerWarper<BlockRunner> { }
    public record HandRunnerWarper(HandRunnerEntity runner) implements RunnerWarper<HandRunnerEntity> { }
    public record NullRunnerWarper(Void runner) implements RunnerWarper<Void> { }
    public record CraftingAnswerWarper(IAnsweringChecker runner) implements RunnerWarper<IAnsweringChecker> { }
    public record ExecutorWarper(IWenyanExecutor runner) implements RunnerWarper<IWenyanExecutor> { }
}
