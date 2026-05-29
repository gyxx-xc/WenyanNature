package indi.wenyan.client.gui.code_editor.llm;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class LlmSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmSession.class);

    private final ILlmClient client;
    private final List<LlmMessage> history = new ArrayList<>();
    private LlmProvider currentProvider = LlmProvider.DEEPSEEK;
    private int requestId = 0;

    public LlmSession() {
        this(new OpenAiCompatibleLlmClient());
    }

    public LlmSession(@NotNull ILlmClient client) {
        this.client = client;
        clearHistory();
    }

    public void setProvider(@NotNull LlmProvider provider) {
        this.currentProvider = provider;
    }

    public LlmProvider getProvider() {
        return currentProvider;
    }

    public synchronized void clearHistory() {
        history.clear();
        history.add(new LlmMessage("system", LlmPromptBuilder.systemPrompt()));
        requestId++;
    }

    /**
     * Sends a prompt, appending it to the history array.
     */
    public void generateCode(String userPrompt, String currentCode,
                             Consumer<String> onSuccess, Consumer<String> onError) {
        processRequest(LlmPromptBuilder.buildGeneratePrompt(userPrompt, currentCode), onSuccess, onError);
    }

    /**
     * Fixes an error based on the output. Appends a pre-prompted message to history.
     */
    public void generateFix(String currentCode, String consoleOutput,
                            Consumer<String> onSuccess, Consumer<String> onError) {
        processRequest(LlmPromptBuilder.buildFixPrompt(currentCode, consoleOutput), onSuccess, onError);
    }

    private void processRequest(String newPrompt, Consumer<String> onSuccess, Consumer<String> onError) {
        LlmConfig.reload();
        LlmProvider provider = currentProvider;
        Optional<String> apiKeyOpt = LlmConfig.getOptional(provider.getApiKeyEnvVar());

        if (apiKeyOpt.isEmpty()) {
            onError.accept(LlmConfig.getEnvPath() + " 中未設 " + provider.getApiKeyEnvVar());
            return;
        }

        String apiKey = apiKeyOpt.get();
        String url = LlmConfig.getOptional(provider.getUrlEnvVar()).orElse(provider.getDefaultUrl());
        String model = LlmConfig.getOptional(provider.getModelEnvVar()).orElse(provider.getDefaultModel());
        LlmMessage userMessage = new LlmMessage("user", newPrompt);
        List<LlmMessage> requestMessages;
        int currentRequestId;

        synchronized (this) {
            history.add(userMessage);
            requestMessages = List.copyOf(history);
            currentRequestId = ++requestId;
        }

        LlmRequest request = new LlmRequest(url, model, apiKey, requestMessages);

        CompletableFuture.supplyAsync(() -> {
            try {
                return client.request(request);
            } catch (LlmException e) {
                throw new CompletionException(e);
            }
        }).whenComplete((result, ex) -> {
            Minecraft.getInstance().execute(() -> {
                if (isOutdated(currentRequestId)) {
                    return;
                }
                if (ex != null) {
                    LOGGER.error("LLM API error", ex);
                    removeLastUserMessage(userMessage);
                    onError.accept(getErrorMessage(ex));
                } else {
                    synchronized (this) {
                        history.add(new LlmMessage("assistant", result.content()));
                    }
                    onSuccess.accept(result.content());
                }
            });
        });
    }

    private static String getErrorMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? throwable.getMessage() : cause.getMessage();
    }

    private synchronized boolean isOutdated(int checkedRequestId) {
        return checkedRequestId != requestId;
    }

    private synchronized void removeLastUserMessage(LlmMessage userMessage) {
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).equals(userMessage)) {
                history.remove(i);
                return;
            }
        }
    }
}
