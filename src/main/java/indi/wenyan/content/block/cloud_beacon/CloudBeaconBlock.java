package indi.wenyan.content.block.cloud_beacon;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.setup.definitions.WenyanBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CloudBeaconBlock extends Block implements EntityBlock {
    public static final String ID = "cloud_beacon";

    public CloudBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    public CloudBeaconBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CloudBeaconBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return (level1, blockPos, blockState1, t) -> {
            if (type == WenyanBlocks.CLOUD_BEACON_ENTITY.get() && t instanceof CloudBeaconBlockEntity entity) {
                if (level1 instanceof ServerLevel sl)
                    entity.tick(sl, blockPos, blockState1, level.getRandom());
                else
                    entity.tickClient(blockPos, blockState1, level.getRandom());
            }
        };
    }
}
