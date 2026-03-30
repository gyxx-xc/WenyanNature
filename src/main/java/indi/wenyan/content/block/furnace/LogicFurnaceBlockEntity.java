package indi.wenyan.content.block.furnace;

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
public class LogicFurnaceBlockEntity extends DataBlockEntity {
    private final InplaceItemstackResource input = new InplaceItemstackResource(this::updateBlock);
    private final InplaceItemstackResource output = new InplaceItemstackResource(this::updateBlock);

    public LogicFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.LOGIC_FURNACE_ENTITY.get(), pos, blockState);
    }

    public ResourceHandler<ItemResource> getInputHandler() {
        return input.handler();
    }

    public ResourceHandler<ItemResource> getOutputHandler() {
        return output.handler();
    }

    @Override
    protected void saveData(ValueOutput output) {
        output.store("input", ItemStack.OPTIONAL_CODEC, input.item());
        output.store("output", ItemStack.OPTIONAL_CODEC, this.output.item());
    }

    @Override
    protected void loadData(ValueInput input) {
        input.read("input", ItemStack.OPTIONAL_CODEC)
                .ifPresent(this.input::replaceItem);
        input.read("output", ItemStack.OPTIONAL_CODEC)
                .ifPresent(output::replaceItem);
    }

    public void updateBlock() {
        assert getLevel() != null;
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        setChanged();
    }
}
