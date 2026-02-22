package indi.wenyan.content.block.pedestal;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PedestalBlockEntity extends BlockEntity {
    @Getter
    private final ResourceHandler<ItemResource> itemHandler = createItemHandler();
    private ItemStack itemStack = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.PEDESTAL_ENTITY.get(), pos, blockState);
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
            }

            @Override
            protected int getCapacity(ItemResource resource) {
                return 1;
            }
        };
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("item", ItemStack.CODEC, itemStack);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("item", ItemStack.CODEC)
                .ifPresent(itemStack -> this.itemStack = itemStack);
    }

    public ItemStack getStack() {
        return itemStack;
    }

    public void setStack(ItemStack stack) {
        itemStack = stack;
    }
}
