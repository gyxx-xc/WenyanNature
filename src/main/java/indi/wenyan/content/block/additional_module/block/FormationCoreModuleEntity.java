package indi.wenyan.content.block.additional_module.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.content.block.runner.RunnerBlock;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.structure.BaseHandleableRequest;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
    private final Map<String, BlockPos> findedPlatforms = new HashMap<>();
    @Getter
    private final List<ICommunicateHolder.CommunicationEffect> communicates = new ArrayList<>();
    private final int formationRange = WenyanConfig.getFormationRange();

    public FormationCoreModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final String basePackageName = "「眼」";

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「啓」", request -> {
                for (var arg : request.args()) {
                    String platformName = arg.as(WenyanString.TYPE).value();
                    var block = getRunner(platformName);
                    if (block == null) throw new WenyanException(NotFindFu.string());
                    if (level instanceof ServerLevel serverLevel)
                        ICommunicateHolder.blockAddCommunicateServer(serverLevel, getBlockPos(), block.getBlockPos().subtract(getBlockPos()));
                    block.newThread(block.getCode())
                            .orElseThrow(() -> new WenyanException(CantStart.string(platformName)));
                }
                return WenyanNull.NULL;
            })
            .handler("「狀」", request -> {
                if (request.args().size() != 1) throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(1, request.args().size()));
                String name = request.args().getFirst().as(WenyanString.TYPE).value();
                String runnerName = Component.translatable("code.wenyan_programming.bracket", name).getString();
                var block = getStartedRunner(runnerName);
                if (block == null) throw new WenyanException(NotFindFu.string());
                var state = block.getBlockState().getValueOrElse(RunnerBlock.RUNNING_STATE, RunnerBlock.RunningState.NOT_RUNNING);
                return new WenyanRunningState(state);
            })
            .handler("「歸」", (BaseHandleableRequest.IRawRequest) (_, request) -> {
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
                    request.thread().getCurrentRuntime().pushReturnValue(WenyanNull.NULL);
                    request.thread().unblock();
                    return true;
                }
                return false;
            })
            .build();

    private @Nullable RunnerBlockEntity getRunner(String name) {
        String runnerName = Component.translatable("code.wenyan_programming.bracket", name).getString();
        // check started
        RunnerBlockEntity cachedPlatform = getStartedRunner(runnerName);
        if (cachedPlatform != null) {
            if (level instanceof ServerLevel serverLevel)
                ICommunicateHolder.blockAddCommunicateServer(serverLevel, getBlockPos(), cachedPlatform.getBlockPos().subtract(getBlockPos()));
            return cachedPlatform;
        }

        if (findedPlatforms.containsKey(runnerName)) {
            var pos = findedPlatforms.get(runnerName);
            assert level != null;
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform) {
                String platformName = platform.getPlatformName();
                if (runnerName.equals(platformName)) {
                    startedPlatforms.put(runnerName, platform);
                    return platform;
                } else {
                    findedPlatforms.remove(runnerName);
                    findedPlatforms.put(platformName, pos);
                    // fall through
                }
            } else {
                findedPlatforms.remove(runnerName);
                // fall through
            }
        }

        // iter found
        // TODO: performance issue
        assert level != null;
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(formationRange, -formationRange, formationRange), getBlockPos().offset(-formationRange, formationRange, -formationRange))) {
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform) {
                String platformName = platform.getPlatformName();
                findedPlatforms.put(platformName, pos);
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
        public static final WenyanType<WenyanRunningState> TYPE = new WenyanType<>("running_state", WenyanRunningState.class);

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
