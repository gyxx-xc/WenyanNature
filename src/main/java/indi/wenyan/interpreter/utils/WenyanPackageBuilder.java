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
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(name, new WenyanValue.Type[0]);
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        environment.setFunction(sign, new JavacallHandler(function));
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function, WenyanValue.Type[] argTypes) {
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(name, argTypes);
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        environment.setFunction(sign, new JavacallHandler(function));
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall) {
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(name, new WenyanValue.Type[0]);
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        environment.setFunction(sign, javacall);
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall, WenyanValue.Type[] argTypes) {
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(name, argTypes);
        environment.setVariable(name, new WenyanValue(WenyanValue.Type.FUNCTION, sign, true));
        environment.setFunction(sign, javacall);
        return this;
    }

}
