package indi.wenyan.content.block.furnace;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.DataBlockEntity;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LogicFurnaceBlockEntity extends DataBlockEntity {
    @Getter
    private final ItemStacksResourceHandler input = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
        }
    };

    @Getter
    private final ItemStacksResourceHandler output = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            updateBlock();
        }
    };

    @Getter
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int i) {
            return i == 0 ? progress : maxProgress;
        }

        @Override
        public void set(int i, int i1) {
            if (i == 0) {
                progress = i1;
            } else {
                maxProgress = i1;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    private int progress;
    private int maxProgress;

    public LogicFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOGIC_FURNACE_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void saveData(ValueOutput output) {
        input.serialize(output);
        this.output.serialize(output);
    }

    @Override
    protected void loadData(ValueInput input) {
        this.input.deserialize(input);
        this.output.deserialize(input);
    }

    public void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }
}
