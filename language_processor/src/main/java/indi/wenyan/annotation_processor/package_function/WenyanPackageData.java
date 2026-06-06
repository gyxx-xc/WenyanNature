package indi.wenyan.annotation_processor.package_function;

import com.palantir.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record WenyanPackageData(
        String wenyanPackageName,
        TypeElement element,
        List<FunctionInfo> functions,
        String blocksMethodName
) {
    public static final String PKG_INTERPRETER = "indi.wenyan.interpreter";
    public static final String PKG_SETUP_REGISTERS = "indi.wenyan.setup.registers";
    public static final String PKG_SETUP_HANDLER = "indi.wenyan.setup.registers.handler";

    public static final String PKG_NEOFORGE_BUS = "net.neoforged.bus.api";
    public static final String PKG_NEOFORGE_EVENT = "net.neoforged.neoforge.event.entity";
    public static final String PKG_NEOFORGE_FML = "net.neoforged.fml.common";

    public static final ClassName CLS_WENYAN_ARGS_RESOLVER = ClassName.get(PKG_INTERPRETER, "WenyanArgsResolver");
    public static final ClassName CLS_WENYAN_VALUES = ClassName.get(PKG_INTERPRETER, "WenyanValues");

    public static final ClassName CLS_DEVICE_CAPABILITY_REGISTERER = ClassName.get(PKG_SETUP_REGISTERS, "DeviceCapabilityRegisterer");
    public static final ClassName CLS_WENYAN_NATURE = ClassName.get("indi.wenyan", "WenyanNature");

    public static final String GENERATED_CLASS_NAME = "WyCapabilities_generated";

    private static final List<WenyanPackageData> accumulated = new ArrayList<>();


    public static void generateCode(Filer filer) throws IOException {
        if (accumulated.isEmpty())
            return;

        var javaFile = JavaFile.builder("indi.wenyan.generated", buildEnum())
                .build();

        javaFile.writeTo(filer);
    }

    public static void add(WenyanPackageData data) {
        accumulated.add(data);
    }

    private static TypeSpec buildEnum() {
        var methodBuilder = MethodSpec.methodBuilder("registerCapabilities")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addAnnotation(ClassName.get(PKG_NEOFORGE_BUS, "SubscribeEvent"))
                .addParameter(ClassName.get(PKG_NEOFORGE_EVENT, "RegisterCapabilitiesEvent"), "event")
                .addStatement("$T registerer = new $T(event)",
                        CLS_DEVICE_CAPABILITY_REGISTERER, CLS_DEVICE_CAPABILITY_REGISTERER);

        for (var data : WenyanPackageData.accumulated) {
            var className = ClassName.get(data.element());

            methodBuilder.addStatement("registerer.registerToBlock((pos, state) -> $L, $S, $L)",
                    buildHandlerBlock(data.functions(), className),
                    data.wenyanPackageName(),
                    CodeBlock.of("$T.$L()", className, data.blocksMethodName()));
        }

        var typeSpecBuilder = TypeSpec.enumBuilder(GENERATED_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.get(PKG_NEOFORGE_FML, "EventBusSubscriber"))
                        .addMember("modid", "$T.MODID", CLS_WENYAN_NATURE)
                        .build())
                .addMethod(methodBuilder.build());

        return typeSpecBuilder.build();
    }

    private static CodeBlock buildHandlerBlock(List<FunctionInfo> functions,
                                               ClassName className) {
        var block = CodeBlock.builder()
                .add("$T.create()", ClassName.get(PKG_SETUP_HANDLER, "HandlerPackageBuilder"));

        for (var func : functions) {
            block.add(".handler($S, ", func.wenyanName());
            buildHandlerLambda(block, func, className);
            block.add(")");
        }

        block.add(".build()");
        return block.build();
    }

    private static void buildHandlerLambda(CodeBlock.Builder block,
                                           FunctionInfo func,
                                           ClassName className) {
        var paramTypes = func.params();
        boolean returnsVoid = func.returnType().getKind() == TypeKind.VOID;

        block.beginControlFlow("(context, args) -> ");

        block.add("$[var ar = new $T(args, $L)$]", CLS_WENYAN_ARGS_RESOLVER, paramTypes.size());
        CodeBlock param = CodeBlock.join(
                paramTypes.stream().map(type -> CodeBlock.of("ar.get$L()", type.type)).toList(),
                ", ");
        CodeBlock functionBody = CodeBlock.builder()
                .add("$T.$L($L)", className, func.javaMethodName(), param).build();
        if (returnsVoid) {
            block.add("$[$L$]", functionBody);
        } else {
            block.add("$[$T.of($L)$]", CLS_WENYAN_VALUES, functionBody);
        }

        block.endControlFlow();
    }

    public record FunctionInfo(
            String wenyanName,
            String javaMethodName,
            boolean threadSafe,
            List<ParamInfo> params,
            TypeMirror returnType
    ) {
    }

    public record ParamInfo(String name, String type, boolean isContext) {
    }

}
