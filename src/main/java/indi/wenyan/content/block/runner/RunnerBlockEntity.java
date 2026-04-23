package indi.wenyan.content.block.runner;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.*;
import indi.wenyan.judou.compiler.IWenyanBytecode;
import indi.wenyan.judou.compiler.WenyanCompiler;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.handler.RequestCallHandler;
import indi.wenyan.judou.exec_interface.structure.*;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanScheduler;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.RunnerCreator;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanSchedularImpl;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.function.ChineseUtils;
import indi.wenyan.judou.utils.language.Symbol;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.language.ExceptionText;
import indi.wenyan.setup.network.client.BlockDebugContextPacket;
import indi.wenyan.setup.network.client.BlockOutputPacket;
import lombok.Getter;
import lombok.Setter;
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
    private final TitleCodeOutputData titleCodeOutput;

    @Setter @Getter private DebugContext debugContext = new DebugContext(0, 0);

    private final LazyProgram<IWenyanScheduler<WenyanSchedularImpl.PCB>> lazyProgram;
    private final Deque<String> errors = new ConcurrentLinkedDeque<>();
    private RunnerBlock.RunningState runningState;
    private final BlockPackageGetter blockPackageGetter = new BlockPackageGetter(this::safeAddCommunicate);

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
        titleCodeOutput = new TitleCodeOutputData("",
                ChineseUtils.bracketOf(blockState.getBlock().getName().getString()));
        titleCodeOutput.setOnChanged(this::setChanged);
        int steps;
        if (blockState.getBlock() instanceof RunnerBlock block)
            steps = block.getTier().getStepSpeed();
        else steps = 1;
        lazyProgram = new LazyProgram<>(() -> new WenyanSchedularImpl(this, steps));
        runningState = blockState.getValue(RUNNING_STATE);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            if (!errors.isEmpty()) {
                for (String error : errors) {
                    runningState = RunnerBlock.RunningState.ERROR;
                    level.setBlock(getBlockPos(), getBlockState().setValue(RUNNING_STATE, runningState), Block.UPDATE_CLIENTS);
                    addOutputBothSide(error, IOutputAcceptor.OutputStyle.ERROR);
                }
                errors.clear();
            }

            RunnerBlock.RunningState newState = lazyProgram.ifCreated()
                    .filter(IWenyanScheduler::isRunning)
                    .map(program -> {
                        boolean remainSteps = program instanceof WenyanSchedularImpl impl && impl.remainSteps();

                        program.step();
                        handle(new BlockRequest.BlockContext(level, pos, state));

                        if (remainSteps) {
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

    private WenyanPackage initEnvironment() {
        var baseEnvironment = IWenyanPlatform.initEnvironment();

        baseEnvironment.put(Symbol.IMPORT_ID, (RequestCallHandler) (t, _, a) ->
                new ImportRequest(t, (_, name) -> {
                    var either = blockPackageGetter.getPackage(level, getBlockPos(), name);
                    if (either == null)
                        throw new WenyanException.WenyanVarException(ExceptionText.ImportNotFound.string(name));
                    return either;
                }, a));
        baseEnvironment.put("書", (RequestCallHandler) (thread, self, argsList) ->
                new SimpleRequest(thread, self, argsList,
                        (ignore, args) -> {
                            StringBuilder sb = new StringBuilder();
                            boolean firstFlag = true;
                            for (IWenyanValue arg : args) {
                                if (!firstFlag) sb.append(" ");
                                else firstFlag = false;
                                sb.append(arg.toString());
                            }
                            addOutputBothSide(sb.toString(), IOutputAcceptor.OutputStyle.NORMAL);
                            return WenyanNull.NULL;
                        }));
        baseEnvironment.put(Symbol.DEBUG_ID, (RequestCallHandler) (thread, _, _) ->
                new BaseHandleableRequest() {
                    private int i = 0;

                    @Override
                    public IWenyanRunner thread() {
                        return thread;
                    }

                    @Override
                    public boolean handle(IHandleContext context) throws WenyanException {
                        if (i == 0) {
                            var runtimeContext = thread.getCurrentRuntime().getBytecode().getContext(
                                    thread.getCurrentRuntime().getProgramCounter() - 1);
                            changeContextBothSide(runtimeContext == null ? new DebugContext(0, 0) :
                                    new DebugContext(runtimeContext.contentStart(), runtimeContext.contentEnd()));
                        }
                        if (++i < 2) return false;
                        thread.unblock();
                        return true;
                    }
                });

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
        lazyProgram.ifCreated().ifPresent(IWenyanScheduler::stop);
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

    public boolean newThread(String pages) {
        IWenyanBytecode bytecode;
        try {
            bytecode = new WenyanCompiler().compile(pages).bytecode();
        } catch (WenyanCompileException e) {
            handleError(e.getMessage());
            return false;
        }
        try {
            IThreadHolder<WenyanSchedularImpl.PCB> runner =
                    RunnerCreator.newRunner(WenyanFrame.ofCode(bytecode), this.initEnvironment());
            lazyProgram.createOrGet().create(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean isRunning() {
        return lazyProgram.ifCreated()
                .map(IWenyanScheduler::isRunning)
                .orElse(false);
    }

    public void playerRun() {
        if (lazyProgram.createOrGet().isRunning()) {
            handleError(ExceptionText.AlreadyRun.string());
            return;
        }
        newThread(titleCodeOutput.getCode());
    }

    public void playerDebugRun() {
        if (lazyProgram.createOrGet().isRunning()) {
            handleError(ExceptionText.AlreadyRun.string());
            return;
        }
        IWenyanBytecode bytecode;
        try {
            bytecode = new WenyanCompiler(true).compile(titleCodeOutput.getCode()).bytecode();
        } catch (WenyanCompileException e) {
            handleError(e.getMessage());
            return;
        }
        try {
            IThreadHolder<WenyanSchedularImpl.PCB> runner =
                    RunnerCreator.newRunner(WenyanFrame.ofCode(bytecode), this.initEnvironment());
            lazyProgram.createOrGet().create(runner);
        } catch (WenyanException e) {
            handleError(e.getMessage());
        }
    }

    private void addOutputBothSide(String error, IOutputAcceptor.OutputStyle style) {
        error = StringUtils.left(error, 512);
        if (getLevel() instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                    new BlockOutputPacket(getBlockPos(), error, style));
        addOutput(error, style);
    }

    private void changeContextBothSide(DebugContext context) {
        if (getLevel() instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, ChunkPos.containing(getBlockPos()),
                    new BlockDebugContextPacket(getBlockPos(), context));
        setDebugContext(context);
    }

    private void safeAddCommunicate(BlockPos blockPos) {
        if (getLevel() instanceof ServerLevel sl)
            ICommunicateHolder.blockAddCommunicateServer(sl, getBlockPos(), blockPos.subtract(getBlockPos()));
    }

    public record DebugContext(int start, int end) {
    }
}
