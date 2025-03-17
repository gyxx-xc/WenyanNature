package indi.wenyan.content.block;

import indi.wenyan.WenyanNature;
import indi.wenyan.content.checker.MiningChecker;
import indi.wenyan.content.item.WenyanHandRunner;
import indi.wenyan.interpreter.utils.CraftingAnswerChecker;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import indi.wenyan.setup.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Item -> recipe -> checker
@ParametersAreNonnullByDefault
public class CraftingBlockEntity extends BlockEntity {
    public static final String RUNNER_TAG = "runner";
    public static final String ITEM_TAG = "item";
    public static final int RUNNER_SLOT = 0;

    private final ItemStackHandler runner = createRunnerItemHandler();
    private final ItemStackHandler item = createItemHandler();
    private final Lazy<IItemHandler> runnerItemHandler = Lazy.of(() -> runner);
    private final Lazy<IItemHandler> itemHandler = Lazy.of(() -> item);

    private Thread program;
    private Semaphore programSemaphore;
    private Semaphore entitySemaphore;
    private Player holder;
    private CraftingAnswerChecker checker;

    public int runStep = 0;
    public boolean isRunning = false;
    public boolean isCrafting = false;

    public CraftingBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.CRAFTING_ENTITY.get(), pos, blockState);
    }

    // consider it as a State Machine
    // with isCrafting and isRunning as the state
    // 00. idle
    // 01. error
    // 10. crafting
    // 11. running
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, CraftingBlockEntity entity) {
        if (!entity.isCrafting) {
            if (entity.isRunning) { // 01. error -> idle
                if (entity.program != null) {
                    entity.program.interrupt();
                    entity.program = null;
                }
                entity.isRunning = false;
            }
            // else: 00. idle -> do nothing
        } else {
            if (entity.isRunning) { // 11. running
                assert entity.program != null;
                int size = 10;
                entity.programSemaphore.release(size);
                try {
                    entity.entitySemaphore.acquire(size);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // if isAlive, 11 -> 11
                // else 11 -> 10
            } else { // 10. crafting
                if (entity.program != null) {
                    entity.program.interrupt();
                    entity.program = null;
                }
                if (entity.runStep >= 8) { // is crafting end?
                    Block.popResource(level, pos.relative(Direction.UP), new ItemStack(Items.DIAMOND)); //TODO
                    entity.endCrafting(); // 10 -> 00
                } else {
                    entity.run(entity.holder); // 10 -> 11
                }
            }
        }
    }

    public void run(Player holder) {
        if (isRunning) {
            endCrafting();
            return;
        }
        ItemStack item = runner.getStackInSlot(RUNNER_SLOT);
        if (item.isEmpty()) {
            endCrafting();
            return;
        }
        WritableBookContent writableBookContent = item.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (writableBookContent == null) {
            endCrafting();
            return;
        }

        Stream<String> pages = writableBookContent.getPages(false);
        String code = pages.collect(Collectors.joining());
        Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
            if (e instanceof WenyanException) {
                holder.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), true);
            } else {
                holder.displayClientMessage(Component.literal("Unknown Error, Check server log to show more").withStyle(ChatFormatting.RED), true);
                WenyanNature.LOGGER.error("Error: {}", e.getMessage());
            }
            isRunning = false;
            endCrafting();
            entitySemaphore.release(100000);
        };

        // ready to visit
        programSemaphore = new Semaphore(0);
        entitySemaphore = new Semaphore(0);
        assert level != null;
        checker = new MiningChecker(level.random);
        program = new Thread(() -> {
            new WenyanMainVisitor(WenyanPackages.craftingEnvironment(checker),
                    new WenyanControl(entitySemaphore, programSemaphore))
                    .visit(WenyanVisitor.program(code));
            runStep ++;
            isRunning = false;
            if (!checker.check()) endCrafting();
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

    public void ejectItem() {
        BlockPos pos = worldPosition.relative(Direction.UP);
        assert level != null;
        Block.popResource(level, pos, runner.extractItem(RUNNER_SLOT, 1, false));
    }

    public ItemStack insertItem(ItemStack stack) {
        return runner.insertItem(RUNNER_SLOT, stack, false);
    }

    public void endCrafting() {
        runStep = 0;
        holder = null;
        isCrafting = false;
    }

    public void setHolder(Player holder) {
        this.holder = holder;
    }

    public IItemHandler getRunnerItemHandler() {
        return runnerItemHandler.get();
    }

    public IItemHandler getItemHandler() {
        return itemHandler.get();
    }

    @Nonnull
    private ItemStackHandler createRunnerItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof WenyanHandRunner;
            }
        };
    }

    @Nonnull
    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                assert level != null;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }

    private void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(RUNNER_TAG, runner.serializeNBT(registries));
        tag.put(ITEM_TAG, item.serializeNBT(registries));
    }

    private void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(RUNNER_TAG)) runner.deserializeNBT(registries, tag.getCompound(RUNNER_TAG));
        if (tag.contains(ITEM_TAG)) item.deserializeNBT(registries, tag.getCompound(ITEM_TAG));
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

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadData(tag, lookupProvider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        loadData(tag, lookupProvider);
    }
}
