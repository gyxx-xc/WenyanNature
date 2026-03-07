package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a package (collection of variables/functions) in Wenyan
 *
 * @param variables Map of variable names to values
 */
public record WenyanPackage(Map<String, IWenyanValue> variables) implements IWenyanObjectType {
    /**
     * Type descriptor for packages
     */
    public static final WenyanType<WenyanPackage> TYPE = new WenyanType<>("package", WenyanPackage.class);

    public void combine(@NotNull WenyanPackage other) {
        variables.putAll(other.variables);
    }

    public void put(String name, IWenyanValue value) {
        variables.put(name, value);
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        var value = variables.get(name);
        if (value == null)
            throw new WenyanException("Unknown package attribute: " + name);
        return value;
    }

    @Override
    public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException {
        throw new WenyanException("Cannot create an object from a package");
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
