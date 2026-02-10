package indi.wenyan.utils;

import com.palantir.javapoet.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WenyanValueData {
    public static final String INTERPRETER_STRUCTURE_PACKET = "indi.wenyan.interpreter.structure";
    public static final String INTERFACE_HANDLER_PACKET = "indi.wenyan.interpreter.exec_interface.handler";
    public static final String WENYAN_VALUE_PACKET = "indi.wenyan.interpreter.structure.values";
    public static final ClassName WENYAN_TYPE_CLASS = ClassName.get(INTERPRETER_STRUCTURE_PACKET, "WenyanType");
    public static final ClassName WENYAN_THROW_EXCEPTION_CLASS = ClassName.get(INTERPRETER_STRUCTURE_PACKET, "WenyanThrowException");
    public static final ClassName WENYAN_EXCEPTION_CLASS = ClassName.get(INTERPRETER_STRUCTURE_PACKET, "WenyanException");
    public static final ClassName WENYAN_INLINE_JAVACALL_CLASS = ClassName.get(INTERFACE_HANDLER_PACKET, "WenyanInlineJavacall");
    public static final ClassName SIMPLE_REQUEST_HANDLER_CLASS = ClassName.get(INTERFACE_HANDLER_PACKET, "SimpleRequestHandler");
    public static final ClassName I_WENYAN_OBJECT_TYPE_CLASS = ClassName.get(WENYAN_VALUE_PACKET, "IWenyanObjectType");
    public static final ClassName I_WENYAN_OBJECT_CLASS = ClassName.get(WENYAN_VALUE_PACKET, "IWenyanObject");
    public static final ClassName I_WENYAN_VALUE_CLASS = ClassName.get(WENYAN_VALUE_PACKET, "IWenyanValue");

    @Getter
    private final Map<String, Attribute> instanceData = new HashMap<>();
    @Getter
    private final Map<String, Attribute> staticData = new HashMap<>();
    @Getter
    private final String originName;
    @Getter
    private final TypeElement element;

    @Setter
    @Getter
    private String constructorName = null;

    // for code generate
    private final String packageName;

    public WenyanValueData(TypeElement element) {
        this.element = element;
        originName = element.getSimpleName().toString();
        packageName = element.getEnclosingElement().toString();
    }

    public void putAttribute(String key, String name, WenyanValueData.Type type, boolean isStatic) {
        var attribute = new WenyanValueData.Attribute(name, type);
        if (isStatic) {
            if (staticData.containsKey(key))
                throw new IllegalArgumentException("attribute already exists");
            staticData.put(key, attribute);
        } else {
            if (staticData.containsKey(key))
                throw new IllegalArgumentException("attribute already exists");
            instanceData.put(key, attribute);
        }
    }

    public void generateCode(Filer filer) throws IOException {
        var classBuilder = TypeSpec.classBuilder(originName + "Object")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(I_WENYAN_OBJECT_CLASS)
                .superclass(ClassName.get(packageName, originName));
        generateWenyanType(classBuilder, originName + "Object");
        generateAttribute(classBuilder);
        generateWenyanObjectType(classBuilder);
        // get package name of element
        JavaFile.builder(packageName, classBuilder.build())
                .build()
                .writeTo(filer);
    }

    private void generateWenyanType(TypeSpec.Builder classBuilder, String name) {
        ClassName returnType = WENYAN_TYPE_CLASS;
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
                .addException(WENYAN_THROW_EXCEPTION_CLASS)
                .returns(I_WENYAN_VALUE_CLASS);
        if (!instanceData.isEmpty())
            generatedAttribute(methodBuilder, instanceData.entrySet(), false);
        else
            methodBuilder.addStatement("throw new $T($S)", WENYAN_EXCEPTION_CLASS, "属性不存在");
        classBuilder.addMethod(methodBuilder.build());
    }

    private void generatedAttribute(MethodSpec.Builder methodBuilder, Set<Map.Entry<String, Attribute>> entrySet, boolean isStatic) {
        methodBuilder.beginControlFlow("return switch (name)");
        for (var entry : entrySet) {
            switch (entry.getValue().type) {
                case FIELD ->
                        methodBuilder.addStatement("case $S -> $L", entry.getKey(), entry.getValue().name);
                case VARIABLE ->
                        methodBuilder.addStatement("case $S -> $L()", entry.getKey(), entry.getValue().name);
                case FUNCTION ->
                        methodBuilder.addStatement("case $S -> new $T($L::$L)", entry.getKey(),
                                WENYAN_INLINE_JAVACALL_CLASS,
                                isStatic ? originName : "this",
                                entry.getValue().name);
                case REQUEST_FUNCTION ->
                        methodBuilder.addStatement("case $S -> new $T($L::$L)", entry.getKey(),
                                SIMPLE_REQUEST_HANDLER_CLASS,
                                isStatic ? originName : "this",
                                entry.getValue().name);
            }
        }
        methodBuilder.addStatement("default -> throw new $T($S)", WENYAN_EXCEPTION_CLASS, "属性不存在");
        methodBuilder.endControlFlow();
        methodBuilder.addCode(";");
    }

    private void generateWenyanObjectType(TypeSpec.Builder classBuilder) {
        var typeClassBuilder = TypeSpec.classBuilder(originName + "ObjectType")
                .addSuperinterface(I_WENYAN_OBJECT_TYPE_CLASS)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        typeClassBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());
        generateWenyanType(typeClassBuilder, originName + "ObjectType");
        var createObjectMethodBuilder = MethodSpec.methodBuilder("createObject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(I_WENYAN_OBJECT_CLASS)
                .addParameter(ParameterSpec.builder(List.class, "argsList").build())
                .addException(WENYAN_THROW_EXCEPTION_CLASS);
        if (constructorName != null)
            createObjectMethodBuilder.addStatement("return $T.$L(argsList)", ClassName.get(packageName, originName), constructorName);
        else
            createObjectMethodBuilder.addStatement("throw new $T($S)", WENYAN_EXCEPTION_CLASS, "无构造函数");
        var attributeMethodBuilder = MethodSpec.methodBuilder("getAttribute")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(I_WENYAN_VALUE_CLASS)
                .addParameter(String.class, "name")
                .addException(WENYAN_THROW_EXCEPTION_CLASS);
        if (!staticData.isEmpty())
            generatedAttribute(attributeMethodBuilder, staticData.entrySet(), true);
        else
            attributeMethodBuilder.addStatement("throw new $T($S)", WENYAN_EXCEPTION_CLASS, "属性不存在");
        typeClassBuilder.addMethod(createObjectMethodBuilder.build());
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
