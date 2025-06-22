package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.structure.values.WenyanValue;

public interface WenyanComputable extends WenyanValue {
    WenyanValue add(WenyanValue other) throws WenyanException.WenyanThrowException;
    WenyanValue subtract(WenyanValue other) throws WenyanException.WenyanThrowException;
    WenyanValue multiply(WenyanValue other) throws WenyanException.WenyanThrowException;
    WenyanValue divide(WenyanValue other) throws WenyanException.WenyanThrowException;
}
