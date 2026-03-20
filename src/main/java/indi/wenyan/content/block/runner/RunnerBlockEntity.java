package indi.wenyan.content.block.runner;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.block.ICommunicateHolder;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.exec_interface.structure.ImportRequest;
import indi.wenyan.judou.exec_interface.structure.SimpleRequest;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.client.PlatformOutputPacket;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import static indi.wenyan.content.block.runner.RunnerBlock.RUNNING_STATE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform, ICommunicateHolder, ICodeOutputHolder {
    public static final String PAGES_ID = "pages";
    public static final String PLATFORM_NAME_ID = "platformName";
    public static final String ID = "runner_block_entity";

    @Getter private final ExecQueue execQueue = new ExecQueue(this);
    @Getter private final List<CommunicationEffect> communicates = new ArrayList<>();

    @Delegate(types = ICodeOutputHolder.class)
    private final TitleCodeOutput titleCodeOutput;

    private final LazyProgram<IWenyanProgram<WenyanProgramImpl.PCB>> lazyProgram = new LazyProgram<>(() ->
            new WenyanProgramImpl(this));
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    private final int steps;
    private RunnerBlock.RunningState runningState;
    private final BlockPackageGetter blockPackageGetter = new BlockPackageGetter(this::safeAddCommunicate);

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
        titleCodeOutput = new TitleCodeOutput("",
                Component.translatable("code.wenyan_programming.bracket", getBlockState().getBlock().getName()).getString());
        titleCodeOutput.setOnChanged(this::setChanged);
        if (blockState.getBlock() instanceof RunnerBlock block)
            steps = block.getTier().getStepSpeed();
        else steps = 1;
        runningState = blockState.getValue(RUNNING_STATE);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            if (!errors.isEmpty()) {
                for (String error : errors) {
                    level.setBlock(getBlockPos(), getBlockState()
                            .setValue(RUNNING_STATE, RunnerBlock.RunningState.ERROR), Block.UPDATE_CLIENTS);
                    addOutputBothSide(error, PlatformOutputPacket.OutputStyle.ERROR);
                }
                errors.clear();
            }

            RunnerBlock.RunningState newState = lazyProgram.ifCreated()
                    .filter(IWenyanProgram::isRunning)
                    .map(program -> {
                        program.step(steps);
                        handle(new BlockRequest.BlockContext(level, pos, state));

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
            if (runningState == RunnerBlock.RunningState.ERROR && newState == RunnerBlock.RunningState.NOT_RUNNING)
                return;

            if (runningState != newState) {
                runningState = newState;
                level.setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, runningState), Block.UPDATE_CLIENTS);
            }
        } else {
            tickCommunicate();
        }
    }

    @Override
    public WenyanPackage initEnvironment() {
        var baseEnvironment = IWenyanPlatform.super.initEnvironment();

        baseEnvironment.put(WenyanPackages.IMPORT_ID, (RequestCallHandler) (t, _, a) ->
                new ImportRequest(t, (_, name) -> {
                    var either = blockPackageGetter.getPackage(level, getBlockPos(), name);
                    if (either == null)
                        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", name).getString());
                    return either;
                }, a));
        baseEnvironment.put("書", (RequestCallHandler) (thread, self, argsList) ->
                new SimpleRequest(thread, self, argsList,
                        (ignore, args) -> {
                            String s = args.getFirst().as(WenyanString.TYPE).value();
                            addOutputBothSide(s, PlatformOutputPacket.OutputStyle.NORMAL);
                            return WenyanNull.NULL;
                        }));

        assert getLevel() != null;
        BlockPos attached = getBlockPos().relative(
                AbstractFuluBlock.getConnectedDirection(getBlockState()).getOpposite());
        var device = getLevel().getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, attached);
        if (device != null)
            baseEnvironment.combine(blockPackageGetter.processPackage(device.getExecPackage(), device));
        return baseEnvironment;
    }

    @Override
    public void handleError(String error) {
        errors.addLast(error);
    }

    @Override
    public void setRemoved() {
        lazyProgram.ifCreated().ifPresent(IWenyanProgram::stop);
        super.setRemoved();
    }

    @Override
    protected void saveData(ValueOutput tag) {
        tag.putString(PAGES_ID, titleCodeOutput.getCode());
        tag.putString(PLATFORM_NAME_ID, titleCodeOutput.getPlatformName());
    }

    @Override
    protected void loadData(ValueInput tag) {
        tag.getString(PAGES_ID).ifPresent(titleCodeOutput::setCode);
        tag.getString(PLATFORM_NAME_ID).ifPresent(titleCodeOutput::setPlatformName);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        titleCodeOutput.setCode(components.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), ""));
        titleCodeOutput.setPlatformName(components.getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(titleCodeOutput.getPlatformName())).getString());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(WyRegistration.PROGRAM_CODE_DATA.get(), titleCodeOutput.getCode());
        components.set(DataComponents.CUSTOM_NAME, Component.literal(titleCodeOutput.getPlatformName()));
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
            lazyProgram.create().create(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
            return Optional.empty();
        }
        return Optional.of(runner);
    }

    public boolean isRunning() {
        return lazyProgram.ifCreated()
                .map(IWenyanProgram::isRunning)
                .orElse(false);
    }

    public void playerRun() {
        if (lazyProgram.create().isRunning()) {
            handleError(Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        newThread(titleCodeOutput.getCode());
    }

    private void addOutputBothSide(String error, PlatformOutputPacket.OutputStyle style) {
        error = StringUtils.left(error, 512);
        if (getLevel() instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                    new PlatformOutputPacket(getBlockPos(), error, style));
        addOutput(error, style);
    }

    private void safeAddCommunicate(BlockPos blockPos) {
        if (getLevel() instanceof ServerLevel sl)
            ICommunicateHolder.blockAddCommunicateServer(sl, getBlockPos(), blockPos.subtract(getBlockPos()));
    }
}
