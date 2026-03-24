package indi.wenyan.judou.structure;

import indi.wenyan.judou.structure.values.IWenyanComparable;
import indi.wenyan.judou.structure.values.IWenyanComputable;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanDouble;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.language.JudouTypeText;

/**
 * Represents a type in the Wenyan language
 *
 * @param <T> The Java class corresponding to this Wenyan type
 */
@SuppressWarnings("ClassCanBeRecord") // no it can't
public class WenyanType<T extends IWenyanValue> {

    /** The name of this type */
    private final String name;

    /** The Java class for this type */
    public final Class<T> tClass;

    /**
     * Creates a new type
     *
     * @param name Type name
     * @param tClass Java class for this type
     */
    public WenyanType(String name, Class<T> tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    /**
     * Determines the wider type for computation between two types
     *
     * @param type1 First type
     * @param type2 Second type
     * @return The wider type that can accommodate both types
     */
    public static WenyanType<? extends IWenyanComputable> computeWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanString.TYPE || type2 == WenyanString.TYPE) {
            return WenyanString.TYPE;
        }
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return WenyanInteger.TYPE;
    }

    /**
     * Determines the wider type for comparison between two types
     *
     * @param type1 First type
     * @param type2 Second type
     * @return The wider type that can accommodate comparing both types
     */
    public static WenyanType<? extends IWenyanComparable> compareWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return WenyanInteger.TYPE;
    }

    /**
     * Returns the ordinal value of this type for comparison purposes
     *
     * @return An integer representing the type's position in the type hierarchy
     */
    public int ordinal() {
        return switch (name) {
            // FIXME: refactor me some time
            case "空無" -> 0;
            case "數" -> 1;
            case "爻" -> 2;
            case "言" -> 3;
            case "列" -> 4;
            case "object" -> 5;
            case "object_type" -> 6;
            case "術" -> 7;
            default -> throw new IllegalArgumentException("Unknown WenyanType: " + name);
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
