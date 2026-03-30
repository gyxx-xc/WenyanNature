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
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WritingBlockEntity extends DataBlockEntity {
    @Getter
    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (!super.isValid(index, resource)) return false;
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


    public WritingBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.WRITING_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void saveData(ValueOutput output) {
        itemHandler.serialize(output);
    }

    @Override
    protected void loadData(ValueInput input) {
        itemHandler.deserialize(input);
    }

    private void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }
}
