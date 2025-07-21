package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.AdditionalPaperEntity;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.block.runner.handler.CommunicateHandler;
import indi.wenyan.content.data.ProgramCodeData;
import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.handler.*;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockOutputPacket;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static indi.wenyan.interpreter.utils.WenyanPackages.WENYAN_BASIC_PACKAGES;

@ParametersAreNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanExecutor {
    public WenyanProgram program;

    public String pages;
    public int speed;

    public List<BlockPos> additionalPages = new ArrayList<>();

    public Vec3 communicate;
    public boolean isCommunicating;
    @Getter
    private final List<String> output = new LinkedList<>();

    public final ExecQueue requests = new ExecQueue();

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && program != null && program.isRunning()) {
            program.step(speed);
            requests.handle();
        }

        if (isCommunicating) {
            isCommunicating = false;
        }
    }

    public void run(Player player) {
        if (program != null && program.isRunning()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_programming.already_run").getString());
            return;
        }
        StringBuilder programBuilder = new StringBuilder().append(pages);
        for (var additionalPage : additionalPages) {
            assert level != null;
            var e = level.getBlockEntity(additionalPage);
            if (e instanceof AdditionalPaperEntity additionalPaperEntity) {
                programBuilder.append("\n").append(String.join("\n", additionalPaperEntity.pages));
            } else {
                WenyanException.handleException(player, Component.translatable("error.wenyan_programming.additional_page_not_found", additionalPage).getString());
            }
        }
        program = new WenyanProgram(programBuilder.toString(),
                player, this);
        program.createThread();
    }

    public void addOutput(String text) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        output.addLast(text);
        if (output.size() > 10) {
            output.removeFirst();
        }
    }

    public void setCommunicate(BlockPos to) {
        if (level == null || level.isClientSide()) {
            return;
        }
        this.communicate = new Vec3(to.getX() - getBlockPos().getX(),
                to.getY() - getBlockPos().getY(), to.getZ() - getBlockPos().getZ());
    }


    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public WenyanRuntime getExecPackage() {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("「放」", new CommunicateHandler(), CommunicateHandler.ARG_TYPES)
                .function(WenyanPackages.IMPORT_ID, new ImportCallHandler())
                .function(new String[]{"書", "书"}, new MyIOutputHandlerHelper())
                .build();
    }

    @Override
    public ExecQueue getExecQueue() {
        return requests;
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
        tag.putDouble("communicate_x", communicate != null ? communicate.x : 0.0);
        tag.putDouble("communicate_y", communicate != null ? communicate.y : 0.0);
        tag.putDouble("communicate_z", communicate != null ? communicate.z : 0.0);
        tag.putInt("speed", speed);
    }

    @SuppressWarnings("unused")
    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.contains("pages")) {
            pages = tag.getString("pages");
        }

        if (tag.contains("communicate_x")) {
            communicate = new Vec3(tag.getDouble("communicate_x"), tag.getDouble("communicate_y"), tag.getDouble("communicate_z"));
            isCommunicating = true;
        } else {
            isCommunicating = false;
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
        pages = componentInput.getOrDefault(Registration.PROGRAM_CODE_DATA.get(), new ProgramCodeData("")).code();
        int speedTier = componentInput.getOrDefault(Registration.TIER_DATA.get(), new RunnerTierData(0)).tier();
        speed = (int) StrictMath.pow(10, Math.min(speedTier, 3));
    }

    private class ImportCallHandler extends ThisCallHandler implements IExecCallHandler {
        @Override
        public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
            String packageName = context.args().getFirst().as(WenyanString.TYPE).value();
            int RANGE = 3;
            BlockPos pos = null;
            for (BlockPos b : BlockPos.betweenClosed(getBlockPos().offset(RANGE, -RANGE, RANGE),
                    getBlockPos().offset(-RANGE, RANGE, -RANGE))) {
                assert level != null;
                if (level.getBlockEntity(b) instanceof IWenyanExecutor executor) {
                    if (executor.getPackageName().equals(packageName)) {
                        var execPackage = executor.getExecPackage();
                        if (context.args().size() == 1) {
                            context.thread().currentRuntime().importEnvironment(execPackage);
                        } else {
                            for (IWenyanValue arg : context.args().subList(1, context.args().size())) {
                                String id = arg.as(WenyanString.TYPE).value();
                                if (execPackage.variables.containsKey(id)) {
                                    context.thread().currentRuntime().setVariable(id, execPackage.variables.get(id));
                                } else {
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.variable_not_found", id).getString());
                                }
                            }
                        }
                        pos = b;
                        break;
                    }
                }
            }

            if (pos == null) {
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
            }
            if (getLevel() instanceof ServerLevel sl) {
                PacketDistributor.sendToPlayersTrackingChunk(sl,
                        new ChunkPos(getBlockPos()),
                        new CommunicationLocationPacket(getBlockPos(), pos)
                );
            }
            return WenyanNull.NULL;
        }
    }

    private class MyIOutputHandlerHelper extends ThisCallHandler implements IOutputHandlerHelper {
        @Override
        public void output(String message) {
            if (getLevel() instanceof ServerLevel sl)
                PacketDistributor.sendToPlayersTrackingChunk(sl,
                        new ChunkPos(getBlockPos()),
                        new BlockOutputPacket(getBlockPos(), message));
        }
    }

    private abstract class ThisCallHandler implements IExecCallHandler {
        @Override
        public Optional<IWenyanExecutor> getExecutor() {
            return Optional.of(RunnerBlockEntity.this);
        }
    }
}
