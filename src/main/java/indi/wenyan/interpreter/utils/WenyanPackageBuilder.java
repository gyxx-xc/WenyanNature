package indi.wenyan.interpreter.utils;

public class WenyanPackageBuilder {
    private final WenyanFunctionEnvironment environment = new WenyanFunctionEnvironment();

    public static WenyanPackageBuilder create() {
        return new WenyanPackageBuilder();
    }

    public WenyanPackageBuilder environment(WenyanFunctionEnvironment environment) {
        this.environment.importEnvironment(environment);
        return this;
    }

    public WenyanFunctionEnvironment build() {
        return environment;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function) {
        environment.setFunction(new WenyanFunctionEnvironment.FunctionSign(name, new WenyanValue.Type[0]), new JavacallHandler(function));
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function, WenyanValue.Type[] argTypes) {
        environment.setFunction(new WenyanFunctionEnvironment.FunctionSign(name, argTypes), new JavacallHandler(function));
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall) {
        environment.setFunction(new WenyanFunctionEnvironment.FunctionSign(name, new WenyanValue.Type[0]), javacall);
        return this;
    }
}