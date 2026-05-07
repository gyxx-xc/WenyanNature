package indi.wenyan.judou.structure;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Consumer;

public interface IHandleableException {
    void handle(Consumer<String> output, Logger logger, @Nullable ErrorContext context);

    record ErrorContext(int line, int column, String segment) {
    }
}
