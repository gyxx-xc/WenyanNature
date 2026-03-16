package indi.wenyan.content.block.runner;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.block.ICommunicateEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.exec_interface.structure.*;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.Either;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.CommunicationLocationPacket;
import indi.wenyan.setup.network.PlatformOutputPacket;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform, ICommunicateEntity {
    public static final String PAGES_ID = "pages";
    public static final String PLATFORM_NAME_ID = "platformName";
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    public static final String ID = "runner_block_entity";
    public static final int DEVICE_SEARCH_RANGE = 3;
    private static final Map<Integer, Integer> TIER_SPEED_MAP = Map.of(
            0, 1,
            1, 10,
            2, 100,
            3, 1000,
            4, 10000,
            5, 100000,
            6, 1000000
    );

    @Getter private final ExecQueue execQueue = new ExecQueue(this);
    @Getter private String platformName;
    @Getter private final List<CommunicationEffect> communicates = new ArrayList<>();

    private final LazyProgram<IWenyanProgram<WenyanProgramImpl.PCB>> lazyProgram = new LazyProgram<>(() ->
            new WenyanProgramImpl(this));
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    private final int steps;
    private RunnerBlock.RunningState runningState;

    @Getter private String code = "";

    @Getter private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
        platformName = Component.translatable("code.wenyan_programming.bracket", getBlockState().getBlock().getName()).getString();
        if (blockState.getBlock() instanceof RunnerBlock block)
            steps = TIER_SPEED_MAP.get(block.getTier());
        else steps = 1;
        runningState = blockState.getValue(RUNNING_STATE);
    }

    public Optional<IWenyanRunner> newThread(String pages) {
        IWenyanRunner runner;
        try {
            runner = WenyanRunner.of(WenyanFrame.ofCode(pages), this.initEnvironment());
        } catch (WenyanCompileException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
        try {
            lazyProgram.get().create(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
        assert getLevel() != null;
        if (getBlockState().getValue(RUNNING_STATE) != RunnerBlock.RunningState.RUNNING)
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE,
                    RunnerBlock.RunningState.RUNNING), Block.UPDATE_CLIENTS);
        return Optional.of(runner);
    }

    @Override
    public WenyanPackage initEnvironment() {
        var baseEnvironment = IWenyanPlatform.super.initEnvironment();
        baseEnvironment.put(WenyanPackages.IMPORT_ID, (RequestCallHandler) (t, _, a) ->
                new ImportRequest(t, this::getPackage, a));
        baseEnvironment.put("書", (RequestCallHandler) (thread, self, argsList) ->
                new SimpleRequest(thread, self, argsList,
                        (ignore, args) -> {
                            String s = args.getFirst().as(WenyanString.TYPE).value();
                            s = StringUtils.left(s, 512);
                            if (getLevel() instanceof ServerLevel sl) {
                                PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                                        new PlatformOutputPacket(getBlockPos(), s, PlatformOutputPacket.OutputStyle.NORMAL));
                            }
                            addOutput(s, PlatformOutputPacket.OutputStyle.NORMAL);
                            return WenyanNull.NULL;
                        }));

        assert getLevel() != null;
        BlockPos attached = getBlockPos().relative(
                AbstractFuluBlock.getConnectedDirection(getBlockState()).getOpposite());
        if (getLevel().getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, attached) instanceof IWenyanBlockDevice device)
            baseEnvironment.combine(processPackage(device.getExecPackage(), device));
        return baseEnvironment;
    }

    @Override
    public void handleError(String error) {
        error = StringUtils.left(error, 512);
        errors.addLast(error);
    }

    private void handleErrorTicked() {
        if (errors.isEmpty()) return;

        assert getLevel() != null;
        for (String error : errors) {
            getLevel().setBlock(getBlockPos(), getBlockState()
                    .setValue(RUNNING_STATE, RunnerBlock.RunningState.ERROR), Block.UPDATE_CLIENTS);
            if (getLevel() instanceof ServerLevel sl)
                PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                        new PlatformOutputPacket(getBlockPos(), error, PlatformOutputPacket.OutputStyle.ERROR));
            addOutput(error, PlatformOutputPacket.OutputStyle.ERROR);
        }
        errors.clear();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            handleErrorTicked();
            RunnerBlock.RunningState newState = lazyProgram.ifCreated()
                    .filter(IWenyanProgram::isRunning)
                    .map(program -> {
                        program.step(steps);
                        handle(new BlockContext(level, pos, state));

                        if (program instanceof WenyanProgramImpl impl && impl.isIdle()) {
                            return RunnerBlock.RunningState.IDLE;
                        } else {
                            return RunnerBlock.RunningState.RUNNING;
                        }
                    }).orElse(RunnerBlock.RunningState.NOT_RUNNING);

            // update showing state
            // As you can see, it's a busy wait checking if the program status
            // but why not listener?
            // the program loop is running on a different thread, access to level
            // when listener is toggle might produce strange result. Meanwhile, ways
            // like sync result until next tick has almost same cost as this
            // (both need const level op every tick)

            // error state will continue showed unless next step's change
            if (runningState == RunnerBlock.RunningState.ERROR &&
                    (newState == RunnerBlock.RunningState.NOT_RUNNING || newState == RunnerBlock.RunningState.IDLE))
                return;

            if (runningState != newState) {
                level.setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, newState), Block.UPDATE_CLIENTS);
            }
        } else {
            tickCommunicate();
        }
    }

    public boolean isRunning() {
        return lazyProgram.ifCreated()
                .map(IWenyanProgram::isRunning)
                .orElse(false);
    }

    public void playerRun() {
        if (lazyProgram.get().isRunning()) {
            handleError(Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        newThread(code);
    }

    @Override
    protected void saveData(ValueOutput tag) {
        tag.putString(PAGES_ID, code);
        tag.putString(PLATFORM_NAME_ID, platformName);
    }

    @Override
    protected void loadData(ValueInput tag) {
        tag.getString(PAGES_ID).ifPresent(this::setCode);
        tag.getString(PLATFORM_NAME_ID).ifPresent(this::setPlatformName);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        code = components.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), "");
        platformName = components.getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(platformName)).getString();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(WyRegistration.PROGRAM_CODE_DATA.get(), code);
        components.set(DataComponents.CUSTOM_NAME, Component.literal(platformName));
    }

    @SneakyThrows
    @Override
    public void setRemoved() {
        lazyProgram.ifCreated().ifPresent(IWenyanProgram::stop);
        super.setRemoved();
    }

    private Either<WenyanPackage, String> getPackage(IHandleContext context, String packageName) throws WenyanException {
        assert level != null;
        for (BlockPos pos : BlockPos.betweenClosed(
                getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RunnerBlockEntity platform) {
                if (platform == this) continue;
                if (platform.platformName.equals(packageName)) {
                    showCommunication(pos);
                    return Either.right(platform.code);
                }
            } else {
                var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, pos);
                if (executor != null) {
                    if (executor.getPackageName().equals(packageName)) {
                        showCommunication(pos);
                        return Either.left(processPackage(executor.getExecPackage(), executor));
                    }
                }
            }
        }
        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
    }

    @Contract("_, _ -> new")
    private WenyanPackage processPackage(RawHandlerPackage rawPackage, IWenyanBlockDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, (RequestCallHandler) (thread, self, argsList) ->
                        new BlockRequest(thread, self, argsList, device, function.get())));
        return new WenyanPackage(map);
    }

    private void showCommunication(BlockPos blockPos) {
        if (getLevel() instanceof ServerLevel sl) {
            PacketDistributor.sendToPlayersTrackingChunk(sl,
                    ChunkPos.containing(getBlockPos()),
                    new CommunicationLocationPacket(getBlockPos(), blockPos.subtract(getBlockPos()))
            );
        }
    }

    public void setCode(String code) {
        this.code = code;
        setChanged();
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
        setChanged();
    }

    public boolean isOutputChanged() {
        var temp = outputChanged;
        outputChanged = false;
        return temp;
    }

    public void addOutput(String output, PlatformOutputPacket.OutputStyle style) {
        if (style == PlatformOutputPacket.OutputStyle.ERROR)
            outputQueue.addLast(Component.literal(output).withStyle(ChatFormatting.RED));
        else if (style == PlatformOutputPacket.OutputStyle.NORMAL)
            outputQueue.addLast(Component.literal(output));
        while (outputQueue.size() > MAX_OUTPUT_SHOWING_SIZE) {
            outputQueue.removeFirst();
        }
        outputChanged = true;
        setChanged();
    }

    public record BlockContext(Level level, BlockPos pos,
                               BlockState state) implements IHandleContext {
    }

    @Value
    @Accessors(fluent = true)
    public class BlockRequest implements BaseHandleableRequest, IArgsRequest {
        IWenyanRunner thread;
        IWenyanValue self;
        List<IWenyanValue> args;

        IWenyanBlockDevice device;
        BlockPos pos;
        IRawRequest request;

        @NonFinal
        boolean communicationShown = false;

        public BlockRequest(IWenyanRunner thread, IWenyanValue self, List<IWenyanValue> argsList, IWenyanBlockDevice device, IRawRequest request) {
            this.thread = thread;
            this.self = self;
            this.args = argsList;
            this.device = device;
            this.pos = device.blockPos();
            this.request = request;
        }

        @Override
        public boolean handle(IHandleContext context) throws WenyanException {
            if (device().isRemoved())
                throw new WenyanException("device removed");
            if (!communicationShown) {
                showCommunication(device.blockPos());
                communicationShown = true;
            }
            return request.handle(context, this);
        }
    }
}
