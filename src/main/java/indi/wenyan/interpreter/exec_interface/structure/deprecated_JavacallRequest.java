package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.exec_interface.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanThreading;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Deprecated
/**
 * Represents the context for calling Java code from Wenyan
 */
@WenyanThreading
@Accessors(fluent = true)
//@Value // since we may want a non-final class
@Getter
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
@AllArgsConstructor
public class deprecated_JavacallRequest {
    /** The Wenyan value acting as 'this' */
    IWenyanValue self;

    /** Arguments passed to the call */
    List<IWenyanValue> args;

    /** The thread executing the call */
    WenyanThread thread;

    /** Handler for execution */
    IExecCallHandler handler;

    public boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException {
        return handler().handle(context, this);
    }
}
