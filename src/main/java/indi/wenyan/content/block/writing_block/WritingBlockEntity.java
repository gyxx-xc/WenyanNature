package indi.wenyan.content.block.writing_block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.block.ICodeHolder;
import indi.wenyan.content.block.ICodeOutputHolder;
import indi.wenyan.content.block.IOutputAcceptor;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WritingBlockEntity extends DataBlockEntity implements ICodeOutputHolder {
    public static final int MAX_OUTPUT_SHOWING_SIZE = 32;
    @Getter
    private final ResourceHandler<ItemResource> itemHandler = createItemHandler();
    @Getter
    private ItemStack itemStack = ItemStack.EMPTY;
    @Getter
    private final Deque<Component> outputQueue = new ArrayDeque<>();
    private boolean outputChanged = false;

    private ResourceHandler<ItemResource> createItemHandler() {
        return new ItemStackResourceHandler() {
            @Override
            protected ItemStack getStack() {
                return itemStack;
            }

            @Override
            protected void setStack(ItemStack stack) {
                itemStack = stack;
                var cap = itemStack.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
                codeHolder = Objects.requireNonNullElse(cap, DummyCodeHolder.INSTANCE);
                updateBlock();
            }

            @Override
            protected int getCapacity(ItemResource resource) {
                return 64; // Stack of fu items
            }

            @Override
            protected boolean isValid(ItemResource resource) {
                for (var i : WenyanItems.HAND_RUNNER.getItems()) {
                    if (resource.is(i))
                        return true;
                }
                for (var i : WenyanItems.THROW_RUNNER.getItems()) {
                    if (resource.is(i))
                        return true;
                }
                return false;
            }
        };
    }

    @Delegate(types = ICodeHolder.class)
    private ICodeHolder codeHolder = DummyCodeHolder.INSTANCE;

    public WritingBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.WRITING_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void saveData(ValueOutput output) {
        output.store("item", ItemStack.OPTIONAL_CODEC, itemStack);
    }

    @Override
    protected void loadData(ValueInput input) {
        input.read("item", ItemStack.OPTIONAL_CODEC)
                .ifPresent(itemStack -> {
                    var cap = itemStack.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
                    codeHolder = Objects.requireNonNullElse(cap, DummyCodeHolder.INSTANCE);
                    this.itemStack = itemStack;
                });
    }

    private void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }

    @Override
    public boolean isOutputChanged() {
        var temp = outputChanged;
        outputChanged = false;
        return temp;
    }

    @Override
    public void setViewCode(String viewCode) {
    }

    @Override
    public String getViewCode() {
        return "";
    }

    @Override
    public void addOutput(String output, IOutputAcceptor.OutputStyle status) {
        if (status == IOutputAcceptor.OutputStyle.ERROR)
            outputQueue.addLast(Component.literal(output).withStyle(ChatFormatting.RED));
        else if (status == IOutputAcceptor.OutputStyle.NORMAL)
            outputQueue.addLast(Component.literal(output));
        while (outputQueue.size() > MAX_OUTPUT_SHOWING_SIZE) {
            outputQueue.removeFirst();
        }
        outputChanged = true;
        updateBlock();
    }
}
