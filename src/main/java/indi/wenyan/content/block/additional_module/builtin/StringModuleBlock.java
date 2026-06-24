package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.judou.api.values.primitive.WenyanString;
import indi.wenyan.setup.language.FunctionMetaText;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StringModuleBlock extends AbstractFuluBlock {
    public static final String ID = "string_module_block";

    public static final MapCodec<StringModuleBlock> CODEC = simpleCodec(StringModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.StringModule;
    @Getter(lazy = true)
    private static final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .description(FunctionMetaText.StringModuleLength.string())
                    .function(WenyanSymbol.StringModule$length,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.length());
                            })
                    .description(FunctionMetaText.StringModuleCharAt.string())
                    .function(WenyanSymbol.StringModule$charAt,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var idx = args.get(1).as(WenyanInteger.TYPE).value();
                                return WenyanValues.of(str.substring(idx, idx + 1));
                            })
                    .description(FunctionMetaText.StringModuleIndexOf.string())
                    .function(WenyanSymbol.StringModule$indexOf,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.indexOf(sub));
                            })
                    .description(FunctionMetaText.StringModuleSplit.string())
                    .function(WenyanSymbol.StringModule$split,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sep = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(Arrays.stream(str.split(sep)).<IWenyanValue>map(WenyanValues::of).toList());
                            })
                    .description(FunctionMetaText.StringModuleReplace.string())
                    .function(WenyanSymbol.StringModule$replace,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var target = args.get(1).as(WenyanString.TYPE).value();
                                var replacement = args.get(2).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.replace(target, replacement));
                            })
                    .description(FunctionMetaText.StringModuleReverse.string())
                    .function(WenyanSymbol.StringModule$reverse,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(new StringBuilder(str).reverse().toString());
                            })
                    .description(FunctionMetaText.StringModuleTrim.string())
                    .function(WenyanSymbol.StringModule$trim,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.trim());
                            })
                    .description(FunctionMetaText.StringModuleContains.string())
                    .function(WenyanSymbol.StringModule$contains,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.contains(sub));
                            })
                    .description(FunctionMetaText.StringModuleStartsWith.string())
                    .function(WenyanSymbol.StringModule$startsWith,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var prefix = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.startsWith(prefix));
                            })
                    .description(FunctionMetaText.StringModuleEndsWith.string())
                    .function(WenyanSymbol.StringModule$endsWith,
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

