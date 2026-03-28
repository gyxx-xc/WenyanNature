package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanThread;
import indi.wenyan.judou.runtime.function_impl.FrameManagerImpl;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanSwitchRunner<T extends IWenyanThread> implements IWenyanRunner, IThreadHolder<T> {
    @Getter
    @Setter
    private T thread;

    @Getter private final IGlobalResolver globalResolver;
    @Getter private final FrameManagerImpl frameManager;

    private boolean willPause = false;

    public WenyanSwitchRunner(@NotNull WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
        frameManager = new FrameManagerImpl(mainRuntime);
    }

    @Override
    public int run(int step) {
        willPause = false;
        int i = 0;
        try {
            for (; i < step; i++) {
                WenyanFrame runtime = getFrameManager().getNullableCurrentRuntime();
                if (runtime == null) { // reach return of main
                    die();
                    return i;
                }
                if (runtime.getProgramCounter() < 0 || runtime.getProgramCounter() >= runtime.getBytecode().size()) {
                    IWenyanRunner.dieWithException(this, new WenyanUnreachedException());
                    return i;
                }

                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.getProgramCounter());
                int args = bytecode.arg();

                switch (bytecode.code()) {
                    case BRANCH_POP_FALSE -> BranchCode.branchPopFalse(args, this);
                    case BRANCH_FALSE -> BranchCode.branchFalse(args, this);
                    case BRANCH_TRUE -> BranchCode.branchTrue(args, this);
                    case JMP -> BranchCode.branch(args, this);
                    case CALL -> FunctionCode.callFunction(args, this, false);
                    case CALL_ATTR -> FunctionCode.callFunction(args, this, true);
                    case CREATE_FNCTION -> CreateFunctionCode.createFunction(args, this);
                    case RET -> ReturnCode.ret(this);
                    case CREATE_LIST -> CreateListCode.createList(this);
                    case PUSH -> StackCode.pushStack(args, this);
                    case POP -> StackCode.popStack(this);
                    case PEEK_ANS -> AnsStackCode.peekAns(this);
                    case PEEK_ANS_N -> AnsStackCode.peekAnsN(args, this);
                    case POP_ANS -> AnsStackCode.popAns(this);
                    case PUSH_ANS -> AnsStackCode.pushAns(this);
                    case FLUSH -> AnsStackCode.flush(this);
                    case LOAD -> VariableCode.load(args, this);
                    case LOAD_REF -> VariableCode.loadRef(args, this);
                    case LOAD_GLOBAL -> VariableCode.loadGlobal(args, this);
                    case STORE -> VariableCode.store(args, this);
                    case SET_VAR -> VariableCode.setValue(this);
                    case CAST -> VariableCode.cast(args, this);
                    case LOAD_ATTR -> ObjectCode.loadAttr(args, this, true);
                    case LOAD_ATTR_REMAIN -> ObjectCode.loadAttr(args, this, false);
                    case STORE_ATTR -> ObjectCode.storeAttr(args, this);
                    case STORE_STATIC_ATTR -> ObjectCode.storeStaticAttr(args, this);
                    case STORE_FUNCTION_ATTR -> ObjectCode.storeFunctionAttr(args, this);
                    case CREATE_TYPE -> ObjectCode.createType(this);
                    case FOR_ITER -> ForCode.forIter(args, this);
                    case FOR_NUM -> ForCode.forNum(args, this);
                }

                if (!runtime.isPCFlag())
                    runtime.setProgramCounter(runtime.getProgramCounter() + 1);
                runtime.setPCFlag(false);

                if (willPause) return i + 1;
            }
            this.yield();
            return step;
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(this, e);
            return i + 1; // might i here, but it's not a big deal
        } catch (RuntimeException e) { // for any other missing exceptions
            IWenyanRunner.dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
            return i + 1;
        }
    }

    @Override
    public void pause() {
        willPause = true;
    }
}
