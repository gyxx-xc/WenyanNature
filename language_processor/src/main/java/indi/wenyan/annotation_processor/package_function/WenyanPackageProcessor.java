package indi.wenyan.annotation_processor.package_function;

import com.google.auto.service.AutoService;
import indi.wenyan.annotation_processor.package_function.annotation.ForBlocks;
import indi.wenyan.annotation_processor.package_function.annotation.WenyanPackage;
import indi.wenyan.annotation_processor.package_function.annotation.WenyanPackageFunction;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static indi.wenyan.annotation_processor.package_function.WenyanPackageData.FunctionInfo;
import static indi.wenyan.annotation_processor.package_function.WenyanPackageData.ParamInfo;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("indi.wenyan.annotation_processor.package_function.annotation.WenyanPackage")
public class WenyanPackageProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnvironment) {
        for (var element : roundEnvironment.getElementsAnnotatedWith(WenyanPackage.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                warn(element, "@%s on non-class element, skipping",
                        WenyanPackage.class.getSimpleName());
                continue;
            }
            var typeElement = (TypeElement) element;
            try {
                var data = buildPackageData(typeElement);
                WenyanPackageData.add(data);
            } catch (IllegalArgumentException e) {
                error(element, e.getMessage());
            }
        }

        if (roundEnvironment.processingOver()) {
            try {
                WenyanPackageData.generateCode(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Failed to generate WyCapabilities: " + e.getMessage());
            }
        }

        return false;
    }

    private WenyanPackageData buildPackageData(TypeElement element) {
        var annotation = element.getAnnotation(WenyanPackage.class);
        var packageName = annotation.value();

        var functions = new ArrayList<FunctionInfo>();
        String blocksMethodName = null;

        for (var enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD)
                continue;

            var method = (ExecutableElement) enclosed;

            var funcAnnotation = method.getAnnotation(WenyanPackageFunction.class);
            if (funcAnnotation != null)
                functions.add(buildFunctionInfo(method, funcAnnotation));

            if (method.getAnnotation(ForBlocks.class) != null) {
                validateForBlocks(method);
                blocksMethodName = method.getSimpleName().toString();
            }
        }

        if (functions.isEmpty())
            warn(element, "@WenyanPackage class has no @WenyanPackageFunction methods");

        return new WenyanPackageData(packageName, element, functions, blocksMethodName);
    }

    private FunctionInfo buildFunctionInfo(ExecutableElement method,
                                           WenyanPackageFunction annotation) {
        var params = new ArrayList<ParamInfo>();
        for (var param : method.getParameters()) {
            var paramType = param.asType();
            var isContext = CONTEXT_TYPES.contains(paramType.toString());
            params.add(new ParamInfo(
                    param.getSimpleName().toString(),
                    paramType,
                    isContext
            ));
        }

        return new FunctionInfo(
                annotation.value(),
                method.getSimpleName().toString(),
                annotation.threadSafe(),
                List.copyOf(params),
                method.getReturnType()
        );
    }

    private void validateForBlocks(ExecutableElement method) {
        if (!method.getModifiers().contains(Modifier.STATIC) || !method.getModifiers().contains(Modifier.PUBLIC))
            throw new IllegalArgumentException("ForBlocks can only be applied to public static method");
        if (!method.getParameters().isEmpty())
            throw new IllegalArgumentException("ForBlocks method must have no parameters");
        if (!method.getReturnType().toString().endsWith("[]"))
            throw new IllegalArgumentException("ForBlocks method must return an array");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void warn(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
    }
}
