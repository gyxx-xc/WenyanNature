package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

public final class WenyanType<T extends WenyanValue> {
    public static final WenyanType<WenyanNativeValue> NULL = new WenyanType<>("null");
    public static final WenyanType<WenyanNativeValue> INT = new WenyanType<>("int");
    public static final WenyanType<WenyanNativeValue> DOUBLE = new WenyanType<>("double");
    public static final WenyanType<WenyanNativeValue> BOOL = new WenyanType<>("bool");
    public static final WenyanType<WenyanNativeValue> STRING = new WenyanType<>("string");
    public static final WenyanType<WenyanNativeValue> LIST = new WenyanType<>("list");
    public static final WenyanType<WenyanNativeValue> OBJECT = new WenyanType<>("object");
    public static final WenyanType<WenyanNativeValue> OBJECT_TYPE = new WenyanType<>("object_type");
    public static final WenyanType<WenyanNativeValue> FUNCTION = new WenyanType<>("function");

    private final String name;

    private WenyanType(String name) {
        this.name = name;
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
