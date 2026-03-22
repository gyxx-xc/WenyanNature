package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import org.jetbrains.annotations.NotNull;

public class BitModuleBlock extends AbstractFuluBlock {
    public static final String ID = "bit_module_block";

    public static final MapCodec<BitModuleBlock> CODEC = simpleCodec(BitModuleBlock::new);
    @SuppressWarnings("UnnecessaryBoxing") // better performance
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .intFunction(WenyanSymbol.var("BitModule.leftShift"), args -> Integer.valueOf(args.getFirst() << args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.rightShift"), args -> Integer.valueOf(args.getFirst() >> args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.zeroFillRightShift"), args -> Integer.valueOf(args.getFirst() >>> args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.bitAnd"), args -> Integer.valueOf(args.getFirst() & args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.bitOr"), args -> Integer.valueOf(args.getFirst() | args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.bitXor"), args -> Integer.valueOf(args.getFirst() ^ args.get(1)))
                    .intFunction(WenyanSymbol.var("BitModule.bitNand"), args -> Integer.valueOf(~(args.getFirst() & args.get(1))))
                    .intFunction(WenyanSymbol.var("BitModule.bitNot"), args -> Integer.valueOf(~args.getFirst()))
            )
            .build();
    public static final String DEVICE_NAME = WenyanSymbol.var("BitModule");

    public BitModuleBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected @NotNull MapCodec<BitModuleBlock> codec() {
        return CODEC;
    }
}
