package indi.wenyan.judou.runtime.test_utils;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;

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
            throw new RuntimeException("trigger error " + error + " after error " + this.error);
        }
        this.error = error;
    }

    @Override
    public ExecQueue getExecQueue() {
        return execQueue;
    }

    @Override
    public WenyanPackage initEnvironment() {
        var baseRuntime = IWenyanPlatform.super.initEnvironment();
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
