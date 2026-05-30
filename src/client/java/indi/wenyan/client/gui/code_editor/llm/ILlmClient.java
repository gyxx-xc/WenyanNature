package indi.wenyan.client.gui.code_editor.llm;

public interface ILlmClient {
    LlmResponse request(LlmRequest request) throws LlmException;
}
