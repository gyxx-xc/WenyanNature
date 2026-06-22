package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.runtime.IGlobalResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/// Represents a package (collection of variables/functions) in Wenyan
///
/// @param variables Map of variable names to values
public record WenyanPackage(Map<String, IWenyanValue> variables) implements IWenyanObject, IGlobalResolver {
    /// Type descriptor for packages
    public static final WenyanType<WenyanPackage> TYPE = new WenyanType<>(JudouTypeText.Package.string(), WenyanPackage.class);

    public void combine(@NotNull WenyanPackage other) {
        variables.putAll(other.variables);
    }

    public void put(String name, IWenyanValue value) {
        variables.put(name, value);
    }

    @Override
    public IWenyanValue getGlobal(String name) throws WenyanException {
        return getAttribute(name);
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        var value = variables.get(name);
        if (value == null)
            throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
        return value;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
