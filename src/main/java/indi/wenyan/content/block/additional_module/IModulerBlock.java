package indi.wenyan.content.block.additional_module;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IModulerBlock extends EntityBlock {
    /**
     * Gets the block entity type for this module block.
     *
     * @return the block entity type
     */
    BlockEntityType<?> getType();

    @Override
    @Nullable
    default BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getType().create(blockPos, blockState);
    }

    @Override
    @Nullable
    default <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == getType())
                ((AbstractModuleEntity) entity).tick();
        };
    }
}
