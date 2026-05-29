package indi.wenyan.client.gui.code_editor.llm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAiCompatibleLlmClient implements ILlmClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public LlmResponse request(LlmRequest request) throws LlmException {
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(buildRequest(request), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new LlmException("HTTP " + response.statusCode() + ": " + response.body());
            }
            return new LlmResponse(extractContent(response.body()));
        } catch (IOException e) {
            throw new LlmException("LLM request failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmException("LLM request interrupted", e);
        }
    }

    private static HttpRequest buildRequest(LlmRequest request) {
        JsonObject body = new JsonObject();
        body.addProperty("model", request.model());
        body.addProperty("stream", false);

        JsonArray messages = new JsonArray();
        for (LlmMessage message : request.messages()) {
            JsonObject jsonMessage = new JsonObject();
            jsonMessage.addProperty("role", message.role());
            jsonMessage.addProperty("content", message.content());
            messages.add(jsonMessage);
        }
        body.add("messages", messages);

        return HttpRequest.newBuilder()
                .uri(URI.create(request.apiUrl()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + request.apiKey())
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }

    private static String extractContent(String responseBody) throws LlmException {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new LlmException("API returned no choices");
            }
            String content = choices.get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            return content.replaceAll("(?s)^```[a-zA-Z]*\\n?", "")
                    .replaceAll("(?s)```\\s*$", "")
                    .strip();
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            throw new LlmException("Failed to parse response: " + e.getMessage(), e);
        }
    }
}
