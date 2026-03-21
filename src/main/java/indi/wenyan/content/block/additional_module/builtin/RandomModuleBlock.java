package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class RandomModuleBlock extends AbstractFuluBlock {

    public static final String ID = "random_module_block";

    public static final MapCodec<RandomModuleBlock> CODEC = simpleCodec(RandomModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.var("RandomModule");
    public static final Random RANDOM = new Random();
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .intFunction(WenyanSymbol.var("RandomModule.nextInt"), args -> switch (args.size()) {
                        case 0 -> RANDOM.nextInt();
                        case 1 -> RANDOM.nextInt(args.getFirst());
                        case 2 -> RANDOM.nextInt(args.get(0), args.get(1));
                        default -> throw new WenyanException(""); // TODO
                    })
                    .doubleFunction(WenyanSymbol.var("RandomModule.nextDouble"), _ -> RANDOM.nextDouble())
                    .doubleFunction(WenyanSymbol.var("RandomModule.nextTriangle"), args -> RANDOM.nextDouble(args.getFirst() - args.get(1), args.getFirst() + args.get(1)))
                    .function(WenyanSymbol.var("RandomModule.nextBoolean"), (IWenyanValue _, List<IWenyanValue> _) -> WenyanValues.of(RANDOM.nextBoolean())))
            .build();

    public RandomModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<RandomModuleBlock> codec() {
        return CODEC;
    }
}
