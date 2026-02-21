package indi.wenyan.content.block.power;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PowerBlock extends Block implements IModuleBlock {
    public static final String ID = "power_block";
    public static final VoxelShape SHAPE = box(1, 1, 1, 15, 15, 15);

    public PowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return WenyanBlocks.POWER_BLOCK_ENTITY.get();
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
