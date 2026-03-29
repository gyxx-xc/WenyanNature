package indi.wenyan.content.block.additional_module.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FormationCoreModuleBlock extends Block implements IModuleBlock {
    public static final String ID = "formation_core_module_block";

    public static final VoxelShape SHAPE = Shapes.or(
            box(3,  0,  3,  13, 16,  13),
            box(0,  3,  0,  16, 13,  16)
    );

    public FormationCoreModuleBlock(Properties properties) {
        super(properties.destroyTime(0.3F));
    }

    @Override
    public BlockEntityType<?> getType() {
        return WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
