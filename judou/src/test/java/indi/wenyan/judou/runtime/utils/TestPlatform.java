package indi.wenyan.judou.runtime.utils;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.runtime.WenyanRuntime;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;

import java.util.ArrayList;
import java.util.List;

public class TestPlatform implements IWenyanPlatform {
    private final ExecQueue execQueue = new ExecQueue(this);
    public String error = null;
    public List<IWenyanValue> output = new ArrayList<>();

    @Override
    public String getPlatformName() {
        return "test";
    }

    @Override
    public void handleError(String error) {
        if (this.error != null) {
            throw new RuntimeException("trigger error after error");
        }
        this.error = error;
    }

    @Override
    public ExecQueue getExecQueue() {
        return execQueue;
    }

    @Override
    public WenyanRuntime initEnvironment() {
        var baseRuntime = IWenyanPlatform.super.initEnvironment();
        baseRuntime.setVariable("書", new WenyanInlineJavacall((self, args) -> {
            output.addAll(args);
            return WenyanNull.NULL;
        }));
        return baseRuntime;
    }
}
