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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LlmSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmSession.class);
    private static final ExecutorService REQUEST_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "WenyanNature-LLM");
        thread.setDaemon(true);
        return thread;
    });

    private final ILlmClient client;
    private final ExecutorService requestExecutor;
    private final Consumer<Runnable> uiExecutor;
    private final BiFunction<LlmProvider, Consumer<String>, Optional<RequestSettings>> settingsLoader;
    private final List<LlmMessage> history = new ArrayList<>();
    private LlmProvider currentProvider = LlmProvider.DEEPSEEK;
    private int requestId = 0;
    private CompletableFuture<LlmResponse> activeRequest = null;
    private Future<?> activeTask = null;

    public LlmSession() {
        this(new OpenAiCompatibleLlmClient());
    }

    public LlmSession(@NotNull ILlmClient client) {
        this(client, REQUEST_EXECUTOR, runnable -> Minecraft.getInstance().execute(runnable), LlmSession::loadSettings);
    }

    LlmSession(@NotNull ILlmClient client,
               @NotNull ExecutorService requestExecutor,
               @NotNull Consumer<Runnable> uiExecutor,
               @NotNull BiFunction<LlmProvider, Consumer<String>, Optional<RequestSettings>> settingsLoader) {
        this.client = client;
        this.requestExecutor = requestExecutor;
        this.uiExecutor = uiExecutor;
        this.settingsLoader = settingsLoader;
        clearHistory();
    }

    public void setProvider(@NotNull LlmProvider provider) {
        this.currentProvider = provider;
    }

    public LlmProvider getProvider() {
        return currentProvider;
    }

    public synchronized void clearHistory() {
        cancelActiveRequest();
        history.clear();
        history.add(new LlmMessage("system", LlmPromptBuilder.systemPrompt()));
    }

    public synchronized void cancelActiveRequest() {
        requestId++;
        if (activeRequest != null) {
            activeRequest.cancel(true);
            activeRequest = null;
        }
        if (activeTask != null) {
            activeTask.cancel(true);
            activeTask = null;
        }
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
        LlmProvider provider = currentProvider;
        Optional<RequestSettings> settingsOpt = settingsLoader.apply(provider, onError);
        if (settingsOpt.isEmpty()) {
            return;
        }
        RequestSettings settings = settingsOpt.get();
        LlmMessage userMessage = new LlmMessage("user", newPrompt);
        List<LlmMessage> requestMessages;
        int currentRequestId;

        synchronized (this) {
            history.add(userMessage);
            requestMessages = List.copyOf(history);
            currentRequestId = ++requestId;
        }

        LlmRequest request = new LlmRequest(settings.url(), settings.model(), settings.apiKey(), requestMessages);

        CompletableFuture<LlmResponse> requestFuture = new CompletableFuture<>();
        Future<?> requestTask = requestExecutor.submit(() -> {
            try {
                requestFuture.complete(client.request(request));
            } catch (LlmException e) {
                requestFuture.completeExceptionally(new CompletionException(e));
            } catch (Exception e) {
                requestFuture.completeExceptionally(e);
            }
        });
        synchronized (this) {
            activeRequest = requestFuture;
            activeTask = requestTask;
        }
        requestFuture.whenComplete((result, ex) -> {
            uiExecutor.accept(() -> {
                if (isOutdated(currentRequestId)) {
                    return;
                }
                clearActiveRequest(requestFuture);
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

    private static Optional<RequestSettings> loadSettings(LlmProvider provider, Consumer<String> onError) {
        LlmConfig.reload();
        Optional<String> apiKeyOpt = LlmConfig.getOptional(provider.getApiKeyEnvVar());
        if (apiKeyOpt.isEmpty()) {
            onError.accept(LlmConfig.getEnvPath() + " 中未設 " + provider.getApiKeyEnvVar());
            return Optional.empty();
        }

        String apiKey = apiKeyOpt.get();
        String url = LlmConfig.getOptional(provider.getUrlEnvVar()).orElse(provider.getDefaultUrl());
        String model = LlmConfig.getOptional(provider.getModelEnvVar()).orElse(provider.getDefaultModel());
        return Optional.of(new RequestSettings(apiKey, url, model));
    }

    private synchronized boolean isOutdated(int checkedRequestId) {
        return checkedRequestId != requestId;
    }

    private synchronized void clearActiveRequest(CompletableFuture<LlmResponse> requestFuture) {
        if (activeRequest == requestFuture) {
            activeRequest = null;
            activeTask = null;
        }
    }

    private synchronized void removeLastUserMessage(LlmMessage userMessage) {
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).equals(userMessage)) {
                history.remove(i);
                return;
            }
        }
    }

    record RequestSettings(@NotNull String apiKey, @NotNull String url, @NotNull String model) {
    }
}
