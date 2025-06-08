package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.runtime.WenyanThread;

/**
 * since a function can be a bytecode or a native function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface WenyanFunction {
    void call(WenyanNativeValue.FunctionSign sign, WenyanNativeValue self,
              WenyanThread thread, int args, boolean noReturn)
            throws WenyanException.WenyanThrowException;
}
