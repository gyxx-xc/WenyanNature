package indi.wenyan.client.gui.code_editor.llm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LlmSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmSession.class);

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private static final String SYSTEM_PROMPT = """
            你是一個文言文程式語言（WenyanNature）的代碼生成助手。
            請嚴格按照以下語法規則輸出純代碼，不要包含任何說明文字、Markdown標記或代碼塊。

            ── 類型 ──
            數（number）、列（list）、言（string）、爻（boolean）

            ── 變量聲明 ──
            吾有一數曰〔值〕。名之曰〔名〕。
            夫〔值〕。                          ← 引用已存在的值（壓棧）
            名之曰〔名〕。                       ← 給棧頂值命名

            ── 賦值 ──
            昔之〔名〕者。今〔值〕是矣。

            ── 算術 ──
            加〔值〕以〔值〕。
            減〔值〕以〔值〕。
            乘〔值〕以〔值〕。
            除〔值〕以〔值〕。
            除〔值〕以〔值〕。所餘幾何。       ← 取餘數

            ── 邏輯 ──
            且〔爻〕以〔爻〕。                 ← AND
            或〔爻〕以〔爻〕。                 ← OR
            變〔爻〕。                          ← NOT
            〔值〕大於〔值〕
            〔值〕小於〔值〕
            〔值〕等於〔值〕
            〔值〕不大於〔值〕
            〔值〕不小於〔值〕
            〔值〕不等於〔值〕

            ── 條件 ──
            若〔爻〕者。
              〔語句〕
            也。

            若〔爻〕者。
              〔語句〕
            若非。
              〔語句〕
            也。

            ── 循環 ──
            為是〔整數〕遍。
              〔語句〕
            云云。

            凡〔列〕中之〔名〕。
              〔語句〕
            云云。

            恆為是
              〔語句〕
            云云。

            乃止。          ← break
            乃止是遍。      ← continue

            ── 術（函數）──
            吾有一術。名之曰〔名〕。是術曰
              〔語句〕
            是謂〔名〕之術也。

            吾有一術。名之曰〔名〕。
            欲行是術必先得〔數量〕〔類型〕曰〔參數名〕。
            乃行是術曰
              〔語句〕
            是謂〔名〕之術也。

            施〔名〕。於〔值〕。             ← 先序調用
            取〔數量〕以施〔名〕。           ← 後序調用（從棧取參數）
            乃得〔值〕。                     ← return 值
            乃得矣。                         ← return（無值）
            乃歸空無。                       ← return null

            ── 輸出 ──
            書〔值〕。                        ← 打印

            ── 字串 ──
            「「〔內容〕」」                  ← 字串字面量

            ── 整數字面量 ──
            零一二三四五六七八九十（中文數字）

            ── 標識符（變量名）──
            「甲」「乙」「丙」「丁」「戊」「己」「庚」「辛」（或自定義中文名）

            ── 其他 ──
            噫。                              ← 引發異常

            請記住：只輸出純代碼，行尾用。結束，不要有任何額外文字。
            """;

    private final List<JsonObject> history = new ArrayList<>();
    private LlmProvider currentProvider = LlmProvider.DEEPSEEK;

    public LlmSession() {
        clearHistory();
    }

    public void setProvider(LlmProvider provider) {
        this.currentProvider = provider;
    }

    public LlmProvider getProvider() {
        return currentProvider;
    }

    public void clearHistory() {
        history.clear();
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", SYSTEM_PROMPT);
        history.add(sys);
    }

    /**
     * Sends a prompt, appending it to the history array.
     */
    public void generateCode(String userPrompt, Consumer<String> onSuccess, Consumer<String> onError) {
        processRequest(userPrompt, onSuccess, onError);
    }

    /**
     * Fixes an error based on the output. Appends a pre-prompted message to history.
     */
    public void generateFix(String consoleOutput, Consumer<String> onSuccess, Consumer<String> onError) {
        String prompt = "運行以上代碼後得到以下輸出/報錯，請修復代碼並僅輸出修復後的完整代碼：\n" + consoleOutput;
        processRequest(prompt, onSuccess, onError);
    }

    private void processRequest(String newPrompt, Consumer<String> onSuccess, Consumer<String> onError) {
        Optional<String> apiKeyOpt = LlmConfig.getOptional(currentProvider.getApiKeyEnvVar());

        if (apiKeyOpt.isEmpty()) {
            onError.accept( LlmConfig.getEnvPath() + " 中未設 " + currentProvider.getApiKeyEnvVar());
            return;
        }

        String apiKey = apiKeyOpt.get();
        String url = LlmConfig.getOptional(currentProvider.getUrlEnvVar()).orElse(currentProvider.getDefaultUrl());
        String model = LlmConfig.getOptional(currentProvider.getModelEnvVar()).orElse(currentProvider.getDefaultModel());

        // Append user prompt to memory
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", newPrompt);
        history.add(userMsg);

        CompletableFuture.supplyAsync(() -> {
            try {
                return callApi(url, model, apiKey, history);
            } catch (Exception e) {
                // Formatting might fail, so we pop the user message off if it completely fails to send
                history.remove(history.size() - 1);
                throw new RuntimeException(e.getMessage(), e);
            }
        }).whenComplete((result, ex) -> {
            Minecraft.getInstance().execute(() -> {
                if (ex != null) {
                    LOGGER.error("LLM API error", ex);
                    onError.accept(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
                } else {
                    // Append assistant response to memory
                    JsonObject asstMsg = new JsonObject();
                    asstMsg.addProperty("role", "assistant");
                    asstMsg.addProperty("content", result);
                    history.add(asstMsg);

                    onSuccess.accept(result);
                }
            });
        });
    }

    private static String callApi(String apiUrl, String model, String apiKey, List<JsonObject> messagesList) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.addProperty("stream", false);

        JsonArray messages = new JsonArray();
        for (JsonObject msg : messagesList) {
            messages.add(msg.deepCopy());
        }
        body.add("messages", messages);

        String bodyJson = body.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }

        return extractContent(response.body());
    }

    private static String extractContent(String responseBody) throws IOException {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("API returned no choices");
            }
            String content = choices.get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();
            
            // Strip markdown fences
            content = content.replaceAll("(?s)^```[a-zA-Z]*\\n?", "").replaceAll("(?s)```\\s*$", "").strip();
            return content;
        } catch (Exception e) {
            throw new IOException("Failed to parse response: " + e.getMessage(), e);
        }
    }
}
