package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock {
    public static final String ID = "math_module_block";

    public static final MapCodec<MathModuleBlock> CODEC = simpleCodec(MathModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.var("MathModule");
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .constant(WenyanSymbol.var("MathModule.PI"), WenyanValues.of(Math.PI))
                    .constant(WenyanSymbol.var("MathModule.TAU"), WenyanValues.of(Math.TAU))
                    .constant(WenyanSymbol.var("MathModule.HALF_PI"), WenyanValues.of(Math.PI / 2))
                    .constant(WenyanSymbol.var("MathModule.QUARTER_PI"), WenyanValues.of(Math.PI / 4))
                    .constant(WenyanSymbol.var("MathModule.E"), WenyanValues.of(Math.E))
                    .constant(WenyanSymbol.var("MathModule.EULER"), WenyanValues.of(0.5772156649))
                    .constant(WenyanSymbol.var("MathModule.GOLDEN_RATIO"), WenyanValues.of((1 + Math.sqrt(5)) / 2))
                    .constant(WenyanSymbol.var("MathModule.SQRT_2"), WenyanValues.of(Math.sqrt(2)))
                    .constant(WenyanSymbol.var("MathModule.LOG_2"), WenyanValues.of(Math.log(2)))
                    .constant(WenyanSymbol.var("MathModule.LOG_10"), WenyanValues.of(Math.log(10)))
                    .doubleFunction(WenyanSymbol.var("MathModule.sin"), args -> StrictMath.sin(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.cos"), args -> StrictMath.cos(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.asin"), args -> StrictMath.asin(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.acos"), args -> StrictMath.acos(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.tan"), args -> StrictMath.tan(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.atan"), args -> StrictMath.atan(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.atan2"), args -> StrictMath.atan2(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.var("MathModule.hypot"), args -> StrictMath.hypot(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.var("MathModule.log"), args -> StrictMath.log(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.exp"), args -> StrictMath.exp(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.pow"), args -> StrictMath.pow(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.var("MathModule.sqrt"), args -> Math.sqrt(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.abs"), args -> Math.abs(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.ceil"), args -> Math.ceil(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.floor"), args -> Math.floor(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.round"), args -> (double) Math.round(args.getFirst()))
                    .doubleFunction(WenyanSymbol.var("MathModule.signum"), args -> Math.signum(args.getFirst())))
            .build();

    public MathModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<MathModuleBlock> codec() {
        return CODEC;
    }
}
