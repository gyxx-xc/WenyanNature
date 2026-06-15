package indi.wenyan.content.block;

import indi.wenyan.judou.api.runtime.IWenyanScheduler;

import java.util.Optional;
import java.util.function.Supplier;

public class LazyProgram<T extends IWenyanScheduler<?>> implements Supplier<T> {
    private T optionalProgram;
    private final Supplier<T> programSupplier;

    public LazyProgram(Supplier<T> programSupplier) {
        this.programSupplier = programSupplier;
    }

    public T createOrGet() {
        if (optionalProgram == null || !optionalProgram.isAvailable())
            optionalProgram = programSupplier.get();
        return optionalProgram;
    }

    @Override
    public T get() {
        return createOrGet();
    }

    public Optional<T> ifCreated() {
        return Optional.ofNullable(optionalProgram);
    }
}
