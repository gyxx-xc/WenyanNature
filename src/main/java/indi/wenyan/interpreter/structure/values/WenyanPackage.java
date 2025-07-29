package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import lombok.Getter;

import java.util.Map;

public class WenyanPackage implements IWenyanObject {
    WenyanType<WenyanPackage> TYPE = new WenyanType<>("package", WenyanPackage.class);

    @Getter
    private final Map<String, IWenyanValue> variables;

    public WenyanPackage(Map<String, IWenyanValue> variables) {
        this.variables = variables;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        if (!variables.containsKey(name))
            throw new WenyanException("Unknown package attribute: " + name);
        return variables.get(name);
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException("Cannot set variable on package: " + name);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
