package indi.wenyan.content.block;

import indi.wenyan.content.data.ProgramCodeData;
import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.content.handler.*;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IWenyanExecutor;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockOutputPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static indi.wenyan.interpreter.utils.WenyanPackages.WENYAN_BASIC_PACKAGES;

@ParametersAreNonnullByDefault
public class BlockRunner extends BlockEntity implements IWenyanExecutor {
    public WenyanProgram program;

    public String pages;
    public int speed;

    public List<BlockPos> additionalPages = new ArrayList<>();

    public Vec3 communicate;
    public boolean isCommunicating;
    @Getter
    private final List<String> output = new LinkedList<>();

    public BlockRunner(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, BlockRunner entity) {
        if (!level.isClientSide && entity.program != null && entity.program.isRunning()) {
            entity.program.step(entity.speed);
            entity.program.handle();
        }

        if (entity.isCommunicating) {
            entity.isCommunicating = false;
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
        program.run();
    }

    public void addOutput(String text) {
        output.addLast(text);
        if (output.size() > 10) {
            output.removeFirst();
        }
    }

    @Override
    public WenyanRuntime getBaseEnvironment() {
        return WenyanPackageBuilder.create()
                .environment(WENYAN_BASIC_PACKAGES)
                .function("「觸」", new TouchHandler(), TouchHandler.ARGS_TYPE)
//                .function("「放置」", new BlockPlaceHandler(holder,
//                        (BlockItem) Items.ACACIA_LOG.asItem()
//                        ,pos, block))
                .function("「移」", new BlockMoveHandler(), BlockMoveHandler.ARGS_TYPE)
                .function("「放」", new CommunicateHandler(), CommunicateHandler.ARG_TYPES)
                .function("「紅石量」", new RedstoneSignalHandler())
                .function("「己於上」", new SelfPositionBlockHandler(Direction.UP))
                .function("「己於下」", new SelfPositionBlockHandler(Direction.DOWN))
                .function("「己於東」", new SelfPositionBlockHandler(Direction.EAST))
                .function("「己於南」", new SelfPositionBlockHandler(Direction.SOUTH))
                .function("「己於西」", new SelfPositionBlockHandler(Direction.WEST))
                .function("「己於北」", new SelfPositionBlockHandler(Direction.NORTH))
                .function(new String[] {"書","书"}, (context)->{
                    StringBuilder result = new StringBuilder();
                    for (IWenyanValue arg : context.args()) {
                        result.append(result.isEmpty() ? "" : " ").append(arg.as(WenyanString.TYPE));
                    }

                    if (getLevel() instanceof ServerLevel sl)
                        PacketDistributor.sendToPlayersTrackingChunk(sl, new ChunkPos(this.getBlockPos()),
                                new BlockOutputPacket(this.getBlockPos(), result.toString()));
                    return WenyanNull.NULL;
                })
                .build();
    }

    @SuppressWarnings("unused")
    private void saveData(CompoundTag tag, HolderLookup.Provider registries) {
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
    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {

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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveData(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadData(tag, registries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveData(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        loadData(pkt.getTag(), lookupProvider);
    }
}
