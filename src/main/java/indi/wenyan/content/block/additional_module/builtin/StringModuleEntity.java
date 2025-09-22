package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class StringModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「文」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「長」", (self, args) -> {
                var str = args.getFirst().as(WenyanString.TYPE).value();
                return WenyanValues.of(str.length());
            })
            .function("「取」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var idx = args.get(1).as(WenyanInteger.TYPE).value();
                return WenyanValues.of(str.substring(idx, idx + 1));
            })
            .function("「尋」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var sub = args.get(1).as(WenyanString.TYPE).value();
                return WenyanValues.of(str.indexOf(sub));
            })
            .function("「分」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var sep = args.get(1).as(WenyanString.TYPE).value();
                return WenyanValues.of(Arrays.stream(str.split(sep)).map(WenyanValues::of).toList());
            })
            .function("「換」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var target = args.get(1).as(WenyanString.TYPE).value();
                var replacement = args.get(2).as(WenyanString.TYPE).value();
                return WenyanValues.of(str.replace(target, replacement));
            })
            .function("「反」", (self, args) -> {
                var str = args.getFirst().as(WenyanString.TYPE).value();
                return WenyanValues.of(new StringBuilder(str).reverse().toString());
            })
            .function("「去空」", (self, args) -> {
                var str = args.getFirst().as(WenyanString.TYPE).value();
                return WenyanValues.of(str.trim());
            })
            .function("「包含」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var sub = args.get(1).as(WenyanString.TYPE).value();
                return WenyanValues.of(str.contains(sub));
            })
            .function("「起始為」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var prefix = args.get(1).as(WenyanString.TYPE).value();
                return WenyanValues.of(str.startsWith(prefix));
            })
            .function("「結束為」", (self, args) -> {
                var str = args.get(0).as(WenyanString.TYPE).value();
                var suffix = args.get(1).as(WenyanString.TYPE).value();
                return WenyanValues.of(str.endsWith(suffix));
            })
            .build();

    public StringModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.STRING_MODULE_ENTITY.get(), pos, blockState);
    }
}

