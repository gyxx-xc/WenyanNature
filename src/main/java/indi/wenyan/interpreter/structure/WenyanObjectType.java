package indi.wenyan.interpreter.structure;

public interface WenyanObjectType {
    String getName();

    WenyanObjectType getParent();

    WenyanNativeValue getFunction(String id);

    void addFunction(String id, WenyanNativeValue function);

    WenyanNativeValue getStaticVariable(String id);

    void addStaticVariable(String id, WenyanNativeValue value);

    default WenyanType type() {
        return WenyanType.OBJECT_TYPE;
    }
}
