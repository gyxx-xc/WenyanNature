package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanProgramCode;

public class JavaCallCodeWarper extends WenyanProgramCode {
    public final JavacallHandler handler;

    public JavaCallCodeWarper(JavacallHandler handler) {
        this.handler = handler;
    }
}
