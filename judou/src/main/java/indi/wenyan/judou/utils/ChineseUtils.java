package indi.wenyan.judou.utils;

import cn.hutool.core.convert.Convert;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;

public enum ChineseUtils {
    ;
    public static final boolean DIRECT_NUMBER_CONVERT = false;

    private static final char[] WENYAN_NUMBERS = new char[]{'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static @NotNull String toChinese(BigInteger i) {
        if (!DIRECT_NUMBER_CONVERT) {
            try {
                return Convert.numberToChinese(i.intValueExact(), false)
                        .replace("万", "萬").replace("亿", "億");
            } catch (ArithmeticException ignored) { // go outward
            }
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

    public static final List<String> WENYAN_FRACTIONS = List.of("分", "釐", "毫", "絲", "忽", "微", "纖", "沙", "塵");

    public static @NotNull String toChinese(Double value) {
        StringBuilder number = new StringBuilder(toChinese(new BigInteger(String.valueOf(value.longValue()))));
        String digit = String.format("%.9f", value).split("\\.")[1];
        if (digit.equals("000000000"))
            return number.toString();
        number.append("又");
        for (int i = 0; i < Math.min(digit.length(), WENYAN_FRACTIONS.size()); i++) {
            if (digit.charAt(i) != '0') {
                number.append(WENYAN_NUMBERS[digit.charAt(i) - '0']);
                number.append(WENYAN_FRACTIONS.get(i));
            }
        }

        return number.toString();
    }
}
