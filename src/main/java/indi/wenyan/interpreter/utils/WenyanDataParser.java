package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.HashSet;

public final class WenyanDataParser {
    private WenyanDataParser(){}

    //Todo: translate to CHS
    public static final String PARENT_ID = "父";
    public static final String SELF_ID = "己";
    public static final String LONG_ID = "長";
    public static final String ARRAY_GET_ID = "取";
    public static final String CONSTRUCTOR_ID = "造";
    public static final String ITER_ID = "循";

    public static final HashMap<String, Integer> DIGIT = new HashMap<>() {{
        put("〇", 0);
        put("又", 0);
        put("零", 0);
        put("一", 1);
        put("二", 2);
        put("三", 3);
        put("四", 4);
        put("五", 5);
        put("六", 6);
        put("七", 7);
        put("八", 8);
        put("九", 9);
    }};

    public static final HashMap<String, Integer> EXP = new HashMap<>() {{
        put("十", 1);
        put("百", 2);
        put("千", 3);
        put("萬", 4);
        put("億", 8);
        put("兆", 12);
        put("京", 16);
        put("垓", 20);
        put("秭", 24);
        put("穰", 28);
        put("溝", 32);
        put("澗", 36);
        put("正", 40);
        put("載", 44);
        put("極", 48);

        //CHS
        put("万", 4);
        put("亿", 8);
        put("沟", 32);
        put("涧", 36);
        put("载", 44);
        put("极", 48);

    }};

    public static final HashSet<String> SIGN = new HashSet<>() {{
        add("負");
        add("负");
    }};

    public static final HashSet<String> FLOAT_DIVISION = new HashSet<>() {{
        add("又");
    }};

    public static final HashMap<String, Integer> FLOAT_EXP = new HashMap<>() {{
        put("分", -1);
        put("釐", -2);
        put("毫", -3);
        put("絲", -4);
        put("忽", -5);
        put("微", -6);
        put("纖", -7);
        put("沙", -8);
        put("塵", -9);
        put("埃", -10);
        put("渺", -11);
        put("漠", -12);

        put("厘", -2);
        put("丝", -4);
        put("纤", -7);
        put("尘", -9);

    }};

    private static final HashMap<String, Boolean> BOOL_MAP = new HashMap<>() {{
        put("陰", false);
        put("陽", true);

        put("阴", false);
        put("阳", true);
    }};

    private static final HashMap<String, WenyanType> TYPE_MAP = new HashMap<>() {{
        put("爻", WenyanType.BOOL);
        put("數", WenyanType.DOUBLE);
        put("言", WenyanType.STRING);
        put("列", WenyanType.LIST);

        put("数", WenyanType.DOUBLE);
    }};

    public static int parseInt(String text) throws WenyanException.WenyanNumberException {
        if (SIGN.contains(text.substring(0, 1)))
            return -parseInt(text.substring(1));
        Num num = parseIntHelper(text);
        int n;
        try{
            n = Integer.parseInt(num.num + "0".repeat(num.exp));
        } catch (NumberFormatException e) {
            throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.invalid_number").getString());
        }
        return n;
    }

    public static double parseFloat(String text) throws WenyanException.WenyanNumberException, NumberFormatException {
        for (String div : FLOAT_DIVISION) {
            if (text.contains(div)) {
                String[] parts = text.split(div);
                if (parts.length != 2)
                    throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.invalid_float_number").getString());
                // parts 1
                double result = parseInt(parts[0]);
                // parts 2 (Int FLOAT_EXP)+
                int last = 0;
                for (int i = 0; i < parts[1].length(); i ++) {
                    if (FLOAT_EXP.containsKey(parts[1].substring(i, i+1))) {
                        result += Double.parseDouble(parseInt(parts[1].substring(last, i))
                                +"e"+FLOAT_EXP.get(parts[1].substring(i, i+1)));
                        last = i + 1;
                    }
                }
                return result;
            }
        }
        throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.invalid_float_number").getString());
    }

