package indi.wenyan.block;

import com.sun.jna.platform.win32.OaIdl;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.Semaphore;

@ParametersAreNonnullByDefault
public class BlockRunner extends BlockEntity {
    public Boolean isRunning;
    public Semaphore programSemaphore;
    public Semaphore entitySemaphore;
    public Thread program;
    public List<String> pages;

    public BlockRunner(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
        isRunning = false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockRunner entity) {
        if (entity.isRunning) {
            assert entity.program != null;
            int size = 100;
            entity.programSemaphore.release(size);
            try {
                entity.entitySemaphore.acquire(size);
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
        };

        // ready to visit
        programSemaphore = new Semaphore(0);
        entitySemaphore = new Semaphore(0);
        program = new Thread(() -> {
            new WenyanMainVisitor(WenyanPackages.blockEnvironment(holder), programSemaphore, entitySemaphore)
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

    @Override
    public void setChanged() {
        WritableBookContent content = components().get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (content != null)
            pages = content.getPages(Minecraft.getInstance().isTextFilteringEnabled()).toList();
        super.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (pages != null) {
            ListTag pagesTag = new ListTag();
            pagesTag.addAll(pages.stream().map(StringTag::valueOf).toList());
            tag.put("pages", pagesTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        pages = tag.getList("pages", Tag.TAG_STRING).stream().map(Tag::getAsString).toList();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (pages != null) {
            ListTag pagesTag = new ListTag();
            pagesTag.addAll(pages.stream().map(StringTag::valueOf).toList());
            tag.put("pages", pagesTag);
        }
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
