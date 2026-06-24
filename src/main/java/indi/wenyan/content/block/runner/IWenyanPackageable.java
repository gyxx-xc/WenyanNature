package indi.wenyan.content.block.runner;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import net.minecraft.core.BlockPos;

public interface IWenyanPackageable {
    String getPlatformName();
    BlockPos getBlockPos();
    boolean newThread(IWenyanBytecode bytecode);
    boolean isRemoved();
}
