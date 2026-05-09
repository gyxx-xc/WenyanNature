package indi.wenyan.judou.test_utils;

import indi.wenyan.judou.api.exec.structure.IExecQueue;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanLeftValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.WenyanPackage;
import indi.wenyan.judou.exec_interface.ExecQueue;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;

import java.util.ArrayList;
import java.util.List;

public class TestPlatform implements IWenyanPlatform {
    private final IExecQueue execQueue = new ExecQueue(this);
    public String error = null;
    public final List<IWenyanValue> output = new ArrayList<>();

    @Override
    public String getPlatformName() {
        return "test";
    }

    @Override
    public void handleError(String error) {
        if (this.error != null) {
            throw new RuntimeException("trigger error " + error + " after error " + this.error);
        }
        this.error = error;
    }

    @Override
    public IExecQueue getExecQueue() {
        return execQueue;
    }

    public WenyanPackage initEnvironment() {
        var baseRuntime = IWenyanPlatform.initEnvironment();
        baseRuntime.put("書", new WenyanInlineJavacall((_, args) -> {
            output.addAll(args.stream().map(v -> {
                if (v instanceof WenyanLeftValue) return ((WenyanLeftValue) v).getValue();
                return v;
            }).toList());
            return WenyanNull.NULL;
        }));
        return baseRuntime;
    }
}
