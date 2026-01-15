package indi.wenyan.content.block.runner;

import com.mojang.datafixers.util.Either;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.interpreter.exec_interface.HandlerPackageBuilder;
import indi.wenyan.interpreter.exec_interface.IWenyanBlockDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.handler.RequestCallHandler;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.IHandleableRequest;
import indi.wenyan.interpreter.exec_interface.structure.ImportRequest;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.CommunicationLocationPacket;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform {
    private WenyanProgram program = null;

    private WenyanProgram getProgram(Player player) {
        if (program == null)
            program = new WenyanProgram(player, this);
        return program;
    }

    private Optional<WenyanProgram> ifProgram() {
        return Optional.ofNullable(program);
    }

    public String pages;
    public int speed;

    @Getter
    public final ExecQueue execQueue = new ExecQueue();
    public static final int DEVICE_SEARCH_RANGE = 3;
    private final RequestCallHandler importFunction = (t, s, a) ->
            new ImportRequest(t, this, this::getPackage, a);

    @Getter
    @Setter
    private String platformName = Component.translatable("code.wenyan_programming.bracket", Component.translatable("block.wenyan_programming.runner_block")).getString();

    @Override
    public void changeInitEnvironment(WenyanRuntime baseEnvironment) {
        baseEnvironment.setVariable(WenyanPackages.IMPORT_ID, importFunction);

        assert getLevel() != null;
        BlockPos attached = getBlockPos().relative(
                RunnerBlock.getConnectedDirection(getBlockState()).getOpposite());
        if (getLevel().getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            baseEnvironment.importPackage(processPackage(device.getExecPackage(), device));
    }

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide)
            ifProgram().ifPresent(program -> {
                if (program.isRunning()) {
                    program.step(speed);
                    handle(new BlockContext(level, pos, state));
                }
            });
    }

    public void playerRun(Player player) {
        var p = getProgram(player);
        if (p.isRunning()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        try {
            p.createThread(pages);
        } catch (WenyanException.WenyanVarException e) {
            WenyanException.handleException(player, e.getMessage());
        }
    }

    public void setCommunicate(Vec3 to) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        var from = getBlockPos().getCenter();
        // distance limit
        if (from.distanceToSqr(to) >= 2)
            level.addParticle(Registration.COMMUNICATION_PARTICLES.get(),
                    from.x(), from.y(), from.z(),
                    to.x(), to.y(), to.z());
    }

    @Override
    public void notice(IHandleableRequest request, IHandleContext context) {
        if (!(context instanceof BlockContext blockContext)) {
            throw new WenyanException.WenyanUnreachedException();
        }
        Level level = blockContext.level();
        if (!(level instanceof ServerLevel sl)) {
            throw new WenyanException.WenyanUnreachedException();
        }

        if (request instanceof BlockRequest blockRequest) {
            if (blockRequest.device().isRemoved()) {
                throw new WenyanException("device removed");
            }
            if (blockRequest.device() instanceof IWenyanBlockDevice device) {
                PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                        new CommunicationLocationPacket(getBlockPos(), device.blockPos().getCenter()));
            }
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (pages != null)
            tag.putString("pages", pages);
        tag.putInt("speed", speed);
        tag.putString("platformName", platformName);
    }

    @SuppressWarnings("unused")
    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.contains("pages")) {
            pages = tag.getString("pages");
        }
        speed = tag.getInt("speed");
        platformName = tag.getString("platformName");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        pages = componentInput.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), "");
        int speedTier = componentInput.getOrDefault(Registration.RUNNING_TIER_DATA.get(), 0);
        // TODO: number design
        speed = (int) StrictMath.pow(10, Math.min(speedTier, 3)) * 8;
        platformName = componentInput.getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(platformName)).getString();
    }

    @Override
    public void setRemoved() {
        ifProgram().ifPresent(WenyanProgram::stop);
        super.setRemoved();
    }

    private Either<WenyanPackage, WenyanThread> getPackage(IHandleContext context, String packageName) throws WenyanException.WenyanThrowException {
        assert level != null;
        for (BlockPos b : BlockPos.betweenClosed(
                getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof IWenyanBlockDevice executor) {
                if (executor.getPackageName().equals(packageName)) {
                    if (getLevel() instanceof ServerLevel sl) {
                        PacketDistributor.sendToPlayersTrackingChunk(sl,
                                new ChunkPos(getBlockPos()),
                                new CommunicationLocationPacket(getBlockPos(), executor.blockPos().getCenter())
                        );
                    }
                    var rawPackage = executor.getExecPackage();
                    return Either.left(processPackage(rawPackage, executor));
                }
            } else if (blockEntity instanceof RunnerBlockEntity platform) {
                if (platform == this) continue;
                if (platform.getPlatformName().equals(packageName)) {
                    if (getLevel() instanceof ServerLevel sl) {
                        PacketDistributor.sendToPlayersTrackingChunk(sl,
                                new ChunkPos(getBlockPos()),
                                new CommunicationLocationPacket(getBlockPos(), platform.getBlockPos().getCenter())
                        );
                    }
                    // STUB
                    if (ifProgram().isPresent()) {
                        WenyanThread thread = platform.getProgram(program.holder).createThread(platform.pages);
                        return Either.right(thread);
                    } else {
                        throw new WenyanException.WenyanUnreachedException();
                    }
                }
            }
        }
        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
    }

    @Contract("_, _ -> new")
    private WenyanPackage processPackage(HandlerPackageBuilder.RawHandlerPackage rawPackage, IWenyanBlockDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, (RequestCallHandler) (thread, self, argsList) ->
                        new BlockRequest(this, device, thread, function.get(), self, argsList)));
        return new WenyanPackage(map);
    }

    private record BlockContext(Level level, BlockPos pos,
                                BlockState state) implements IHandleContext {
    }

    public record BlockRequest(
            IWenyanPlatform platform,
            IWenyanBlockDevice device,
            WenyanThread thread,

            IRawRequest request,
            IWenyanValue self,
            List<IWenyanValue> args
    ) implements IHandleableRequest {

        @Override
        public boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException {
            return request.handle(context, this);
        }
    }
}
