package indi.wenyan;

import com.google.auto.service.AutoService;
import indi.wenyan.annotation.WenyanObjectValue;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("indi.wenyan.annotation.WenyanObjectValue")
public class WenyanValueProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
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
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printError(String.format(msg, args), e);
    }
}
