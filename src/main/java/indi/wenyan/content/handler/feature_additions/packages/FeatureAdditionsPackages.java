package indi.wenyan.content.handler.feature_additions.packages;

import indi.wenyan.content.handler.feature_additions.packages.string_utiles.*;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className FeatureAdditionsPackages
 * @Description TODO 拓展功能包注入类
 * @date 2025/6/15 0:06
 */

public class FeatureAdditionsPackages {
    public static final WenyanRuntime STRING_UTILS_PACKAGES = WenyanPackageBuilder.create()
            .function("「观字位」", new StringUtil_IndexOf())
            .function("「契首字」", new StringUtil_StartWith())
            .function("「合尾字」", new StringUtil_EndWith())
            .function("「蕴字象」", new StringUtil_Contains())
            .function("「截首爻」",new StringUtil_SubString_Start())
            .function("「截全爻」",new StringUtil_SubString_StartAndEnd())
            .function("「易篆文」",new StringUtil_Replace())
            .function("「升阳文」", new StringUtil_ToUpperCase())
            .function("「化阴文」", new StringUtil_ToLowerCase())
            .function("「去芜文」", new StringUtil_Trim())
            .function("「分文炁」", new StringUtil_Split())
            .function("「合谶文」", new StringUtil_Matches())
            .function("「验谶文」", new StringUtil_ValidateProphecy())
            .function("「辨谶凶」", new StringUtil_DiagnoseProphecy())
            .function("「蕴谶骨」", new StringUtil_ContainsProphecyCharacters())
            .function("「虚室文」", new StringUtil_IsEmpty())
            .build();
}
