package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class WenyanValue {

    public enum Type {
        NULL,
        INT, // used as default number type
        DOUBLE,
        BOOL,
        STRING,
        LIST,
        OBJECT, // not implemented
        OBJECT_TYPE,
        FUNCTION;

        @Override
        public String toString() {
            return Component.translatable("type.wenyan_nature." + name().toLowerCase()).getString();
        }
    }

    public static final HashMap<Type, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(Type.STRING, 0);
        put(Type.LIST, 1);
        put(Type.FUNCTION, 1);
        put(Type.OBJECT, 1);
        put(Type.OBJECT_TYPE, 1);
        put(Type.DOUBLE, 2);
        put(Type.INT, 3);
        put(Type.BOOL, 4);
    }};

    private final Type type;
    private Object value;
    private final boolean isConst;

    public static final WenyanValue NULL = new WenyanValue(Type.NULL, null, true);

    public WenyanValue(Type type, Object value, boolean isConst) {
        this.type = type;
        this.value = value;
        this.isConst = isConst;
    }

    public Type getType() {
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

    public static WenyanValue emptyOf(Type type, boolean isConst) throws WenyanException.WenyanTypeException {
        Object value1 = switch (type) {
            case NULL -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_create_empty_of_null").getString());
            case INT -> 0;
            case DOUBLE -> 0.0;
            case BOOL -> false;
            case STRING -> "";
            case LIST -> new WenyanArrayObject();
            case OBJECT, OBJECT_TYPE, FUNCTION -> null;
        };
        return new WenyanValue(type, value1, isConst);
    }

    public static WenyanValue constOf(WenyanValue value) {
        return new WenyanValue(value.type, value.value, true);
    }

    public static WenyanValue varOf(WenyanValue value) {
        return new WenyanValue(value.type, value.value, false);
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
    public WenyanValue casting(Type type) throws WenyanException.WenyanTypeException {
        if (this.type == type)
            return this;
        if (this.type == Type.NULL)
            return this;
        if (type == Type.INT) {
            if (this.type == Type.DOUBLE) {
                return new WenyanValue(Type.INT, (int) (double) value, isConst);
            }
            if (this.type == Type.BOOL) {
                return new WenyanValue(Type.INT, (boolean) value ? 1 : 0, isConst);
            }
        }
        if (type == Type.DOUBLE) {
            if (this.type == Type.INT) {
                return new WenyanValue(Type.DOUBLE, (double) (int) value, isConst);
            }
            if (this.type == Type.BOOL) {
                return new WenyanValue(Type.DOUBLE, (boolean) value ? 1.0 : 0.0, isConst);
            }
        }
        if (type == Type.STRING) {
            return new WenyanValue(Type.STRING, this.toString(), isConst);
        }
        if (type == Type.BOOL) {
            if (this.type == Type.INT) {
                return new WenyanValue(Type.BOOL, (int) value != 0, isConst);
            }
            if (this.type == Type.DOUBLE) {
                return new WenyanValue(Type.BOOL, (double) value != 0.0, isConst);
            }
            if (this.type == Type.STRING) {
                return new WenyanValue(Type.BOOL, !((String) value).isEmpty(), isConst);
            }
            if (this.type == Type.LIST) {
                return new WenyanValue(Type.BOOL, !((ArrayList<?>) value).isEmpty(), isConst);
            }
        }
        if (type == Type.FUNCTION) {
            if (this.type == Type.OBJECT_TYPE) {
                return ((WenyanObjectType) this.value).functions.get("造"); // TODO: change name
            }
        }
        if (type == Type.OBJECT) {
            if (this.type == Type.LIST) {
                return this;
            }
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    public Type widerType(Type type) {
        if (TYPE_CASTING_ORDER.get(this.type) < TYPE_CASTING_ORDER.get(type))
            return this.type;
        return type;
    }

    public WenyanValue add(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        Type addType = widerType(other.widerType(Type.INT)); // widest type in three
        WenyanValue left = this.casting(addType);
        WenyanValue right = other.casting(addType);
        return switch (addType) {
            case INT -> new WenyanValue(addType, (int) left.value + (int) right.value, true);
            case DOUBLE -> new WenyanValue(addType, (double) left.value + (double) right.value, true);
            case STRING -> new WenyanValue(addType, left.value.toString() + right.value.toString(), true);
            // change self if it is a list
            case LIST -> {
                this.value = ((WenyanArrayObject) left.value).concat((WenyanArrayObject) right.value);
                yield this;
            }
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_added").getString());
        };
    }

    public WenyanValue sub(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        Type subType = widerType(other.widerType(Type.INT)); // widest type in three
        WenyanValue left = this.casting(subType);
        WenyanValue right = other.casting(subType);
        return switch (subType) {
            case INT -> new WenyanValue(subType, (int) left.value - (int) right.value, true);
            case DOUBLE -> new WenyanValue(subType, (double) left.value - (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_subtracted").getString());
        };
    }

    public WenyanValue mul(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        Type mulType = widerType(other.widerType(Type.INT)); // widest type in three
        WenyanValue left = this.casting(mulType);
        WenyanValue right = other.casting(mulType);
        return switch (mulType) {
            case INT -> new WenyanValue(mulType, (int) left.value * (int) right.value, true);
            case DOUBLE -> new WenyanValue(mulType, (double) left.value * (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_multiplied").getString());
        };
    }

    public WenyanValue div(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type double
        Type divType = widerType(other.widerType(Type.DOUBLE)); // widest type in three
        WenyanValue left = this.casting(divType);
        WenyanValue right = other.casting(divType);
        if (divType == Type.DOUBLE) {
            return new WenyanValue(divType, (double) left.value / (double) right.value, true);
        } else {
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_divided").getString());
        }
    }

    public WenyanValue not() throws WenyanException.WenyanTypeException {
        // require type bool
        WenyanValue wenyanValue = this.casting(Type.BOOL);
        return new WenyanValue(Type.BOOL, !(boolean) wenyanValue.value, true);
    }

    public WenyanValue mod(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        Type modType = widerType(other.widerType(Type.INT)); // widest type in three
        WenyanValue left = this.casting(modType);
        WenyanValue right = other.casting(modType);
        return switch (modType) {
            case INT -> new WenyanValue(modType, (int) left.value % (int) right.value, true);
            case DOUBLE -> new WenyanValue(modType, (double) left.value % (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_mod").getString());
        };
    }

    public boolean equals(WenyanValue other) throws WenyanException.WenyanTypeException {
        Type eqType = widerType(other.type);
        WenyanValue left, right;
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

    public int compareTo(WenyanValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        Type cmpType = widerType(other.widerType(Type.INT)); // widest type in three
        WenyanValue left = this.casting(cmpType);
        WenyanValue right = other.casting(cmpType);
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

    public record FunctionSign(String name, Type[] argTypes, WenyanProgramCode bytecode) {}
}
