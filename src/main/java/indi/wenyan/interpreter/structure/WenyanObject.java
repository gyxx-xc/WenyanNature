package indi.wenyan.interpreter.structure;

public interface WenyanObject extends WenyanValue {
    WenyanNativeValue getVariable(String name);

    void setVariable(String name, WenyanNativeValue value);

    WenyanNativeValue getFunction(String name);

    WenyanObjectType getType();

    default WenyanType type() {
        return WenyanType.OBJECT;
    }
}
