package indi.wenyan.client.gui.llm.backend.llm;

public interface ILlmClient {
    LlmResponse request(LlmRequest request) throws LlmException;
}
