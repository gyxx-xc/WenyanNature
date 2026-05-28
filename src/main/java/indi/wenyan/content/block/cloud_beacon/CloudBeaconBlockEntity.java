package indi.wenyan.content.block.cloud_beacon;

import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.NonNull;

public class CloudBeaconBlockEntity extends BlockEntity implements ICloudBeaconRenderable {
    @Getter
    private int transmitAnimationTime = 0;
    private int nextTransmitTime;

    @Getter
    private int litUpAnimationTime;

    private int checkingY;
    private int levels = 0;

    public CloudBeaconBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WenyanBlocks.CLOUD_BEACON_ENTITY.get(), worldPosition, blockState);
    }

    public void tick(ServerLevel sl, BlockPos blockPos, BlockState blockState1, RandomSource random) {
    }

    public void tickClient(BlockPos blockPos, BlockState blockState1, RandomSource random) {
        // copy from BeaconBlockEntity, but check whether it has block in the way only
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        if (checkingY <= y) checkingY = y + 1;

        assert level != null;
        int maxHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        for (int i = 0; i < 10 && checkingY < maxHeight; i++) {
            BlockState state = level.getBlockState(new BlockPos(x, checkingY, z));
            if (state.getLightDampening() >= 15 && !state.is(Blocks.BEDROCK)) {
                checkingY = y;
                litUpAnimationTime = -1;
                break;
            }
            checkingY++;
        }

        if (checkingY >= maxHeight) {
            checkingY = y + 1;
            if (litUpAnimationTime < 0) {
                litUpAnimationTime = 0;
                transmitAnimationTime = 0;
            }
        }

        if (litUpAnimationTime >= 0) {
            if (litUpAnimationTime < 20) {
                litUpAnimationTime++;
            } else {
                transmitAnimationTime++;
                if (transmitAnimationTime > nextTransmitTime) {
                    transmitAnimationTime = random.nextInt(2) != 0 ? 0 : -40;
                    nextTransmitTime = random.nextInt(30) + 30;
                }
            }
        }
    }

    // copy from BeaconBlockEntity, if bug, ask mojang
    @Override
    public void setLevel(@NonNull Level level) {
        super.setLevel(level);
        this.checkingY = level.getMinY() - 1;
    }

    public static void playSound(Level level, BlockPos worldPosition, SoundEvent event) {
        level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
