package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.runtime.WenyanThread;

import java.util.List;

/**
 * since a function can be a bytecode or a native function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface WenyanFunction {
    void call(WenyanNativeValue self, WenyanThread thread,
              List<WenyanNativeValue> argsList)
            throws WenyanException.WenyanThrowException;
}
