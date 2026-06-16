package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.runtime.IWenyanScheduler;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.*;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.api.values.primitive.WenyanBoolean;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.judou.api.values.primitive.WenyanList;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.executor.BreakpointCode;
import indi.wenyan.judou.structure.ParsableType;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFunction;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinObject;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinObjectType;
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
public class WenyanSwitchInlineRunner<T extends IWenyanScheduler.IWenyanThread> implements IWenyanRunner, IThreadHolder<T> {
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
        boolean pcFlag = false;
        int i = 0;
        try {
            for (; i < step; i++) {
                WenyanFrame runtime = getFrameManager().getCurrentRuntime();
                if (runtime == null) { // reach return of main
                    die();
                    return i;
                }

                IWenyanBytecode bytecode = runtime.getBytecode();
                int programCounter = runtime.getProgramCounter();
                int args = bytecode.getArg(programCounter);

                switch (bytecode.getCodeOrdinal(programCounter)) {
                    case 0 -> {
                        boolean value = runtime.getProcessStack().pop()
                                .as(WenyanBoolean.TYPE).value();
                        if (!value) {
                            runtime.setProgramCounter(args);
                            pcFlag = true;
                        }
                    }
                    case 1 -> {
                        assert runtime.getProcessStack().peek() != null;
                        boolean value = runtime.getProcessStack().peek()
                                .as(WenyanBoolean.TYPE).value();
                        if (!value) {
                            runtime.setProgramCounter(args);
                            pcFlag = true;
                        }
                    }
                    case 2 -> {
                        assert runtime.getProcessStack().peek() != null;
                        boolean value = runtime.getProcessStack().peek()
                                .as(WenyanBoolean.TYPE).value();
                        if (value) {
                            runtime.setProgramCounter(args);
                            pcFlag = true;
                        }
                    }
                    case 3 -> {
                        runtime.setProgramCounter(args);
                        pcFlag = true;
                    }
                    case 4 -> {
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
                        callable.callWithReturn(null, this, argsList, this.getCurrentRuntime().getProcessStack()::push);
                    }
                    case 5 -> {
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
                        callable.callWithReturn(self, this, argsList, this.getCurrentRuntime().getProcessStack()::push);
                    }
                    case 6 -> {
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
                                .forEach(i1 -> {
                                    assert newFunc.refs() != null;
                                    newFunc.refs().add(i1);
                                });
                        runtime.getProcessStack().push(newFunc);
                    }
                    case 7 -> {
                        WenyanFrame currentRuntime = getCurrentRuntime();
                        currentRuntime.getReturnBehavior().onReturn((IWenyanRunner) this, currentRuntime.getProcessStack().pop());
                    }
                    case 8 -> {
                        WenyanFrame wenyanFrame = getCurrentRuntime();
                        IWenyanValue value = new WenyanList();
                        wenyanFrame.getProcessStack().push(value);
                    }
                    case 9 -> {
                        IWenyanValue value = bytecode.getConst(args);
                        runtime.getProcessStack().push(value);
                    }
                    case 10 -> runtime.getProcessStack().pop();
                    case 11 -> {
                        IWenyanValue value = runtime.getResultStack().peek();
                        runtime.getProcessStack().push(value);
                    }
                    case 12 -> {
                        // TODO: costly, consider ArrayCopy
                        List<IWenyanValue> list = new ArrayList<>(args);
                        for (int i1 = 0; i1 < args; i1++) {
                            list.add(runtime.getResultStack().pop());
                            runtime.getProcessStack().push(list.getLast());
                        }
                        for (var i1 : list) {
                            runtime.getResultStack().push(i1);
                        }
                    }
                    case 13 -> {
                        IWenyanValue value = runtime.getResultStack().pop();
                        runtime.getProcessStack().push(value);
                    }
                    case 14 -> runtime.getResultStack().push(runtime.getProcessStack().pop());
                    case 15 -> runtime.getResultStack().clear();
                    case 16 -> {
                        IWenyanValue value = runtime.getLocals().get(args);
                        runtime.getProcessStack().push(value);
                    }
                    case 17 -> {
                        IWenyanValue value = runtime.getReferences().get(args);
                        runtime.getProcessStack().push(value);
                    }
                    case 18 -> {
                        String id = bytecode.getIdentifier(args);
                        IWenyanValue value = getGlobalResolver().getGlobal(id);
                        runtime.getProcessStack().push(value);
                    }
                    case 19 -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        runtime.setLocal(args, WenyanLeftValue.varOf(value));
                    }
                    case 20 -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        IWenyanValue variable = runtime.getProcessStack().pop();
                        if (variable instanceof WenyanLeftValue lv) {
                            lv.setValue(value);
                        } else
                            throw new WenyanException(JudouExceptionText.SetValueToNonLeftValue.string());
                    }
                    case 21 -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        IWenyanValue value1 = value.as(ParsableType.values()[args].getType());
                        runtime.getProcessStack().push(value1);
                    }
                    case 22 -> {
                        String id = bytecode.getIdentifier(args);
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
                        runtime.getProcessStack().push(attr);
                    }
                    case 23 -> {
                        String id = bytecode.getIdentifier(args);
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
                        runtime.getProcessStack().push(attr);
                    }
                    case 24 -> {
                        String id = bytecode.getIdentifier(args);
                        WenyanBuiltinObject self = runtime.getProcessStack().pop().as(WenyanBuiltinObject.TYPE);
                        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                        self.createAttribute(id, value);
                    }
                    case 25 -> {
                        String id = bytecode.getIdentifier(args);
                        IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                        assert runtime.getProcessStack().peek() != null;
                        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                        type.addStaticVariable(id, value);
                    }
                    case 26 -> {
                        String id = bytecode.getIdentifier(args);
                        IWenyanValue value = runtime.getProcessStack().pop();
                        assert runtime.getProcessStack().peek() != null;
                        WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                        type.addFunction(id, value);
                    }
                    case 27 -> {
                        var parent = runtime.getProcessStack().pop();
                        IWenyanValue type;
                        if (parent.is(WenyanNull.TYPE))
                            type = new WenyanBuiltinObjectType(null);
                        else
                            type = new WenyanBuiltinObjectType(parent
                                    .as(WenyanBuiltinObjectType.TYPE));
                        runtime.getProcessStack().push(type);
                    }
                    case 28 -> {
                        Iterator<?> iter;
                        assert runtime.getProcessStack().peek() != null;
                        iter = runtime.getProcessStack().peek().as(WenyanList.WenyanIterator.TYPE).value();
                        if (iter.hasNext()) {
                            IWenyanValue value = (IWenyanValue) iter.next();
                            runtime.getProcessStack().push(value);
                        } else {
                            runtime.getProcessStack().pop();
                            runtime.setProgramCounter(args);
                            pcFlag = true;
                        }
                    }
                    case 29 -> {
                        IWenyanValue value = runtime.getProcessStack().pop();
                        int num = value.as(WenyanInteger.TYPE).value();
                        if (num > 0) {
                            IWenyanValue value1 = WenyanValues.of((long) num - 1);
                            runtime.getProcessStack().push(value1);
                        } else {
                            runtime.setProgramCounter(args);
                            pcFlag = true;
                        }
                    }
                    case 30 -> BreakpointCode.breakpoint(0, this);
                }

                if (!pcFlag)
                    runtime.setProgramCounter(programCounter + 1);
                pcFlag = false;

                if (willPause) return i + 1;
            }
            this.yield();
            return step;
        } catch (WenyanException e) {
            WenyanFrame.dieWithException(this, e);
            return i + 1; // might i here, but it's not a big deal
        } catch (RuntimeException e) { // for any other missing exceptions
            WenyanFrame.dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
            return i + 1;
        }
    }

    @Override
    public void pause() {
        willPause = true;
    }
}
