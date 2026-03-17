package indi.wenyan.content.block.runner;

import indi.wenyan.judou.runtime.IWenyanProgram;

import java.util.Optional;
import java.util.function.Supplier;

public class LazyProgram<T extends IWenyanProgram<?>> {
    private T optionalProgram;
    private final Supplier<T> programSupplier;

    public LazyProgram(Supplier<T> programSupplier) {
        this.programSupplier = programSupplier;
    }

    public T create() {
        if (optionalProgram == null || !optionalProgram.isAvailable())
            optionalProgram = programSupplier.get();
        return optionalProgram;
    }

    public Optional<T> ifCreated() {
        return Optional.ofNullable(optionalProgram);
    }
}
