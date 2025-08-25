package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;

/**
 * Interface representing a platform that can execute Wenyan code and send
 * execute command to Wenyan devices
 */
public interface IWenyanPlatform {
    /**
     * Accepts and processes a JavacallContext, and execute or forwards it to the appropriate device
     * @param context The context to process
     */
    void accept(JavacallContext context);

    /**
     * Initializes the platform environment for the Wenyan runtime
     * @param baseEnvironment The base runtime environment
     */
    void initEnvironment(WenyanRuntime baseEnvironment);
}
