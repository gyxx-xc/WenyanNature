package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.interpreter.exec_interface.IWenyanBlockDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import indi.wenyan.interpreter.exec_interface.handler.IHandlerWarper;
import indi.wenyan.interpreter.exec_interface.handler.IImportHandler;
import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.utils.WenyanThreading;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.CommunicationLocationPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParametersAreNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform {
    public WenyanProgram program;

    public String pages;
    public int speed;

    @Deprecated
    public List<BlockPos> additionalPages = new ArrayList<>();

    @Getter
    public final ExecQueue execQueue = new ExecQueue();
    public static final int DEVICE_SEARCH_RANGE = 3;
    private final IImportHandler importFunction = (context, packageName) -> {
        IWenyanBlockDevice wenyanExecutor = null;
        assert level != null;
        for (BlockPos b : BlockPos.betweenClosed(
                getBlockPos().offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                getBlockPos().offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            if (level.getBlockEntity(b) instanceof IWenyanBlockDevice executor) {
                if (executor.getPackageName().equals(packageName)) {
                    wenyanExecutor = executor;
                    break;
                }
            }
        }
        if (wenyanExecutor == null) {
            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
        }
        if (getLevel() instanceof ServerLevel sl) {
            PacketDistributor.sendToPlayersTrackingChunk(sl,
                    new ChunkPos(getBlockPos()),
                    new CommunicationLocationPacket(getBlockPos(), wenyanExecutor.blockPos().getCenter())
            );
        }
        var rawPackage = wenyanExecutor.getExecPackage();
        return processPackage(rawPackage, wenyanExecutor);
    };

    @Override
    public void initEnvironment(WenyanRuntime baseEnvironment) {
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
        if (!level.isClientSide && program != null && program.isRunning()) {
            program.step(speed);
            handle(new BlockContext(level, pos, state));
        }
    }

    public void run(Player player) {
        if (program != null && program.isRunning()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        program = new WenyanProgram(pages, player, this);
        program.createMainThread();
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
    @WenyanThreading
    public void notice(JavacallRequest request) {
        if (level instanceof ServerLevel sl && request.handler() instanceof BlockHandler handler) {
            PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                    new CommunicationLocationPacket(getBlockPos(), handler.getBlockPos().getCenter()));
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (pages != null)
            tag.putString("pages", pages);
        if (additionalPages != null && !additionalPages.isEmpty()) {
            ListTag additionalPagesTag = new ListTag();
            for (BlockPos pos : additionalPages) {
                var posTag = new CompoundTag();
                posTag.putInt("x", pos.getX());
                posTag.putInt("y", pos.getY());
                posTag.putInt("z", pos.getZ());
                additionalPagesTag.add(posTag);
            }
            tag.put("additional_pages", additionalPagesTag);
        }
        tag.putInt("speed", speed);
    }

    @SuppressWarnings("unused")
    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.contains("pages")) {
            pages = tag.getString("pages");
        }

        if (tag.contains("additional_pages")) {
            additionalPages = new ArrayList<>();
            ListTag additionalPagesTag = tag.getList("additional_pages", Tag.TAG_COMPOUND);
            for (Tag posTag : additionalPagesTag) {
                if (posTag instanceof CompoundTag compoundTag) {
                    int x = compoundTag.getInt("x");
                    int y = compoundTag.getInt("y");
                    int z = compoundTag.getInt("z");
                    additionalPages.add(new BlockPos(x, y, z));
                }
            }
        }
        speed = tag.getInt("speed");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        pages = componentInput.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), "");
        int speedTier = componentInput.getOrDefault(Registration.RUNNING_TIER_DATA.get(), 0);
        speed = (int) StrictMath.pow(10, Math.min(speedTier, 3));
    }

    @Contract("_, _ -> new")
    private @NotNull WenyanPackage processPackage(HandlerPackageBuilder.RawHandlerPackage rawPackage, IWenyanBlockDevice device) {
        var map = new HashMap<>(rawPackage.variables());
        rawPackage.functions().forEach((name, function) ->
                map.put(name, new BlockHandler() {
                    @Override
                    public BlockState getBlockState() {
                        return device.blockState();
                    }

                    @Override
                    public BlockPos getBlockPos() {
                        return device.blockPos();
                    }

                    @Override
                    public boolean handle(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanThrowException {
                        return function.handle(context, request);
                    }
                }));
        return new WenyanPackage(map);
    }

    private interface BlockHandler extends IHandlerWarper{
        BlockState getBlockState();
        BlockPos getBlockPos();

        @Override
        default boolean check(IHandleContext context, JavacallRequest request) {
            if (!(context instanceof BlockContext blockContext)) return false;
            Level level = blockContext.level();
            if (level instanceof ServerLevel sl) {
                // TODO: better equal
                return sl.getBlockState(getBlockPos()).equals(getBlockState());
            } else {
                throw new WenyanException("unreached");
            }
        }
    }

    private record BlockContext(Level level, BlockPos pos, BlockState state) implements IHandleContext { }
}
