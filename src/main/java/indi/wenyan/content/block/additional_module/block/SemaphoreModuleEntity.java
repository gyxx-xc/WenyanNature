package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.Semaphore;

public class SemaphoreModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「信」";

    private final Semaphore semaphore = new Semaphore(1);

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「獲取」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException {
                    try {
                        semaphore.acquire();
                        return new WenyanBoolean(true);
                    } catch (InterruptedException e) {
                        return new WenyanBoolean(false);
                    }
                }
            })
            .function("「釋放」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    semaphore.release();
                    return WenyanNull.NULL;
                }
            })
            .build();

    public SemaphoreModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.SEMAPHORE_MODULE_ENTITY.get(), pos, blockState);
    }
}
