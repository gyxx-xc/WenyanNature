package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LockModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("SemaphoreModule");
    private WenyanThread lockHolder;

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(wenyanPackageBuilder -> wenyanPackageBuilder
                    .function(WenyanSymbol.var("SemaphoreModule.acquire"), this::acquireSemaphore)
                    .function(WenyanSymbol.var("SemaphoreModule.release"), this::releaseSemaphore)
            )
            .build();

    private @NotNull IWenyanValue acquireSemaphore(IWenyanValue self, List<IWenyanValue> args) throws WenyanException {
        level.setBlock(getBlockPos(), getBlockState().setValue(LockModuleBlock.LOCK_STATE, true), Block.UPDATE_CLIENTS);
        return WenyanNull.NULL;
    }

    private @NotNull WenyanNull releaseSemaphore(IWenyanValue self, List<IWenyanValue> args) throws WenyanException {
        if (!blockState().getValue(LockModuleBlock.LOCK_STATE)) {
            throw new WenyanException("not hold");
        }
        level.setBlock(getBlockPos(), getBlockState().setValue(LockModuleBlock.LOCK_STATE, false), Block.UPDATE_CLIENTS);
        return WenyanNull.NULL;
    }

    public LockModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.LOCK_MODULE_ENTITY.get(), pos, blockState);
    }
}
