package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanPackages;

public class ImportCode extends WenyanCode {
    private final Operation operation;

    public ImportCode(Operation op) {
        super(name(op));
        operation = op;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        String id = runtime.bytecode.getIdentifier(args);
        switch (operation) {
            case IMPORT -> runtime.importEnvironment(WenyanPackages.PACKAGES
                    .get(id));
            case IMPORT_FROM -> {
                try {
                    String name = runtime.processStack.peek()
                            .as(WenyanString.TYPE).value();
                    runtime.setVariable(id,
                            WenyanPackages.PACKAGES.get(name).variables.get(id));
                } catch (WenyanException.WenyanTypeException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public enum Operation {
        IMPORT,
        IMPORT_FROM
    }

    private static String name(Operation operation) {
        return switch (operation) {
            case IMPORT -> "IMPORT";
            case IMPORT_FROM -> "IMPORT_FROM";
        };
    }
}
