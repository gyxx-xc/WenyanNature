package indi.wenyan.judou.structure;

public class WenyanUnreachedException extends WenyanException {
    public WenyanUnreachedException() {
        super("unreached, please report an issue");
    }

    public static class WenyanUnexceptedException extends WenyanUnreachedException {
        public final Throwable cause;

        public WenyanUnexceptedException(Throwable e) {
            super();
            cause = e;
        }
    }
}
