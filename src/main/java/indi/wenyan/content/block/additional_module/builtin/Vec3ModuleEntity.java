package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.HandlerPackageBuilder;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Vec3ModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("Vec3Module");

    @Getter
    private final HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .object(WenyanSymbol.var("Vec3Module.object"), WenyanVec3.OBJECT_TYPE))
            .build();

    public Vec3ModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.VEC3_MODULE_ENTITY.get(), pos, blockState);
    }
}
