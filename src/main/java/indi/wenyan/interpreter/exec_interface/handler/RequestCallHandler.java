package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.structure.JavacallRequest;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanThreading;

import java.util.List;
import java.util.function.Supplier;

public class RequestCallHandler
        implements IJavacallHandler {
    private final IWenyanPlatform platform;
    private final IWenyanDevice device;
    private final Supplier<JavacallRequest.IRawRequest> newRawRequest;

    public RequestCallHandler(IWenyanPlatform platform, IWenyanDevice device,
                              Supplier<JavacallRequest.IRawRequest> newRawRequest) {
        this.platform = platform;
        this.device = device;
        this.newRawRequest = newRawRequest;
    }

    JavacallRequest newRequest(WenyanThread thread, IWenyanValue self,
                               List<IWenyanValue> argsList) {
        return new JavacallRequest(platform, device, thread, newRawRequest.get(), self, argsList);
    }

    @Override
    @WenyanThreading
    public void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList) {
        thread.program.platform.receive(newRequest(thread, self, argsList));
        thread.block();
    }
}
