package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;

// about to Deprecated and change to WenyanValue
public class WenyanNativeValue implements WenyanValue {

    public static final HashMap<WenyanType, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(WenyanType.STRING, 0);
        put(WenyanType.LIST, 1);
        put(WenyanType.FUNCTION, 1);
        put(WenyanType.OBJECT, 1);
        put(WenyanType.OBJECT_TYPE, 1);
        put(WenyanType.DOUBLE, 2);
        put(WenyanType.INT, 3);
        put(WenyanType.BOOL, 4);
    }};

    private final WenyanType type;
    private Object value;
    private final boolean isConst;

    public WenyanNativeValue(WenyanType type, Object value, boolean isConst) {
        this.type = type;
        this.value = value;
        this.isConst = isConst;
    }

    public WenyanType type() {
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

    public static WenyanNativeValue emptyOf(WenyanType type, boolean isConst) throws WenyanException.WenyanTypeException {
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
    public WenyanNativeValue casting(WenyanType type) throws WenyanException.WenyanTypeException {
        if (this.type == type)
            return this;
        if (this.type == WenyanType.NULL)
            return this;
        if (type == WenyanType.INT) {
            if (this.type == WenyanType.DOUBLE) {
                return new WenyanNativeValue(WenyanType.INT, (int) (double) value, isConst);
            }
            if (this.type == WenyanType.BOOL) {
                return new WenyanNativeValue(WenyanType.INT, (boolean) value ? 1 : 0, isConst);
            }
        }
        if (type == WenyanType.DOUBLE) {
            if (this.type == WenyanType.INT) {
                return new WenyanNativeValue(WenyanType.DOUBLE, (double) (int) value, isConst);
            }
            if (this.type == WenyanType.BOOL) {
                return new WenyanNativeValue(WenyanType.DOUBLE, (boolean) value ? 1.0 : 0.0, isConst);
            }
        }
        if (type == WenyanType.STRING) {
            return new WenyanNativeValue(WenyanType.STRING, this.toString(), isConst);
        }
        if (type == WenyanType.BOOL) {
            if (this.type == WenyanType.INT) {
                return new WenyanNativeValue(WenyanType.BOOL, (int) value != 0, isConst);
            }
            if (this.type == WenyanType.DOUBLE) {
                return new WenyanNativeValue(WenyanType.BOOL, (double) value != 0.0, isConst);
            }
            if (this.type == WenyanType.STRING) {
                return new WenyanNativeValue(WenyanType.BOOL, !((String) value).isEmpty(), isConst);
            }
            if (this.type == WenyanType.LIST) {
                return new WenyanNativeValue(WenyanType.BOOL, !((ArrayList<?>) value).isEmpty(), isConst);
            }
        }
        if (type == WenyanType.FUNCTION) {
            if (this.type == WenyanType.OBJECT_TYPE) {
                return ((WenyanObjectType) this.value).getFunction(WenyanDataParser.CONSTRUCTOR_ID);
            }
        }
        if (type == WenyanType.OBJECT) {
            if (this.type == WenyanType.LIST) {
                return this;
            }
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    public WenyanType widerType(WenyanType type) {
        if (TYPE_CASTING_ORDER.get(this.type) < TYPE_CASTING_ORDER.get(type))
            return this.type;
        return type;
    }

    public WenyanNativeValue add(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType addType = widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = this.casting(addType);
        WenyanNativeValue right = other.casting(addType);
        return switch (addType) {
            case INT -> new WenyanNativeValue(addType, (int) left.value + (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(addType, (double) left.value + (double) right.value, true);
            case STRING -> new WenyanNativeValue(addType, left.value.toString() + right.value.toString(), true);
            // change self if it is a list
            case LIST -> {
                this.value = ((WenyanArrayObject) left.value).concat((WenyanArrayObject) right.value);
                yield this;
            }
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_added").getString());
        };
    }

    public WenyanNativeValue sub(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType subType = widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = this.casting(subType);
        WenyanNativeValue right = other.casting(subType);
        return switch (subType) {
            case INT -> new WenyanNativeValue(subType, (int) left.value - (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(subType, (double) left.value - (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_subtracted").getString());
        };
    }

    public WenyanNativeValue mul(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType mulType = widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = this.casting(mulType);
        WenyanNativeValue right = other.casting(mulType);
        return switch (mulType) {
            case INT -> new WenyanNativeValue(mulType, (int) left.value * (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(mulType, (double) left.value * (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_multiplied").getString());
        };
    }

    public WenyanNativeValue div(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type double
        WenyanType divType = widerType(other.widerType(WenyanType.DOUBLE)); // widest type in three
        WenyanNativeValue left = this.casting(divType);
        WenyanNativeValue right = other.casting(divType);
        if (divType == WenyanType.DOUBLE) {
            return new WenyanNativeValue(divType, (double) left.value / (double) right.value, true);
        } else {
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_divided").getString());
        }
    }

    public WenyanNativeValue not() throws WenyanException.WenyanTypeException {
        // require type bool
        WenyanNativeValue wenyanValue = this.casting(WenyanType.BOOL);
        return new WenyanNativeValue(WenyanType.BOOL, !(boolean) wenyanValue.value, true);
    }

    public WenyanNativeValue mod(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType modType = widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = this.casting(modType);
        WenyanNativeValue right = other.casting(modType);
        return switch (modType) {
            case INT -> new WenyanNativeValue(modType, (int) left.value % (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(modType, (double) left.value % (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_mod").getString());
        };
    }

    public boolean equals(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType eqType = widerType(other.type);
        WenyanNativeValue left, right;
        try {
            left = this.casting(eqType);
            right = other.casting(eqType);
        } catch (WenyanException.WenyanTypeException e) {
            return false;
        }
        return switch (eqType) {
            case INT -> (int) left.value == (int) right.value;
            case DOUBLE -> (double) left.value == (double) right.value;
            case BOOL -> (boolean) left.value == (boolean) right.value;
            case STRING -> left.value.equals(right.value);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_compared").getString());
        };
    }

    public int compareTo(WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType cmpType = widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = this.casting(cmpType);
        WenyanNativeValue right = other.casting(cmpType);
        return switch (cmpType) {
            case INT -> Integer.compare((int) left.value, (int) right.value);
            case DOUBLE -> Double.compare((double) left.value, (double) right.value);
            case STRING -> left.value.toString().compareTo(right.value.toString());
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_compared").getString());
        };
    }

    @Override
    public String toString() {

        return switch (type) {
            case NULL -> Component.translatable("type.wenyan_nature.null").getString();
            case INT -> WenyanString((int) value);
            case DOUBLE -> WenyanString((double) value);
            case BOOL -> WenyanString((boolean) value);
            case STRING -> (String) value;
            case LIST -> value.toString();
            case FUNCTION -> WenyanString((FunctionSign) value);
            case OBJECT -> Component.translatable("type.wenyan_nature.object").getString();
            case OBJECT_TYPE -> Component.translatable("type.wenyan_nature.object_type").getString();
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
        sb.append(functionSign.name).append("(");
        for (int i = 0; i < functionSign.argTypes.length; i++) {
            sb.append(functionSign.argTypes[i].toString());
            if (i < functionSign.argTypes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public record FunctionSign(String name, WenyanType[] argTypes, WenyanFunction function) {}
}
