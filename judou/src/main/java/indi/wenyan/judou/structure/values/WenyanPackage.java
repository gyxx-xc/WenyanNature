package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.runtime.function_impl.IGlobalResolver;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a package (collection of variables/functions) in Wenyan
 *
 * @param variables Map of variable names to values
 */
public record WenyanPackage(Map<String, IWenyanValue> variables) implements IWenyanObjectType, IGlobalResolver {
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
            throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
        return value;
    }

    @Override
    public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException {
        throw new WenyanException(JudouExceptionText.CannotCreateObject.string());
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
