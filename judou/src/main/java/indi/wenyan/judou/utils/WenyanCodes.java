package indi.wenyan.judou.utils;

import indi.wenyan.judou.runtime.executor.*;
import lombok.Getter;

public enum WenyanCodes {
    BRANCH_POP_FALSE(new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.POP)),
    BRANCH_FALSE(new BranchCode(BranchCode.Condition.FALSE, BranchCode.Operation.NONE)),
    BRANCH_TRUE(new BranchCode(BranchCode.Condition.TRUE, BranchCode.Operation.NONE)),
    JMP(new BranchCode(BranchCode.Condition.NONE, BranchCode.Operation.NONE)),

    CALL(new FunctionCode(FunctionCode.Operation.CALL)),
    CALL_ATTR(new FunctionCode(FunctionCode.Operation.CALL_ATTR)),
    RET(new ReturnCode()),

    PUSH(new StackCode(StackCode.Operation.PUSH)),
    POP(new StackCode(StackCode.Operation.POP)),

    PEEK_ANS(new AnsStackCode(AnsStackCode.Operation.PEEK)),
    PEEK_ANS_N(new AnsStackCode(AnsStackCode.Operation.PEEK_N)),
    POP_ANS(new AnsStackCode(AnsStackCode.Operation.POP)),
    PUSH_ANS(new AnsStackCode(AnsStackCode.Operation.PUSH)),
    FLUSH(new AnsStackCode(AnsStackCode.Operation.FLUSH)),

    LOAD(new VariableCode(VariableCode.Operation.LOAD)),
    STORE(new VariableCode(VariableCode.Operation.STORE)),
    SET_VAR(new VariableCode(VariableCode.Operation.SET_VALUE)),
    CAST(new VariableCode(VariableCode.Operation.CAST)),

    LOAD_ATTR(new ObjectCode(ObjectCode.Operation.ATTR)),
    LOAD_ATTR_REMAIN(new ObjectCode(ObjectCode.Operation.ATTR_REMAIN)),
    STORE_ATTR(new ObjectCode(ObjectCode.Operation.STORE_ATTR)),
    STORE_STATIC_ATTR(new ObjectCode(ObjectCode.Operation.STORE_STATIC_ATTR)),
    STORE_FUNCTION_ATTR(new ObjectCode(ObjectCode.Operation.STORE_FUNCTION_ATTR)),
    CREATE_TYPE(new ObjectCode(ObjectCode.Operation.CREATE_TYPE)),

    FOR_ITER(new ForCode(ForCode.Operation.FOR_ITER)),
    FOR_NUM(new ForCode(ForCode.Operation.FOR_NUM));

    @Getter
    private final WenyanCode code;

    WenyanCodes(WenyanCode code) {
        this.code = code;
    }
}
