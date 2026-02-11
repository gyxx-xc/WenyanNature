package indi.wenyan.judou.structure.values.annotation;

import indi.wenyan.judou.exec_interface.handler.SimpleRequestHandler;
import indi.wenyan.judou.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.annotation.tested_classes.TestPassValueObject;
import indi.wenyan.judou.utils.WenyanValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationWenyanValueTest {
    @Test
    void baseClass() throws WenyanThrowException {
        TestPassValueObject object = new TestPassValueObject();
        assertEquals(object.getAttribute("test-field"), WenyanValues.of("test-field"));
        assertEquals(object.getAttribute("test-method"), WenyanValues.of("test-method"));
        IWenyanValue method = object.getAttribute("test-method-inline");
        assertTrue(method.is(WenyanInlineJavacall.TYPE));
        IWenyanValue method2 = object.getAttribute("test-method-request");
        assertInstanceOf(SimpleRequestHandler.class, method2);
        assertEquals(TestPassValueObject.OBJECT_TYPE.getAttribute("test-field"), WenyanValues.of("test-field"));
        assertEquals(TestPassValueObject.OBJECT_TYPE.getAttribute("test-method"), WenyanValues.of("test-method"));
        IWenyanValue method3 = TestPassValueObject.OBJECT_TYPE.getAttribute("test-method-inline");
        assertTrue(method3.is(WenyanInlineJavacall.TYPE));
        IWenyanValue method4 = TestPassValueObject.OBJECT_TYPE.getAttribute("test-method-request");
        assertInstanceOf(SimpleRequestHandler.class, method4);
    }
}
