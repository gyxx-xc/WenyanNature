package indi.wenyan.client.gui.llm.backend.llm;

public class LlmException extends Exception {
    public LlmException(String message) {
        super(message);
    }

    public LlmException(String message, Throwable cause) {
        super(message, cause);
    }
}
