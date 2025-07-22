package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.AdditionalPaperEntity;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.data.ProgramCodeData;
import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.handler.IJavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import indi.wenyan.interpreter.utils.IWenyanPlatform;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@ParametersAreNonnullByDefault
public class RunnerBlockEntity extends DataBlockEntity implements IWenyanPlatform {
    public WenyanProgram program;

    public String pages;
    public int speed;

    public List<BlockPos> additionalPages = new ArrayList<>();

    public Vec3 communicate;
    public boolean isCommunicating;
    @Getter
    private final List<String> output = new LinkedList<>();
    private final IWenyanExecutor.ExecQueue execQueue = new IWenyanExecutor.ExecQueue();

    @Getter
    IWenyanFunction importFunction = (IJavacallHandler) (self, thread, argsList) -> {
        JavacallContext context = new JavacallContext(
                new JavacallContext.BlockRunnerWarper(this), self, argsList, thread,
                (context1) -> {
                    String packageName = context1.args().getFirst().as(WenyanString.TYPE).value();
                    int RANGE = 3;
                    IWenyanExecutor wenyanExecutor = null;
                    assert level != null;
                    for (BlockPos b : BlockPos.betweenClosed(getBlockPos().offset(RANGE, -RANGE, RANGE),
                            getBlockPos().offset(-RANGE, RANGE, -RANGE))) {
                        if (level.getBlockEntity(b) instanceof IWenyanExecutor executor) {
                            if (executor.getPackageName().equals(packageName)) {
                                wenyanExecutor = executor;
                                break;
                            }
                        }
                    }

                    if (wenyanExecutor == null) {
                        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.import_package_not_found", packageName).getString());
                    }

                    var execPackage = wenyanExecutor.getExecPackage();
                    if (context1.args().size() == 1) {
                        context1.thread().currentRuntime().importEnvironment(execPackage);
                    } else {
                        for (IWenyanValue arg : context1.args().subList(1, context1.args().size())) {
                            String id = arg.as(WenyanString.TYPE).value();
                            if (execPackage.variables.containsKey(id)) {
                                context1.thread().currentRuntime().setVariable(id,
                                        execPackage.variables.get(id));
                            } else {
                                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_programming.variable_not_found", id).getString());
                            }
                        }
                    }

                    if (getLevel() instanceof ServerLevel sl) {
                        PacketDistributor.sendToPlayersTrackingChunk(sl,
                                new ChunkPos(getBlockPos()),
                                new CommunicationLocationPacket(getBlockPos(), wenyanExecutor.getPosition())
                        );
                    }
                    return WenyanNull.NULL;
                }
                , thread.program.holder);
        execQueue.receive(context);
        thread.block();
    };

    public RunnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.RUNNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide && program != null && program.isRunning()) {
            program.step(speed);
            execQueue.handle();
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

    public void setCommunicate(Vec3 to) {
        if (level == null || !level.isClientSide()) {
            return;
        }
        communicate = new Vec3(to.x() - getBlockPos().getX(),
                to.y() - getBlockPos().getY(), to.z() - getBlockPos().getZ());
        isCommunicating = true;
    }

    @Override
    public void accept(JavacallContext context) {
        context.handler().getExecutor().ifPresent((executor) -> {
            if (level instanceof ServerLevel sl)
                PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(getBlockPos()),
                        new CommunicationLocationPacket(getBlockPos(), executor.getPosition()));
            executor.getExecQueue().receive(context);
        });
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
}
