package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import org.jetbrains.annotations.NotNull;

public class Vec3ModuleBlock extends AbstractFuluBlock {
    public static final String ID = "vec3_module_block";

    public static final MapCodec<Vec3ModuleBlock> CODEC = simpleCodec(Vec3ModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.var("Vec3Module");
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .object(WenyanSymbol.var("Vec3Module.object"), WenyanVec3.OBJECT_TYPE))
            .build();

    public Vec3ModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<Vec3ModuleBlock> codec() {
        return CODEC;
    }
}
