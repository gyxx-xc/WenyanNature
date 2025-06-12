package indi.wenyan.interpreter.structure;

public interface WenyanValue {
    WenyanNativeValue NULL = new WenyanNativeValue(WenyanType.NULL, null, true);

    WenyanType type();
}
