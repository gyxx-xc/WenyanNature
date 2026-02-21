package indi.wenyan.content.block.crafting_block;

import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
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
public class CraftingBlock extends Block implements IModuleBlock {
    public static final String ID = "crafting_block";
    public static final VoxelShape SHAPE = Block.box(1, 0, 3, 15, 13.5, 15);

    public CraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return Registration.CRAFTING_BLOCK_ENTITY.get();
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
