package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.exec_interface.structure.*;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.Either;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.CommunicationLocationPacket;
import indi.wenyan.setup.network.PlatformOutputPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;
import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_TIER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform {
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    public static final int COMMUNICATE_EFFECT_LIFETIME = 12;
    private IWenyanProgram optionalProgram = null;

    private IWenyanProgram getProgram() {
        if (optionalProgram == null || !optionalProgram.isAvailable())
            optionalProgram = new WenyanProgramImpl(this);
        return optionalProgram;
    }

    private Optional<IWenyanProgram> ifProgram() {
        return Optional.ofNullable(optionalProgram);
    }

    @Getter
    @Setter
    private String code;
    @Getter
    private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;

    @Getter
    public final ExecQueue execQueue = new ExecQueue(this);
    public static final int DEVICE_SEARCH_RANGE = 3;
    private final RequestCallHandler importFunction = (t, s, a) ->
            new ImportRequest(t, this, this::getPackage, a);

    @Getter
    private String platformName = Component.translatable("code.wenyan_programming.bracket", getBlockState().getBlock().getName()).getString();

    @Getter
    private final Map<Vec3, Integer> communications = new HashMap<>();

    @Override
    public WenyanRuntime initEnvironment() {
        var baseEnvironment = IWenyanPlatform.super.initEnvironment();
        baseEnvironment.setVariable(WenyanPackages.IMPORT_ID, importFunction);
        baseEnvironment.setVariable("書", (RequestCallHandler) (thread, self, argsList) ->
                new SimpleRequest(thread, self, argsList,
                        (ignore, args) -> {
                            String s = args.getFirst().as(WenyanString.TYPE).value();
                            s = StringUtils.left(s, 512);
                            if (getLevel() instanceof ServerLevel sl) {
                                PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                                        new PlatformOutputPacket(getBlockPos(), s, PlatformOutputPacket.OutputStyle.NORMAL));
                            }
                            addOutput(s, PlatformOutputPacket.OutputStyle.NORMAL);
                            return WenyanNull.NULL;
                        }));

        assert getLevel() != null;
        BlockPos attached = getBlockPos().relative(
                AbstractFuluBlock.getConnectedDirection(getBlockState()).getOpposite());
        if (getLevel().getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            baseEnvironment.importPackage(processPackage(device.getExecPackage(), device));
        return baseEnvironment;
    }

    @Override
    public void handleError(String error) {
        error = StringUtils.left(error, 512);
        assert getLevel() != null;
        getLevel().setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, RunnerBlock.RunningState.ERROR), Block.UPDATE_CLIENTS);
        if (getLevel() instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                    new PlatformOutputPacket(getBlockPos(), error, PlatformOutputPacket.OutputStyle.ERROR));
        addOutput(error, PlatformOutputPacket.OutputStyle.ERROR);
    }

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            ifProgram().ifPresentOrElse(program -> {
                if (program.isRunning()) {
                    program.step(speedFromTier(getBlockState().getValue(RUNNING_TIER)));
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
        } else {
            var iterator = communications.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue() > COMMUNICATE_EFFECT_LIFETIME)
                    iterator.remove();
                else
                    entry.setValue(entry.getValue() + 1);
            }
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

    public void playerRun() {
        if (getProgram().isRunning()) {
            handleError(Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        newThread(code);
    }

    public void setCommunicate(Vec3 to) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        var from = getBlockPos().getCenter();
        // distance limit
        if (from.distanceToSqr(to) >= 2) {
//            level.addParticle(Registration.COMMUNICATION_PARTICLES.get(),
//                    from.x(), from.y(), from.z(),
//                    to.x(), to.y(), to.z());
            communications.put(to.subtract(from), 0);
        }
    }

    public static final String PAGES_ID = "pages";
    public static final String PLATFORM_NAME_ID = "platformName";

    @SuppressWarnings("unused")
    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (code != null)
            tag.putString(PAGES_ID, code);
        tag.putString(PLATFORM_NAME_ID, platformName);
    }

    @SuppressWarnings("unused")
    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(PAGES_ID)) {
            code = tag.getString(PAGES_ID);
        }
        platformName = tag.getString(PLATFORM_NAME_ID);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        code = componentInput.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), "");
        platformName = componentInput.getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(platformName)).getString();
    }

    @SneakyThrows
    @Override
    public void setRemoved() {
        ifProgram().ifPresent(IWenyanProgram::stop);
        super.setRemoved();
    }

    private Either<WenyanPackage, WenyanThread> getPackage(IHandleContext context, String packageName) throws WenyanException {
        assert level != null;
        for (BlockPos b : BlockPos.betweenClosed(
                getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof IWenyanBlockDevice executor) {
                if (executor.getPackageName().equals(packageName))
                    return Either.left(getExecutorPackage(executor));
            } else if (blockEntity instanceof RunnerBlockEntity platform) {
                if (platform == this) continue;
                if (platform.getPlatformName().equals(packageName))
                    return Either.right(createPlatformThread(platform));
            }
        }
        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
    }

    private WenyanThread createPlatformThread(RunnerBlockEntity platform) throws WenyanException {
        showCommunication(platform.getBlockPos());
        // STUB
        var threadOptional = platform.newThread(platform.code);
        // this exception is for import-er thread
        // in the same time, there's an error in import-ee's platform
        return threadOptional.orElseThrow(() -> new WenyanException("cannot import"));
    }

    public Optional<WenyanThread> newThread(String pages) {
        assert getLevel() != null;
        if (getBlockState().getValue(RUNNING_STATE) != RunnerBlock.RunningState.RUNNING)
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, RunnerBlock.RunningState.RUNNING), Block.UPDATE_CLIENTS);
        try {
            WenyanThread runner = WenyanThread.ofCode(pages, this);
            getProgram().create(runner);
            return Optional.of(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
    }

    private WenyanPackage getExecutorPackage(IWenyanBlockDevice executor) {
        showCommunication(executor.blockPos());
        return processPackage(executor.getExecPackage(), executor);
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
                    new ChunkPos(getBlockPos()),
                    new CommunicationLocationPacket(getBlockPos(), blockPos.getCenter())
            );
        }
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
            case 1 -> COMMUNICATE_EFFECT_LIFETIME;
            case 2 -> 100;
            case 3 -> 1000;
            default -> throw new IllegalArgumentException("invalid tier");
        };
    }

    private record BlockContext(Level level, BlockPos pos,
                                BlockState state) implements IHandleContext {
    }

    @Value
    @Accessors(fluent = true)
    public class BlockRequest implements BaseHandleableRequest {
        WenyanThread thread;
        IWenyanValue self;
        List<IWenyanValue> args;

        IWenyanBlockDevice device;
        IRawRequest request;

        @Override
        public boolean handle(IHandleContext context) throws WenyanException {
            return request.handle(context, this);
        }

        @Override
        public void noticePlatform(IWenyanPlatform platform, IHandleContext context) throws WenyanException {
            if (device().isRemoved()) {
                throw new WenyanException("device removed");
            }
            showCommunication(device.blockPos());
        }
    }
}
