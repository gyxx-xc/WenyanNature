package indi.wenyan.content.block.additional_module.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.IModuleBlock;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LockModuleBlock extends Block implements IModuleBlock {
    public static final String ID = "lock_module_block";
    public static final BooleanProperty LOCK_STATE = BooleanProperty.create("locked");

    public static final VoxelShape LOCKED_AABB;
    public static final VoxelShape UNLOCKED_AABB;

    public LockModuleBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(LOCK_STATE, false));
    }

    @Override
    public BlockEntityType<?> getType() {
        return Registration.LOCK_MODULE_ENTITY.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LOCK_STATE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        if (state == null)
            return null;
        else
            return state.setValue(LOCK_STATE, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(LOCK_STATE) ? LOCKED_AABB : UNLOCKED_AABB;
    }

    static {
        LOCKED_AABB = Shapes.or(
                box(6, 0, 4, 10, 16, 12),
                box(0, 4, 6, 16, 12, 10),
                box(4, 6, 0, 12, 10, 16)
        );
        UNLOCKED_AABB = Shapes.or(
                box(0, 1, 0, 16, 7, 16), // flat
                box(10, 0, 10, 14.25, 16, 14.25) // upwards
        );
    }
}
