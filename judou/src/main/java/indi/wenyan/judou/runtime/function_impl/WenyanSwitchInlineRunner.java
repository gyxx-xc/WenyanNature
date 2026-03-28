package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanThread;
import indi.wenyan.judou.structure.ParsableType;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.*;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinFunction;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObject;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObjectType;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanThreading;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanSwitchInlineRunner<T extends IWenyanThread> implements IWenyanRunner, IThreadHolder<T> {
    @Getter
    @Setter
    private T thread;

    @Getter private final IGlobalResolver globalResolver;
    @Getter private final FrameManagerImpl frameManager;

    private boolean willPause = false;

    public WenyanSwitchInlineRunner(@NotNull WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
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
                    case BRANCH_POP_FALSE -> {
                        boolean value = runtime.getProcessStack().pop()
                                .as(WenyanBoolean.TYPE).value();
                        if (!value) {
                            runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                            runtime.setPCFlag(true);
                        }
                    }
                    case BRANCH_FALSE -> {
                        assert runtime.getProcessStack().peek() != null;
                        boolean value = runtime.getProcessStack().peek()
                                .as(WenyanBoolean.TYPE).value();
                        if (!value) {
                            runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                            runtime.setPCFlag(true);
                        }
                    }
                    case BRANCH_TRUE -> {
                        assert runtime.getProcessStack().peek() != null;
                        boolean value = runtime.getProcessStack().peek()
                                .as(WenyanBoolean.TYPE).value();
                        if (value) {
                            runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                            runtime.setPCFlag(true);
                        }
                    }
                    case JMP -> {
                        runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                        runtime.setPCFlag(true);
                    }
                    case CALL -> {
                        IWenyanValue func = runtime.getProcessStack().pop();
                        IWenyanFunction callable;

                        // object_type
                        if (func.is(IWenyanObjectType.TYPE)) {
                            callable = func.as(IWenyanObjectType.TYPE);
                        } else { // function
                            // handleWarper self first
                            callable = func.as(IWenyanFunction.TYPE);
                        }

                        List<IWenyanValue> argsList = new ArrayList<>(args);
                        for (int i1 = 0; i1 < args; i1++)
                            argsList.add(runtime.getProcessStack().pop());

                        // NOTE: must make the callF at end, because it may block thread
                        //   which is a fake block, it will still run the rest command before blocked
                        //   it will only block the next WenyanCode being executed
                        callable.call(null, (IWenyanRunner) this, argsList);
                    }
                    case CALL_ATTR -> {
                        IWenyanValue func = runtime.getProcessStack().pop();
                        IWenyanValue self;
                        IWenyanFunction callable;
                        self = runtime.getProcessStack().pop();

                        // object_type
                        if (func.is(IWenyanObjectType.TYPE)) {
                            callable = func.as(IWenyanObjectType.TYPE);
                        } else { // function
                            // handleWarper self first
                            // try casting to object (might be list)
                            // if not, static method
                            if (!self.is(IWenyanObject.TYPE)) {
                                self = null;
                            }
                            callable = func.as(IWenyanFunction.TYPE);
                        }

                        List<IWenyanValue> argsList = new ArrayList<>(args);
                        for (int i1 = 0; i1 < args; i1++)
                            argsList.add(runtime.getProcessStack().pop());

                        // NOTE: must make the callF at end, because it may block thread
                        //   which is a fake block, it will still run the rest command before blocked
                        //   it will only block the next WenyanCode being executed
                        callable.call(self, (IWenyanRunner) this, argsList);
                    }
                    case CREATE_FNCTION -> {
                        WenyanBuiltinFunction func = runtime.getProcessStack().pop().as(WenyanBuiltinFunction.TYPE);
                        var newFunc = new WenyanBuiltinFunction(func.bytecode(), func.args(), new ArrayList<>());
                        func.bytecode().getCapturedValues().stream()
                                .map(v -> {
                                    if (v.fromLocal()) {
                                        if (v.index() == args) return newFunc; // recursive call
                                        return runtime.getLocals().get(v.index());
                                    }
                                    return runtime.getReferences().get(v.index());
                                })
                                .forEach(i1 -> newFunc.refs().add(i1));
                        runtime.pushReturnValue(newFunc);
                    }
                    case RET -> {
                        WenyanFrame currentRuntime = getCurrentRuntime();
                        currentRuntime.getReturnBehavior().onReturn((IWenyanRunner) this, currentRuntime.getProcessStack().pop());
                    }
                    case CREATE_LIST -> getCurrentRuntime().pushReturnValue(new WenyanList());
                    case PUSH -> runtime.pushReturnValue(runtime.getBytecode().getConst(args));
                    case POP -> runtime.getProcessStack().pop();
                    case PEEK_ANS -> runtime.pushReturnValue(runtime.getResultStack().peek());
                    case PEEK_ANS_N -> {
                        // TODO: costy, consider ArrayCopy
                        List<IWenyanValue> list = new ArrayList<>(args);
                        for (int i1 = 0; i1 < args; i1++) {
                            list.add(runtime.getResultStack().pop());
                            runtime.pushReturnValue(list.getLast());
                        }
                        for (var i1 : list) {
                            runtime.getResultStack().push(i1);
                        }
                    }
                    case POP_ANS -> runtime.pushReturnValue(runtime.getResultStack().pop());
                    case PUSH_ANS -> runtime.getResultStack().push(runtime.getProcessStack().pop());
                    case FLUSH -> runtime.getResultStack().clear();
                    case LOAD -> {
                        IWenyanValue value = runtime.getLocals().get(args);
                        runtime.pushReturnValue(value);
                    }
                    case LOAD_REF -> {
                        IWenyanValue value = runtime.getReferences().get(args);
                        runtime.pushReturnValue(value);
                    }
                    case LOAD_GLOBAL -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        IWenyanValue value = getGlobalResolver().getGlobal(id);
                        runtime.pushReturnValue(value);
                    }
                    case STORE -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        runtime.setLocal(args, WenyanLeftValue.varOf(value));
                    }
                    case SET_VAR -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        IWenyanValue variable = runtime.getProcessStack().pop();
                        if (variable instanceof WenyanLeftValue lv) {
                            if (value == WenyanNull.NULL)
                                lv.setValue(WenyanNull.NULL);
                            else
                                lv.setValue(value.as(lv.type()));
                        } else
                            throw new WenyanException(JudouExceptionText.SetValueToNonLeftValue.string());
                    }
                    case CAST -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        runtime.pushReturnValue(value.as(ParsableType.values()[args].getType()));
                    }
                    case LOAD_ATTR -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        IWenyanValue attr;
                        IWenyanValue value = runtime.getProcessStack().pop();
                        assert value != null;
                        if (value.is(IWenyanObjectType.TYPE)) {
                            IWenyanObjectType object = value.as(IWenyanObjectType.TYPE);
                            attr = object.getAttribute(id);
                        } else {
                            IWenyanObject object = value.as(IWenyanObject.TYPE);
                            attr = object.getAttribute(id);
                        }
                        runtime.pushReturnValue(attr);
                    }
                    case LOAD_ATTR_REMAIN -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        IWenyanValue attr;
                        IWenyanValue value = runtime.getProcessStack().peek();
                        assert value != null;
                        if (value.is(IWenyanObjectType.TYPE)) {
                            IWenyanObjectType object = value.as(IWenyanObjectType.TYPE);
                            attr = object.getAttribute(id);
                        } else {
                            IWenyanObject object = value.as(IWenyanObject.TYPE);
                            attr = object.getAttribute(id);
                        }
                        runtime.pushReturnValue(attr);
                    }
                    case STORE_ATTR -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        WenyanBuiltinObject self = runtime.getProcessStack().pop().as(WenyanBuiltinObject.TYPE);
                        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                        self.createAttribute(id, value);
                    }
                    case STORE_STATIC_ATTR -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                        assert runtime.getProcessStack().peek() != null;
                        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                        type.addStaticVariable(id, value);
                    }
                    case STORE_FUNCTION_ATTR -> {
                        String id = runtime.getBytecode().getIdentifier(args);
                        IWenyanValue value = runtime.getProcessStack().pop();
                        assert runtime.getProcessStack().peek() != null;
                        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                        type.addFunction(id, value);
                    }
                    case CREATE_TYPE -> {
                        var parent = runtime.getProcessStack().pop();
                        IWenyanValue type;
                        if (parent.is(WenyanNull.TYPE))
                            type = new WenyanBuiltinObjectType(null);
                        else
                            type = new WenyanBuiltinObjectType(parent
                                    .as(WenyanBuiltinObjectType.TYPE));
                        runtime.pushReturnValue(type);
                    }
                    case FOR_ITER -> {
                        Iterator<?> iter;
                        assert runtime.getProcessStack().peek() != null;
                        iter = runtime.getProcessStack().peek().as(WenyanList.WenyanIterator.TYPE).value();
                        if (iter.hasNext()) {
                            runtime.pushReturnValue((IWenyanValue) iter.next());
                        } else {
                            runtime.getProcessStack().pop();
                            runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                            runtime.setPCFlag(true);
                        }
                    }
                    case FOR_NUM -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        int num = value.as(WenyanInteger.TYPE).value();
                        if (num > 0) {
                            runtime.pushReturnValue(WenyanValues.of((long) num - 1));
                        } else {
                            runtime.setProgramCounter(runtime.getBytecode().getLabel(args));
                            runtime.setPCFlag(true);
                        }
                    }
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
