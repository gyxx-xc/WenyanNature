package indi.wenyan.content.block.power;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CreativePowerBlock extends Block implements IModuleBlock {
    public static final String ID = "creative_power_block";
    public static final VoxelShape SHAPE = box(1, 1, 1, 15, 15, 15);

    public CreativePowerBlock(Properties properties) {
        super(properties.destroyTime(0.2F));
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return WenyanBlocks.CREATIVE_POWER_BLOCK_ENTITY.get();
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof CreativePowerBlockEntity entity) {
                entity.tick(level1, blockPos, blockState, level1.getRandom());
            }
        };
    }
}
