package indi.wenyan.interpreter.structure;

public interface WenyanComputable extends WenyanValue {
    WenyanValue add(WenyanValue other);
    WenyanValue subtract(WenyanValue other);
    WenyanValue multiply(WenyanValue other);
    WenyanValue divide(WenyanValue other);
}
