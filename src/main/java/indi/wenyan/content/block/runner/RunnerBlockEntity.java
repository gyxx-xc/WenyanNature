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
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    public static final String ID = "runner_block_entity";
    private IWenyanProgram<WenyanProgramImpl.PCB> optionalProgram = null;

    private IWenyanProgram<WenyanProgramImpl.PCB> getProgram() {
        if (optionalProgram == null || !optionalProgram.isAvailable())
            optionalProgram = new WenyanProgramImpl(this);
        return optionalProgram;
    }

    private Optional<IWenyanProgram<WenyanProgramImpl.PCB>> ifProgram() {
        return Optional.ofNullable(optionalProgram);
    }

    @Getter
    private String code = "";
    @Getter
    private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;

    @Getter
    public final ExecQueue execQueue = new ExecQueue(this);
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    public static final int DEVICE_SEARCH_RANGE = 3;
    private final RequestCallHandler importFunction = (t, _, a) ->
            new ImportRequest(t, this::getPackage, a);

    @Getter
    private String platformName = Component.translatable("code.wenyan_programming.bracket", getBlockState().getBlock().getName()).getString();

    @Getter
    private final List<CommunicationEffect> communicates = new ArrayList<>();

    @Override
    public WenyanPackage initEnvironment() {
        var baseEnvironment = IWenyanPlatform.super.initEnvironment();
        baseEnvironment.put(WenyanPackages.IMPORT_ID, importFunction);
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
        if (getLevel().getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            baseEnvironment.combine(processPackage(device.getExecPackage(), device));
        return baseEnvironment;
    }

    @Override
    public void handleError(String error) {
        error = StringUtils.left(error, 512);
        errors.addLast(error);
    }

    private void handleErrorTicked() {
        for (String error : errors) {
            assert getLevel() != null;
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, RunnerBlock.RunningState.ERROR), Block.UPDATE_CLIENTS);
            if (getLevel() instanceof ServerLevel sl)
                PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                        new PlatformOutputPacket(getBlockPos(), error, PlatformOutputPacket.OutputStyle.ERROR));
            addOutput(error, PlatformOutputPacket.OutputStyle.ERROR);

        }
        errors.clear();
    }

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            ifProgram().ifPresentOrElse(program -> {
                if (program.isRunning()) {
                    program.step(speedFromTier(((RunnerBlock) getBlockState().getBlock()).getTier()));
                    handle(new BlockContext(level, pos, state));
                }
                // update showing state
                // As you can see, it's a busy wait checking if the program status
                // but why not listener?
                // the program loop is running on a different thread, access to level
                // when listener is toggle might produce strange result. Meanwhile, ways
                // like sync result until next tick has almost same cost as this
                // (both need const level op every tick)
                RunnerBlock.RunningState runningState;
                if (program.isRunning()) {
                    if (program instanceof WenyanProgramImpl impl && impl.isIdle()) {
                        runningState = RunnerBlock.RunningState.IDLE;
                    } else {
                        runningState = RunnerBlock.RunningState.RUNNING;
                    }
                } else {
                    runningState = RunnerBlock.RunningState.NOT_RUNNING;
                }
                updateShowingState(runningState);
            }, () -> updateShowingState(RunnerBlock.RunningState.NOT_RUNNING));
            handleErrorTicked();
        } else {
            tickCommunicate();
        }
    }

    private void updateShowingState(RunnerBlock.RunningState state) {
        var oldState = getBlockState().getValue(RUNNING_STATE);
        if (oldState != state) {
            // error state will continue showed unless next step's change
            if (oldState == RunnerBlock.RunningState.ERROR &&
                    (state == RunnerBlock.RunningState.NOT_RUNNING || state == RunnerBlock.RunningState.IDLE))
                return;
            assert level != null;
            level.setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, state), Block.UPDATE_CLIENTS);
        }
    }

    public boolean isRunning() {
        return ifProgram()
                .map(IWenyanProgram::isRunning)
                .orElse(false);
    }

    public void playerRun() {
        if (getProgram().isRunning()) {
            handleError(Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        newThread(code);
    }

    public static final String PAGES_ID = "pages";
    public static final String PLATFORM_NAME_ID = "platformName";

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
        ifProgram().ifPresent(IWenyanProgram::stop);
        super.setRemoved();
    }

    private Either<WenyanPackage, String> getPackage(IHandleContext context, String packageName) throws WenyanException {
        assert level != null;
        for (BlockPos b : BlockPos.betweenClosed(
                getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof IWenyanBlockDevice executor) {
                if (executor.getPackageName().equals(packageName)) {
                    showCommunication(executor.blockPos());
                    return Either.left(processPackage(executor.getExecPackage(), executor));
                }
            } else if (blockEntity instanceof RunnerBlockEntity platform) {
                if (platform == this) continue;
                if (platform.platformName.equals(packageName)) {
                    showCommunication(platform.getBlockPos());
                    return Either.right(platform.code);
                }
            }
        }
        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
    }

    public Optional<IWenyanRunner> newThread() {
        return newThread(code);
    }

    public Optional<IWenyanRunner> newThread(String pages) {
        try {
            return newThread(WenyanRunner.of(WenyanFrame.ofCode(pages), this.initEnvironment()));
        } catch (WenyanCompileException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<IWenyanRunner> newThread(IWenyanRunner runner) {
        assert getLevel() != null;
        if (getBlockState().getValue(RUNNING_STATE) != RunnerBlock.RunningState.RUNNING)
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, RunnerBlock.RunningState.RUNNING), Block.UPDATE_CLIENTS);
        try {
            getProgram().create(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
        return Optional.of(runner);
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

    private int speedFromTier(int tier) {
        return switch (tier) {
            case 0 -> 1;
            case 1 -> 10;
            case 2 -> 100;
            case 3 -> 1000;
            case 4 -> 10000;
            case 5 -> 100000;
            case 6 -> 1000000;
            default -> throw new IllegalArgumentException("invalid tier");
        };
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
        IRawRequest request;

        @NonFinal
        boolean communicationShown = false;

        public BlockRequest(IWenyanRunner thread, IWenyanValue self, List<IWenyanValue> argsList, IWenyanBlockDevice device, IRawRequest request) {
            this.thread = thread;
            this.self = self;
            this.args = argsList;
            this.device = device;
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
