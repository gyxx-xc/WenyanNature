package indi.wenyan.content.block.additional_module;

import indi.wenyan.setup.Registration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InformativeAdditionalModuleBlock extends AbstractAdditionalModuleBlock {
    public static final String ID = "informative_module_block";

    @Override
    BlockEntityType<?> getType() {
        return Registration.INFORMATIVE_MODULE_ENTITY.get();
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        var entity = level.getBlockEntity(pos);
        if (entity instanceof InformativeAdditionalModuleEntity module) {
            return module.getSignal();
        }
        return 0;
    }
}
