package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanProgramCode;
import indi.wenyan.interpreter.structure.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanValue;

public class JavaCallCodeWarper extends WenyanProgramCode {
    public final JavacallHandler handler;

    public JavaCallCodeWarper(JavacallHandler handler) {
        this.handler = handler;
    }

    public record Request(
        WenyanThread thread,
        WenyanValue[] args,
        boolean noReturn,
        JavacallHandler handler
    ) {
        public void handle() throws WenyanException.WenyanThrowException {
            handler.handle(thread, args, noReturn);
            thread.program.readyQueue.add(thread);
            thread.state = WenyanThread.State.READY;
        }
    }
}
