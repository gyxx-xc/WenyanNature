package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InformationModuleBlock extends AbstractModuleBlock {
    public static final String ID = "information_module_block";

    @Override
    protected BlockEntityType<?> getType() {
        return Registration.INFORMATION_MODULE_ENTITY.get();
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        var entity = level.getBlockEntity(pos);
        if (entity instanceof InformationModuleEntity module)
            return module.getSignal();
        return 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getConnectedDirection(state) == direction ?
                getSignal(state, level, pos, direction) : 0;
    }

    static void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, state.getBlock());
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), state.getBlock());
    }
}
