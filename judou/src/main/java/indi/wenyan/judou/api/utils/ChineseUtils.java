package indi.wenyan.judou.api.utils;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import indi.wenyan.judou.utils.NumberChineseFormatter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.List;

/// Number-to-Chinese conversion, simplified/traditional conversion, and bracket formatting.
public enum ChineseUtils {
    ;
    public static final boolean DIRECT_NUMBER_CONVERT = false;

    private static final char[] WENYAN_NUMBERS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static @NotNull String toChinese(BigInteger i) {
        if (!DIRECT_NUMBER_CONVERT) {
            try {
                return NumberChineseFormatter.format(i.intValueExact());
            } catch (ArithmeticException ignored) { // go outward
            }
        }
        StringBuilder chinese = new StringBuilder();
        var number = i;
        while (number.compareTo(BigInteger.ZERO) > 0) {
            int digit = number.mod(BigInteger.valueOf(10)).intValue();
            chinese.insert(0, NumberChineseFormatter.DIGITS[digit]);
            number = number.divide(BigInteger.valueOf(10));
        }
        if (i.compareTo(BigInteger.ZERO) < 0) {
            chinese.insert(0, "負");
        }
        return chinese.toString();
    }

    public static final List<String> WENYAN_FRACTIONS = List.of("分", "釐", "毫", "絲", "忽", "微", "纖", "沙", "塵");

    public static @NotNull String toChinese(double value) {
        StringBuilder number = new StringBuilder();
        if (value < 0) number.append("負");
        number.append(toChinese (new BigInteger(String.valueOf(Math.abs((long) value)))));
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

    public static @NotNull String toSimplifiedVar(String s) {
        // STUB: whatever, it works
        if (!s.isEmpty() && s.charAt(0) == '「')
            return ZhConverterUtil.toSimple(s);
        return s;
    }

    public static String bracketOf(String string) {
        return "「" + string + "」";
    }


    public sealed interface SymbolFormat permits SymbolFormat.Both, SymbolFormat.Simplified, SymbolFormat.Traditional {
        enum EnumWarper {
            BOTH(new SymbolFormat.Both()),
            SIMPLIFIED(new SymbolFormat.Simplified()),
            TRADITIONAL(new SymbolFormat.Traditional());

            @Getter final SymbolFormat symbolFormat;

            EnumWarper(SymbolFormat symbolFormat) {
                this.symbolFormat = symbolFormat;
            }
        }

        List<String> names(String name, String simplifiedName);

        record Traditional() implements SymbolFormat {
            @Override
            public List<String> names(String name, String simplifiedName) {
                return List.of(name);
            }
        }

        record Both() implements SymbolFormat {
            @Override
            public List<String> names(String name, String simplifiedName) {
                return List.of(name, simplifiedName);
            }
        }

        record Simplified() implements SymbolFormat {
            @Override
            public List<String> names(String name, String simplifiedName) {
                return List.of(simplifiedName);
            }
        }
    }
}
