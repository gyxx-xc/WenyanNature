package indi.wenyan.content.block;

import indi.wenyan.WenyanNature;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import indi.wenyan.setup.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
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
import javax.xml.crypto.Data;
import java.util.List;
import java.util.concurrent.Semaphore;

@ParametersAreNonnullByDefault
public class BlockRunner extends BlockEntity {
    public Boolean isRunning;
    public Semaphore programSemaphore;
    public Semaphore entitySemaphore;
    public Thread program;
    public List<String> pages;
    public int speed;
    public Vec3 communicate;
    public boolean isCommunicating;

    public BlockRunner(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
        isRunning = false;
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, BlockRunner entity) {
        if (!level.isClientSide){
            if (entity.isRunning) {
                assert entity.program != null;
                entity.programSemaphore.release(entity.speed);
                try {
                    entity.entitySemaphore.acquire(entity.speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                entity.isRunning = entity.program.isAlive();
            } else {
                if (entity.program != null) {
                    entity.program.interrupt();
                    entity.program = null;
                }
            }
        }        if (!entity.isCommunicating) {
            entity.communicate = null;
        } else {
            entity.isCommunicating = false;
        }
    }

    public void run(Player holder) {
        if (isRunning)
            return;
        String code = String.join("\n", pages);
        Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
            if (e instanceof WenyanException) {
                holder.sendSystemMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
            } else {
                holder.sendSystemMessage(Component.literal("Error").withStyle(ChatFormatting.RED));
                WenyanNature.LOGGER.info("Error: {}", e.getMessage());
            }
            entitySemaphore.release(100000);
        };

        // ready to visit
        programSemaphore = new Semaphore(0);
        entitySemaphore = new Semaphore(0);
        program = new Thread(() -> {
            new WenyanMainVisitor(WenyanPackages.blockEnvironment(getBlockPos(), getBlockState(), holder), programSemaphore, entitySemaphore)
                    .visit(WenyanVisitor.program(code));
            entitySemaphore.release(100000);
        });
        program.setUncaughtExceptionHandler(exceptionHandler);
        isRunning = true;
        program.start();
        try {
            entitySemaphore.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void copy(BlockRunner other) {
        this.pages = other.pages;
        this.program = other.program;
        this.programSemaphore = other.programSemaphore;
        this.entitySemaphore = other.entitySemaphore;
        this.isRunning = other.isRunning;
    }

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

    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        pages = tag.getList("pages", Tag.TAG_STRING).stream().map(Tag::getAsString).toList();
        isRunning = false;
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
        Object o = components().get(DataComponents.DAMAGE);
        if (o != null) speed = (int)Math.pow(10, Math.min((int) o, 3));
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
