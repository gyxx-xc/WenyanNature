package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.structure.values.*;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class WenyanType<T extends WenyanValue> {
    public static final HashMap<WenyanType<?>, Integer> TYPE_CASTING_ORDER = new HashMap<>() {{
        put(WenyanString.TYPE, 0);
        put(WenyanArrayObject.TYPE, 1);
        put(WenyanFunction.TYPE, 1);
        put(WenyanObject.TYPE, 1);
        put(WenyanObjectType.TYPE, 1);
        put(WenyanDouble.TYPE, 2);
        put(WenyanInteger.TYPE, 3);
        put(WenyanBoolean.TYPE, 4);
    }};

    private final String name;
    public final Class<T> tClass;

    public WenyanType(String name, Class<T> tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    // TODO
    public static WenyanType<? extends WenyanComputable> computeWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        return null;
    }

    public static WenyanType<? extends WenyanComparable> compareWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        return null;
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
