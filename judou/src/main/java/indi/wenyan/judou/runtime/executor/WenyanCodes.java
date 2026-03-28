package indi.wenyan.judou.runtime.executor;

import lombok.Getter;

public enum WenyanCodes {
    BRANCH_POP_FALSE(BranchCode::branchPopFalse),
    BRANCH_FALSE(BranchCode::branchFalse),
    BRANCH_TRUE(BranchCode::branchTrue),
    JMP(BranchCode::branch),

    CALL((arg, thread) -> FunctionCode.callFunction(arg, thread, false)),
    CALL_ATTR((arg, thread) -> FunctionCode.callFunction(arg, thread, true)),
    CREATE_FNCTION(CreateFunctionCode::createFunction),
    RET((_, t) -> ReturnCode.ret(t)),

    CREATE_LIST((_, t) -> CreateListCode.createList(t)),

    PUSH(StackCode::pushStack),
    POP((_, t) -> StackCode.popStack(t)),

    PEEK_ANS((_, t) -> AnsStackCode.peekAns(t)),
    PEEK_ANS_N(AnsStackCode::peekAnsN),
    POP_ANS((_, t) -> AnsStackCode.popAns(t)),
    PUSH_ANS((_, t) -> AnsStackCode.pushAns(t)),
    FLUSH((_, t) -> AnsStackCode.flush(t)),

    LOAD(VariableCode::load),
    LOAD_REF(VariableCode::loadRef),
    LOAD_GLOBAL(VariableCode::loadGlobal),
    STORE(VariableCode::store),
    SET_VAR((_, t) -> VariableCode.setValue(t)),
    CAST(VariableCode::cast),

    LOAD_ATTR(((arg, thread) -> ObjectCode.loadAttr(arg, thread, true))),
    LOAD_ATTR_REMAIN((arg, thread) -> ObjectCode.loadAttr(arg, thread, false)),
    // currently only used at define (mzy SELF ZHI STRING)
    STORE_ATTR(ObjectCode::storeAttr),
    STORE_STATIC_ATTR(ObjectCode::storeStaticAttr),
    STORE_FUNCTION_ATTR(ObjectCode::storeFunctionAttr),
    CREATE_TYPE((_, t) -> ObjectCode.createType(t)),

    FOR_ITER(ForCode::forIter),
    FOR_NUM(ForCode::forNum);

    @Getter
    private final WenyanCode code;

    WenyanCodes(WenyanCode code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name();
    }
}
