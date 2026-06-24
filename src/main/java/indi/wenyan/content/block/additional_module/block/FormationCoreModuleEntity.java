package indi.wenyan.content.block.additional_module.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.interpreter_impl.value.WenyanCodeWithExecutor;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.utils.ChineseUtils;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.IWenyanWarperValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.primitive.WenyanString;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFunction;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.FunctionMetaText;
import indi.wenyan.setup.language.TypeText;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static indi.wenyan.setup.language.ExceptionText.CantStart;
import static indi.wenyan.setup.language.ExceptionText.NotFindFu;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FormationCoreModuleEntity extends AbstractModuleEntity implements ICommunicateHolder {

    private final Map<String, RunnerBlockEntity> startedPlatforms = new HashMap<>();
    private final Map<String, BlockPos> foundPlatforms = new HashMap<>();
    @Getter
    private final List<ICommunicateHolder.CommunicationEffect> communicates = new ArrayList<>();
    private final int formationRange = WenyanConfig.getFormationRange();

    public FormationCoreModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final String basePackageName = WenyanSymbol.FORMATION_CORE;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .description(FunctionMetaText.CoreStart.string())
            .handler(WenyanSymbol.CORE_START, request -> {
                for (var arg : request.args()) {
                    String platformName = arg.as(WenyanString.TYPE).value();
                    var block = getRunner(platformName);
                    if (block == null) throw new WenyanException(NotFindFu.string());
                    if (level instanceof ServerLevel serverLevel)
                        ICommunicateHolder.blockAddCommunicateServer(serverLevel, getBlockPos(), block.getBlockPos().subtract(getBlockPos()));
                    if (!block.newThread(block.getCode()))
                        throw new WenyanException(CantStart.string(platformName));
                }
                return WenyanNull.NULL;
            })
            .description(FunctionMetaText.CoreStatus.string())
            .handler(WenyanSymbol.CORE_STATUS, request -> {
                if (request.args().size() != 1)
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(1, request.args().size()));
                String name = request.args().getFirst().as(WenyanString.TYPE).value();
                String runnerName = ChineseUtils.bracketOf(name);
                var block = getStartedRunner(runnerName);
                if (block == null) throw new WenyanException(NotFindFu.string());
                var state = block.getBlockState().getValueOrElse(RunnerBlock.RUNNING_STATE, RunnerBlock.RunningState.NOT_RUNNING);
                return new WenyanRunningState(state);
            })
            .description(FunctionMetaText.CoreJoin.string())
            .handler(WenyanSymbol.CORE_JOIN, (_, request, onReturn) -> {
                boolean running = false;
                var iter = startedPlatforms.entrySet().iterator();
                while (iter.hasNext()) {
                    var platformEntry = iter.next();
                    RunnerBlockEntity entity = platformEntry.getValue();
                    if (entity.isRemoved()) {
                        iter.remove();
                        continue; // ignore
                    }
                    if (entity.isRunning()) {
                        running = true;
                        break;
                    } else {
                        iter.remove();
                    }
                }
                if (!running) {
                    onReturn.accept(WenyanNull.NULL);
                    request.thread().unblock();
                    return true;
                }
                return false;
            })
            .description(FunctionMetaText.CoreExec.string())
            .handler(WenyanSymbol.CORE_EXEC, request -> {
                var args = request.args();
                if (args.size() != 2)
                    throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(2, args.size()));
                var fuObj = args.get(0).as(WenyanCodeWithExecutor.TYPE);
                var function = args.get(1).as(WenyanBuiltinFunction.TYPE);
                var packageable = fuObj.packageable();
                if (packageable.isRemoved())
                    throw new WenyanException(NotFindFu.string());
                if (level instanceof ServerLevel serverLevel)
                    ICommunicateHolder.blockAddCommunicateServer(serverLevel, getBlockPos(),
                            packageable.getBlockPos().subtract(getBlockPos()));
                packageable.newThread(function.bytecode());
                return WenyanNull.NULL;
            })
            .build();

    private @Nullable RunnerBlockEntity getRunner(String name) {
        String runnerName = ChineseUtils.bracketOf(name);
        // check started
        RunnerBlockEntity cachedPlatform = getStartedRunner(runnerName);
        if (cachedPlatform != null) {
            if (level instanceof ServerLevel serverLevel)
                ICommunicateHolder.blockAddCommunicateServer(serverLevel, getBlockPos(), cachedPlatform.getBlockPos().subtract(getBlockPos()));
            return cachedPlatform;
        }

        if (foundPlatforms.containsKey(runnerName)) {
            var pos = foundPlatforms.get(runnerName);
            assert level != null;
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform) {
                String platformName = platform.getPlatformName();
                if (runnerName.equals(platformName)) {
                    startedPlatforms.put(runnerName, platform);
                    return platform;
                } else {
                    foundPlatforms.remove(runnerName);
                    foundPlatforms.put(platformName, pos);
                    // fall through
                }
            } else {
                foundPlatforms.remove(runnerName);
                // fall through
            }
        }

        // iter found
        // TODO: performance issue
        assert level != null;
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(formationRange, -formationRange, formationRange), getBlockPos().offset(-formationRange, formationRange, -formationRange))) {
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform) {
                String platformName = platform.getPlatformName();
                foundPlatforms.put(platformName, pos);
                if (runnerName.equals(platformName)) {
                    startedPlatforms.put(runnerName, platform);
                    return platform;
                }
            }
        }

        // not found
        return null;
    }

    private @Nullable RunnerBlockEntity getStartedRunner(String runnerName) {
        var started = startedPlatforms.get(runnerName);
        if (started != null) {
            if (!started.isRemoved())
                return started;
            else
                startedPlatforms.remove(runnerName);
        }
        return null;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        ICommunicateHolder.super.tickCommunicate();
    }

    public record WenyanRunningState(RunnerBlock.RunningState value)
            implements IWenyanWarperValue<RunnerBlock.RunningState> {
        public static final WenyanType<WenyanRunningState> TYPE = new WenyanType<>(TypeText.RunningState.string(), WenyanRunningState.class);

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IWenyanValue wenyanValue) {
                try {
                    return wenyanValue.is(TYPE) && value == wenyanValue.as(TYPE).value;
                } catch (WenyanException.WenyanTypeException ignored) {
                    // unreached
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return switch (value) {
                // TODO: change name
                case RUNNING -> "running";
                case IDLE -> "idle";
                case ERROR -> "error";
                case NOT_RUNNING -> "not_running";
            };
        }
    }
}
