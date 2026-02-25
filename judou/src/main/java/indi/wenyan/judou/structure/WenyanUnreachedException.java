package indi.wenyan.judou.structure;

import org.slf4j.Logger;

import java.util.function.Consumer;

public class WenyanUnreachedException extends WenyanException {
    public WenyanUnreachedException() {
        super("unreached");
    }

    public WenyanUnreachedException(String message) {
        super(message);
    }

    public static class WenyanUnexceptedException extends WenyanUnreachedException {
        public final Throwable cause;

        public WenyanUnexceptedException(Throwable e) {
            super();
            cause = e;
        }

        @Override
        public void handle(Consumer<String> output, Logger logger, ErrorContext context) {
            if (context != null)
                logger.error("At {}:{} {}", context.line(), context.column(), context.segment());
            logger.error("WenyanThread died with an unexpected exception", this);
            logger.error("caused by: ", cause);
            output.accept("WenyanThread died with an unexpected exception, killed");
        }
    }

    @Override
    public void handle(Consumer<String> output, Logger logger, ErrorContext context) {
        if (context != null)
            logger.error("At {}:{} {}", context.line(), context.column(), context.segment());
        logger.error("WenyanThread died with an unexpected exception", this);
        output.accept("WenyanThread died with an unexpected exception, killed");
    }
}
