package indi.wenyan.judou.utils;

import cn.hutool.core.convert.Convert;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChineseUtils {
    ;
    public static final boolean DIRECT_NUMBER_CONVERT = false;

    private static final char[] WENYAN_NUMBERS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static @NotNull String toChinese(BigInteger i) {
        if (!DIRECT_NUMBER_CONVERT) {
            try {
                return ZhConverterUtil.toTraditional(
                        Convert.numberToChinese(i.intValueExact(), false));
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

    public static @NotNull String toChinese(double value) {
        StringBuilder number = new StringBuilder(toChinese(new BigInteger(String.valueOf((long) value))));
        String digit = String.format("%.9f", value).split("\\.")[1];
        if ("000000000".equals(digit))
            return number.toString();
        number.append("又");
        int min = Math.min(digit.length(), WENYAN_FRACTIONS.size());
        for (int i = 0; i < min; i++) {
            if (digit.charAt(i) != '0') {
                number.append(WENYAN_NUMBERS[digit.charAt(i) - '0']);
                number.append(WENYAN_FRACTIONS.get(i));
            }
        }

        return number.toString();
    }

    private final static Pattern PATTERN = Pattern.compile("「「.*?」」|「.*?」");
    public static @NotNull String toTraditionalCode(String s) {
        Matcher matcher = PATTERN.matcher(s);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // Convert text before the current bracket
            String beforeBracket = s.substring(lastEnd, matcher.start());
            result.append(ZhConverterUtil.toTraditional(beforeBracket));
            // Preserve text inside the bracket
            result.append(matcher.group());
            lastEnd = matcher.end();
        }

        // Convert remaining text after the last bracket
        String remaining = s.substring(lastEnd);
        result.append(ZhConverterUtil.toTraditional(remaining));

        return result.toString();
    }

    public static @NotNull String toSimplifiedVar(String s) {
        // STUB: whatever, it works
        if (!s.isEmpty() && s.charAt(0) == '「')
            return ZhConverterUtil.toSimple(s);
        return s;
    }

    public static String bracketOf(String string) {
        return "「" + string + "」";
    }
}
