package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.structure.WenyanException;

public interface IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException;
}
