package indi.wenyan.interpreter.structure;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record JavacallContext(RunnerWarper<?> runnerWarper, WenyanNativeValue self, List<WenyanNativeValue> args,
                              boolean noReturn, WenyanThread thread, JavacallHandler handler, Player holder) {

    public interface RunnerWarper<T> {
        T runner();
    }

    public record BlockRunnerWarper(BlockRunner runner) implements RunnerWarper<BlockRunner> { }
    public record HandRunnerWarper(HandRunnerEntity runner) implements RunnerWarper<HandRunnerEntity> { }
    public record NullRunnerWarper(Void runner) implements RunnerWarper<Void> { }
    public record CraftingAnswerWarper(CraftingAnswerChecker runner) implements RunnerWarper<CraftingAnswerChecker> { }
}
