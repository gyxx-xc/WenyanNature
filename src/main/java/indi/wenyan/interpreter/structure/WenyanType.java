package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.structure.values.WenyanDouble;
import indi.wenyan.interpreter.structure.values.WenyanInteger;
import indi.wenyan.interpreter.structure.values.WenyanString;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import net.minecraft.network.chat.Component;

public class WenyanType<T extends WenyanValue> {

    private final String name;
    public final Class<T> tClass;

    public WenyanType(String name, Class<T> tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    // TODO
    public static WenyanType<? extends WenyanComputable> computeWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanString.TYPE || type2 == WenyanString.TYPE) {
            return WenyanString.TYPE;
        }
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return WenyanInteger.TYPE;
    }

    public static WenyanType<? extends WenyanComparable> compareWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return WenyanInteger.TYPE;
    }

    public int ordinal() {
        return switch (name) {
            case "null" -> 0;
            case "int" -> 1;
            case "double" -> 2;
            case "bool" -> 3;
            case "string" -> 4;
            case "list" -> 5;
            case "object" -> 6;
            case "object_type" -> 7;
            case "function" -> 8;
            default -> throw new IllegalArgumentException("Unknown WenyanType: " + name);
        };
    }

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_nature." + name.toLowerCase()).getString();
    }
}
