package indi.wenyan.content.handler.feature_additions.packages.string_utiles;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className Prophecy
 * @Description TODO 校验是否为正则表达式的工具类
 * @date 2025/6/15 1:16
 */
public class Prophecy {
    private static final String prophecyCharacters = ".[]()*+?{}|^$\\";

    // 基础检测
    public static boolean validateProphecy(String suspectedProphecy) {
        try {
            Pattern.compile(suspectedProphecy);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    // 详细诊断
    public static String diagnoseProphecy(String suspectedProphecy) {
        try {
            Pattern.compile(suspectedProphecy);
            return "无咎";
        } catch (PatternSyntaxException e) {
            return String.format("谶文有凶：位：%d  咎：%s",
                    e.getIndex(), e.getDescription());
        }
    }

    // 检测元字符
    public static boolean containsProphecyCharacters(String text) {
        for (char c : text.toCharArray()) {
            if (prophecyCharacters.indexOf(c) != -1) return true;
        }
        return false;
    }
}
