package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.utils.function.WenyanValues;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock {
    public static final String ID = "math_module_block";

    public static final MapCodec<MathModuleBlock> CODEC = simpleCodec(MathModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.MathModule;
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .constant(WenyanSymbol.MathModule$PI, WenyanValues.of(Math.PI))
                    .constant(WenyanSymbol.MathModule$TAU, WenyanValues.of(Math.TAU))
                    .constant(WenyanSymbol.MathModule$HALF_PI, WenyanValues.of(Math.PI / 2))
                    .constant(WenyanSymbol.MathModule$QUARTER_PI, WenyanValues.of(Math.PI / 4))
                    .constant(WenyanSymbol.MathModule$E, WenyanValues.of(Math.E))
                    .constant(WenyanSymbol.MathModule$EULER, WenyanValues.of(0.5772156649))
                    .constant(WenyanSymbol.MathModule$GOLDEN_RATIO, WenyanValues.of((1 + Math.sqrt(5)) / 2))
                    .constant(WenyanSymbol.MathModule$SQRT_2, WenyanValues.of(Math.sqrt(2)))
                    .constant(WenyanSymbol.MathModule$LOG_2, WenyanValues.of(Math.log(2)))
                    .constant(WenyanSymbol.MathModule$LOG_10, WenyanValues.of(Math.log(10)))
                    .doubleFunction(WenyanSymbol.MathModule$sin, args -> StrictMath.sin(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$cos, args -> StrictMath.cos(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$asin, args -> StrictMath.asin(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$acos, args -> StrictMath.acos(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$tan, args -> StrictMath.tan(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$atan, args -> StrictMath.atan(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$atan2, args -> StrictMath.atan2(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.MathModule$hypot, args -> StrictMath.hypot(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.MathModule$log, args -> StrictMath.log(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$exp, args -> StrictMath.exp(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$pow, args -> StrictMath.pow(args.getFirst(), args.get(1)))
                    .doubleFunction(WenyanSymbol.MathModule$sqrt, args -> Math.sqrt(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$abs, args -> Math.abs(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$ceil, args -> Math.ceil(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$floor, args -> Math.floor(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$round, args -> (double) Math.round(args.getFirst()))
                    .doubleFunction(WenyanSymbol.MathModule$signum, args -> Math.signum(args.getFirst())))
            .build();

    public MathModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<MathModuleBlock> codec() {
        return CODEC;
    }
}
