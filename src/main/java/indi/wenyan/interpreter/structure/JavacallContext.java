package indi.wenyan.interpreter.structure;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.content.checker.IAnsweringChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanPlatform;
import lombok.Value;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@Accessors(fluent = true)
@Value
public class JavacallContext {
    RunnerWarper<?> runnerWarper;
    IWenyanValue self;
    List<IWenyanValue> args;
    WenyanThread thread;
    IExecCallHandler handler;
    Player holder;

    public interface RunnerWarper<T> {
        T runner();
    }

    public record BlockRunnerWarper(
            RunnerBlockEntity runner) implements RunnerWarper<RunnerBlockEntity> {
    }

    public record HandRunnerWarper(
            HandRunnerEntity runner) implements RunnerWarper<HandRunnerEntity> {
    }

    public record NullRunnerWarper(Void runner) implements RunnerWarper<Void> {
    }

    public record CraftingAnswerWarper(
            IAnsweringChecker runner) implements RunnerWarper<IAnsweringChecker> {
    }

    public record PlatformWarper(IWenyanPlatform runner) implements RunnerWarper<IWenyanPlatform> {
    }
}
