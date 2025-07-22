package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;

public interface IWenyanPlatform {
    void accept(JavacallContext context);

    IWenyanFunction getImportFunction();
}
