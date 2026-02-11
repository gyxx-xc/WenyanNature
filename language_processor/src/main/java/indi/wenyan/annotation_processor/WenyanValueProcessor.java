package indi.wenyan.annotation_processor;

import com.google.auto.service.AutoService;
import indi.wenyan.annotation_processor.annotation.WenyanConstructor;
import indi.wenyan.annotation_processor.annotation.WenyanField;
import indi.wenyan.annotation_processor.annotation.WenyanMethod;
import indi.wenyan.annotation_processor.annotation.WenyanObjectValue;
import indi.wenyan.annotation_processor.utils.WenyanValueData;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Set;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("indi.wenyan.annotation_processor.annotation.WenyanObjectValue")
public class WenyanValueProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    private TypeMirror IWenyanValueType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        IWenyanValueType = elementUtils.getTypeElement(WenyanValueData.WENYAN_VALUE_PACKET + ".IWenyanValue").asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set,
                           RoundEnvironment roundEnvironment) {
        for (var element : roundEnvironment.getElementsAnnotatedWith(WenyanObjectValue.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "@%s can only be applied to class",
                        WenyanObjectValue.class.getSimpleName());
                return true;
            }
            var data = new WenyanValueData((TypeElement) element);
            if (!validateType(data.getElement()))
                return true;
            try {
                data.getElement().getEnclosedElements().forEach(e -> {
                    tryAddVariable(data, e);
                    tryAddFunction(data, e);
                    tryGetConstructor(data, e);
                });
            } catch (IllegalArgumentException e) {
                error(element, e.getMessage());
                return true;
            }
            try {
                data.generateCode(filer);
            } catch (IOException e) {
                error(element, e.getMessage());
                return true;
            }
        }
        return false;
    }

    private boolean validateType(TypeElement element) {
        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            error(element, "WenyanObjectValue can only be applied to public class");
            return false;
        }
        if (element.getModifiers().contains(Modifier.FINAL) || element.getModifiers().contains(Modifier.ABSTRACT)) {
            error(element, "WenyanObjectValue can only be applied to non-final and non-abstract class");
            return false;
        }
        if (element.getNestingKind().isNested()) {
            error(element, "WenyanObjectValue can only be applied to top-level class");
            return false;
        }
        return true;
    }

    private void tryAddVariable(WenyanValueData data, Element element) {
        var annotation = element.getAnnotation(WenyanField.class);
        if (annotation == null) {
            return;
        }

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            throw new IllegalArgumentException("WenyanField can only be applied to public field");
        }

        boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
        if (element.getKind() == ElementKind.FIELD) {
            validateFieldType(element);
            data.putAttribute(annotation.value(), element.getSimpleName().toString(), WenyanValueData.Type.FIELD, isStatic);
        } else if (element.getKind() == ElementKind.METHOD) {
            validateMethodReturnType((ExecutableElement) element);
            data.putAttribute(annotation.value(), element.getSimpleName().toString(), WenyanValueData.Type.VARIABLE, isStatic);
        } else {
            throw new IllegalArgumentException("WenyanField can only be applied to field or method");
        }
    }

    private void tryAddFunction(WenyanValueData data, Element element) {
        var annotation = element.getAnnotation(WenyanMethod.class);
        if (annotation == null) {
            return;
        }

        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            throw new IllegalArgumentException("WenyanMethod can only be applied to public method");
        }

        boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
        if (element.getKind() == ElementKind.METHOD) {
            validateMethodReturnType((ExecutableElement) element);
            data.putAttribute(annotation.value(), element.getSimpleName().toString(),
                    annotation.threadSafe() ? WenyanValueData.Type.FUNCTION : WenyanValueData.Type.REQUEST_FUNCTION, isStatic);
        } else {
            throw new IllegalArgumentException("WenyanMethod can only be applied to method");
        }
    }

    private void tryGetConstructor(WenyanValueData data, Element element) {
        if (element.getAnnotation(WenyanConstructor.class) == null) {
            return;
        }

        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException("WenyanConstructor can only be applied to method");
        }

        var executableElement = (ExecutableElement) element;

        if (!executableElement.getModifiers().contains(Modifier.STATIC) || !executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new IllegalArgumentException("WenyanConstructor can only be applied to public static method");
        }

        TypeMirror type = elementUtils.getTypeElement(WenyanValueData.WENYAN_VALUE_PACKET + ".IWenyanObject").asType();
        if (!typeUtils.isSameType(executableElement.getReturnType(), type)) {
            throw new IllegalArgumentException("WenyanConstructor can only be applied to method that returns IWenyanValue");
        }

        var parameters = executableElement.getParameters();
        if (parameters.size() != 1 || !typeUtils.isSameType(parameters.getFirst().asType(),
                typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.List"), IWenyanValueType))) {
            throw new IllegalArgumentException("WenyanConstructor can only be applied to method that takes List<IWenyanValue>");
        }
        data.setConstructorName(executableElement.getSimpleName().toString());
    }

    private void validateFieldType(Element element) {
        if (!typeUtils.isSameType(element.asType(), IWenyanValueType)) {
            throw new IllegalArgumentException("WenyanField can only be applied to field of IWenyanValue");
        }
    }

    private void validateMethodReturnType(ExecutableElement method) {
        if (!typeUtils.isSameType(method.getReturnType(), IWenyanValueType)) {
            throw new IllegalArgumentException("WenyanField can only be applied to method that returns IWenyanValue");
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printError(String.format(msg, args), e);
    }
}
