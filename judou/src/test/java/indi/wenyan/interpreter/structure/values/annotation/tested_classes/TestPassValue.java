package indi.wenyan.interpreter.structure.values.annotation.tested_classes;

import indi.wenyan.annotation.WenyanConstructor;
import indi.wenyan.annotation.WenyanField;
import indi.wenyan.annotation.WenyanMethod;
import indi.wenyan.annotation.WenyanObjectValue;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanValues;

import java.util.List;

@WenyanObjectValue
public class TestPassValue {
    @WenyanField("test-field")
    public IWenyanValue testField = WenyanValues.of("test-field");

    @WenyanField("test-method")
    public IWenyanValue testMethod() {
        return WenyanValues.of("test-method");
    }

    @WenyanMethod(value = "test-method-inline", threadSafe = true)
    public IWenyanValue testMethodInline(IWenyanValue ignoredSelf, List<IWenyanValue> ignoredArgs) {
        return WenyanValues.of("test-method-inline");
    }

    @WenyanMethod(value = "test-method-request")
    public IWenyanValue testMethodRequest(IWenyanValue ignoredSelf, List<IWenyanValue> ignoredArgs) {
        return WenyanValues.of("test-method-request");
    }

    @WenyanField("test-field")
    public static IWenyanValue testFieldStat = WenyanValues.of("test-field");

    @WenyanField("test-method")
    public static IWenyanValue testMethodStat() {
        return WenyanValues.of("test-method");
    }

    @WenyanMethod(value = "test-method-inline", threadSafe = true)
    public static IWenyanValue testMethodInlineStat(IWenyanValue ignoredSelf, List<IWenyanValue> ignoredArgs) {
        return WenyanValues.of("test-method-inline");
    }

    @WenyanMethod(value = "test-method-request")
    public static IWenyanValue testMethodRequestStat(IWenyanValue ignoredSelf, List<IWenyanValue> ignoredArgs) {
        return WenyanValues.of("test-method-request");
    }

    @WenyanConstructor
    public static IWenyanObject testConstructor(List<IWenyanValue> ignoredArgs) {
        return new TestPassValueObject();
    }
}
