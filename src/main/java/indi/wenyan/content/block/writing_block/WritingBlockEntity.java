package indi.wenyan.content.block.writing_block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.content.block.InplaceItemstackResource;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WritingBlockEntity extends DataBlockEntity {
    private final InplaceItemstackResource item = new InplaceItemstackResource(this::updateBlock);

    public WritingBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.WRITING_BLOCK_ENTITY.get(), pos, blockState);
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return item.handler();
    }

    @Override
    protected void saveData(ValueOutput output) {
        output.store("item", ItemStack.OPTIONAL_CODEC, item.item());
    }

    @Override
    protected void loadData(ValueInput input) {
        input.read("item", ItemStack.OPTIONAL_CODEC).ifPresent(item::replaceItem);
    }

    private void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }
}
