package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MathAdditionalModuleEntity extends AbstractAdditionalModuleEntity{
    @Getter
    private final String packageName = "「算經」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .constant("「圓周率」", new WenyanDouble(Math.PI))
            .constant("「倍圓周率」", new WenyanDouble(Math.TAU))
            .constant("「半圓周率」", new WenyanDouble(Math.PI / 2))
            .constant("「四分圓周率」", new WenyanDouble(Math.PI / 4))
            .constant("「自然常數」", new WenyanDouble( Math.E))
            .constant("「歐拉常數」", new WenyanDouble(0.5772156649))
            .constant("「黃金分割數」", new WenyanDouble(1.6180339887))
            .constant("「二之平方根」", new WenyanDouble(1.4142135623730951))
            .constant("「二之對數」", new WenyanDouble(Math.log(2)))
            .constant("「十之對數」", new WenyanDouble(Math.log(10)))
            .doubleFunction("「正弦」", args -> StrictMath.sin(args.getFirst()))
            .doubleFunction("「餘弦」", args -> StrictMath.cos(args.getFirst()))
            .doubleFunction("「反正弦」", args -> StrictMath.asin(args.getFirst()))
            .doubleFunction("「反餘弦」", args -> StrictMath.acos(args.getFirst()))
            .doubleFunction("「正切」", args -> StrictMath.tan(args.getFirst()))
            .doubleFunction("「反正切」", args -> StrictMath.atan(args.getFirst()))
            .doubleFunction("「勾股求角」", args -> StrictMath.atan2(args.getFirst(), args.get(1)))
            .doubleFunction("「勾股求弦」", args -> StrictMath.hypot(args.getFirst(), args.get(1)))
            .doubleFunction("「對數」", args -> StrictMath.log(args.getFirst()))
            .doubleFunction("「指數」", args -> StrictMath.exp(args.getFirst()))
            .doubleFunction("「冪」", args -> StrictMath.pow(args.getFirst(), args.get(1)))
            .doubleFunction("「平方根」", args -> Math.sqrt(args.getFirst()))
            .doubleFunction("「絕對」", args -> Math.abs(args.getFirst()))
            .doubleFunction("「取頂」", args -> Math.ceil(args.getFirst()))
            .doubleFunction("「取底」", args -> Math.floor(args.getFirst()))
            .doubleFunction("「取整」", args -> (double) Math.round(args.getFirst()))
            .doubleFunction("「正負」", args -> Math.signum(args.getFirst()))
            .build();

    public MathAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.MATH_MODULE_ENTITY.get(), pos, blockState);
    }
}
