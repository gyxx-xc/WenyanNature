package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.executor.*;

public class WenyanCodes {
    public static final WenyanCode BRANCH_POP_FALSE = new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.POP);
    public static final WenyanCode BRANCH_FALSE = new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.NONE);
    public static final WenyanCode BRANCH_TRUE = new BranchCode(BranchCode.Condition.TRUE, BranchCode.Operation.NONE);
    public static final WenyanCode JMP = new BranchCode(BranchCode.Condition.NONE, BranchCode.Operation.NONE);

    public static final WenyanCode CALL = new FunctionCode(FunctionCode.Operation.CALL);
    public static final WenyanCode RET = new FunctionCode(FunctionCode.Operation.RETURN);

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
    public static final WenyanCode CREATE_TYPE = new ObjectCode(ObjectCode.Operation.CREATE_TYPE);
}
