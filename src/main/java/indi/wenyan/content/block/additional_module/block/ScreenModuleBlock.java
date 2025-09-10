package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ScreenModuleBlock extends Block implements EntityBlock {
    public static final String ID = "screen_block_module_block";

    public ScreenModuleBlock() {
        super(Properties.of());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return Registration.SCREEN_MODULE_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == Registration.SCREEN_MODULE_BLOCK_ENTITY.get())
                ((ScreenModuleBlockEntity) entity).tick();
        };
    }
}
