package indi.wenyan.interpreter.utils;

import cn.hutool.core.convert.Convert;

import java.math.BigInteger;

public enum ChineseUtils {
    ;
    public static final boolean DIRECT_NUMBER_CONVERT = false;

    public static final char[] WENYAN_NUMBERS = new char[] {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static String toChinese(BigInteger i) {
        if (!DIRECT_NUMBER_CONVERT) {
            try {
                return Convert.numberToChinese(i.intValueExact(), false)
                        .replaceAll("万", "萬").replaceAll("亿", "億");
            } catch (ArithmeticException ignored) {
            } // go outward
        }
        StringBuilder chinese = new StringBuilder();
        var number = i;
        while (number.compareTo(BigInteger.ZERO) > 0) {
            int digit = number.mod(BigInteger.valueOf(10)).intValue();
            chinese.insert(0, WENYAN_NUMBERS[digit]);
            number = number.divide(BigInteger.valueOf(10));
        }
        if (i.compareTo(BigInteger.ZERO) < 0) {
            chinese.insert(0, "負");
        }
        return chinese.toString();
    }
}
