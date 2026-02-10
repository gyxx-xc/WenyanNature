package indi.wenyan.interpreter.structure.values.annotation;

import indi.wenyan.interpreter.exec_interface.handler.SimpleRequestHandler;
import indi.wenyan.interpreter.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public class TestAnnotationWenyanValueObject1 extends TestAnnotationWenyanValue implements IWenyanObject {
    public static final WenyanType<TestAnnotationWenyanValueObject1> TYPE = new WenyanType<>("TestAnnotationWenyanValueObject", TestAnnotationWenyanValueObject1.class);
    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    public static final IWenyanObjectType OBJECT_TYPE = new TestAnnotationWenyanValueObjectType();

    public IWenyanValue getAttribute(String name) throws WenyanThrowException {
        return switch (name) {
            case "attribute" -> attribute;
            case "method" -> method();
            case "meth" -> new WenyanInlineJavacall(this::meth);
            case "meth1" -> new SimpleRequestHandler(this::meth1);
            default -> throw new WenyanException("aaa");
        };
    }

    public static class TestAnnotationWenyanValueObjectType implements IWenyanObjectType {
        private TestAnnotationWenyanValueObjectType() {}

        @Override
        public IWenyanValue getAttribute(String name) throws WenyanThrowException {
            return switch (name) {
                case "statiAttr" -> TestAnnotationWenyanValue.statiAttr;
                case "statiMeth" -> new WenyanInlineJavacall(TestAnnotationWenyanValue::statiMeth);
                default -> throw new WenyanException("aaa");
            };
        }

        @Override
        public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanThrowException {
            return TestAnnotationWenyanValue.createObject(argsList);
        }

        public static final WenyanType<TestAnnotationWenyanValueObjectType> TYPE = new WenyanType<>("TestAnnotationWenyanValueObjectType", TestAnnotationWenyanValueObjectType.class);
        @Override
        public WenyanType<?> type() {
            return TYPE;
        }
    }

}
