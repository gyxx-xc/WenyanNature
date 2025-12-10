package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.IImportHandler;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    ImportExecQueue getImportExecQueue();
    default void receiveImport(IImportHandler.ImportContext context) {
        getImportExecQueue().receive(context);
    }
    default void handleImport() {
        getImportExecQueue().handle();
    }

    class ImportExecQueue {
        private final Queue<IImportHandler.ImportContext> queue = new ConcurrentLinkedQueue<>();

        public void receive(IImportHandler.ImportContext context) {
            queue.add(context);
        }

        public void handle() {
            while (!queue.isEmpty()) {
                IImportHandler.ImportContext context = queue.remove();
                try {
                    context.handler().handleImport(context);
                    WenyanProgram.unblock(context.thread());
                } catch (Exception e) {
                    context.thread().dieWithException(e);
                }
            }
        }
    }
}
