package indi.wenyan.content.block.writing_block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WritingBlockEntity extends DataBlockEntity {
    @Getter
    private final ResourceHandler<ItemResource> itemHandler = createItemHandler();
    @Getter
    private ItemStack itemStack = ItemStack.EMPTY;

    public WritingBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.WRITING_BLOCK_ENTITY.get(), pos, blockState);
    }

    private ResourceHandler<ItemResource> createItemHandler() {
        return new ItemStackResourceHandler() {
            @Override
            protected ItemStack getStack() {
                return itemStack;
            }

            @Override
            protected void setStack(ItemStack stack) {
                itemStack = stack;
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
                return false;
            }
        };
    }

    @Override
    protected void saveData(ValueOutput output) {
        output.store("item", ItemStack.OPTIONAL_CODEC, itemStack);
    }

    @Override
    protected void loadData(ValueInput input) {
        input.read("item", ItemStack.OPTIONAL_CODEC)
                .ifPresent(itemStack -> this.itemStack = itemStack);
    }

    public void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }
}
