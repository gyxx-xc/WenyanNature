package indi.wenyan.interpreter.structure;

public interface WenyanObject {
    WenyanValue getVariable(String name);

    void setVariable(String name, WenyanValue value);

    WenyanValue getFunction(String name);

    WenyanObjectType getType();
}
