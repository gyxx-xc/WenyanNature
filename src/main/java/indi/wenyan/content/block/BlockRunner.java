package indi.wenyan.content.block;

import indi.wenyan.content.data.RunnerTierData;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

@ParametersAreNonnullByDefault
public class BlockRunner extends BlockEntity {
    public WenyanProgram program;

    public Vec3 communicate;
    public boolean isCommunicating;

    public List<String> pages;
    public int speed;
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

        if (!entity.isCommunicating) {
            entity.communicate = null;
        } else {
            entity.isCommunicating = false;
        }
    }

    public void run(Player player) {
        if (program != null && program.isRunning()) {
            WenyanException.handleException(player, Component.translatable("error.wenyan_nature.already_run").getString());
            return;
        }
        program = new WenyanProgram(String.join("\n", pages),
                WenyanPackages.blockEnvironment(getBlockPos(), getBlockState(), player, this), player);
        program.run();
    }

    public void copy(BlockRunner other) {
        this.pages = other.pages;
        this.program = other.program;
    }

    public void addOutput(String text) {
        output.addLast(text);
        if (output.size() > 10) {
            output.removeFirst();
        }
    }

    public List<String> getOutput() {
        return output;
    }

    @SuppressWarnings("unused")
    private void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (pages != null) {
            ListTag pagesTag = new ListTag();
            pagesTag.addAll(pages.stream().map(StringTag::valueOf).toList());
            tag.put("pages", pagesTag);
        }
        tag.putDouble("communicate_x", communicate != null ? communicate.x : 0.0);
        tag.putDouble("communicate_y", communicate != null ? communicate.y : 0.0);
        tag.putDouble("communicate_z", communicate != null ? communicate.z : 0.0);
        tag.putInt("speed", speed);
    }

    @SuppressWarnings("unused")
    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        pages = tag.getList("pages", Tag.TAG_STRING).stream().map(Tag::getAsString).toList();
        if (tag.contains("communicate_x")) {
            communicate = new Vec3(tag.getDouble("communicate_x"), tag.getDouble("communicate_y"), tag.getDouble("communicate_z"));
            isCommunicating = true;
        } else {
            isCommunicating = false;
        }
        speed = tag.getInt("speed");
    }

    @Override
    public void setChanged() {
        WritableBookContent content = components().get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (content != null)
            pages = content.getPages(false).toList();
        Object o = components().get(Registration.TIER_DATA.get());
        int speedTier;
        if (o instanceof RunnerTierData(int tier)) {
            speedTier = tier;
        } else {
//            WenyanException.handleException(null, "BlockRunner does not have a valid tier data component.");
            return;
        }

        speed = (int)Math.pow(10, Math.min(speedTier, 3));
        super.setChanged();
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
