package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.WenyanType;

import java.util.Map;

/**
 * Represents a package (collection of variables/functions) in Wenyan
 *
 * @param variables Map of variable names to values
 */
public record WenyanPackage(Map<String, IWenyanValue> variables) implements IWenyanObject {
    /**
     * Type descriptor for packages
     */
    public static final WenyanType<WenyanPackage> TYPE = new WenyanType<>("package", WenyanPackage.class);

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanThrowException {
        if (!variables.containsKey(name))
            throw new WenyanException("Unknown package attribute: " + name);
        return variables.get(name);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
