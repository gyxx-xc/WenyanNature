package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.setup.language.FunctionMetaText;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class MathModuleBlock extends AbstractFuluBlock {
    public static final String ID = "math_module_block";

    public static final MapCodec<MathModuleBlock> CODEC = simpleCodec(MathModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.MathModule;
    @Getter(lazy = true)
    private static final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .description(FunctionMetaText.MathModulePI.string())
                    .constant(WenyanSymbol.MathModule$PI, WenyanValues.of(Math.PI))
                    .description(FunctionMetaText.MathModuleTAU.string())
                    .constant(WenyanSymbol.MathModule$TAU, WenyanValues.of(Math.TAU))
                    .description(FunctionMetaText.MathModuleHalfPi.string())
                    .constant(WenyanSymbol.MathModule$HALF_PI, WenyanValues.of(Math.PI / 2))
                    .description(FunctionMetaText.MathModuleQuarterPi.string())
                    .constant(WenyanSymbol.MathModule$QUARTER_PI, WenyanValues.of(Math.PI / 4))
                    .description(FunctionMetaText.MathModuleE.string())
                    .constant(WenyanSymbol.MathModule$E, WenyanValues.of(Math.E))
                    .description(FunctionMetaText.MathModuleEuler.string())
                    .constant(WenyanSymbol.MathModule$EULER, WenyanValues.of(0.5772156649))
                    .description(FunctionMetaText.MathModuleGoldenRatio.string())
                    .constant(WenyanSymbol.MathModule$GOLDEN_RATIO, WenyanValues.of((1 + Math.sqrt(5)) / 2))
                    .description(FunctionMetaText.MathModuleSqrt2.string())
                    .constant(WenyanSymbol.MathModule$SQRT_2, WenyanValues.of(Math.sqrt(2)))
                    .description(FunctionMetaText.MathModuleLog2.string())
                    .constant(WenyanSymbol.MathModule$LOG_2, WenyanValues.of(Math.log(2)))
                    .description(FunctionMetaText.MathModuleLog10.string())
                    .constant(WenyanSymbol.MathModule$LOG_10, WenyanValues.of(Math.log(10)))
                    .description(FunctionMetaText.MathModuleSin.string())
                    .doubleFunction(WenyanSymbol.MathModule$sin, args -> StrictMath.sin(args.getFirst()))
                    .description(FunctionMetaText.MathModuleCos.string())
                    .doubleFunction(WenyanSymbol.MathModule$cos, args -> StrictMath.cos(args.getFirst()))
                    .description(FunctionMetaText.MathModuleAsin.string())
                    .doubleFunction(WenyanSymbol.MathModule$asin, args -> StrictMath.asin(args.getFirst()))
                    .description(FunctionMetaText.MathModuleAcos.string())
                    .doubleFunction(WenyanSymbol.MathModule$acos, args -> StrictMath.acos(args.getFirst()))
                    .description(FunctionMetaText.MathModuleTan.string())
                    .doubleFunction(WenyanSymbol.MathModule$tan, args -> StrictMath.tan(args.getFirst()))
                    .description(FunctionMetaText.MathModuleAtan.string())
                    .doubleFunction(WenyanSymbol.MathModule$atan, args -> StrictMath.atan(args.getFirst()))
                    .description(FunctionMetaText.MathModuleAtan2.string())
                    .doubleFunction(WenyanSymbol.MathModule$atan2, args -> StrictMath.atan2(args.getFirst(), args.get(1)))
                    .description(FunctionMetaText.MathModuleHypot.string())
                    .doubleFunction(WenyanSymbol.MathModule$hypot, args -> StrictMath.hypot(args.getFirst(), args.get(1)))
                    .description(FunctionMetaText.MathModuleLog.string())
                    .doubleFunction(WenyanSymbol.MathModule$log, args -> StrictMath.log(args.getFirst()))
                    .description(FunctionMetaText.MathModuleExp.string())
                    .doubleFunction(WenyanSymbol.MathModule$exp, args -> StrictMath.exp(args.getFirst()))
                    .description(FunctionMetaText.MathModulePow.string())
                    .doubleFunction(WenyanSymbol.MathModule$pow, args -> StrictMath.pow(args.getFirst(), args.get(1)))
                    .description(FunctionMetaText.MathModuleSqrt.string())
                    .doubleFunction(WenyanSymbol.MathModule$sqrt, args -> Math.sqrt(args.getFirst()))
                    .description(FunctionMetaText.MathModuleAbs.string())
                    .doubleFunction(WenyanSymbol.MathModule$abs, args -> Math.abs(args.getFirst()))
                    .description(FunctionMetaText.MathModuleCeil.string())
                    .doubleFunction(WenyanSymbol.MathModule$ceil, args -> Math.ceil(args.getFirst()))
                    .description(FunctionMetaText.MathModuleFloor.string())
                    .doubleFunction(WenyanSymbol.MathModule$floor, args -> Math.floor(args.getFirst()))
                    .description(FunctionMetaText.MathModuleRound.string())
                    .doubleFunction(WenyanSymbol.MathModule$round, args -> (double) Math.round(args.getFirst()))
                    .description(FunctionMetaText.MathModuleSignum.string())
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
