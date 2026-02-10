package indi.wenyan.interpreter.structure.values.annotation;

import indi.wenyan.annotation.WenyanField;
import indi.wenyan.annotation.WenyanMethod;
import indi.wenyan.annotation.WenyanObjectValue;
import indi.wenyan.interpreter.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// TODO: we may need a compile level solution of it,
//   so, maybe delaying forever
@WenyanObjectValue
public class TestAnnotationWenyanValue implements IWenyanObject {

    @WenyanField("attribute")
    public IWenyanValue attribute;


    @WenyanField("attribute")
    public IWenyanValue attribute12;

    @WenyanField
    public IWenyanValue method() {
        return WenyanNull.NULL;
    }

    @WenyanMethod("meth")
    public IWenyanValue meth(IWenyanValue self, List<IWenyanValue> arg) {
        return WenyanNull.NULL;
    }

    // make above phase as following
    public static final Map<String, Function<TestAnnotationWenyanValue, IWenyanValue>> members = new HashMap<>();
    Function<TestAnnotationWenyanValue, IWenyanValue> sample1 = (instance) -> instance.attribute;
    Function<TestAnnotationWenyanValue, IWenyanValue> sample2 = TestAnnotationWenyanValue::method;
    Function<TestAnnotationWenyanValue, IWenyanValue> sample3 = (instance) -> new WenyanInlineJavacall(instance::meth);
    public IWenyanValue getAttribute(String name) {
        return members.get(name).apply(this);
    }

    @Override
    public WenyanType<?> type() {
        return null;
    }
}
