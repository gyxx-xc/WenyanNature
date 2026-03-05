package indi.wenyan.content.block.additional_module.block;

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
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FormationCoreModuleEntity extends AbstractModuleEntity {

    private final Map<String, RunnerBlockEntity> platforms = new HashMap<>();
    private static final int RANGE = 10;

    public FormationCoreModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get(), pos, blockState);
    }

    @Getter
    private final String basePackageName = "「眼」";

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler("「啓」", request -> {
                for (var arg : request.args()) {
                    var block = getRunner(arg.as(WenyanString.TYPE).value());
                    if (block == null) throw new WenyanException("can't find fu");
                    block.newThread().orElseThrow(() -> new WenyanException("can't start"));
                }
                return WenyanNull.NULL;
            })
            .handler("「狀」", request -> {
                if (request.args().size() != 1) throw new WenyanException("args not correct");
                var block = getRunner(request.args().getFirst().as(WenyanString.TYPE).value());
                if (block == null) throw new WenyanException("can't find fu");
                var state = block.getBlockState().getValueOrElse(RunnerBlock.RUNNING_STATE, RunnerBlock.RunningState.NOT_RUNNING);
                return new WenyanRunningState(state);
            })
            .handler("「歸」", (BaseHandleableRequest.IRawRequest) (_, request) -> {
                var running = BlockPos.betweenClosedStream(getBlockPos().offset(RANGE, -RANGE, RANGE), getBlockPos().offset(-RANGE, RANGE, -RANGE))
                        .map(pos -> {
                            assert level != null;
                            var state = level.getBlockState(pos)
                                    .getValueOrElse(RunnerBlock.RUNNING_STATE, RunnerBlock.RunningState.NOT_RUNNING);
                            return state == RunnerBlock.RunningState.RUNNING;
                        })
                        .reduce(false, (b1, b2) -> b1 || b2);
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
        // check cache
        var cachedPlatform = platforms.get(runnerName);
        if (cachedPlatform != null) {
            if (!cachedPlatform.isRemoved())
                return cachedPlatform;
            else
                platforms.remove(runnerName);
        }

        // iter found
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(RANGE, -RANGE, RANGE), getBlockPos().offset(-RANGE, RANGE, -RANGE))) {
            assert level != null;
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform) {
                platforms.putIfAbsent(platform.getPlatformName(), platform);
                if (runnerName.equals(platform.getPlatformName())) {
                    // if found, stop
                    return platform;
                }
            }
        }

        // not found
        return null;
    }

    public record WenyanRunningState(
            RunnerBlock.RunningState value) implements IWenyanWarperValue<RunnerBlock.RunningState> {
        public static final WenyanType<WenyanRunningState> TYPE = new WenyanType<>("running_state", WenyanRunningState.class);

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IWenyanValue wenyanValue) {
                try {
                    return wenyanValue.is(TYPE) && value.equals(wenyanValue.as(TYPE).value);
                } catch (WenyanException.WenyanTypeException ignored) {
                    // unreached
                }
            }
            return false;
        }

        @Override
        public @NonNull String toString() {
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
