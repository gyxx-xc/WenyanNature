package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

/**
 * Represents a package (collection of variables/functions) in Wenyan
 */
public class WenyanPackage implements IWenyanObject {
    /** Type descriptor for packages */
    WenyanType<WenyanPackage> TYPE = new WenyanType<>("package", WenyanPackage.class);

    /** Map of variable names to values */
    @Getter
    private final Map<String, IWenyanValue> variables;

    /**
     * Creates a new package with the specified variables
     * @param variables Map of variable names to values
     */
    public WenyanPackage(Map<String, IWenyanValue> variables) {
        this.variables = variables;
    }

    public Set<String> getAttributeSet() {
        return variables.keySet();
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        if (!variables.containsKey(name))
            throw new WenyanException("Unknown package attribute: " + name);
        return variables.get(name);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
