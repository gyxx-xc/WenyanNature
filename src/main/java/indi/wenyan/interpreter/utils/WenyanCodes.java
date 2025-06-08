package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.executor.*;
import indi.wenyan.interpreter.runtime.executor.WenyanCode;

public final class WenyanCodes {
    private WenyanCodes(){}

    public static final WenyanCode BRANCH_POP_FALSE = new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.POP);
    public static final WenyanCode BRANCH_FALSE = new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.NONE);
    public static final WenyanCode BRANCH_TRUE = new BranchCode(BranchCode.Condition.TRUE, BranchCode.Operation.NONE);
    public static final WenyanCode JMP = new BranchCode(BranchCode.Condition.NONE, BranchCode.Operation.NONE);

    public static final WenyanCode CALL = new FunctionCode(FunctionCode.Operation.CALL);
    public static final WenyanCode CALL_ATTR = new FunctionCode(FunctionCode.Operation.CALL_ATTR);
    public static final WenyanCode RET = new ReturnCode();

    public static final WenyanCode PUSH = new StackCode(StackCode.Operation.PUSH);
    public static final WenyanCode POP = new StackCode(StackCode.Operation.POP);

    public static final WenyanCode PEEK_ANS = new AnsStackCode(AnsStackCode.Operation.PEEK);
    public static final WenyanCode PEEK_ANS_N = new AnsStackCode(AnsStackCode.Operation.PEEK_N);
    public static final WenyanCode POP_ANS = new AnsStackCode(AnsStackCode.Operation.POP);
    public static final WenyanCode PUSH_ANS = new AnsStackCode(AnsStackCode.Operation.PUSH);
    public static final WenyanCode FLUSH = new AnsStackCode(AnsStackCode.Operation.FLUSH);

    public static final WenyanCode LOAD = new VariableCode(VariableCode.Operation.LOAD);
    public static final WenyanCode STORE = new VariableCode(VariableCode.Operation.STORE);
    public static final WenyanCode SET_VAR = new VariableCode(VariableCode.Operation.SET_VALUE);
    public static final WenyanCode CAST = new VariableCode(VariableCode.Operation.CAST);

    public static final WenyanCode LOAD_ATTR = new ObjectCode(ObjectCode.Operation.ATTR);
    public static final WenyanCode LOAD_ATTR_REMAIN = new ObjectCode(ObjectCode.Operation.ATTR_REMAIN);
    public static final WenyanCode STORE_ATTR = new ObjectCode(ObjectCode.Operation.STORE_ATTR);
    public static final WenyanCode STORE_STATIC_ATTR = new ObjectCode(ObjectCode.Operation.STORE_STATIC_ATTR);
    public static final WenyanCode STORE_FUNCTION_ATTR = new ObjectCode(ObjectCode.Operation.STORE_FUNCTION_ATTR);
    public static final WenyanCode CREATE_TYPE = new ObjectCode(ObjectCode.Operation.CREATE_TYPE);

    public static final WenyanCode IMPORT = new ImportCode(ImportCode.Operation.IMPORT);
    public static final WenyanCode IMPORT_FROM = new ImportCode(ImportCode.Operation.IMPORT_FROM);

    public static final WenyanCode FOR_ITER = new ForCode(ForCode.Operation.FOR_ITER);
    public static final WenyanCode FOR_NUM = new ForCode(ForCode.Operation.FOR_NUM);
}
