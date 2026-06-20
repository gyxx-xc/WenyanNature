package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.setup.language.FunctionMetaText;
import org.jetbrains.annotations.NotNull;

public class BitModuleBlock extends AbstractFuluBlock {
    public static final String ID = "bit_module_block";

    public static final MapCodec<BitModuleBlock> CODEC = simpleCodec(BitModuleBlock::new);
    @SuppressWarnings("UnnecessaryBoxing") // better performance
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .description(FunctionMetaText.BitModuleLeftShift.string())
                    .intFunction(WenyanSymbol.BitModule$leftShift, args -> Integer.valueOf(args.getFirst() << args.get(1)))
                    .description(FunctionMetaText.BitModuleRightShift.string())
                    .intFunction(WenyanSymbol.BitModule$rightShift, args -> Integer.valueOf(args.getFirst() >> args.get(1)))
                    .description(FunctionMetaText.BitModuleZeroFillRightShift.string())
                    .intFunction(WenyanSymbol.BitModule$zeroFillRightShift, args -> Integer.valueOf(args.getFirst() >>> args.get(1)))
                    .description(FunctionMetaText.BitModuleBitAnd.string())
                    .intFunction(WenyanSymbol.BitModule$bitAnd, args -> Integer.valueOf(args.getFirst() & args.get(1)))
                    .description(FunctionMetaText.BitModuleBitOr.string())
                    .intFunction(WenyanSymbol.BitModule$bitOr, args -> Integer.valueOf(args.getFirst() | args.get(1)))
                    .description(FunctionMetaText.BitModuleBitXor.string())
                    .intFunction(WenyanSymbol.BitModule$bitXor, args -> Integer.valueOf(args.getFirst() ^ args.get(1)))
                    .description(FunctionMetaText.BitModuleBitNand.string())
                    .intFunction(WenyanSymbol.BitModule$bitNand, args -> Integer.valueOf(~(args.getFirst() & args.get(1))))
                    .description(FunctionMetaText.BitModuleBitNot.string())
                    .intFunction(WenyanSymbol.BitModule$bitNot, args -> Integer.valueOf(~args.getFirst()))
            )
            .build();
    public static final String DEVICE_NAME = WenyanSymbol.BitModule;

    public BitModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<BitModuleBlock> codec() {
        return CODEC;
    }
}
