package indi.wenyan.interpreter.structure;

public interface WenyanObject {
    WenyanNativeValue getVariable(String name);

    void setVariable(String name, WenyanNativeValue value);

    WenyanNativeValue getFunction(String name);

    WenyanObjectType getType();
}
