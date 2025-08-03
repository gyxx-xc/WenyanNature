package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Vec3ModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「向」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .object("「向」", WenyanVec3.OBJECT_TYPE)
            .build();

    public Vec3ModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.VEC3_MODULE_ENTITY.get(), pos, blockState);
    }
}
