package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InformativeAdditionalModuleEntity extends AbstractAdditionalModuleEntity{
    @Getter
    private final String packageName = "「im」";
    @Getter
    private final WenyanRuntime execPackage = WenyanPackageBuilder.create()
            .function("「h」", new IThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    int value = 0;
                    if (getLevel() != null) {
                        value = getLevel().getBestNeighborSignal(getBlockPos());
                    }
                    return new WenyanInteger(value);
                }
            })
            .build();

    public InformativeAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INFORMATIVE_MODULE_ENTITY.get(), pos, blockState);
    }
}
