package indi.wenyan.content.block.additional_module.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
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
import indi.wenyan.setup.network.CommunicationLocationPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FormationCoreModuleEntity extends AbstractModuleEntity {

    private final Map<String, RunnerBlockEntity> platforms = new HashMap<>();
    @Getter
    private final List<CommunicationEffect> effects = new ArrayList<>();
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
                String name = request.args().getFirst().as(WenyanString.TYPE).value();
                String runnerName = Component.translatable("code.wenyan_programming.bracket", name).getString();
                var block = getStartedRunner(runnerName);
                if (block == null) throw new WenyanException("can't find fu");
                var state = block.getBlockState().getValueOrElse(RunnerBlock.RUNNING_STATE, RunnerBlock.RunningState.NOT_RUNNING);
                return new WenyanRunningState(state);
            })
            .handler("「歸」", (BaseHandleableRequest.IRawRequest) (_, request) -> {
                boolean running = false;
                var iter = platforms.entrySet().iterator();
                while (iter.hasNext()) {
                    var platformEntry = iter.next();
                    RunnerBlockEntity entity = platformEntry.getValue();
                    if (entity.isRemoved())
                        iter.remove();
                    else if (entity.getBlockState().getValueOrElse(RunnerBlock.RUNNING_STATE,
                            RunnerBlock.RunningState.NOT_RUNNING) == RunnerBlock.RunningState.RUNNING) {
                        running = true;
                        break;
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
        // check cache
        RunnerBlockEntity cachedPlatform = getStartedRunner(runnerName);
        if (cachedPlatform != null) {
            if (level instanceof ServerLevel serverLevel)
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, ChunkPos.containing(getBlockPos()),
                        new CommunicationLocationPacket(getBlockPos(), cachedPlatform.getBlockPos().subtract(getBlockPos())));
            return cachedPlatform;
        }

        // iter found
        // TODO: performance issue
        assert level != null;
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().offset(RANGE, -RANGE, RANGE), getBlockPos().offset(-RANGE, RANGE, -RANGE))) {
            if (level.getBlockEntity(pos) instanceof RunnerBlockEntity platform &&
                    runnerName.equals(platform.getPlatformName())) {
                platforms.put(runnerName, platform);
                if (level instanceof ServerLevel serverLevel)
                    PacketDistributor.sendToPlayersTrackingChunk(serverLevel, ChunkPos.containing(getBlockPos()),
                            new CommunicationLocationPacket(getBlockPos(), pos.subtract(getBlockPos())));
                return platform;
            }
        }

        // not found
        return null;
    }

    private @Nullable RunnerBlockEntity getStartedRunner(String runnerName) {
        var cachedPlatform = platforms.get(runnerName);
        if (cachedPlatform != null) {
            if (!cachedPlatform.isRemoved())
                return cachedPlatform;
            else
                platforms.remove(runnerName);
        }
        return null;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        effects.removeIf(communicationEffect ->
            communicationEffect.life -- <= 0
        );
    }

    public void setCommunicate(BlockPos pos) {
        effects.add(new CommunicationEffect(new Vector3f(pos.getX(), pos.getY(), pos.getZ())));
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
                    return wenyanValue.is(TYPE) && value.equals(wenyanValue.as(TYPE).value);
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

    public static class CommunicationEffect {
        public final Vector3f pos;
        public int life = 14;

        public CommunicationEffect(Vector3f pos) {
            this.pos = pos;
        }
    }
}
