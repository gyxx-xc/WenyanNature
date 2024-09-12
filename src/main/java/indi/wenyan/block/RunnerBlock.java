package indi.wenyan.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;

public class RunnerBlock extends FaceAttachedHorizontalDirectionalBlock {
    protected RunnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return null;
    }
}
