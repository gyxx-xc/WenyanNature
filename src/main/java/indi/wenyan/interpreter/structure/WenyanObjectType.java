package indi.wenyan.interpreter.structure;

public interface WenyanObjectType {
    String getName();

    WenyanObjectType getParent();

    WenyanValue getFunction(String id);

    void addFunction(String id, WenyanValue function);

    WenyanValue getStaticVariable(String id);

    void addStaticVariable(String id, WenyanValue value);
}
