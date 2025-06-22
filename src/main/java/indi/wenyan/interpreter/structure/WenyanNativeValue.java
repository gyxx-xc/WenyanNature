package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

// about to Deprecated and change to WenyanValue
public class WenyanNativeValue implements WenyanValue {

    private final WenyanType<?> type;
    private Object value;
    private final boolean isConst;

    public WenyanNativeValue(WenyanType<?> type, Object value, boolean isConst) {
        this.type = type;
        this.value = value;
        this.isConst = isConst;
    }

    public WenyanType<?> type() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isConst() {
        return isConst;
    }

    public static WenyanNativeValue emptyOf(WenyanType<?> type, boolean isConst) throws WenyanException.WenyanTypeException {
        Object value1 = switch (type) {
            case NULL -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_create_empty_of_null").getString());
            case INT -> 0;
            case DOUBLE -> 0.0;
            case BOOL -> false;
            case STRING -> "";
            case LIST -> new WenyanArrayObject();
            case OBJECT, OBJECT_TYPE, FUNCTION -> null;
        };
        return new WenyanNativeValue(type, value1, isConst);
    }

    public static WenyanNativeValue constOf(WenyanNativeValue value) {
        return new WenyanNativeValue(value.type, value.value, true);
    }

    public static WenyanNativeValue varOf(WenyanNativeValue value) {
        return new WenyanNativeValue(value.type, value.value, false);
    }

    // what we need to do these function?
    // 1. wide link
    // 2. required type

    // 1. wide link
    // string <- big_int, double <- int <- bool
    //      ^- list

    // 2. required type
    // downgrade + wide link
    // double -> int
    // ~list -> bool
    // obj -> function (constructor)
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T As(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (this.type == type)
            return (T) this;
        if (this.type == WenyanType.NULL)
            return (T) this;
        if (type == WenyanType.INT) {
            if (this.type == WenyanType.DOUBLE) {
                return (T) new WenyanNativeValue(WenyanType.INT, (int) (double) value, isConst);
            }
            if (this.type == WenyanType.BOOL) {
                return (T) new WenyanNativeValue(WenyanType.INT, (boolean) value ? 1 : 0, isConst);
            }
        }
        if (type == WenyanType.DOUBLE) {
            if (this.type == WenyanType.INT) {
                return (T) new WenyanNativeValue(WenyanType.DOUBLE, (double) (int) value, isConst);
            }
            if (this.type == WenyanType.BOOL) {
                return (T) new WenyanNativeValue(WenyanType.DOUBLE, (boolean) value ? 1.0 : 0.0, isConst);
            }
        }
        if (type == WenyanType.STRING) {
            return (T) new WenyanNativeValue(WenyanType.STRING, this.toString(), isConst);
        }
        if (type == WenyanType.BOOL) {
            if (this.type == WenyanType.INT) {
                return (T) new WenyanNativeValue(WenyanType.BOOL, (int) value != 0, isConst);
            }
            if (this.type == WenyanType.DOUBLE) {
                return (T) new WenyanNativeValue(WenyanType.BOOL, (double) value != 0.0, isConst);
            }
            if (this.type == WenyanType.STRING) {
                return (T) new WenyanNativeValue(WenyanType.BOOL, !((String) value).isEmpty(), isConst);
            }
            if (this.type == WenyanType.LIST) {
                return (T) new WenyanNativeValue(WenyanType.BOOL, !((ArrayList<?>) value).isEmpty(), isConst);
            }
        }
        if (type == WenyanType.FUNCTION) {
            if (this.type == WenyanType.OBJECT_TYPE) {
                return (T) ((WenyanObjectType) this.value).getFunction(WenyanDataParser.CONSTRUCTOR_ID);
            }
        }
        if (type == WenyanType.OBJECT) {
            if (this.type == WenyanType.LIST) {
                return (T) this;
            }
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    public WenyanType<?> widerType(WenyanType<?> type) {
        if (TYPE_CASTING_ORDER.get(this.type) < TYPE_CASTING_ORDER.get(type))
            return this.type;
        return type;
    }

    public WenyanNativeValue not() throws WenyanException.WenyanTypeException {
        // require type bool
        WenyanNativeValue wenyanValue = this.As(WenyanType.BOOL);
        return new WenyanNativeValue(WenyanType.BOOL, !(boolean) wenyanValue.value, true);
    }

    @Override
    public String toString() {
        return switch (type) {
            case WenyanType.NULL -> Component.translatable("type.wenyan_nature.null").getString();
            case WenyanType.INT -> WenyanString((int) value);
            case WenyanType.DOUBLE -> WenyanString((double) value);
            case WenyanType.BOOL -> WenyanString((boolean) value);
            case WenyanType.STRING -> (String) value;
            case WenyanType.LIST -> value.toString();
            case WenyanType.FUNCTION -> WenyanString((FunctionSign) value);
            case WenyanType.OBJECT -> Component.translatable("type.wenyan_nature.object").getString();
            case WenyanType.OBJECT_TYPE -> Component.translatable("type.wenyan_nature.object_type").getString();
        };
    }

    private static String WenyanString(int i) {
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        StringBuilder result = new StringBuilder();
        if (i < 0) {
            result.append("負");
            i = -i;
        }
        for (char digit : Integer.toString(i).toCharArray())
            result.append(numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }

    private static String WenyanString(boolean b) {
        return b ? "陽" : "陰";
    }

    private static String WenyanString(double d) {
        if (d == (int) d) // if it's an integer
            return WenyanString((int) d);
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        String dot = "又";
        StringBuilder result = new StringBuilder();
        if (d < 0) {
            result.append("負");
            d = -d;
        }
        for (char digit : Double.toString(d).toCharArray())
            result.append(digit == '.' ? dot : numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }

    private static String WenyanString(FunctionSign functionSign) {
        StringBuilder sb = new StringBuilder();
        sb.append(functionSign.name()).append("(");
        for (int i = 0; i < functionSign.argTypes().length; i++) {
            sb.append(functionSign.argTypes()[i].toString());
            if (i < functionSign.argTypes().length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
