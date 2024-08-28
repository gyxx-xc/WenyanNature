package indi.wenyan.interpreter.visitor;

import indi.wenyan.WenyanRBaseVisitor;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;

public abstract class WenyanVisitor extends WenyanRBaseVisitor<WenyanValue> {
    protected WenyanFunctionEnvironment functionEnvironment;

    public WenyanVisitor(WenyanFunctionEnvironment functionEnvironment) {
        this.functionEnvironment = functionEnvironment;
    }
}
