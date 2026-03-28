package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.function.WenyanValues;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

import static indi.wenyan.judou.utils.language.JudouExceptionText.ArgsNumWrongRange;

public class RandomModuleBlock extends AbstractFuluBlock {

    public static final String ID = "random_module_block";

    public static final MapCodec<RandomModuleBlock> CODEC = simpleCodec(RandomModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.RandomModule;
    public static final Random RANDOM = new Random();
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .intFunction(WenyanSymbol.RandomModule$nextInt, args -> switch (args.size()) {
                        case 0 -> RANDOM.nextInt();
                        case 1 -> RANDOM.nextInt(args.getFirst());
                        case 2 -> RANDOM.nextInt(args.get(0), args.get(1));
                        default -> throw new WenyanException(ArgsNumWrongRange.string(0, 2, args.size()));
                    })
                    .doubleFunction(WenyanSymbol.RandomModule$nextDouble, _ -> RANDOM.nextDouble())
                    .doubleFunction(WenyanSymbol.RandomModule$nextTriangle, args -> RANDOM.nextDouble(args.getFirst() - args.get(1), args.getFirst() + args.get(1)))
                    .function(WenyanSymbol.RandomModule$nextBoolean, (IWenyanValue _, List<IWenyanValue> _) -> WenyanValues.of(RANDOM.nextBoolean())))
            .build();

    public RandomModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<RandomModuleBlock> codec() {
        return CODEC;
    }
}
