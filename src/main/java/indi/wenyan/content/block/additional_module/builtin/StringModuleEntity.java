package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.RawHandlerPackage;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public class StringModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("StringModule");

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .function(WenyanSymbol.var("StringModule.length"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.length());
                            })
                    .function(WenyanSymbol.var("StringModule.charAt"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var idx = args.get(1).as(WenyanInteger.TYPE).value();
                                return WenyanValues.of(str.substring(idx, idx + 1));
                            })
                    .function(WenyanSymbol.var("StringModule.indexOf"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.indexOf(sub));
                            })
                    .function(WenyanSymbol.var("StringModule.split"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sep = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(Arrays.stream(str.split(sep)).map(WenyanValues::of).toList());
                            })
                    .function(WenyanSymbol.var("StringModule.replace"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var target = args.get(1).as(WenyanString.TYPE).value();
                                var replacement = args.get(2).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.replace(target, replacement));
                            })
                    .function(WenyanSymbol.var("StringModule.reverse"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(new StringBuilder(str).reverse().toString());
                            })
                    .function(WenyanSymbol.var("StringModule.trim"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.getFirst().as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.trim());
                            })
                    .function(WenyanSymbol.var("StringModule.contains"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var sub = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.contains(sub));
                            })
                    .function(WenyanSymbol.var("StringModule.startsWith"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var prefix = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.startsWith(prefix));
                            })
                    .function(WenyanSymbol.var("StringModule.endsWith"),
                            (IWenyanValue self, List<IWenyanValue> args) -> {
                                var str = args.get(0).as(WenyanString.TYPE).value();
                                var suffix = args.get(1).as(WenyanString.TYPE).value();
                                return WenyanValues.of(str.endsWith(suffix));
                            }))
            .build();

    public StringModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.STRING_MODULE_ENTITY.get(), pos, blockState);
    }
}
