package indi.wenyan.judou.utils;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.warper.WenyanList;

import java.util.List;

public enum WenyanPackages {;
    // these string for candy visitor
    public static final String AND_ID = "且";
    public static final String OR_ID = "或";
    public static final String MOD_ID = "模";
    public static final String IMPORT_ID = "觀";

    public static final WenyanPackage WENYAN_BASIC_PACKAGES = WenyanPackageBuilder.create()
            .function("加", WenyanPackageBuilder.reduceWith(IWenyanValue::add))
            .function(new String[]{"減","减"}, WenyanPackageBuilder.reduceWith(IWenyanValue::sub))
            .function("乘", WenyanPackageBuilder.reduceWith(IWenyanValue::mul))
            .function("除", WenyanPackageBuilder.reduceWith(IWenyanValue::div))

            .function(new String[]{"變","变"}, (IWenyanValue self, List<IWenyanValue> args) ->
                    args.getFirst().as(WenyanBoolean.TYPE).not())

            .function(new String[]{"銜","衔"}, (IWenyanValue self, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                for (IWenyanValue v : args.subList(1, args.size())) {
                    value.concat(v.as(WenyanList.TYPE));
                }
                return value;
            })
            .function("充", (IWenyanValue self, List<IWenyanValue> args) -> {
                if (args.size() <= 1)
                    throw new WenyanException.WenyanVarException(LanguageManager.getTranslation("error.wenyan_programming.number_of_arguments_does_not_match"));
                WenyanList value = args.getFirst().as(WenyanList.TYPE);
                args.subList(1, args.size()).forEach((v) -> value.add(WenyanLeftValue.varOf(v)));
                return value;
            })

            // 模, 且, 或
            .function("模", WenyanPackageBuilder.reduceWith(IWenyanValue::mod))
            .function("且", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalAnd))
            .function("或", WenyanPackageBuilder.boolBinaryOperation(Boolean::logicalOr))

            .function(new String [] {"不等於","不等于"}, WenyanPackageBuilder.compareOperation((a, b) -> !IWenyanValue.equals(a, b)))
            .function(new String [] {"不大於","不大于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) <= 0))
            .function(new String [] {"不小於","不小于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) >= 0))
            .function(new String [] {"等於","等于"}, WenyanPackageBuilder.compareOperation((value, other) -> IWenyanValue.equals(value, other)))
            .function(new String [] {"大於","大于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) > 0))
            .function(new String[] {"小於","小于"}, WenyanPackageBuilder.compareOperation((a, b) -> IWenyanValue.compareTo(a, b) < 0))

            .function(WenyanSymbol.var("Null"), (IWenyanValue self, List<IWenyanValue> args) -> WenyanNull.NULL)
            .build();
}
