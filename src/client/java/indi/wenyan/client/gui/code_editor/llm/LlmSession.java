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
import java.util.function.Consumer;

public class LlmSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmSession.class);
    private static final ExecutorService REQUEST_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "WenyanNature-LLM");
        thread.setDaemon(true);
        return thread;
    });
    private static final int MAX_HISTORY_MESSAGES = 6;
    private static final int MAX_HISTORY_CHARS = 12_000;

    private final ILlmClient client;
    private final ExecutorService requestExecutor;
    private final Consumer<Runnable> uiExecutor;
    private final SettingsLoader settingsLoader;
    private final List<LlmMessage> history = new ArrayList<>();
    private LlmProvider currentProvider = LlmProvider.DEEPSEEK;
    private ModelTier currentModelTier = ModelTier.NORMAL;
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
               @NotNull SettingsLoader settingsLoader) {
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

    public ModelTier getModelTier() {
        return currentModelTier;
    }

    public void toggleModelTier() {
        currentModelTier = currentModelTier == ModelTier.NORMAL ? ModelTier.REASONING : ModelTier.NORMAL;
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

    public synchronized void dispose() {
        cancelActiveRequest();
        history.clear();
    }

    /**
     * Sends a prompt, appending it to the history array.
     */
    public void generateCode(String userPrompt, String currentCode,
                             Consumer<String> onSuccess, Consumer<String> onError) {
        processRequest(LlmPromptBuilder.buildGeneratePrompt(userPrompt, currentCode), onSuccess, onError);
    }

    private void processRequest(String newPrompt, Consumer<String> onSuccess, Consumer<String> onError) {
        LlmProvider provider = currentProvider;
        ModelTier modelTier = currentModelTier;
        Optional<RequestSettings> settingsOpt = settingsLoader.load(provider, modelTier, onError);
        if (settingsOpt.isEmpty()) {
            return;
        }
        RequestSettings settings = settingsOpt.get();
        LlmMessage userMessage = new LlmMessage("user", newPrompt);
        List<LlmMessage> requestMessages;
        int currentRequestId;

        synchronized (this) {
            history.add(userMessage);
            trimHistory();
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
                        trimHistory();
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

    private static Optional<RequestSettings> loadSettings(LlmProvider provider, ModelTier modelTier, Consumer<String> onError) {
        LlmConfig.reload();
        Optional<String> apiKeyOpt = LlmConfig.getOptional(provider.getApiKeyEnvVar());
        if (apiKeyOpt.isEmpty()) {
            onError.accept(LlmConfig.getEnvPath() + " 中未設 " + provider.getApiKeyEnvVar());
            return Optional.empty();
        }

        String apiKey = apiKeyOpt.get();
        String url = LlmConfig.getOptional(provider.getUrlEnvVar()).orElse(provider.getDefaultUrl());
        if (url.isBlank()) {
            onError.accept(LlmConfig.getEnvPath() + " 中未設 " + provider.getUrlEnvVar());
            return Optional.empty();
        }
        String modelEnvVar = modelTier.getModelEnvVar(provider);
        String defaultModel = modelTier.getDefaultModel(provider);
        String model = LlmConfig.getOptional(modelEnvVar).orElse(defaultModel);
        if (model.isBlank()) {
            onError.accept(LlmConfig.getEnvPath() + " 中未設 " + modelEnvVar);
            return Optional.empty();
        }
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

    private void trimHistory() {
        while (history.size() > MAX_HISTORY_MESSAGES + 1) {
            history.remove(1);
        }
        while (history.size() > 2 && historyCharCount() > MAX_HISTORY_CHARS) {
            history.remove(1);
        }
    }

    private int historyCharCount() {
        int total = 0;
        for (LlmMessage message : history) {
            total += message.content().length();
        }
        return total;
    }

    synchronized List<LlmMessage> getHistorySnapshotForTesting() {
        return List.copyOf(history);
    }

    record RequestSettings(@NotNull String apiKey, @NotNull String url, @NotNull String model) {
    }

    public enum ModelTier {
        NORMAL("普通模型") {
            @Override
            String getModelEnvVar(LlmProvider provider) {
                return provider.getModelEnvVar();
            }

            @Override
            String getDefaultModel(LlmProvider provider) {
                return provider.getDefaultModel();
            }
        },
        REASONING("推理模型") {
            @Override
            String getModelEnvVar(LlmProvider provider) {
                return provider.getReasoningModelEnvVar();
            }

            @Override
            String getDefaultModel(LlmProvider provider) {
                return provider.getDefaultReasoningModel();
            }
        };

        private final String label;

        ModelTier(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        abstract String getModelEnvVar(LlmProvider provider);

        abstract String getDefaultModel(LlmProvider provider);
    }

    @FunctionalInterface
    interface SettingsLoader {
        Optional<RequestSettings> load(LlmProvider provider, ModelTier modelTier, Consumer<String> onError);
    }
}
