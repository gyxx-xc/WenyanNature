package indi.wenyan.judou.utils.function;

import indi.wenyan.judou.exec_interface.handler.AwaitCallHandler;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinAsyncFunction;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinFunction;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanPackageBuilder;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import indi.wenyan.judou.utils.language.Symbol;

import java.util.List;

public enum WenyanPackages {
    ;

    public static final WenyanPackage WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function(Symbol.PLUS_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::add))
            .function(Symbol.SUB_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::sub))
            .function(Symbol.MUL_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::mul))
            .function(Symbol.DIV_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::div))

            .function(Symbol.NOT_ID, (IWenyanValue _, List<IWenyanValue> args) ->
                    args.getFirst().as(WenyanBoolean.TYPE).not())

            .function(Symbol.CONCAT_ID, (IWenyanValue _, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrongRange.string(2, 256, args.size()));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                for (IWenyanValue v : args.subList(1, args.size())) {
                    v.as(WenyanList.TYPE).value().stream().map(WenyanLeftValue::varOf).forEach(value::add);
                }
                return value;
            })
            .function(Symbol.LIST_ADD_ID, (IWenyanValue _, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrongRange.string(2, 256, args.size()));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                args.subList(1, args.size()).forEach((v) -> value.add(WenyanLeftValue.varOf(v)));
                return value;
            })

            // 模, 且, 或
            .function(Symbol.MOD_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::mod))
            .function(Symbol.AND_ID, WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalAnd))
            .function(Symbol.OR_ID, WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalOr))

            .function(Symbol.NEQ_ID, WenyanPackageBuilder.compareOperation((a, b) -> !IWenyanValue.equals(a, b)))
            .function(Symbol.LESS_EQUAL_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) <= 0))
            .function(Symbol.GREAT_EQUAL_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) >= 0))
            .function(Symbol.EQ_ID, WenyanPackageBuilder.compareOperation((value, other) -> IWenyanValue.equals(value, other)))
            .function(Symbol.GRATER_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) > 0))
            .function(Symbol.LESSER_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) < 0))

            .function(Symbol.AWAIT_ID, AwaitCallHandler.INSTANCE)
            .function(Symbol.CREATE_ASYNC_ID, (_, args) -> new WenyanBuiltinAsyncFunction(args.getFirst().as(WenyanBuiltinFunction.TYPE)))

            .function(Symbol.EMPTY_ID, (IWenyanValue _, List<IWenyanValue> _) -> WenyanNull.NULL)
            .build();
}
