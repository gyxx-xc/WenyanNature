package indi.wenyan.utils;

import com.palantir.javapoet.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static indi.wenyan.WenyanValueProcessor.WENYAN_VALUE_PACKET;

public class WenyanValueData {
    private final Map<String, Attribute> instanceData = new HashMap<>();
    private final Map<String, Attribute> staticData = new HashMap<>();
    @Getter
    private final String originName;
    @Getter
    private final TypeElement element;

    @Setter
    @Getter
    private String constructorName = null;
    private final String packageName;

    public WenyanValueData(TypeElement element) {
        this.element = element;
        originName = element.getSimpleName().toString();
        packageName = element.getEnclosingElement().toString();
    }

    public void putAttribute(String key, String name, WenyanValueData.Type type, boolean isStatic) {
        var attribute = new WenyanValueData.Attribute(name, type);
        if (isStatic) {
            staticData.put(key, attribute);
        } else {
            instanceData.put(key, attribute);
        }
    }


    public void generateCode(Filer filer) throws IOException {
        var classBuilder = TypeSpec.classBuilder(originName + "Object")
                .addSuperinterface(ClassName.get(WENYAN_VALUE_PACKET, "IWenyanObject"))
                .superclass(ClassName.get(packageName, originName));
        generateWenyanType(classBuilder, originName + "Object");
        generateAttribute(classBuilder);
        generateWenyanObjectType(classBuilder);
        classBuilder.build();
        // get package name of element
        var javaFile = JavaFile.builder(packageName, classBuilder.build())
                .build();
        javaFile.writeTo(filer);
    }

    private void generateWenyanType(TypeSpec.Builder classBuilder, String name) {
        ClassName returnType = ClassName.get("indi.wenyan.interpreter.structure", "WenyanType");
        FieldSpec wenyanType = FieldSpec.builder(returnType, "TYPE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new WenyanType<>($S, $L.class)", name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(), name)
                .build();
        MethodSpec wenyanTypeMethod = MethodSpec.methodBuilder("type")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("return TYPE")
                .returns(returnType)
                .build();
        classBuilder.addField(wenyanType)
                .addMethod(wenyanTypeMethod);
    }

    private void generateAttribute(TypeSpec.Builder classBuilder) {
        var methodBuilder = MethodSpec.methodBuilder("getAttribute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "name")
                .addException(ClassName.get("indi.wenyan.interpreter.structure", "WenyanThrowException"))
                .returns(ClassName.get(WENYAN_VALUE_PACKET, "IWenyanValue"));
        methodBuilder.beginControlFlow("return switch (name)");
        for (var entry : instanceData.entrySet()) {
            switch (entry.getValue().type) {
                case FIELD ->
                        methodBuilder.addStatement("case $S -> $L", entry.getKey(), entry.getValue().name);
                case VARIABLE ->
                        methodBuilder.addStatement("case $S -> $L()", entry.getKey(), entry.getValue().name);
                case FUNCTION ->
                        methodBuilder.addStatement("case $S -> new $T(this::$L)", entry.getKey(),
                                ClassName.get("indi.wenyan.interpreter.exec_interface.handler", "WenyanInlineJavacall"),
                                entry.getValue().name);
                case REQUEST_FUNCTION ->
                        methodBuilder.addStatement("case $S -> new $T(this::$L)", entry.getKey(),
                                ClassName.get("indi.wenyan.interpreter.exec_interface.handler", "SimpleRequestHandler"),
                                entry.getValue().name);
            }
        }
        methodBuilder.addStatement("default -> throw new $T($S)", ClassName.get("indi.wenyan.interpreter.structure", "WenyanException"), "属性不存在");
        methodBuilder.endControlFlow();
        methodBuilder.addCode(";");
        classBuilder.addMethod(methodBuilder.build());
    }

    private void generateWenyanObjectType(TypeSpec.Builder classBuilder) {
        var typeClassBuilder = TypeSpec.classBuilder(originName + "ObjectType")
                .addSuperinterface(ClassName.get(WENYAN_VALUE_PACKET, "IWenyanObjectType"))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        typeClassBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());
        generateWenyanType(typeClassBuilder, originName + "ObjectType");
        var createObjectMethod = MethodSpec.methodBuilder("createObject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(WENYAN_VALUE_PACKET, "IWenyanObject"))
                .addParameter(ParameterSpec.builder(ClassName.get("java.util", "List"), "argsList").build())
                .addException(ClassName.get("indi.wenyan.interpreter.structure", "WenyanThrowException"))
                .addStatement("return $T.$L(argsList)", ClassName.get(packageName, originName), constructorName)
                .build();
        var attributeMethodBuilder = MethodSpec.methodBuilder("getAttribute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(WENYAN_VALUE_PACKET, "IWenyanValue"))
                .addParameter(String.class, "name")
                .addException(ClassName.get("indi.wenyan.interpreter.structure", "WenyanThrowException"));
        attributeMethodBuilder.beginControlFlow("return switch (name)");
        for (var entry : staticData.entrySet()) {
            switch (entry.getValue().type) {
                case FIELD ->
                        attributeMethodBuilder.addStatement("case $S -> $T.$L", entry.getKey(), ClassName.get(packageName, originName), entry.getValue().name);
                case VARIABLE ->
                        attributeMethodBuilder.addStatement("case $S -> $T.$L()", entry.getKey(), ClassName.get(packageName, originName), entry.getValue().name);
                case FUNCTION ->
                        attributeMethodBuilder.addStatement("case $S -> new $T($T::$L)", entry.getKey(),
                                ClassName.get("indi.wenyan.interpreter.exec_interface.handler", "WenyanInlineJavacall"),
                                ClassName.get(packageName, originName),
                                entry.getValue().name);
                case REQUEST_FUNCTION ->
                        attributeMethodBuilder.addStatement("case $S -> new $T($T::$L)", entry.getKey(),
                                ClassName.get("indi.wenyan.interpreter.exec_interface.handler", "SimpleRequestHandler"),
                                ClassName.get(packageName, originName),
                                entry.getValue().name);
            }
        }
        attributeMethodBuilder.addStatement("default -> throw new $T($S)", ClassName.get("indi.wenyan.interpreter.structure", "WenyanException"), "属性不存在");
        attributeMethodBuilder.endControlFlow();
        attributeMethodBuilder.addCode(";");
        typeClassBuilder.addMethod(createObjectMethod);
        typeClassBuilder.addMethod(attributeMethodBuilder.build());
        TypeSpec typeSpec = typeClassBuilder.build();
        classBuilder.addType(typeSpec);
        classBuilder.addField(FieldSpec.builder(ClassName.get(packageName, originName + "Object", originName + "ObjectType"),
                "OBJECT_TYPE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T()", ClassName.get(packageName, originName + "Object", originName + "ObjectType"))
                .build());
    }

    public record Attribute(String name, Type type) {
    }

    public enum Type {
        FIELD,
        VARIABLE,
        FUNCTION,
        REQUEST_FUNCTION
    }
}
