package indi.wenyan.judou.api;

import indi.wenyan.judou.api.values.IWenyanComparable;
import indi.wenyan.judou.api.values.IWenyanComputable;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.primitive.WenyanDouble;
import indi.wenyan.judou.api.values.primitive.WenyanString;

/// Represents a type in the Wenyan language
///
/// @param <T> The Java class corresponding to this Wenyan type
public class WenyanType<T extends IWenyanValue> {

    /// The name of this type
    private final String name;

    /// The Java class for this type
    public final Class<T> tClass;

    /// Creates a new type
    ///
    /// @param name   Type name
    /// @param tClass Java class for this type
    public WenyanType(String name, Class<T> tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    /// Determines the wider type for computation between two types
    ///
    /// @param type1 First type
    /// @param type2 Second type
    /// @return The wider type that can accommodate both types
    public static WenyanType<? extends IWenyanComputable> computeWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanString.TYPE || type2 == WenyanString.TYPE) {
            return WenyanString.TYPE;
        }
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return IWenyanComputable.TYPE;
    }

    /// Determines the wider type for comparison between two types
    ///
    /// @param type1 First type
    /// @param type2 Second type
    /// @return The wider type that can accommodate comparing both types
    public static WenyanType<? extends IWenyanComparable> compareWiderType(WenyanType<?> type1, WenyanType<?> type2) {
        if (type1 == WenyanDouble.TYPE || type2 == WenyanDouble.TYPE) {
            return WenyanDouble.TYPE;
        }
        return IWenyanComparable.TYPE;
    }

    @Override
    public String toString() {
        return name;
    }
}
