package indi.wenyan.content.block.additional_module.block;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.HandlerPackageBuilder;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.Semaphore;

public class SemaphoreModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("SemaphoreModule"); // like this

    private final Semaphore semaphore = new Semaphore(1);

    @Getter
    private final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("SemaphoreModule.acquire"), (request) -> {
                try {
                    semaphore.acquire();
                    return WenyanValues.of(true);
                } catch (InterruptedException e) {
                    return WenyanValues.of(false);
                }
            })
            .handler(WenyanSymbol.var("SemaphoreModule.release"), (request) -> {
                semaphore.release();
                return WenyanNull.NULL;
            })
            .build();

    public SemaphoreModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.SEMAPHORE_MODULE_ENTITY.get(), pos, blockState);
    }
}
