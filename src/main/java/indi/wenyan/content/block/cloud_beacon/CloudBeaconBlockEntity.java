package indi.wenyan.content.block.cloud_beacon;

import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CloudBeaconBlockEntity extends BlockEntity implements ICloudBeaconRenderable {
    @Getter
    private int transmitAnimationTime;
    private int nextTransmitTime;

    public CloudBeaconBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WenyanBlocks.CLOUD_BEACON_ENTITY.get(), worldPosition, blockState);
    }

    public void tick(ServerLevel sl, BlockPos blockPos, BlockState blockState1, RandomSource random) {
    }

    public void tickClient(BlockPos blockPos, BlockState blockState1, RandomSource random) {
        transmitAnimationTime ++;
        System.out.println(transmitAnimationTime);
        if (transmitAnimationTime > nextTransmitTime) {
            transmitAnimationTime = random.nextInt(2) != 0 ? 0 : -40;
            nextTransmitTime = random.nextInt(30) + 30;
        }
    }
}
