package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.structure.values.IWenyanValue;

public interface IWenyanComputable extends IWenyanValue {
    IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue subtract(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue multiply(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue divide(IWenyanValue other) throws WenyanException.WenyanThrowException;
}
