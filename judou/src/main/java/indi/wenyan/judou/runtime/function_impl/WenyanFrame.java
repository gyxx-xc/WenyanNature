package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

/**
 * Represents the runtime environment for executing Wenyan bytecode.
 * Stores variables, execution state, and handles the program flow.
 */
// TODO: to interface
@WenyanThreading
public class WenyanFrame {
    /**
     * -- GETTER --
     * The bytecode to be executed
     */
    @Getter
    @NotNull
    private final WenyanBytecode bytecode;

    /**
     * -- GETTER --
     *  Current instruction pointer
     */
    @Setter
    @Getter
    private int programCounter = 0;

    @Getter
    @Nullable
    private final WenyanFrame returnRuntime;

    @Getter
    private final List<IWenyanValue> locals = new ArrayList<>();

    @Getter
    private final List<IWenyanValue> references;

    /**
     * -- GETTER --
     * Stack for operation results
     */
    @Getter
    private final WenyanResultStack resultStack = new WenyanResultStack();

    /**
     * -- GETTER --
     * Stack for processing intermediate values
     */
    @Getter
    private final Deque<IWenyanValue> processStack = new ArrayDeque<>();

    @Getter
    @Setter
    private boolean PCFlag = false;

    @Getter
    @Setter
    private ReturnBehavior returnBehavior = this::onReturn;

    /**
     * Creates a new runtime environment with the specified bytecode.
     *
     * @param bytecode The bytecode to execute (can be null)
     */
    public WenyanFrame(@NotNull WenyanBytecode bytecode, List<IWenyanValue> refs, @Nullable WenyanFrame returnRuntime) {
        this.bytecode = bytecode;
        this.references = refs;
        this.returnRuntime = returnRuntime;
    }

    private WenyanFrame(@NotNull WenyanBytecode bytecode) {
        this(bytecode, Collections.emptyList(), null);
    }

    public static @NotNull WenyanFrame ofCode(String code) {
        var bytecode = new WenyanBytecode(code);
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode, null, Collections.emptyList());
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        visitor.visit(WenyanVisitor.program(code));
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        WenyanVerifier.verify(bytecode);
        return new WenyanFrame(bytecode);
    }

    public static @NotNull WenyanFrame ofImportCode(String code, WenyanFrame returnRuntime) {
        // FIXME: reduce code redundancy
        var bytecode = new WenyanBytecode(code);
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode, null, Collections.emptyList());
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        visitor.visit(WenyanVisitor.program(code));
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        WenyanVerifier.verify(bytecode);
        return getRuntime(returnRuntime, bytecode, environment);
    }

    private static @NotNull WenyanFrame getRuntime(WenyanFrame returnRuntime, WenyanBytecode bytecode, WenyanCompilerEnvironment environment) {
        WenyanFrame wenyanRuntime = new WenyanFrame(bytecode, Collections.emptyList(), returnRuntime);
        var exportedIdentifier = environment.getExportedValues();
        int exportSize = exportedIdentifier.size();
        wenyanRuntime.returnBehavior = (runner, _) -> {
            Map<String, IWenyanValue> result = new HashMap<>(exportSize);
            WenyanFrame currentRuntime = runner.getCurrentRuntime();
            for (int i = 0; i < exportSize; i++) {
                result.put(exportedIdentifier.get(i), currentRuntime.locals.get(i));
            }
            runner.getFrameManager().ret();
            runner.getCurrentRuntime().pushReturnValue(new WenyanPackage(result));
        };
        return wenyanRuntime;
    }

    public void setLocal(int index, IWenyanValue value) {
        int currentSize = locals.size();
        assert index >= 0;
        if (index < currentSize) {
            locals.set(index, value);
        } else if (index == currentSize) {
            locals.add(value);
        } else {
            // however, this is not being entered normally
            for (int i = currentSize; i < index; i++) locals.add(null);
            locals.add(value);
        }
    }

    public void pushReturnValue(IWenyanValue value) {
        processStack.push(value);
    }

    public WenyanException.@Nullable ErrorContext getErrorContext(WenyanException e, Logger logger) {
        WenyanException.ErrorContext errorContext = null;
        try {
            WenyanBytecode.Context context = bytecode.getContext(getProgramCounter() - 1);
            errorContext = new WenyanException.ErrorContext(
                    context.line(), context.column(),
                    bytecode.getSourceCode().substring(context.contentStart(), context.contentEnd()));
        } catch (WenyanException.WenyanVarException |
                 IndexOutOfBoundsException ignore) {// cause error context be null, handled below
        }
        if (errorContext == null)
            logger.error("Unexpected, failed to get code context during handling an exception", e);
        return errorContext;
    }

    private void onReturn(IWenyanRunner runner, IWenyanValue returnValue) throws WenyanUnreachedException {
        runner.getFrameManager().ret();
        if (returnRuntime != null)
            returnRuntime.pushReturnValue(returnValue);
    }

    @FunctionalInterface
    public interface ReturnBehavior {
        void onReturn(IWenyanRunner runner, IWenyanValue returnValue) throws WenyanException;
    }
}