    public static boolean parseBool(String text) throws WenyanException.WenyanDataException {
        if (BOOL_MAP.containsKey(text))
            return BOOL_MAP.get(text);
        else
            throw new WenyanException.WenyanDataException(Component.translatable("error.wenyan_nature.invalid_bool_value").getString());
    }

    public static String parseString(String text) {
        return text.substring(2, text.length() - 2);
    }

    public static WenyanType parseType(String text) throws WenyanException.WenyanDataException {
        if (TYPE_MAP.containsKey(text))
            return TYPE_MAP.get(text);
        else
            throw new WenyanException.WenyanDataException(Component.translatable("error.wenyan_nature.invalid_data_type").getString());
    }

    private static Num parseIntHelper(String num) throws WenyanException.WenyanNumberException {
        if (num.isEmpty())
            return new Num("", 0);
        String lastChar = num.substring(num.length() - 1);
        if (EXP.containsKey(lastChar)) {
            // case of (num EXP num EXP) 一萬一百
            for (int i = num.length() - 2; i >= 0; i--) {
                if (EXP.containsKey(num.substring(i, i+1))
                        && EXP.get(num.substring(i, i+1)) > EXP.get(lastChar)) {

                    Num l = parseIntHelper(num.substring(0, i+1));
                    Num r;
                    // if (num EXP 0 num EXP) 一萬零一百
                    if (DIGIT.containsKey(num.substring(i+1, i+2))
                            && DIGIT.get(num.substring(i+1, i+2)) == 0)
                        r = parseIntHelper(num.substring(i+2));
                    else
                        r = parseIntHelper(num.substring(i+1));
                    return l.add(r);
                }
            }
            // case of (EXP) 萬
            if (num.length() == 1)
                return new Num("1", EXP.get(lastChar));
            // case of (num EXP) 一百萬
            return parseIntHelper(num.substring(0, num.length() - 1)).shift(EXP.get(lastChar));
        } else if (DIGIT.containsKey(lastChar)) {
            // case of (num EXP num EXP) 一萬一
            for (int i = num.length() - 2; i >= 0; i--) {
                if (EXP.containsKey(num.substring(i, i+1))) {
                    Num l = parseIntHelper(num.substring(0, i+1));
                    Num r;
                    // if (num EXP 0 num EXP) 一萬零一
                    if (DIGIT.containsKey(num.substring(i+1, i+2))
                            && DIGIT.get(num.substring(i+1, i+2)) == 0)
                        r = parseIntHelper(num.substring(i+2));
                    else
                        r = parseIntHelper(num.substring(i+1));
                    return l.add(r);
                }
            }
            // case of (num) 一
            StringBuilder res = new StringBuilder();
            boolean zero = true;
            for (int i = 0; i < num.length(); i++) {
                if (!DIGIT.containsKey(num.substring(i, i+1)))
                    throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.unexpected_character").getString());
                if (zero && DIGIT.get(num.substring(i, i+1)) != 0)
                    zero = false;
                if (!zero)
                    res.append(DIGIT.get(num.substring(i, i+1)));
            }
            if (zero)
                return new Num("0", 0);
            else
                return new Num(res.toString(), 0);
        } else {
            throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.unexpected_character").getString());
        }
    }

    private record Num(String num, int exp) {

        Num add(Num other) throws WenyanException.WenyanNumberException {
                if (exp - other.exp < other.num.length())
                    throw new WenyanException.WenyanNumberException(Component.translatable("error.wenyan_nature.invalid_number").getString());
                return new Num(
                        num + "0".repeat(exp - other.exp - other.num.length()) + other.num,
                        other.exp);
            }

            Num shift(int exp) {
                if (num.equals("0"))
                    return this;
                else
                    return new Num(this.num, this.exp + exp);
            }
        }
}
