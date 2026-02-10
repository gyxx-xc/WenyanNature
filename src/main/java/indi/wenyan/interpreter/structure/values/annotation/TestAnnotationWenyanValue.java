package indi.wenyan.interpreter.structure.values.annotation;

import indi.wenyan.annotation.WenyanConstructor;
import indi.wenyan.annotation.WenyanField;
import indi.wenyan.annotation.WenyanMethod;
import indi.wenyan.annotation.WenyanObjectValue;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;

import java.util.List;

// TODO: we may need a compile level solution of it,
//   so, maybe delaying forever
@WenyanObjectValue
public class TestAnnotationWenyanValue {

    protected TestAnnotationWenyanValue() {}

    @WenyanField("attribute")
    public IWenyanValue attribute = WenyanNull.NULL;

    @WenyanField("method")
    public IWenyanValue method() {
        return WenyanNull.NULL;
    }

    @WenyanMethod(value = "meth", threadSafe = true)
    public IWenyanValue meth(IWenyanValue self, List<IWenyanValue> arg) {
        return WenyanNull.NULL;
    }

    @WenyanMethod(value = "meth1")
    public IWenyanValue meth1(IWenyanValue self, List<IWenyanValue> arg) {
        return WenyanNull.NULL;
    }

    @WenyanField("statiAttr")
    public static IWenyanValue statiAttr = WenyanNull.NULL;
    @WenyanMethod("statiMeth")
    public static IWenyanValue statiMeth(IWenyanValue self, List<IWenyanValue> arg) {
        return WenyanNull.NULL;
    }

    @WenyanConstructor
    public static IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanThrowException {
        return new TestAnnotationWenyanValueObject();
    }
}
