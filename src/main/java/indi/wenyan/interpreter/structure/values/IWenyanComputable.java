package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;

public interface IWenyanComputable extends IWenyanValue {
    IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue subtract(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue multiply(IWenyanValue other) throws WenyanException.WenyanThrowException;
    IWenyanValue divide(IWenyanValue other) throws WenyanException.WenyanThrowException;
}
