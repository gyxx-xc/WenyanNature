package indi.wenyan.judou.structure;

/**
 * Base class for exceptions that can be thrown during Wenyan execution
 */
public abstract class WenyanThrowException extends Exception {
    protected WenyanThrowException(String message) {
        super(message);
    }
}
