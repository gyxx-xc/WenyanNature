package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.HashMap;

public interface WenyanValue {
    WenyanNativeValue NULL = new WenyanNativeValue(WenyanType.NULL, null, true);
    HashMap<WenyanType<?>, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(WenyanType.STRING, 0);
        put(WenyanType.LIST, 1);
        put(WenyanType.FUNCTION, 1);
        put(WenyanType.OBJECT, 1);
        put(WenyanType.OBJECT_TYPE, 1);
        put(WenyanType.DOUBLE, 2);
        put(WenyanType.INT, 3);
        put(WenyanType.BOOL, 4);
    }};

    static WenyanNativeValue add(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType addType = self.widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = self.casting(addType);
        WenyanNativeValue right = other.casting(addType);
        return switch (addType) {
            case INT -> new WenyanNativeValue(addType, (int) left.value + (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(addType, (double) left.value + (double) right.value, true);
            case STRING -> new WenyanNativeValue(addType, left.value.toString() + right.value.toString(), true);
            // change self if it is a list
            case LIST -> {
                self.value = ((WenyanArrayObject) left.value).concat((WenyanArrayObject) right.value);
                yield self;
            }
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_added").getString());
        };
    }

    static WenyanNativeValue sub(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType subType = self.widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = self.casting(subType);
        WenyanNativeValue right = other.casting(subType);
        return switch (subType) {
            case INT -> new WenyanNativeValue(subType, (int) left.value - (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(subType, (double) left.value - (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_subtracted").getString());
        };
    }

    static WenyanNativeValue mul(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType mulType = self.widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = self.casting(mulType);
        WenyanNativeValue right = other.casting(mulType);
        return switch (mulType) {
            case INT -> new WenyanNativeValue(mulType, (int) left.value * (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(mulType, (double) left.value * (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_multiplied").getString());
        };
    }

    static WenyanNativeValue div(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type double
        WenyanType divType = self.widerType(other.widerType(WenyanType.DOUBLE)); // widest type in three
        WenyanNativeValue left = self.casting(divType);
        WenyanNativeValue right = other.casting(divType);
        if (divType == WenyanType.DOUBLE) {
            return new WenyanNativeValue(divType, (double) left.value / (double) right.value, true);
        } else {
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_divided").getString());
        }
    }

    static WenyanNativeValue mod(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType modType = self.widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = self.casting(modType);
        WenyanNativeValue right = other.casting(modType);
        return switch (modType) {
            case INT -> new WenyanNativeValue(modType, (int) left.value % (int) right.value, true);
            case DOUBLE -> new WenyanNativeValue(modType, (double) left.value % (double) right.value, true);
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_mod").getString());
        };
    }

    static boolean equals(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        WenyanType eqType = self.widerType(other.type);
        WenyanNativeValue left, right;
        try {
            left = self.casting(eqType);
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

    static int compareTo(WenyanNativeValue self, WenyanNativeValue other) throws WenyanException.WenyanTypeException {
        // require type >int
        WenyanType cmpType = self.widerType(other.widerType(WenyanType.INT)); // widest type in three
        WenyanNativeValue left = self.casting(cmpType);
        WenyanNativeValue right = other.casting(cmpType);
        return switch (cmpType) {
            case INT -> Integer.compare((int) left.value, (int) right.value);
            case DOUBLE -> Double.compare((double) left.value, (double) right.value);
            case STRING -> left.value.toString().compareTo(right.value.toString());
            default -> throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.type_cannot_be_compared").getString());
        };
    }

    WenyanType<?> type();

    record FunctionSign(String name, WenyanType<?>[] argTypes, WenyanFunction function) {}
}
