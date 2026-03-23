package indi.wenyan.judou.utils;

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
import indi.wenyan.judou.utils.language.JudouExceptionText;

import java.util.List;

public enum WenyanPackages {
    ;
    // these string for candy visitor
    public static final String AND_ID = "且";
    public static final String OR_ID = "或";
    public static final String MOD_ID = "模";
    public static final String IMPORT_ID = "觀";
    public static final String CREATE_ASYNC_ID = "同";

    public static final String PLUS_ID = "加";
    public static final String SUB_ID = "減";
    public static final String MUL_ID = "乘";
    public static final String DIV_ID = "除";
    public static final String NOT_ID = "變";
    public static final String CONCAT_ID = "銜";
    public static final String LIST_ADD_ID = "充";

    public static final String NEQ_ID = "不等於";
    public static final String LESS_EQUAL_ID = "不大於";
    public static final String GREAT_EQUAL_ID = "不小於";
    public static final String EQ_ID = "等於";
    public static final String GRATER_ID = "大於";
    public static final String LESSER_ID = "小於";
    public static final String AWAIT_ID = "待";
    public static final String EMPTY_ID = "「」";

    public static final WenyanPackage WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function(PLUS_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::add))
            .function(SUB_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::sub))
            .function(MUL_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::mul))
            .function(DIV_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::div))

            .function(NOT_ID, (IWenyanValue self, List<IWenyanValue> args) ->
                    args.getFirst().as(WenyanBoolean.TYPE).not())

            .function(CONCAT_ID, (IWenyanValue self, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrongRange.string(2, 256, args.size()));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                for (IWenyanValue v : args.subList(1, args.size())) {
                    v.as(WenyanList.TYPE).value().stream().map(WenyanLeftValue::varOf).forEach(value::add);
                }
                return value;
            })
            .function(LIST_ADD_ID, (IWenyanValue self, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrongRange.string(2, 256, args.size()));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                args.subList(1, args.size()).forEach((v) -> value.add(WenyanLeftValue.varOf(v)));
                return value;
            })

            // 模, 且, 或
            .function(MOD_ID, WenyanPackageBuilder.reduceWith(IWenyanValue::mod))
            .function(AND_ID, WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalAnd))
            .function(OR_ID, WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalOr))

            .function(NEQ_ID, WenyanPackageBuilder.compareOperation((a, b) -> !IWenyanValue.equals(a, b)))
            .function(LESS_EQUAL_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) <= 0))
            .function(GREAT_EQUAL_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) >= 0))
            .function(EQ_ID, WenyanPackageBuilder.compareOperation((value, other) -> IWenyanValue.equals(value, other)))
            .function(GRATER_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) > 0))
            .function(LESSER_ID, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) < 0))

            .function(AWAIT_ID, AwaitCallHandler.INSTANCE)
            .function(CREATE_ASYNC_ID, (self, args) -> new WenyanBuiltinAsyncFunction(args.getFirst().as(WenyanBuiltinFunction.TYPE)))

            .function(EMPTY_ID, (IWenyanValue self, List<IWenyanValue> args) -> WenyanNull.NULL)
            .build();
}
