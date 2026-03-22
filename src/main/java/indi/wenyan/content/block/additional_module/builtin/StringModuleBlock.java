package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StringModuleBlock extends AbstractFuluBlock {
    public static final String ID = "string_module_block";

    public static final MapCodec<StringModuleBlock> CODEC = simpleCodec(StringModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.var("StringModule");
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .function(WenyanSymbol.var("StringModule.length"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.length());
                            })
                    .function(WenyanSymbol.var("StringModule.charAt"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var idx = args.get(1).as(WenyanInteger.TYPE).value();
                                return WenyanValues.of(str.substring(idx, idx + 1));
                            })
                    .function(WenyanSymbol.var("StringModule.indexOf"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.indexOf(sub));
                            })
                    .function(WenyanSymbol.var("StringModule.split"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sep = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(Arrays.stream(str.split(sep)).<IWenyanValue>map(WenyanValues::of).toList());
                            })
                    .function(WenyanSymbol.var("StringModule.replace"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var target = args.get(1).as(WenyanString.TYPE).value();
                                var replacement = args.get(2).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.replace(target, replacement));
                            })
                    .function(WenyanSymbol.var("StringModule.reverse"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(new StringBuilder(str).reverse().toString());
                            })
                    .function(WenyanSymbol.var("StringModule.trim"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.trim());
                            })
                    .function(WenyanSymbol.var("StringModule.contains"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.contains(sub));
                            })
                    .function(WenyanSymbol.var("StringModule.startsWith"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var prefix = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.startsWith(prefix));
                            })
                    .function(WenyanSymbol.var("StringModule.endsWith"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var suffix = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.endsWith(suffix));
                            }))
            .build();

    public StringModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<StringModuleBlock> codec() {
        return CODEC;
    }


}

