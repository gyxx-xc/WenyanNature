package indi.wenyan.content.block.cloud_beacon;

import com.ibm.icu.impl.Pair;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WyRegistration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CloudBeaconBlockEntity extends BlockEntity implements ICloudBeaconRenderable {
    @Getter
    private int transmitAnimationTime = 0;
    private int nextTransmitTime;

    @Getter
    private int litUpAnimationTime;

    private int checkingY;
    private final List<Pair<BlockPos, String>> cloudedModule = new ArrayList<>();
    private final List<Pair<BlockPos, String>> checkingCloudedModule = new ArrayList<>();

    public CloudBeaconBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WenyanBlocks.CLOUD_BEACON_ENTITY.get(), worldPosition, blockState);
    }

    @SuppressWarnings("unused")
    public void tick(ServerLevel sl, BlockPos blockPos, BlockState blockState1, RandomSource random) {
        checkBlocked(blockPos, new BeaconListHelper() {
            @Override
            public void init() {
                checkingCloudedModule.clear();
            }

            @Override
            public boolean add(BlockPos pos, BlockState state) {
                if (litUpAnimationTime >= 0) {
                    if (state.is(WyRegistration.RUNNABLE_BLOCK)) {
                        BlockEntity blockEntity = sl.getBlockEntity(pos);
                        if (blockEntity instanceof RunnerBlockEntity platform)
                            checkingCloudedModule.add(Pair.of(pos, platform.getPlatformName()));
                    }
                }
                return true;
            }

            @Override
            public void finish() {
                if (litUpAnimationTime >= 0) {
                    // hashcode of list(pair(pos, string)) all permits, can check
                    if (checkingCloudedModule.hashCode() != cloudedModule.hashCode()) {
                        cloudedModule.clear();
                        cloudedModule.addAll(checkingCloudedModule);

                        var manager = GlobalPackageManager.getInstance();
                        manager.unregister(blockPos);
                        cloudedModule.forEach(pair -> manager.register(blockPos, pair.first, pair.second));
                    }
                }
            }
        });

        if (litUpAnimationTime >= 0) {
            if (sl.getGameTime() % 80L == 0L) {
                sl.playSound(null, blockPos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @SuppressWarnings("unused")
    public void tickClient(BlockPos blockPos, BlockState blockState1, RandomSource random) {
        checkBlocked(blockPos, (_, _) -> true);

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

    private void checkBlocked(BlockPos blockPos, BeaconListHelper helper) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        if (checkingY <= y) {
            checkingY = y + 1;
            helper.init();
        }

        assert level != null;
        int maxHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        for (int i = 0; i < 10 && checkingY < maxHeight; i++) {
            BlockPos pos = new BlockPos(x, checkingY, z);
            BlockState state = level.getBlockState(pos);
            if (helper.add(pos, state) && state.getLightDampening() >= 15 && !state.is(Blocks.BEDROCK)) {
                checkingY = y;
                litUpAnimationTime = -1;
                break;
            }
            checkingY++;
        }

        if (checkingY >= maxHeight) {
            helper.finish();
            helper.init();
            checkingY = y + 1;
            if (litUpAnimationTime < 0) {
                litUpAnimationTime = 0;
                transmitAnimationTime = 0;
            }
        }
    }


    // copy from BeaconBlockEntity, if bug, ask mojang
    @Override
    public void setLevel(@NonNull Level level) {
        super.setLevel(level);
        this.checkingY = level.getMinY() - 1;
    }

    private interface BeaconListHelper {
        default void init() {
        }

        boolean add(BlockPos pos, BlockState state);

        default void finish() {
        }
    }
}
